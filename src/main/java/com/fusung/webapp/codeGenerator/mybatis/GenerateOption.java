package com.fusung.webapp.codeGenerator.mybatis;

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
	//是否生成SQL文件
	private boolean isSetSqlFile;
	//是否新SQL文件
	private boolean isNewSqlFileScheme;
	//是否原有SQL文件
	private boolean isAddSqlFileScheme;
	//是否执行到本地数据库
	private boolean isLocalScheme;
	//是否执行到服务器数据库
	private boolean isServerScheme;
}
