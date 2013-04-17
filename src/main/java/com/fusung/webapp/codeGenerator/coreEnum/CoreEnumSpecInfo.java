package com.fusung.webapp.codeGenerator.coreEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou
 * ICoreEnum对象描述信息
 * @param <T>
 */
@Getter
@Setter
public class CoreEnumSpecInfo<T> {
	private String variableName;
	private String description;
	private T value;
	
	public CoreEnumSpecInfo(){
		
	}
	
	public CoreEnumSpecInfo(String n,String d,T v){
		variableName=n;
		description=d;
		value=v;
	}
}
