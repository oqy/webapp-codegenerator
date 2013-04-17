package com.fusung.webapp.codeGenerator.mybatis;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;

@Getter
@Setter
public class PojoProperty {
	// 对象变量名
	private String variableName;
	// 变量java类型
	private Class<?> variableType;
	// sql字段名
	private String sqlFieldName;
	// sql字段类型
	private String sqlFieldType;
	// 是否外键
	private boolean isReferenceKey;
	// 是否需要生成ibatis映射信息
	private boolean needGenIbatisInfo;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PojoProperty)) {
			return false;
		}
		return StringUtils.equals(this.variableName,
				((PojoProperty) obj).getVariableName());
	}
}
