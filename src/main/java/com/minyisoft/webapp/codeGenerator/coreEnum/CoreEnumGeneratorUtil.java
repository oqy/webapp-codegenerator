package com.minyisoft.webapp.codeGenerator.coreEnum;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.swt.widgets.Text;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.minyisoft.webapp.codeGenerator.config.CodeGeneratorConfig;
import com.minyisoft.webapp.codeGenerator.util.CodeGeneratorUtil;

public class CoreEnumGeneratorUtil {
	private static Logger logger = Logger.getLogger(CoreEnumGeneratorUtil.class);
	
	public static void generate(Map<String,Object> inputMap) {
		String enumClassName = (String) inputMap.get("enumName");
		String moduleName = (String) inputMap.get("modelName");
		@SuppressWarnings("unchecked")
		List<CoreEnumSpecInfo<?>> enumInfoList = (List<CoreEnumSpecInfo<?>>) inputMap.get("enumInfoList");
		boolean intEnum = (Boolean)inputMap.get("isIntEnum");
		
		final String enumFolder=CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_JAVA_FILE_ROOT+moduleName+"\\"+CodeGeneratorConfig.ENUM_FOLDER;
		
		try {
			Map<String,Object> model=new HashMap<String,Object>();
			model.put("intEnum", intEnum);
			model.put("enumClassName", enumClassName + "Enum");
			model.put("enumInfoList", enumInfoList);
			model.put("classFullNamePrefix", CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX+moduleName);
			
			/*生成枚举类*/
			String templateString=VelocityEngineUtils.mergeTemplateIntoString(getVe(), CodeGeneratorConfig.ENUM_TEMPLATE, "utf-8", model);
			File file=new File(enumFolder);
			if(!file.exists()){
				file.mkdirs();
			}
			FileUtils.writeStringToFile(new File(enumFolder + enumClassName + "Enum.java"), templateString, "utf-8");

			/*生成配置文件信息*/
			file = new File(CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT+moduleName+"\\"+CodeGeneratorConfig.ENUM_FOLDER+CodeGeneratorConfig.ENUM_DESCRIPTION_FILE_NAME);
			for(CoreEnumSpecInfo<?> enumInfo:enumInfoList){
				FileUtils.writeStringToFile(file, "\n"+CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX+moduleName+".model.enumField."+enumClassName+"Enum_"+enumInfo.getVariableName()+"="+CodeGeneratorUtil.toUnicodeString(enumInfo.getDescription()), true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static CoreEnumSpecInfo<?> createCoreEnumSpecInfo(Text variableName,Text value,Text description,boolean isIntEnum){
		if(StringUtils.isBlank(variableName.getText())||StringUtils.isBlank(value.getText())){
			return null;
		}else if(isIntEnum&&!StringUtils.isNumeric(value.getText())){
			return null;
		}else{
			if(isIntEnum){
				return new CoreEnumSpecInfo<Integer>(variableName.getText().toUpperCase(),description.getText(),Integer.parseInt(value.getText())); 
			}else{
				return new CoreEnumSpecInfo<String>(variableName.getText().toUpperCase(),description.getText(),value.getText()); 
			}
		}
	}

	private static VelocityEngine ve = null;

	private static VelocityEngine getVe() throws Exception {
		if (ve == null) {
			logger.info("初始化vm引擎");
			ve = new VelocityEngine();
			// 初始化并取得Velocity引擎
			Properties p = new Properties();
			p
					.put("file.resource.loader.class",
							"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.put("input.encoding", "utf-8");
			p.put("output.encoding", "utf-8");
			ve.init(p);
		}
		return ve;
	}
}

