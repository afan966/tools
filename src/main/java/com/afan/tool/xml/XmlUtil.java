package com.afan.tool.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.afan.tool.string.StringUtil;

/**
 * 基于dom4j的xml解析，
 * 大文本xml不要用这个，浪费内存
 * @author afan
 *
 */
public class XmlUtil {
	
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String xml, Class<T> valueType) {
		try {
			T obj = valueType.newInstance();
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			List<Element> properties=root.elements();
			xml2Obj(properties, valueType, obj);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static void xml2Obj(List<Element> properties, Class<?> valueType, Object obj) {
		try {
			Map<String, Field> fieldMap = getDeclaredFields(obj);
			Map<String, Method> methodMap = getSetterDeclaredMethods(obj);
		    //获取根节点
			for(Element pro:properties){
				//获取属性名(首字母大写)
				String name=pro.getName();
				String value=pro.getStringValue();
				Field field = fieldMap.get(name);
				if(field!=null){
					if(field.getAnnotation(XmlIgnore.class)!=null){
						continue;
					}
					Class<?> type = field.getType();
					Object pValue = null;
					if(!pro.isTextOnly()){
						pValue = type.newInstance();
						xml2Obj(pro.elements(), type, pValue);
					}else{
						if(value!=null && value.length()>0){
							if (int.class == type || Integer.class == type){
								pValue = Integer.valueOf(value);
							}else if (long.class == type || Long.class == type){
								pValue = Long.valueOf(value);
							}else if (double.class == type || Double.class == type){
								pValue = Double.valueOf(value);
							}else if (boolean.class == type || Boolean.class == type){
								pValue = Boolean.valueOf(value);
							}else if (byte.class == type || Byte.class == type){
								pValue = Byte.valueOf(value);
							}else if (char.class == type || Character.class == type){
								pValue = value.charAt(0);
							}else if (short.class == type || Short.class == type){
								pValue = Short.valueOf(value);
							}else if (float.class == type || Float.class == type){
								pValue = Float.valueOf(value);
							}else{
								pValue = value;
							}
						}
					}
					if((XmlConfig.NON_NULL && pValue!=null)||!XmlConfig.NON_NULL){
						field.set(obj, pValue);
					}
				}else{
					Method method = methodMap.get(name);
					if(method!=null){
						if(method.getAnnotation(XmlIgnore.class)!=null){
							continue;
						}
						Class<?> type = method.getParameterTypes()[0];
						Object pValue = null;
						if(!pro.isTextOnly()){
							pValue = type.newInstance();
							xml2Obj(pro.elements(), type, pValue);
						}else{
							if(value!=null && value.length()>0){
								if (int.class == type || Integer.class == type){
									pValue = Integer.valueOf(value);
								}else if (long.class == type || Long.class == type){
									pValue = Long.valueOf(value);
								}else if (double.class == type || Double.class == type){
									pValue = Double.valueOf(value);
								}else if (boolean.class == type || Boolean.class == type){
									pValue = Boolean.valueOf(value);
								}else if (byte.class == type || Byte.class == type){
									pValue = Byte.valueOf(value);
								}else if (char.class == type || Character.class == type){
									pValue = value.charAt(0);
								}else if (short.class == type || Short.class == type){
									pValue = Short.valueOf(value);
								}else if (float.class == type || Float.class == type){
									pValue = Float.valueOf(value);
								}else{
									pValue = value;
								}
							}
						}
						if((XmlConfig.NON_NULL && pValue!=null)||!XmlConfig.NON_NULL){
							method.invoke(obj, pValue);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String xml){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			List<Element> properties=root.elements();
			xml2Map(properties, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> xml2Map(List<Element> properties, Map<String, Object> result){
		try {
			for(Element pro:properties){
				String name=pro.getName();
				if(pro.isTextOnly()){
					String value=pro.getStringValue();
					result.put(name, value);
				}else{
					Map<String, Object> subResult = new HashMap<String, Object>();
					xml2Map(pro.elements(), subResult);
					result.put(name, subResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String toXml(Object obj) {
		return XmlConfig.ROOT_START + obj2Xml(obj) + XmlConfig.ROOT_END;
	}
	
	public static String toXml(Object obj, String root) {
		return xmlTag(root, obj2Xml(obj));
	}
	
	public static String xmlTag(String tag, String value) {
		StringBuilder xml = new StringBuilder();
		xml.append(XmlConfig.TAG_START);
		xml.append(tag);
		xml.append(XmlConfig.TAG_END);
		xml.append(value);
		xml.append(XmlConfig.ETAG_START);
		xml.append(tag);
		xml.append(XmlConfig.TAG_END);
		return xml.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private static String obj2Xml(Object obj) {
		StringBuilder xml = new StringBuilder();
		if(obj instanceof Collection){
			Collection coll = (Collection)obj;
 			Iterator iter = coll.iterator();
 			while (iter.hasNext()) {
 				Object object = iter.next();
 				xml.append(xmlTag(object.getClass().getSimpleName(), obj2Xml(object)));
			}
		}else if(obj instanceof Map){
			Map map = (Map) obj;
			Iterator iter = map.keySet().iterator();
			while(iter.hasNext()){
				Object key = iter.next();
				xml.append(xmlTag(key.toString(), obj2Xml(map.get(key))));
			}
		}else{
			xml.append(objectXml(obj));
		}
		return xml.toString();
	}	
	
	private static String objectXml(Object obj) {	
		StringBuilder xmlData = new StringBuilder();
		try {
			Set<String> fieldSet = new HashSet<String>();
			Map<String, Field> fieldMap = getDeclaredFields(obj);
			for (String key : fieldMap.keySet()) {
				Field field = fieldMap.get(key);
				String tagName = key;
				Object tagValue = field.get(obj);
				String subXml = null;
				
				if(field.getAnnotation(XmlIgnore.class)!=null){
					continue;
				}
				
				if (XmlConfig.NON_NULL && tagValue == null) {
					continue;
				}
				
				if (field.getType().isPrimitive() || isPackagePrimitive(field.getType())) {
				} else if (String.class == field.getType() ) {
					tagValue = "<![CDATA[" + tagValue + "]]>";
				} else {
					if (tagValue != null) {
						subXml = obj2Xml(tagValue);
					}
				}
				
				if (isChar(field.getType())) {
					if((Character)tagValue == Character.MIN_VALUE){
						tagValue = "";
					}
				}
				
				if (subXml != null) {
					tagValue = subXml;
				} else {
					tagValue = (tagValue == null ? "" : tagValue);
				}
				
				xmlData.append(XmlConfig.TAG_START);
				xmlData.append(tagName);
				xmlData.append(XmlConfig.TAG_END);
				xmlData.append(tagValue);
				xmlData.append(XmlConfig.ETAG_START);
				xmlData.append(tagName);
				xmlData.append(XmlConfig.TAG_END);
				
				fieldSet.add(field.getName());
			}
			
			List<Method> methods = getGetterDeclaredMethods(obj);
			for (Method method : methods) {
				String tagName = null;
				Object tagValue = method.invoke(obj);
				String subXml = null;
				
				if(method.getName().startsWith(XmlConfig.GETTER_METHOD_FIX)){
					tagName = method.getName().substring(XmlConfig.GETTER_METHOD_FIX.length());
				}else if(method.getName().startsWith(XmlConfig.BOOLGETTER_METHOD_FIX)){
					tagName = method.getName().substring(XmlConfig.BOOLGETTER_METHOD_FIX.length());
				}
				if (StringUtil.isBlank(tagName)) {
					continue;
				}
				tagName = tagName.substring(0, 1).toLowerCase() + tagName.substring(1);
				if (fieldSet.contains(tagName)){
					continue;
				}
				if (XmlConfig.NON_NULL && tagValue == null) {
					continue;
				}
				
				if(Class.class == method.getReturnType()){
					continue;
				}
				
				if(fieldMap.get(tagName)!=null && fieldMap.get(tagName).getAnnotation(XmlIgnore.class)!=null){
					continue;
				}
				
				if (method.getReturnType().isPrimitive() || isPackagePrimitive(method.getReturnType())) {
				} else if (String.class == method.getReturnType()) {
					tagValue = "<![CDATA[" + tagValue + "]]>";
				} else {
					if (tagValue != null) {
						subXml = obj2Xml(tagValue);
					}
				}
				
				if (isChar(method.getReturnType())) {
					if((Character)tagValue == Character.MIN_VALUE){
						tagValue = "";
					}
				}
				
				if (subXml != null) {
					tagValue = subXml;
				} else {
					tagValue = (tagValue == null ? "" : tagValue);
				}
				
				xmlData.append(XmlConfig.TAG_START);
				xmlData.append(tagName);
				xmlData.append(XmlConfig.TAG_END);
				xmlData.append(tagValue);
				xmlData.append(XmlConfig.ETAG_START);
				xmlData.append(tagName);
				xmlData.append(XmlConfig.TAG_END);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlData.toString();
	}
	
	private static Map<String, Field> getDeclaredFields(Object obj){
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		Field[] declaredFields = null;
		Field[] fields = obj.getClass().getDeclaredFields();
		Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();
		if (superFields.length > 0) {
			declaredFields = new Field[fields.length + superFields.length];
			System.arraycopy(fields, 0, declaredFields, 0, fields.length);
			System.arraycopy(superFields, 0, declaredFields, fields.length, superFields.length);
		} else {
			declaredFields = fields;
		}
		
		for (Field field : declaredFields) {
			String key = field.getName();
			XmlProperty property = field.getAnnotation(XmlProperty.class);
			if (property != null) {
				key = property.alia();
			}
			
			switch (XmlConfig.FIELD) {
			case ANY:
				field.setAccessible(true);
				fieldMap.put(key, field);
				break;
			case PUBLIC_ONLY:
				if (Modifier.isPublic(field.getModifiers())) {
					fieldMap.put(key, field);
				}
				break;
			case PUBLIC_PROTECTED:
				if (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) {
					fieldMap.put(key, field);
				}
				break;
			default:
				break;
			}
		}
		return fieldMap;
	}
	
	private static Method[] getDeclaredMethods(Object obj) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		Method[] superMethods = obj.getClass().getSuperclass().getDeclaredMethods();
		if (superMethods.length == 0) {
			return methods;
		} else {
			Method[] declaredMethods = new Method[methods.length + superMethods.length];
			System.arraycopy(methods, 0, declaredMethods, 0, methods.length);
			System.arraycopy(superMethods, 0, declaredMethods, methods.length, superMethods.length);
			return declaredMethods;
		}
	}
	
	private static Map<String, Method> getSetterDeclaredMethods(Object obj) {
		Map<String, Method> methodMap = new HashMap<String, Method>();
		Method[] methods = getDeclaredMethods(obj);
		for (Method method : methods) {
			if (method.getName().startsWith(XmlConfig.SETTER_METHOD_FIX) && method.getParameterTypes().length==1) {
				String key = method.getName();
				
				XmlProperty property = method.getAnnotation(XmlProperty.class);
				if(property!=null){
					key = property.alia();
				}else{
					key = key.substring(XmlConfig.SETTER_METHOD_FIX.length());
					key = key.substring(0,1).toLowerCase() + key.substring(1);
				}
				
				switch (XmlConfig.SETTER) {
				case ANY:
					method.setAccessible(true);
					methodMap.put(key, method);
					break;
				case PUBLIC_ONLY:
					if (Modifier.isPublic(method.getModifiers())) {
						methodMap.put(key, method);
					}
					break;
				case PUBLIC_PROTECTED:
					if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
						methodMap.put(key, method);
					}
					break;
				default:
					break;
				}
			}
		}
		return methodMap;
	}
	
	private static List<Method> getGetterDeclaredMethods(Object obj) {
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = getDeclaredMethods(obj);
		for (Method method : methods) {
			if (method.getParameterTypes().length==0 && void.class != method.getReturnType()) {
				if(method.getName().startsWith(XmlConfig.GETTER_METHOD_FIX) ||
					((method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) && method.getName().startsWith(XmlConfig.BOOLGETTER_METHOD_FIX))){
					switch (XmlConfig.GETTER) {
					case ANY:
						method.setAccessible(true);
						methodList.add(method);
						break;
					case PUBLIC_ONLY:
						if (Modifier.isPublic(method.getModifiers())) {
							methodList.add(method);
						}
						break;
					case PUBLIC_PROTECTED:
						if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
							methodList.add(method);
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return methodList;
	}
	
	private static boolean isPackagePrimitive(Class<?> clazz) {
		return (Integer.class == clazz || 
				Long.class == clazz || 
				Double.class == clazz || 
				Boolean.class == clazz || 
				Byte.class == clazz ||  
				Character.class == clazz ||
				Short.class == clazz || 
				Float.class == clazz ||
				Enum.class == clazz);
	}
	
	private static boolean isChar(Class<?> clazz) {
		return char.class == clazz || Character.class == clazz;
	}

	
	public static void main(String[] args) {
		User user = new User();
		user.setUserId(10003);
		user.setName("haha");
		user.setAge(18);
		user.setSex(false);
		user.setM('m');
		
		User user1 = new User();
		user1.setUserId(10123);
		user1.setName("xsxs");
		user1.setAge(22);
		user1.setSex(true);
		//user.setSubUser(user1);
		
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		userList.add(user1);
		
		Map<Long, User> userMap = new HashMap<Long, User>();
		userMap.put(10003l, user);
		userMap.put(10123l, user1);
		String data = toXml(userMap);
		System.out.println(data);
		
		String xmlData = data;
		User user2 = toObject(xmlData, User.class);
		System.out.println(user2);
		
		System.out.println(toMap(xmlData));
	}
}

class User{
	long userId;
	@XmlProperty(alia="nick")
	String name;
	int age;
	boolean sex;
	private User subUser;
	@XmlIgnore
	char m;
	
	public boolean isSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
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

	public User getSubUser() {
		return subUser;
	}

	public void setSubUser(User subUser) {
		this.subUser = subUser;
	}
	public char getM() {
		return m;
	}
	public void setM(char m) {
		this.m = m;
	}
	
}
