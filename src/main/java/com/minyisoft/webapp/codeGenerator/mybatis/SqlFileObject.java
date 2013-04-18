package com.minyisoft.webapp.codeGenerator.mybatis;

public class SqlFileObject {
	//sql�ļ�·��
	private String sqlFilePath;
	//sql��
	private String sql;
	//sql��ִ���ļ�·��(����)
	private String exucuteLocalSqlFilePath;
	//sql��ִ���ļ�·��(������)
	private String exucuteServerSqlFilePath;
//	//sql��ִ���ļ�����
//	private String exucuteSqlFileContent;
	//sql��ִ���ļ�����ǰ(����)
	private String text1 = "sqlplus fusung/fusung@xe @";
	//sql��ִ���ļ�����ǰ(������)
	private String text2 = "sqlplus fusung/fusung@192.168.0.253/xe @";
	//sql��Log�ļ�·��
	private String sqlLogPath;
	
	public String getSqlFilePath() {
		return sqlFilePath;
	}
	public void setSqlFilePath(String sqlFilePath) {
		this.sqlFilePath = sqlFilePath;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getExucuteLocalSqlFilePath() {
		return exucuteLocalSqlFilePath;
	}
	public void setExucuteLocalSqlFilePath(String exucuteLocalSqlFilePath) {
		this.exucuteLocalSqlFilePath = exucuteLocalSqlFilePath;
	}
	public String getExucuteServerSqlFilePath() {
		return exucuteServerSqlFilePath;
	}
	public void setExucuteServerSqlFilePath(String exucuteServerSqlFilePath) {
		this.exucuteServerSqlFilePath = exucuteServerSqlFilePath;
	}
	public String getExucuteLocalSqlFileContent() {
		return this.text1 + this.sqlFilePath + " >" + this.sqlLogPath;
	}
	public String getExucuteServerSqlFileContent() {
		return this.text2 + this.sqlFilePath + " >" + this.sqlLogPath;
	}
	public String getSqlLogPath() {
		return sqlLogPath;
	}
	public void setSqlLogPath(String sqlLogPath) {
		this.sqlLogPath = sqlLogPath;
	}
	
}
