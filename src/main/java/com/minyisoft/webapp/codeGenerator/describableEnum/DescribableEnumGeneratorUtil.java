package com.minyisoft.webapp.codeGenerator.describableEnum;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.minyisoft.webapp.codeGenerator.config.CodeGeneratorConfig;

public class DescribableEnumGeneratorUtil {
	private static Logger logger = LoggerFactory.getLogger(DescribableEnumGeneratorUtil.class);

	public static void generate(Map<String, Object> inputMap) {
		String enumClassName = (String) inputMap.get("enumName");
		String moduleName = (String) inputMap.get("modelName");
		@SuppressWarnings("unchecked")
		List<DescribableEnumSpecInfo<?>> enumInfoList = (List<DescribableEnumSpecInfo<?>>) inputMap.get("enumInfoList");
		boolean intEnum = (Boolean) inputMap.get("isIntEnum");

		final String enumFolder = CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_JAVA_FILE_ROOT + moduleName + "\\"
				+ CodeGeneratorConfig.ENUM_FOLDER;

		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("intEnum", intEnum);
			model.put("enumClassName", enumClassName + "Enum");
			model.put("enumInfoList", enumInfoList);
			model.put("classFullNamePrefix", CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX + moduleName);

			/* 生成枚举类 */
			String templateString = VelocityEngineUtils.mergeTemplateIntoString(getVe(),
					CodeGeneratorConfig.ENUM_TEMPLATE, Charsets.UTF_8.name(), model);
			File file = new File(enumFolder);
			if (!file.exists()) {
				file.mkdirs();
			}
			Files.write(templateString, new File(enumFolder + enumClassName + "Enum.java"), Charsets.UTF_8);

			/* 生成配置文件信息 */
			file = new File(CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT + moduleName + "\\"
					+ CodeGeneratorConfig.ENUM_FOLDER);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT + moduleName + "\\"
					+ CodeGeneratorConfig.ENUM_FOLDER + CodeGeneratorConfig.ENUM_DESCRIPTION_FILE_NAME);
			for (DescribableEnumSpecInfo<?> enumInfo : enumInfoList) {
				Files.append(
						"\n" + CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX + moduleName + ".model.enumField."
								+ enumClassName + "Enum_" + enumInfo.getVariableName() + "="
								+ toUnicodeString(enumInfo.getDescription()), file, Charsets.UTF_8);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DescribableEnumSpecInfo<?> createCoreEnumSpecInfo(Text variableName, Text value, Text description,
			boolean isIntEnum) {
		if (StringUtils.isBlank(variableName.getText()) || StringUtils.isBlank(value.getText())) {
			return null;
		} else if (isIntEnum && !StringUtils.isNumeric(value.getText())) {
			return null;
		} else {
			if (isIntEnum) {
				return new DescribableEnumSpecInfo<Integer>(variableName.getText().toUpperCase(),
						description.getText(), Integer.parseInt(value.getText()));
			} else {
				return new DescribableEnumSpecInfo<String>(variableName.getText().toUpperCase(), description.getText(),
						value.getText());
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
			p.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.put("input.encoding", Charsets.UTF_8.name());
			p.put("output.encoding", Charsets.UTF_8.name());
			ve.init(p);
		}
		return ve;
	}

	private static String toUnicodeString(String s) {
		StringBuffer sb = new StringBuffer();
		char charArray[] = s.toCharArray();
		for (char c : charArray) {
			if (c >= 0 && c <= 127) {
				sb.append(c);// 英文ACSII，直接输出
			} else {
				sb.append("\\u" + Integer.toHexString(c));
			}
		}
		return sb.toString();
	}
}
