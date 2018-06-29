package com.afan.tool.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * 基于连接池的Http工具(http-client4.x)
 * 
 */
public class WebUtil {

    static final int requestTimeOut = 10 * 1000;
    static final int connectTimeOut = 10 * 1000;
    static final int readTimeOut = 10 * 1000;
    
    static final String HTTP = "http";
    static final String HTTPS = "https";
    static final String CONTENT_TYPE = "Content-Type";
    
    private static CloseableHttpClient httpClient = null;
    private final static Object syncLock = new Object();
    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(requestTimeOut)
                .setConnectTimeout(connectTimeOut)
                .setSocketTimeout(readTimeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient
     * @param url
     * @return
     */
    public static CloseableHttpClient getHttpClient(String url) {
    	return getHttpClient(url, null);
    }
    
    /**
     * 获取HttpClient,带ssl证书
     * @param url
     * @param sslFactory
     * @return
     */
    public static CloseableHttpClient getHttpClient(String url, SSLConnectionSocketFactory sslFactory) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(20, 5, 10, hostname, port, sslFactory);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     * @param maxTotal
     * @param maxPerRoute
     * @param maxRoute
     * @param hostname
     * @param port
     * @param sslFactory
     * @return
     */
    public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname, int port, SSLConnectionSocketFactory sslFactory) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        //LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		if (sslFactory == null) {
			sslFactory = SSLConnectionSocketFactory.getSocketFactory();
		}
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register(HTTP, plainsf).register(HTTPS, sslFactory).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
        return httpClient;
    }

    private static void setPostParams(HttpPost httpost, Map<String, Object> params, String charset) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String post(String url, Map<String, Object> params, String charset) throws IOException {
    	return post(url, params, charset, null);
    }
    
    /**
     * POST请求
     * @param url
     * @param params
     * @param charset
     * @param factory
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> params, String charset, SSLConnectionFactory factory) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        config(httpPost);
        setPostParams(httpPost, params, charset);
        CloseableHttpClient client = factory == null ? getHttpClient(url) : getHttpClient(url, factory.getSSLConnectionSocketFactory());
		try (CloseableHttpResponse response = client.execute(httpPost, HttpClientContext.create())) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, charset);
			EntityUtils.consume(entity);
			return result;
		} catch (IOException e) {
			throw e;
		}
    }
    
    public static String post(String url, String data, String contentType, String charset) throws IOException {
    	return post(url, data, contentType, charset, null);
    }
    
    /**
     * POST数据
     * @param url
     * @param data
     * @param contentType
     * @param charset
     * @param factory
     * @return
     * @throws IOException
     */
    public static String post(String url, String data, String contentType, String charset, SSLConnectionFactory factory) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        config(httpPost);
        StringEntity myEntity = new StringEntity(data, charset);
        httpPost.addHeader(CONTENT_TYPE, contentType);
        httpPost.setEntity(myEntity);
        CloseableHttpClient client = factory == null ? getHttpClient(url) : getHttpClient(url, factory.getSSLConnectionSocketFactory());
        try (CloseableHttpResponse response = client.execute(httpPost, HttpClientContext.create());
        		InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), charset)) {
            char[] buff = new char[1024];
            int length = 0;
            StringBuilder result = new StringBuilder();
            while ((length = reader.read(buff)) != -1) {
            	result.append(new String(buff, 0, length));
            }
            return result.toString();
        } catch (IOException e) {
			throw e;
		}
    }

    /**
     * GET请求
     * @param url
     * @param charset
     * @return
     */
    public static String get(String url, String charset) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        config(httpGet);
        try (CloseableHttpResponse response = getHttpClient(url).execute(httpGet, HttpClientContext.create())) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
        	throw e;
        }
    }

    public static void main(String[] args) {
    	String data = "<xml><appid><![CDATA[wxc9063e3a56b05d76]]></appid>"+"\n"+"<bank_type><![CDATA[CFT]]></bank_type>"+"\n"+"<cash_fee><![CDATA[1]]></cash_fee>"+"\n"+"<fee_type><![CDATA[CNY]]></fee_type>"+"\n"+"<is_subscribe><![CDATA[N]]></is_subscribe>"+"\n"+"<mch_id><![CDATA[1487722302]]></mch_id>"+"\n"+"<nonce_str><![CDATA[f2cdd13c54c54eb5a7174ec7e3b271cc]]></nonce_str>"+"\n"+"<openid><![CDATA[oy10b0ekbCLlcM5wTgsHoqvxfBtU]]></openid>"+"\n"+"<out_trade_no><![CDATA[150536043500686]]></out_trade_no>"+"\n"+"<result_code><![CDATA[SUCCESS]]></result_code>"+"\n"+"<return_code><![CDATA[SUCCESS]]></return_code>"+"\n"+"<sign><![CDATA[BC41B87449FF023A650E49D58E9DF749]]></sign>"+"\n"+"<time_end><![CDATA[20171107195412]]></time_end>"+"\n"+"<total_fee>1</total_fee>"+"\n"+"<trade_type><![CDATA[MWEB]]></trade_type>"+"\n"+"<transaction_id><![CDATA[4200000006201711073117052294]]></transaction_id>"+"\n"+"</xml>";
		try {
			String re = post("https://fhdowx.xyy001.com/pay/h5Notify.do", data, "text/html", "UTF-8");
			System.out.println(re);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        /*
        String[] urisToGet = {
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497" };

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                config(httpget);
                // 启动线程抓取
                executors
                        .execute(new GetRunnable(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
        */
    }

    static class GetRunnable implements Runnable {
        private CountDownLatch countDownLatch;
        private String url;

        public GetRunnable(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println(WebUtil.get(url,"utf-8"));
            } catch (Exception e) {
            	e.printStackTrace();
			} finally {
                countDownLatch.countDown();
            }
        }
    }
}
