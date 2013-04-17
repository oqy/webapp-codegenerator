package com.fusung.webapp.codeGenerator.mybatis;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

@Setter
@Getter
public class MyBatisVMTransferObject {
	// 包名
	private String packageName;
	// 类别名
	private String alias;
	// 类全名
	private String classFullName;
	// 过滤器全名
	private String criteriaFullName;
	// 对应数据库表名
	private String tableName;
	// 插入sql
	private String insertSQLString;
	// 更新sql
	private String updateSQLString;
	// 对象属性和数据库字段对应关系
	private List<MyBatisVMPropertyPair> propertyList = new ArrayList<MyBatisVMPropertyPair>();
	// 是否生成缓存信息
	private boolean useCache;

	public void setClassFullName(String classFullName) {
		this.classFullName = classFullName;
		this.packageName = StringUtils.substringBefore(classFullName, ".model");
	}
}
