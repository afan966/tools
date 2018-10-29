package com.afan.tool.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 基于Jackson2.x.x的JSON工具
 * 包com.fasterxml.jackson
 * 
 * @author afan
 *
 */
public class JsonUtil {
	
	private static final ThreadLocal<ObjectMapper> mapper = new ThreadLocal<ObjectMapper>();
	private static final Object LOCK = new Object();
	
	private static ObjectMapper getMapper(){
		ObjectMapper m = mapper.get();
		if(m==null){
			synchronized (LOCK) {
				mapper.set(new ObjectMapper());
			}
			m = mapper.get();
		}
		return m;
	}
	
	public static void init(){
		ObjectMapper m = getMapper();
		//在反序列化时忽略在 JSON 中存在但 Java 对象不存在的属性
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		//在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss.SSSZ
		m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
		//在序列化时忽略值为 null 的属性
		m.setSerializationInclusion(Include.NON_NULL);
		//Include.Include.ALWAYS 默认
		//Include.NON_DEFAULT 属性为默认值不序列化
		//Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
		//Include.NON_NULL 属性为NULL 不序列化 
		//只对VO起作用，Map List不起作用
		
		//@JsonInclude(Include.NON_NULL) 
		//将该标记放在属性上，如果该属性为NULL则不参与序列化
		//如果放在类上边,那对这个类的全部属性起作用 
		
		/*jackson默认的字段属性发现规则如下：
		所有被public修饰的字段->所有被public修饰的getter->所有被public修饰的setter
		若类中的一个private属性，且没有设置public的getter和setter方法，则对该类对象进行序列化时，默认不对这个private属性进行序列化。
		若此时任然需要对该private属性进行序列化，可以通过设置自动检测功能来实现：
		m.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY) // 自动检测所有类的全部属性
		.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY) //自动检测所有类的public getter方法
		.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.ANY); //自动检测所有类的public setter方法
		*/
		/*@JsonAutoDetect（作用在类上）来开启/禁止自动检测　　
	　　　fieldVisibility:字段的可见级别
	　　　ANY:任何级别的字段都可以自动识别
	　　　NONE:所有字段都不可以自动识别
	　　　NON_PRIVATE:非private修饰的字段可以自动识别
	　　　PROTECTED_AND_PUBLIC:被protected和public修饰的字段可以被自动识别
	　　　PUBLIC_ONLY:只有被public修饰的字段才可以被自动识别
	　　　DEFAULT:同PUBLIC_ONLY
	　	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) 
		*/
		
		/*序列化属性
		//这个特性，决定了解析器是否将自动关闭那些不属于parser自己的输入源。
        // 如果禁止，则调用应用不得不分别去关闭那些被用来创建parser的基础输入流InputStream和reader；
        //默认是true
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        //是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //设置为true时，属性名称不带双引号
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        //反序列化是是否允许属性名称不带双引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否允许单引号来包住属性名称和字符串值
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //是否允许JSON整数以多个0开始
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        //null的属性不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //按字母顺序排序属性,默认false
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY,true);
        //是否以类名作为根元素，可以通过@JsonRootName来自定义根元素名称,默认false
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,true);
        //是否缩放排列输出,默认false
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT,false);
        //序列化Date日期时以timestamps输出，默认true
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,true);
        //序列化枚举是否以toString()来输出，默认false，即默认以name()来输出
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        //序列化枚举是否以ordinal()来输出，默认false
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX,false);
        //序列化单元素数组时不以数组来输出，默认false
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,true);
        //序列化Map时对key进行排序操作，默认false
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
        //序列化char[]时以json数组输出，默认false
        objectMapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS,true);
        //序列化BigDecimal时是输出原始数字还是科学计数，默认false，即以toPlainString()科学计数方式来输出
        objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN,true);
		*/
		
		/*反序列化属性
		//当遇到未知属性（没有映射到属性，没有任何setter或者任何可以处理它的handler，是否应该抛出JsonMappingException异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//该特性决定对于json浮点数，是否使用BigDecimal来序列化。如果不允许，则使用Double序列化。 默认为false
		objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);
  		//该特性决定对于json整形（非浮点），是否使用BigInteger来序列化。如果不允许，则根据数值大小来确定 是使用Integer或者Long
		objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, false);
		//该特性决定JSON ARRAY是映射为Object[]还是List<Object>。如果开启，都为Object[]，false时，则使用List  默认为false
		objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false);
		//是否使用Enum.toString()的值对json string进行反序列化。这个的设置和WRITE_ENUMS_USING_TO_STRING需要一致。
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		*/
		
		/*序列化和反序列化注解
		https://www.cnblogs.com/jian-xiao/p/6009435.html?utm_source=itdadao&utm_medium=referral
		1.@JsonIgnore：作用在字段或方法上，用来完全忽略被注解的字段和方法对应的属性。
 　  		2.@JsonProperty：作用在字段或方法上，用来对属性的序列化/反序列化，可以用来避免遗漏属性，同时提供对属性名称重命名。
　　　　	对属性添加了@JsonProperty注解后，即使该属性为private且没有getter和setter方法，也会进行序列化。
　　		3.@JsonIgnoreProperties
		作用在类上，用来说明有些属性在序列化/反序列化时需要忽略掉，可以将它看做是@JsonIgnore的批量操作，它还有一个重要的功能是作用在反序列化时解析字段时过滤一些未知的属性，否则通常情况下解析到我们定义的类不认识的属性便会抛出异常。
		可以注明是想要忽略的属性列表如@JsonIgnoreProperties({"name","age","title"})，
		也可以注明过滤掉未知的属性如@JsonIgnoreProperties(ignoreUnknown=true)
		4、@JsonUnwrapped作用在属性字段或方法上，用来将子JSON对象的属性添加到封闭的JSON对象。
		5.@JsonSerialize和@JsonDeserialize:作用于方法和字段上，通过 using(JsonSerializer)和using(JsonDeserializer)来指定序列化和反序列化的实现。下面的例子中自定义了日期的序列化和反序列化方式，可以将Date和指定日期格式字符串之间相互转换。
		6.@JsonPropertyOrder:作用在类上，被用来指明当序列化时需要对属性做排序。@jsonPropertyOrder(alphabetic = true)
		7.@JsonView:视图模板，作用于方法和属性上，用来指定哪些属性可以被包含在JSON视图中，在前面我们知道已经有@JsonIgnore和@JsonIgnoreProperties可以排除过滤掉不需要序列化的属性，可是如果一个POJO中有h很多个属性，而我们可能只需要概要简单信息即序列化时只想输出其中几个属性，此时使用@JsonIgnore和@JsonIgnoreProperties就显得非常繁琐，而使用@JsonView便会非常方便，只许在你想要输出的属性(或对应的getter)上添加@JsonView即可。
		8.@JsonFilter:Json属性过滤器，作用于类，作用同上面的@JsonView，都是过滤掉不想要的属性，输出自己想要的属性。和@FilterView不同的是@JsonFilter可以动态的过滤属性。eg:
		定义了一个名为myFilter的SimpleFilterProvider，这个过滤器将会过滤掉所有除a属性以外的属性。
		9.@JsonAnySetter:作用于方法，在反序列化时用来处理遇到未知的属性的时候调用，在本文前面我们知道可以通过注解@JsonIgnoreProperties(ignoreUnknown=true)来过滤未知的属性，但是如果需要这些未知的属性该如何是好?那么@JsonAnySetter就可以派上用场了，它通常会和map属性配合使用用来保存未知的属性，　
		*
		*/
	}
	
	/**
	 * 对象转JSON字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		try {
			return getMapper().writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * JSON字符串转对象
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> T toObject(String json, Class<T> valueType) {
		try {
			return getMapper().readValue(json, valueType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * JSON转对象,自定义返回对象
	 * @param json
	 * @param typeReference->new TypeReference<List<User>>() {}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String json, TypeReference<T> typeReference) {
		try {
			return (T) getMapper().readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * JSON字符串转List对象
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> List<T> toList(String json, Class<T> valueType) {
		try {
			//return getMapper().readValue("", getMapper().getTypeFactory().constructCollectionType(List.class, valueType));
			return toObject(json, new TypeReference<List<T>>() {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * JSON字符串转Map对象
	 * @param json
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public static <K,V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
		try {
			//return getMapper().readValue("", getMapper().getTypeFactory().constructMapType(HashMap.class, valueKeyType, valueValueType));
			return toObject(json, new TypeReference<Map<K, V>>() {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * JSON字符串转StringMap
	 * @param json
	 * @return
	 */
	public static Map<String, String> toMap(String json) {
		try {
			return getMapper().readValue(json, getMapper().getTypeFactory().constructMapType(HashMap.class, String.class, String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * JSON字符串转Map
	 * @param json
	 * @return
	 */
	public static Map<?, ?> toMapObject(String json) {
		try {
			return getMapper().readValue(json, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		User user = new User();
		user.setUserId(10003);
		user.setName("haha");
		user.setAge(10);
		
		User user1 = new User();
		user1.setUserId(10005);
		user1.setName("xixi");
		user1.setAge(10);
		
		Map<Long, User> userMap = new HashMap<Long, User>();
		userMap.put(user.getUserId(), user);
		userMap.put(user1.getUserId(), user1);
		System.out.println(toJson(userMap));
		
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		userList.add(user1);
		System.out.println(toJson(userList));
		
		System.out.println(user.getClass().getSuperclass().getDeclaredMethods());
		
		String json = "{\"10005\":{\"userId\":10005,\"name\":\"xixi\",\"age\":10},\"10003\":{\"userId\":10003,\"name\":\"haha\",\"age\":10}}";
		
		json = "[{\"userId\":10003,\"name\":\"haha\",\"age\":10},{\"userId\":10005,\"name\":\"xixi\",\"age\":10}]";
		
		List<User> list2 = toObject(json, new TypeReference<List<User>>() {
		});
		System.out.println(list2);
		
//		Map<Long, User> map = toMap(json, Long.class, User.class)
//		System.out.println();
	}
	

}
class User{
	long userId;
	String name;
	int age;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}