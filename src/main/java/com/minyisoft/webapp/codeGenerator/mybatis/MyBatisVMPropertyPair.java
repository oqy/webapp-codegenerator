package com.minyisoft.webapp.codeGenerator.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyBatisVMPropertyPair {
	// 对象属性名
	private String propertyName;
	// 数据库字段名
	private String columnName;
	// 整形枚举类转换对象名
	private String typeHandler;

	public MyBatisVMPropertyPair() {

	}

	public MyBatisVMPropertyPair(String pName, String cName) {
		this.propertyName = pName;
		this.columnName = cName;
	}
}
