package com.fusung.webapp.codeGenerator.mybatis;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fusung.webapp.codeGenerator.config.CodeGeneratorConfig;
import com.fusung.webapp.codeGenerator.util.CodeGeneratorUtil;

public class MyBatisGeneratorUtil {
	private static Logger logger = Logger.getLogger(MyBatisGeneratorUtil.class);
	
	/**
	 * 根据option设定的条件，生成ibatis配置文件，写入pojo shortcut信息，及返回sql ddl或alter语句
	 * @param targetPojoName
	 * @param outputDictPath
	 * @param outputSqlFilePath
	 * @param pojoPropertyList
	 * @param option
	 * @return
	 */
	public static Map<String,Object> generate(String targetPojoName, String outputDictPath, String outputSqlFilePath,
			List<PojoProperty> pojoPropertyList,GenerateOption option) {
		MyBatisVMTransferObject transferObject = new MyBatisVMTransferObject();

		String classFullName = targetPojoName;
		String alias = classFullName.substring(classFullName.lastIndexOf(".") + 1);
		if (StringUtils.endsWithIgnoreCase(alias,"info")) {
			alias = alias.substring(0, alias.length() - 4);
		}
		
		transferObject.setClassFullName(classFullName);
		transferObject.setCriteriaFullName(classFullName.substring(0,classFullName.lastIndexOf('.'))+".criteria."+alias+"Criteria");
		transferObject.setAlias(alias);
		//获取模块名，如包名为com.fusung.webapp.tex100.shop，则模块名为shop
		String tableName = CodeGeneratorConfig.TABLE_PREFIX +StringUtils.lowerCase(StringUtils.removeStart(transferObject.getPackageName(), CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX).replace('.', '_'))+"_"+ alias.toLowerCase();
		transferObject.setTableName(tableName);

		//ibatis配置文件通用插入语句
		StringBuffer insertSql = new StringBuffer("insert into ").append(tableName).append(" (");
		StringBuffer insertSubSql1 = new StringBuffer();
		StringBuffer insertSubSql2 = new StringBuffer();
		//ibatis配置文件通用更新语句
		StringBuffer updateSql = new StringBuffer("update ").append(tableName).append(" set ");
		//sql ddl语句
		StringBuffer createTableSql = new StringBuffer("create table ").append(tableName).append("(");
		//sql alter add语句
		StringBuffer alterTableSql=new StringBuffer();

		String fieldName = null;
		String columnName = null;
		PojoProperty pojoPropertyInfo = null;
		MyBatisVMPropertyPair iBatisVMPropertyPair=null;

		for (int j = 0; j < pojoPropertyList.size(); j++) {
			pojoPropertyInfo = pojoPropertyList.get(j);
			//若该属性设置了不需输出映射信息，跳过
			if(!pojoPropertyInfo.isNeedGenIbatisInfo()){
				continue;
			}

			fieldName = pojoPropertyInfo.getVariableName();
			if (pojoPropertyInfo.isReferenceKey()) {
				fieldName += ".id";
			}
			columnName = pojoPropertyInfo.getSqlFieldName();
			createTableSql.append(columnName).append(" ").append(
					pojoPropertyInfo.getSqlFieldType());

			iBatisVMPropertyPair = new MyBatisVMPropertyPair();
			iBatisVMPropertyPair.setColumnName(columnName);
			iBatisVMPropertyPair.setPropertyName(fieldName);
			transferObject.getPropertyList().add(iBatisVMPropertyPair);

			insertSubSql1.append(columnName).append(",");
			insertSubSql2.append("#{").append(fieldName).append("},");

			if (fieldName.equalsIgnoreCase("id")) {
				createTableSql.append(" not null");
			} else {
				updateSql.append(columnName).append("=").append("#{").append(
						fieldName).append("},");
			}
			createTableSql.append(",");
			
			alterTableSql.append("alter table ").append(transferObject.getTableName());
			if(option.isOracleScheme()){
				alterTableSql.append(" add ");
			}else if(option.isMySQLScheme()){
				alterTableSql.append(" add ");
			}
			alterTableSql.append(columnName).append(" ").append(pojoPropertyInfo.getSqlFieldType()).append(";");
		}

		//去除最后一个多余的','号
		insertSubSql1.deleteCharAt(insertSubSql1.length() - 1);
		insertSubSql2.deleteCharAt(insertSubSql2.length() - 1);
		updateSql.deleteCharAt(updateSql.length() - 1);

		insertSql.append(insertSubSql1).append(") values(").append(insertSubSql2).append(")");
		updateSql.append(" where fid=#{id}");
		transferObject.setInsertSQLString(insertSql.toString());
		transferObject.setUpdateSQLString(updateSql.toString());

		createTableSql.append(" primary key (fid));");

		//生成iBatis配置文件
		if(option.isGenerateIbatisConfig()){
			transferObject.setUseCache(option.isGenerateIbatisCache());
			generateIbatisConfig(transferObject,outputDictPath);
		}
		//将POJO快捷标签写入属性文件
		String objectShortCut=writePojoShortCut(transferObject);
		//生成CRUD权限信息
		if(option.isGenerateCRUDPermission()){
			generateCRUDPermission(transferObject,objectShortCut,option.getPojoChineseName(),outputDictPath);
		}
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		StringBuffer tableSql = null;
		if(option.isGenerateSQLDDL()){
			tableSql = createTableSql;
		}else if(option.isGenerateSQLAlterAdd()){
			tableSql = alterTableSql;
		}
		//++++++++++++
		/*生成SQL文件
		if(option.isSetSqlFile()){
			SqlFileObject sqlFileObject = new SqlFileObject();
			sqlFileObject.setSql(tableSql.toString());
			String temp = targetPojoName.substring(0, targetPojoName.lastIndexOf(".model"));
			String projectBranchName = temp.substring(temp.lastIndexOf(".") + 1, temp.length()).toLowerCase();
			String fileName = projectBranchName + CommonSysdate.getSysdate_yyyyMMddSim();
			String sqlFilePath = CodeGeneratorConfig.SQL_FILE_ROOT + fileName + "\\SQL\\" + fileName + ".sql";
			sqlFileObject.setSqlFilePath(sqlFilePath);
			String exucuteLocalSqlFilePath = CodeGeneratorConfig.SQL_FILE_ROOT + fileName + "\\" + CodeGeneratorConfig.EXECUTE_LOCAL_SQL_FILE_NAME;
			sqlFileObject.setExucuteLocalSqlFilePath(exucuteLocalSqlFilePath);
			String exucuteServerSqlFilePath = CodeGeneratorConfig.SQL_FILE_ROOT + fileName + "\\" + CodeGeneratorConfig.EXECUTE_SERVER_SQL_FILE_NAME;
			sqlFileObject.setExucuteServerSqlFilePath(exucuteServerSqlFilePath);
			if(option.isNewSqlFileScheme()){//新文件
				writeSqlFile(true, sqlFileObject);
			} else if(option.isAddSqlFileScheme()){//追加到文件
				String sqlPathField = "";
				if(StringUtils.isBlank(outputSqlFilePath)) {
					sqlPathField = sqlFilePath;
				} else {
					sqlPathField = outputSqlFilePath;
				}
				sqlFileObject.setSqlFilePath(sqlPathField);
				writeSqlFile(false, sqlFileObject);
			}
			sqlFileObject.setSqlLogPath(CodeGeneratorConfig.SQL_LOG_PATH + fileName + ".log");
			writeExecuteSqlFile(sqlFileObject, option);
		}*/
		//++++++++++++
		resultMap.put("SQL", tableSql);
		return resultMap;

	}

	private static VelocityEngine ve = null;

	private static VelocityEngine getVe() throws Exception {
		if (ve == null) {
			logger.info("初始化vm引擎");
			ve = new VelocityEngine();
			// 初始化并取得Velocity引擎
			Properties p = new Properties();
			p.put("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.put("input.encoding", "utf-8");
			p.put("output.encoding", "utf-8");
			ve.init(p);
		}
		return ve;
	}
	
	private static void generateIbatisConfig(MyBatisVMTransferObject transferObject,String outputDictPath){
		try {
			Map<String,Object> model=new HashMap<String,Object>();
			model.put("c", transferObject);
			
			//生成iBatis配置文件
			String templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.MYBATIS_TEMPLATE_FILE, "utf-8", model);
			File file=new File(outputDictPath+CodeGeneratorConfig.MAPPING_FOLDER);
			if(!file.exists()){
				file.mkdirs();
			}
			String resourceOutputDictPath=StringUtils.replace(outputDictPath, "src\\main\\java\\", "src\\main\\resources\\");
			FileUtils.writeStringToFile(new File(resourceOutputDictPath+CodeGeneratorConfig.MAPPING_FOLDER + transferObject.getAlias() + "SqlMap.xml"), templateString, "utf-8");
			
			//生成过滤器java类
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.CRITERIA_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.CRITERIA_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			FileUtils.writeStringToFile(new File(outputDictPath+CodeGeneratorConfig.CRITERIA_FILE_PATH + transferObject.getAlias() + "Criteria.java"), templateString, "utf-8");
			
			//生成DAO接口
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.DAO_INTERFACE_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.DAO_INTERFACE_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			FileUtils.writeStringToFile(new File(outputDictPath+CodeGeneratorConfig.DAO_INTERFACE_FILE_PATH + "I"+transferObject.getAlias() + "Dao.java"), templateString, "utf-8");
			
			//生成业务接口
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.SERVICE_INTERFACE_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.SERVICE_INTERFACE_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			FileUtils.writeStringToFile(new File(outputDictPath+CodeGeneratorConfig.SERVICE_INTERFACE_FILE_PATH + "I"+transferObject.getAlias() + ".java"), templateString, "utf-8");
			
			//生成业务接口实现类
			model.put("serviceName", StringUtils.uncapitalize(transferObject.getAlias()));
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.SERVICE_IMPL_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.SERVICE_CLASS_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			FileUtils.writeStringToFile(new File(outputDictPath+CodeGeneratorConfig.SERVICE_CLASS_FILE_PATH + transferObject.getAlias() + "Impl.java"), templateString, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//+++++++++++++++
	/**
	 * 写sql文
	 * @param transferObject
	private static void writeSqlFile(boolean newFlag, SqlFileObject sqlFileObject){
		try {
			final String fileEndTag="exit;";
			String sqlPath = sqlFileObject.getSqlFilePath();
			if(newFlag) {
				File newSqlFilePath = new File(sqlPath);
				if(!newSqlFilePath.exists() && !sqlPath.endsWith(File.separator)) {
					if(!newSqlFilePath.getParentFile().exists()) {
						newSqlFilePath.getParentFile().mkdirs();
					}
					if(newSqlFilePath.exists()) {
						newSqlFilePath.delete();
					}
					newSqlFilePath.createNewFile();
				}
				FileWriter fw=new FileWriter(sqlPath);
				fw.write(sqlFileObject.getSql() + "\n" + fileEndTag);
				fw.close();
			} else {
				FileReader fr=new FileReader(sqlPath);
				BufferedReader br=new BufferedReader(fr);
				String line=null;
				StringBuffer sb=new StringBuffer();
				while((line=br.readLine()) != null && !line.equalsIgnoreCase(fileEndTag)){
					sb.append(line + "\n");
				}
				br.close();
				fr.close();
				sb.append("\n" + sqlFileObject.getSql());
				FileWriter fw=new FileWriter(sqlPath);
				fw.write(sb.toString() + "\n" + fileEndTag);
				fw.close();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 写sql文执行文件
	 * @param sqlFileObject
	 * @param option
	private static void writeExecuteSqlFile(SqlFileObject sqlFileObject,GenerateOption option){
		try {
			if(option.isLocalScheme()) {
				String exucuteLocalSqlPath = sqlFileObject.getExucuteLocalSqlFilePath();
				File exucuteSqlFile = new File(exucuteLocalSqlPath);
				if(!exucuteSqlFile.exists() && !exucuteLocalSqlPath.endsWith(File.separator)) {
					if(!exucuteSqlFile.getParentFile().exists()) {
						exucuteSqlFile.getParentFile().mkdirs();
					}
					if(!exucuteSqlFile.exists()) {
						exucuteSqlFile.createNewFile();
						FileWriter fw=new FileWriter(exucuteLocalSqlPath);
						fw.write(sqlFileObject.getExucuteLocalSqlFileContent());
						fw.close();
					}
				}
			}
			if(option.isServerScheme()) {
				String exucuteServerSqlPath = sqlFileObject.getExucuteServerSqlFilePath();
				File exucuteSqlFile = new File(exucuteServerSqlPath);
				if(!exucuteSqlFile.exists() && !exucuteServerSqlPath.endsWith(File.separator)) {
					if(!exucuteSqlFile.getParentFile().exists()) {
						exucuteSqlFile.getParentFile().mkdirs();
					}
					if(!exucuteSqlFile.exists()) {
						exucuteSqlFile.createNewFile();
						FileWriter fw=new FileWriter(exucuteServerSqlPath);
						fw.write(sqlFileObject.getExucuteServerSqlFileContent());
						fw.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//+++++++++++++++
	
	/**
	 * 向pojo shortcut属性文件追加信息
	 * @param transferObject
	 */
	private static String writePojoShortCut(MyBatisVMTransferObject transferObject){
		try {
			String key=CodeGeneratorUtil.getPojoKey(transferObject.getClassFullName());
			if(key!=null){
				return key;
			}
			key=Long.toHexString(new Date().getTime()).substring(3).toUpperCase();
			FileUtils.writeLines(new File(CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT+StringUtils.removeStart(transferObject.getPackageName(), CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX)+"\\model\\modelKey.properties"),
					Arrays.asList("",key+ "=" + transferObject.getClassFullName()), true);
			return key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 生成权限文件
	 * @param transferObject
	 * @param shortCut
	 * @param pojoChinsesName
	 */
	private static void generateCRUDPermission(MyBatisVMTransferObject transferObject,String shortCut,String pojoChinsesName,String outputDictPath){
		final String PERMISSION_CREATE="create";
		final String PERMISSION_UPDATE="update";
		final String PERMISSION_DELETE="delete";
		try {
			String pojoShortName=transferObject.getAlias();
			String targetDirectory=CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT+StringUtils.removeStart(transferObject.getPackageName(), CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX)+"\\"+CodeGeneratorConfig.PERMISSION_FILE_PATH;
			
			File file=new File(targetDirectory);
			if(!file.exists()){
				file.mkdirs();
			}
			
			FileUtils.writeLines(new File(targetDirectory+pojoShortName+".permission"), 
								Arrays.asList(CodeGeneratorUtil.createObjectID(CodeGeneratorConfig.PERMISSION_MODEL_KEY)+"="+pojoShortName+":"+PERMISSION_CREATE+","+pojoChinsesName+"_新增,"+shortCut,
												CodeGeneratorUtil.createObjectID(CodeGeneratorConfig.PERMISSION_MODEL_KEY)+"="+pojoShortName+":"+PERMISSION_UPDATE+","+pojoChinsesName+"_更新,"+shortCut,
												CodeGeneratorUtil.createObjectID(CodeGeneratorConfig.PERMISSION_MODEL_KEY)+"="+pojoShortName+":"+PERMISSION_DELETE+","+pojoChinsesName+"_删除,"+shortCut));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据pojo类名获取该类所有属性信息，属性需带有公共getter或is方法
	 * @param targetPojoName
	 * @param option
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<PojoProperty> getPojoProperties(String targetPojoName,GenerateOption option) throws Exception {
		try {
			Object pojoInfo = Class.forName(targetPojoName).newInstance();
			
			List<String> propertyNameList=new ArrayList<String>();
			Field[] fields=pojoInfo.getClass().getDeclaredFields();
			for(Field f:fields){
				if(!Modifier.isStatic(f.getModifiers())){
					propertyNameList.add(f.getName());
				}
			}
			List<Class<?>> superClassList=(List<Class<?>>)ClassUtils.getAllSuperclasses(pojoInfo.getClass());
			for(Class<?> clazz:superClassList){
				fields=clazz.getDeclaredFields();
				for(Field f:fields){
					if(!Modifier.isStatic(f.getModifiers())){
						propertyNameList.add(f.getName());
					}
				}
				
				if(StringUtils.endsWithIgnoreCase(clazz.getSimpleName(), "CoreBaseInfo")){
					break;
				}
			}
			
			List<PojoProperty> resultList = new ArrayList<PojoProperty>();
			for (String propertyName:propertyNameList) {
				PojoProperty propertyInfo = new PojoProperty();
				propertyInfo.setVariableName(propertyName);
				Class<?> clazz = PropertyUtils.getPropertyType(pojoInfo,propertyName);
				propertyInfo.setVariableType(clazz);
				propertyInfo.setSqlFieldName(CodeGeneratorConfig.FIELD_PREFIX+ propertyName.toLowerCase());

				Map<String,String> sqlFieldTypeMap=null;
				if(option.isMySQLScheme()){
					sqlFieldTypeMap=CodeGeneratorConfig.MYSQL_FIELD_TYPE_MAP;
				}else if(option.isOracleScheme()){
					sqlFieldTypeMap=CodeGeneratorConfig.ORACLE_FIELD_TYPE_MAP;
				}
				String sqlType = sqlFieldTypeMap.get(clazz.getSimpleName());
				boolean isEnum=CodeGeneratorUtil.isEnumType(clazz);
				boolean isBoolean=Boolean.class.isAssignableFrom(clazz);
				if (sqlType == null&& !isEnum&& !isBoolean) {
					if (!clazz.isEnum()) {
						propertyInfo.setReferenceKey(true);
						propertyInfo.setSqlFieldName(propertyInfo
								.getSqlFieldName()
								+ "id");
					}
					propertyInfo.setSqlFieldType(CodeGeneratorConfig.DEFAULT_COLUMN_TYPE);
				} else if (isEnum || isBoolean) {
					propertyInfo.setSqlFieldType(sqlFieldTypeMap.get("int"));
				} else {
					propertyInfo.setSqlFieldType(sqlType);
				}
				resultList.add(propertyInfo);
			}
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
