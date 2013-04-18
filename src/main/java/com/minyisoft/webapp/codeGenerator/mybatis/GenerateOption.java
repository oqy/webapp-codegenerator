package com.minyisoft.webapp.codeGenerator.mybatis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateOption {
	//是否生成iBatis配置文件
	private boolean isGenerateIbatisConfig;
	//是否在iBatis配置文件中生成缓存配置信息
	private boolean isGenerateIbatisCache;
	//是否将POJO快捷标签写入属性文件
	//private boolean isWritePojoShortcut;
	//是否生成SQL DDL语句
	private boolean isGenerateSQLDDL;
	//是否生成SQL Alter Add语句
	private boolean isGenerateSQLAlterAdd;
	//是否oracle语法
	private boolean isOracleScheme;
	//是否mySQL语法
	private boolean isMySQLScheme;
	//是否生成CRUD权限信息
	private boolean isGenerateCRUDPermission;
	//pojo类中文名称
	private String pojoChineseName;
}
