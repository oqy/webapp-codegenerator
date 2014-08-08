package com.minyisoft.webapp.codeGenerator.config;

import java.util.HashMap;
import java.util.Map;


/**
 * @author qingyong_ou
 * 代码生成器配置信息
 */
public final class CodeGeneratorConfig {
	private CodeGeneratorConfig() {

	}

	/**
	 * 目标工程磁盘根目录路径，供统一引用和修改
	 */
	private static final String TARGET_PROJECT_FILE_ROOT = "E:\\eclipse3.7\\workspace\\tex100-core\\";
	
	/**
	 * 目标工程代码包根目录，供统一引用和修改
	 */
	public static final String TARGET_PROJECT_PACKAGE_JAVA_FILE_ROOT=TARGET_PROJECT_FILE_ROOT+"src\\main\\java\\com\\minyisoft\\webapp\\tex100\\";
	
	/**
	 * 目标工程代码包根目录，供统一引用和修改
	 */
	public static final String TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT=TARGET_PROJECT_FILE_ROOT+"src\\main\\resources\\com\\minyisoft\\webapp\\tex100\\";
	
	/**
	 * 生成的类对象全名前缀
	 */
	public static final String CLASS_FULL_NAME_PREFIX="com.minyisoft.webapp.tex100.";
	
	/**
	 * 目标工程枚举描述文件名，供统一引用和修改
	 */
	public static final String ENUM_DESCRIPTION_FILE_NAME="enumDescription.properties";
	
	/*-------代码输出的相对目录路径，若按约定的规范进行编码，以下设定无需修改 begin---------------------------------------------------------*/
	/**
	 * 枚举对象相对工程模块的目录路径
	 */
	public static final String ENUM_FOLDER="model\\enumField\\";
	/**
	 * mybatis配置文件相对工程模块的目录路径
	 */
	public static final String MAPPING_FOLDER="persistence\\";
	/**
	 * 权限文件相对工程模块的目录路径
	 */
	public static final String PERMISSION_FILE_PATH="security\\permission\\";
	/**
	 * 过滤对象文件相对工程模块的目录路径
	 */
	public static final String CRITERIA_FILE_PATH="model\\criteria\\";
	/**
	 * DAO接口相对工程模块的目录路径
	 */
	public static final String DAO_INTERFACE_FILE_PATH="persistence\\";
	/**
	 * 服务接口相对工程模块的目录路径
	 */
	public static final String SERVICE_INTERFACE_FILE_PATH="service\\";
	/**
	 * 服务实现类相对工程模块的目录路径
	 */
	public static final String SERVICE_CLASS_FILE_PATH="service\\impl\\";
	/*-------代码输出的相对目录路径，若按约定的规范进行编码，以上设定无需修改 end---------------------------------------------------------*/

	/**
	 * mybatis模板路径
	 */
	public static final String MYBATIS_TEMPLATE_FILE="com/minyisoft/webapp/codeGenerator/vm/MyBatisTemplate.vm";
	/**
	 * 过滤对象模板路径
	 */
	public static final String CRITERIA_TEMPLATE_FILE="com/minyisoft/webapp/codeGenerator/vm/CriteriaTemplate.vm";
	/**
	 * DAO接口对象模板路径
	 */
	public static final String DAO_INTERFACE_TEMPLATE_FILE="com/minyisoft/webapp/codeGenerator/vm/DaoInterfaceTemplate.vm";
	/**
	 * 服务接口对象模板路径
	 */
	public static final String SERVICE_INTERFACE_TEMPLATE_FILE="com/minyisoft/webapp/codeGenerator/vm/ServiceInterfaceTemplate.vm";
	/**
	 * 服务实现类模板路径
	 */
	public static final String SERVICE_IMPL_TEMPLATE_FILE="com/minyisoft/webapp/codeGenerator/vm/ServiceImplTemplate.vm";
	/**
	 * 枚举模板路径
	 */
	public static final String ENUM_TEMPLATE="com/minyisoft/webapp/codeGenerator/vm/CoreEnumTemplate.vm";
	
	/**
	 * 数据表表名前缀
	 */
	public static final String TABLE_PREFIX = "t_";
	/**
	 * 数据表字段前缀
	 */
	public static final String FIELD_PREFIX = "f";
	/**
	 * 默认数据表字段类型
	 */
	public static final String DEFAULT_COLUMN_TYPE = "varchar(32)";
	
	/*SQL文件路径*/
	public static final String SQL_FILE_ROOT = "D:\\";
	/*SQL文件执行文件名（本地）*/
	public static final String EXECUTE_LOCAL_SQL_FILE_NAME = "executeSqlToLoacal.bat";
	/*SQL文件执行文件名（服务器）*/
	public static final String EXECUTE_SERVER_SQL_FILE_NAME = "executeSqlToServer.bat";
	/*SQL文件执行的log文件路径*/
	public static final String SQL_LOG_PATH = "C:\\SQL_LOG\\";
	
	/**
	 * java类型对应oracle数据表字段类型 映射map
	 */
	public static Map<String, String> ORACLE_FIELD_TYPE_MAP = new HashMap<String, String>();
	/**
	 * java类型对应mysql数据表字段类型 映射map
	 */
	public static Map<String, String> MYSQL_FIELD_TYPE_MAP = new HashMap<String, String>();
	static {
		MYSQL_FIELD_TYPE_MAP.put("int", "int default 0");
		MYSQL_FIELD_TYPE_MAP.put("String", "varchar(32)");
		MYSQL_FIELD_TYPE_MAP.put("Date", "datetime");
		MYSQL_FIELD_TYPE_MAP.put("BigDecimal", "decimal(13,2) default 0");
		MYSQL_FIELD_TYPE_MAP.put("boolean", "int default 0");
		
		ORACLE_FIELD_TYPE_MAP.put("Date","date");
		ORACLE_FIELD_TYPE_MAP.put("int", "int default 0");
		ORACLE_FIELD_TYPE_MAP.put("String", "varchar(32)");
		ORACLE_FIELD_TYPE_MAP.put("BigDecimal", "decimal(13,2) default 0");
		ORACLE_FIELD_TYPE_MAP.put("boolean", "int default 0");
	}
}
