package com.minyisoft.webapp.codeGenerator.mybatis;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.minyisoft.webapp.codeGenerator.config.CodeGeneratorConfig;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class MyBatisGeneratorUtil {
	private static Logger logger = LoggerFactory.getLogger(MyBatisGeneratorUtil.class);
	
	/**
	 * 根据option设定的条件，生成ibatis配置文件，写入pojo shortcut信息，及返回sql ddl或alter语句
	 * @param targetPojoName
	 * @param outputDictPath
	 * @param outputSqlFilePath
	 * @param pojoPropertyList
	 * @param option
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static Map<String,Object> generate(String targetPojoName, String outputDictPath,
			List<PojoProperty> pojoPropertyList,GenerateOption option) throws ClassNotFoundException {
		MyBatisVMTransferObject transferObject = new MyBatisVMTransferObject();

		String classFullName = targetPojoName;
		Class<?> modelClazz=Class.forName(classFullName);
		Assert.isTrue(CoreBaseInfo.class.isAssignableFrom(modelClazz), "指定的java类并不继承自"+CoreBaseInfo.class.getName());
		Assert.notNull(AnnotationUtils.findAnnotation(modelClazz, ModelKey.class),"指定的java类并未添加注解"+ModelKey.class.getName());
		
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
			columnName = pojoPropertyInfo.getSqlFieldName();
			createTableSql.append(columnName).append(" ").append(
					pojoPropertyInfo.getSqlFieldType());

			iBatisVMPropertyPair = new MyBatisVMPropertyPair();
			iBatisVMPropertyPair.setColumnName(columnName);
			iBatisVMPropertyPair.setPropertyName(fieldName);
			if(IModelObject.class.isAssignableFrom(pojoPropertyInfo.getVariableType())){
				iBatisVMPropertyPair.setTypeHandler("bizModelHandler");
			}
			transferObject.getPropertyList().add(iBatisVMPropertyPair);

			if (pojoPropertyInfo.isReferenceKey()) {
				fieldName += ".id";
			}
			
			if(!fieldName.equalsIgnoreCase("version")){
				insertSubSql1.append(columnName).append(",");
				insertSubSql2.append("#{").append(fieldName).append("},");
			}

			if (fieldName.equalsIgnoreCase("id")) {
				createTableSql.append(" not null");
			} else if(!fieldName.equalsIgnoreCase("version")) {
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

		insertSql.append(insertSubSql1).append(") values(").append(insertSubSql2).append(")");
		updateSql.append("fversion=fversion+1 where fid=#{id} and fversion=#{version}");
		transferObject.setInsertSQLString(insertSql.toString());
		transferObject.setUpdateSQLString(updateSql.toString());

		createTableSql.append(" primary key (fid));");

		//生成iBatis配置文件
		if(option.isGenerateIbatisConfig()){
			transferObject.setUseCache(option.isGenerateIbatisCache());
			generateIbatisConfig(transferObject,outputDictPath);
		}
		
		//生成CRUD权限信息
		if(option.isGenerateCRUDPermission()){
			generateCRUDPermission(transferObject,
					Long.toHexString(AnnotationUtils.findAnnotation(modelClazz, ModelKey.class).value()).toUpperCase(),
					option.getPojoChineseName(),outputDictPath);
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
			p.put("input.encoding", Charsets.UTF_8.name());
			p.put("output.encoding", Charsets.UTF_8.name());
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
			Files.write(templateString, new File(resourceOutputDictPath+CodeGeneratorConfig.MAPPING_FOLDER + transferObject.getAlias() + "SqlMap.xml"), Charsets.UTF_8);
			
			//生成过滤器java类
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.CRITERIA_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.CRITERIA_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			Files.write(templateString, new File(outputDictPath+CodeGeneratorConfig.CRITERIA_FILE_PATH + transferObject.getAlias() + "Criteria.java"), Charsets.UTF_8);
			
			//生成DAO接口
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.DAO_INTERFACE_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.DAO_INTERFACE_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			Files.write(templateString, new File(outputDictPath+CodeGeneratorConfig.DAO_INTERFACE_FILE_PATH + "I"+transferObject.getAlias() + "Dao.java"), Charsets.UTF_8);
			
			//生成业务接口
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.SERVICE_INTERFACE_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.SERVICE_INTERFACE_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			Files.write(templateString, new File(outputDictPath+CodeGeneratorConfig.SERVICE_INTERFACE_FILE_PATH + "I"+transferObject.getAlias() + ".java"), Charsets.UTF_8);
			
			//生成业务接口实现类
			model.put("serviceName", StringUtils.uncapitalize(transferObject.getAlias()));
			templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.SERVICE_IMPL_TEMPLATE_FILE, "utf-8", model);
			file=new File(outputDictPath+CodeGeneratorConfig.SERVICE_CLASS_FILE_PATH);
			if(!file.exists()){
				file.mkdirs();
			}
			Files.write(templateString, new File(outputDictPath+CodeGeneratorConfig.SERVICE_CLASS_FILE_PATH + transferObject.getAlias() + "Impl.java"), Charsets.UTF_8);
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
			ObjectUuidUtils.registerModelClass(PermissionInfo.class);
			file = new File(targetDirectory+pojoShortName+".permission");
			Files.write(ObjectUuidUtils.createObjectID(PermissionInfo.class)+"="+pojoShortName+":"+PERMISSION_CREATE+","+pojoChinsesName+"_新增,"+shortCut, file, Charsets.UTF_8);
			Files.append(ObjectUuidUtils.createObjectID(PermissionInfo.class)+"="+pojoShortName+":"+PERMISSION_UPDATE+","+pojoChinsesName+"_更新,"+shortCut, file, Charsets.UTF_8);
			Files.append(ObjectUuidUtils.createObjectID(PermissionInfo.class)+"="+pojoShortName+":"+PERMISSION_DELETE+","+pojoChinsesName+"_删除,"+shortCut, file, Charsets.UTF_8);
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
				boolean isEnum=DescribableEnum.class.isAssignableFrom(clazz);
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
