package com.minyisoft.webapp.codeGenerator.describableEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou ICoreEnum对象描述信息
 * @param <T>
 */
@Getter
@Setter
public class DescribableEnumSpecInfo<T> {
	private String variableName;
	private String description;
	private T value;

	public DescribableEnumSpecInfo() {

	}

	public DescribableEnumSpecInfo(String n, String d, T v) {
		variableName = n;
		description = d;
		value = v;
	}
}
