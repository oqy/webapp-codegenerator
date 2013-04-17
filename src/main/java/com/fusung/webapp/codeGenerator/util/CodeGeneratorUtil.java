package com.fusung.webapp.codeGenerator.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.fusung.webapp.codeGenerator.config.CodeGeneratorConfig;

public final class CodeGeneratorUtil {
	private CodeGeneratorUtil() {

	}
	
	/**
	 * 判断类是否属于系统枚举
	 * @param clazz
	 * @return
	 */
	public static boolean isEnumType(Class<?> clazz){
		if(!clazz.isEnum()){
			return false;
		}
		
		Class<?>[] interfaces=clazz.getInterfaces();
		for(Class<?> face : interfaces){
			if(StringUtils.startsWithIgnoreCase(face.getSimpleName(), CodeGeneratorConfig.CORE_ENUM_INTERFAC_SIMPLE_NAME)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取指定类名的pojo key，没有则返回null
	 * @param className
	 * @return
	 */
	public static String getPojoKey(String className){
		try{
			String packageName=StringUtils.substringBetween(className, CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX, ".");
			String keyFilePath=CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT+packageName+"\\model\\modelKey.properties";
			List<String> shortCuts=FileUtils.readLines(new File(keyFilePath));
			for(String line:shortCuts){
				if(StringUtils.endsWithIgnoreCase(line, className)){
					return StringUtils.substring(line, 0,line.indexOf('='));
				}
			}
			return null;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 根据pojo key生成主键id
	 * @param info
	 * @return
	 */
	public static String createObjectID(String pojoKey) throws Exception{
		UUID uuid = UUID.randomUUID();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput out = new DataOutputStream(baos);
		try {
			out.writeLong(Long.parseLong(pojoKey,16));
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		} catch (IOException ioe) {
			throw new Exception("Pojo键值["+pojoKey+"]无法产生主键ID");
		}
		return Base64.encodeBase64URLSafeString(baos.toByteArray());
	}
	
	/**
	 * 将输入的字符串参数转换成unicode编码
	 * @param s
	 * @return
	 */
	public static String toUnicodeString(String s){
		StringBuffer sb=new StringBuffer();
		char charArray[] = s.toCharArray();
		for (char c : charArray) {
			if(c>=0&&c<=127)
			{
				sb.append(c);//英文ACSII，直接输出
			}
			else
			{
				sb.append("\\u"+Integer.toHexString(c));
			}
		}
		return sb.toString();
	}
}