package com.fusung.webapp.codeGenerator.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用系统时间类
 * @author qingyong_ou
 */
public class CommonSysdate {
	public CommonSysdate(){
		super();
	}

	public static String changeSysdate_yyyyMMddCHN(String date) {
		String reStr = date;
		reStr = reStr.replaceFirst("/", "年");
		reStr = reStr.replaceFirst("/", "月");
		reStr = reStr + "日";
		return reStr;
	}
	public static String getSysdate(String format){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
	
	public static String getSysdate_yyyyMMddHHmmss(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return formatter.format(new Date());
	}
	
	public static String getSysdate_yyyyMMddHHmmssSSS(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
		return formatter.format(new Date());
	}

	public static String getSysdate_yyyyMMdd(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyy/MM/dd");
		return formatter.format(new Date());
	}

	public static String getSysdate_yyyyMMddCHN(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyy年MM月dd日");
		return formatter.format(new Date());
	}
	
	public static String getSysdate_yyyyMMddSim(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new Date());
	}

	public static String getSysdate_yyyyMMddHHmmssSim(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(new Date());
	}
	
	public static String getSysdate_HHmmss(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(new Date());
	}

	public static String getSysdate_yyyyMM(){
		SimpleDateFormat formatter = null;

		formatter = new SimpleDateFormat("yyyyMM");
		return formatter.format(new Date());
	}
}