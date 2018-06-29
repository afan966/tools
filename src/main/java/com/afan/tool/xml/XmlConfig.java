package com.afan.tool.xml;

/**
 * XML配置
 * @author afan
 *
 */
public interface XmlConfig {
	
	enum Visibility{
		ANY,//所有
		PUBLIC_ONLY,//只要PUBLIC
		PUBLIC_PROTECTED;//PUBLIC和PROTECTED
	}
	
	//属性可见
	public Visibility FIELD = Visibility.ANY;
	//set方法
	public Visibility SETTER = Visibility.ANY;
	//get方法
	public Visibility GETTER = Visibility.ANY;
	//NULL转化
	public boolean NON_NULL = true;
	
	public static final String SETTER_METHOD_FIX = "set";
	public static final String GETTER_METHOD_FIX = "get";
	public static final String BOOLGETTER_METHOD_FIX = "is";
	
	
	public static final String TAG_START = "<";
	public static final String TAG_END = ">";
	public static final String ETAG_START = "</";
	
	public static final String ROOT ="xml";
	
	public static final String ROOT_START = TAG_START + ROOT + TAG_END;
	public static final String ROOT_END = ETAG_START + ROOT + TAG_END;
	
}
