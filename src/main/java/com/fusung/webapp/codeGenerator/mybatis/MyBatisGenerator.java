package com.fusung.webapp.codeGenerator.mybatis;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class MyBatisGenerator {

	private Text txtPojoChineseName;
	private Table table;
	private Text sqlOutput;
	private Text outputFolderPath;
	private Text sqlFileOutputFolderPath;
	private Text pojoFileName;
	private List<PojoProperty> propertyInfoList=null; 
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MyBatisGenerator window = new MyBatisGenerator();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(800, 680);
		shell.setText("ibatis配置文件生成器");

		final Group group = new Group(shell, SWT.NONE);
		group.setText("功能选项卡");
		group.setBounds(10, 10, 772, 460);

		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 22,174, 12);
		label.setText("请选择需要转换的java pojo类：");

		pojoFileName = new Text(group, SWT.BORDER);
		pojoFileName.setBounds(10, 40,300, 25);
		
		final Group group_2 = new Group(group, SWT.NONE);
		group_2.setText("SQL语法");
		group_2.setBounds(10, 147, 300, 45);

		final Button isOracleScheme = new Button(group_2, SWT.RADIO);
		isOracleScheme.setSelection(true);
		isOracleScheme.setText("Oracle语法");
		isOracleScheme.setBounds(10, 20, 93, 16);

		final Button isMySQLScheme = new Button(group_2, SWT.RADIO);
		isMySQLScheme.setText("mySQL语法");
		isMySQLScheme.setBounds(137, 20, 93, 16);

		final Group group_2_1 = new Group(group, SWT.NONE);
		group_2_1.setBounds(316, 147, 364, 45);
		group_2_1.setText("SQL语句");

		final Button isGenerateSQLAlterAdd = new Button(group_2_1, SWT.RADIO);
		isGenerateSQLAlterAdd.setBounds(155, 20,165, 16);
		isGenerateSQLAlterAdd.setText("生成SQL Alter Add语句");

		final Button isGenerateSQLDDL = new Button(group_2_1, SWT.RADIO);
		isGenerateSQLDDL.setBounds(20, 20,129, 16);
		isGenerateSQLDDL.setSelection(true);
		isGenerateSQLDDL.setText("生成SQL DDL语句");

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText("SQL语句");
		group_1.setBounds(10, 487, 772, 150);

		final Button isGenIBatisConfigFile = new Button(group, SWT.CHECK);
		isGenIBatisConfigFile.setBounds(10, 74,129, 16);
		isGenIBatisConfigFile.setSelection(true);
		isGenIBatisConfigFile.setText("生成iBatis配置文件");

		final Button isGenerateCacheConfig = new Button(group, SWT.CHECK);
		isGenerateCacheConfig.setBounds(145, 74,185, 16);
		isGenerateCacheConfig.setSelection(true);
		isGenerateCacheConfig.setText("生成iBatis缓存配置信息");
		
		final Button isGenerateCRUDPermission = new Button(group, SWT.CHECK);
		isGenerateCRUDPermission.setBounds(335, 74, 117, 16);
		isGenerateCRUDPermission.setSelection(true);
		isGenerateCRUDPermission.setText("生成CRUD权限信息");

		final Button browseButton = new Button(group, SWT.NONE);
		browseButton.setBounds(316, 38,48, 22);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog filedlg=new FileDialog(shell,SWT.OPEN);
		        //设置文件对话框的标题
		        filedlg.setText("java pojo类文件选择");
		        //设置初始路径
		        filedlg.setFilterPath("SystemRoot");
		        //打开文件对话框，返回选中文件的绝对路径
		        String selectedFile=filedlg.open();
		        if(!StringUtils.isBlank(selectedFile)){
		        	if(selectedFile.indexOf("src")>0){
		        		String currentFolderPath=selectedFile.substring(0,selectedFile.lastIndexOf('\\'));
		        		if(currentFolderPath.endsWith("model")){
		        			currentFolderPath=currentFolderPath.substring(0, currentFolderPath.length()-5);
		        		}else{
		        			MessageBox messageBox = new MessageBox(shell, SWT.OK);
							messageBox
									.setMessage("java pojo类文件应放置在包含model目录的文件路径内，请检查");
							messageBox.open(); 
							return;
		        		}
		        		pojoFileName.setText(selectedFile.substring(selectedFile.lastIndexOf("src")+14,selectedFile.lastIndexOf(".")).replace('\\', '.'));
		        		outputFolderPath.setText(currentFolderPath);
		        		
		        		try{
		        			GenerateOption option=new GenerateOption();
		    				option.setOracleScheme(isOracleScheme.getSelection());
		    				option.setMySQLScheme(isMySQLScheme.getSelection());
		    				
		        			propertyInfoList=MyBatisGeneratorUtil.getPojoProperties(pojoFileName.getText(),option);
		        			table.removeAll();
		        			for(int i=0;i<propertyInfoList.size();i++){
		        				TableItem item = new TableItem(table, SWT.NONE);
				        		item.setText(0,propertyInfoList.get(i).getVariableName());
				        		item.setText(1,propertyInfoList.get(i).getVariableType().getName());
				        		item.setText(2,propertyInfoList.get(i).getSqlFieldName());
				        		item.setText(3,propertyInfoList.get(i).getSqlFieldType());
				        		item.setText(4,Boolean.toString(propertyInfoList.get(i).isReferenceKey()));
				        		item.setChecked(true);
		        			}
		        		}catch(Exception e){
		        			e.printStackTrace();
		        		}
		        	}else{
		        		MessageBox messageBox = new MessageBox(shell, SWT.OK);
						messageBox
								.setMessage("java pojo类文件应放置在包含src目录的文件路径内，请检查");
						messageBox.open(); 
		        	}
		        }
			}
		});
		browseButton.setText("浏览");

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setBounds(380, 22,192, 12);
		label_1.setText("请选择ibatis配置文件的输出路径：");

		outputFolderPath = new Text(group, SWT.BORDER);
		outputFolderPath.setBounds(380, 40,300, 25);

		final Button browseButton2 = new Button(group, SWT.NONE);
		browseButton2.setBounds(686, 38,48, 22);
		browseButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				DirectoryDialog folderdlg=new DirectoryDialog(shell);
		        //设置文件对话框的标题
		        folderdlg.setText("配置文件输出目录选择");
		        //设置初始路径
		        folderdlg.setFilterPath(outputFolderPath.getText());
		        //设置对话框提示文本信息
		        folderdlg.setMessage("请选择相应的文件夹");
		        //打开文件对话框，返回选中文件夹目录
		        String selecteddir=folderdlg.open();
		        if(!StringUtils.isBlank(selecteddir)){
		        	outputFolderPath.setText(selecteddir);
		        }                 
			}
		});
		browseButton2.setText("浏览");

		//+++++++++++++++++++++++++++++
		final Group groupSqlFileSet = new Group(group, SWT.NONE);
		groupSqlFileSet.setText("SQL文件生成选项");
		groupSqlFileSet.setBounds(10, 96, 753, 47);

		sqlFileOutputFolderPath = new Text(groupSqlFileSet, SWT.BORDER);
		sqlFileOutputFolderPath.setBounds(10, 15,290, 25);
		
		final Button browseButton3 = new Button(groupSqlFileSet, SWT.NONE);
		browseButton3.setBounds(310, 12,48, 22);
		browseButton3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog sqlFile=new FileDialog(shell,SWT.OPEN);
		        //设置文件对话框的标题
				sqlFile.setText("Sql文件选择");
		        //设置初始路径
				sqlFile.setFilterPath("SystemRoot");
		        //打开文件对话框，返回选中文件的绝对路径
		        String selectedFile=sqlFile.open();
		        if(!StringUtils.isBlank(selectedFile)){
		        	sqlFileOutputFolderPath.setText(selectedFile);
		        }
			}
		});
		browseButton3.setText("浏览");
		
		final Button isSetSqlFile = new Button(groupSqlFileSet, SWT.CHECK);
		isSetSqlFile.setBounds(370, 12,90, 30);
		isSetSqlFile.setSelection(true);
		isSetSqlFile.setText("生成SQL文件");

		final Group groupNewFileSet = new Group(groupSqlFileSet, SWT.NONE);
		groupNewFileSet.setText("新SQL文件选项");
		groupNewFileSet.setBounds(460, 10, 160, 35);
		
		final Button isNewSqlFileScheme = new Button(groupNewFileSet, SWT.RADIO);
		isNewSqlFileScheme.setSelection(true);
		isNewSqlFileScheme.setText("新文件");
		isNewSqlFileScheme.setBounds(10, 15, 60, 13);
		
		final Button isAddSqlFileScheme = new Button(groupNewFileSet, SWT.RADIO);
		isAddSqlFileScheme.setSelection(false);
		isAddSqlFileScheme.setText("追加到文件");
		isAddSqlFileScheme.setBounds(75, 15, 80, 13);

		final Group groupIsLoacl = new Group(groupSqlFileSet, SWT.NONE);
		groupIsLoacl.setText("是否执行本地DB");
		groupIsLoacl.setBounds(625, 10, 120, 35);
		
		final Button isLocalScheme = new Button(groupIsLoacl, SWT.CHECK);
		isLocalScheme.setSelection(true);
		isLocalScheme.setText("本地");
		isLocalScheme.setBounds(10, 15, 45, 13);
		
		final Button isServerScheme = new Button(groupIsLoacl, SWT.CHECK);
		isServerScheme.setSelection(true);
		isServerScheme.setText("服务器");
		isServerScheme.setBounds(60, 15, 55, 13);
		//+++++++++++++++++++++++++++++
		
		final Button generateButton = new Button(group, SWT.NONE);
		generateButton.setBounds(686, 161,48, 22);
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if(StringUtils.isBlank(pojoFileName.getText())){
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox
							.setMessage("java pojo类文件为空，请先选择");
					messageBox.open(); 
					return;
				}
				if(StringUtils.isBlank(outputFolderPath.getText())){
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox
							.setMessage("输出配置文件的目标目录为空，请先选择");
					messageBox.open(); 
					return;
				}
				
				/*若pojo属性链表中没有元素(或表格中没有行)，给予提示*/
				if(table.getItems()==null||table.getItems().length==0||propertyInfoList==null||propertyInfoList.size()==0){
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox
							.setMessage("属性列表为空");
					messageBox.open(); 
					return;
				}
				//根据表格中行是否被勾选，设置pojo属性链表中的needGenIbatisInfo属性
				int uncheckCount=0;
				for(int i=0;i<table.getItems().length;i++){
					TableItem item=table.getItem(i);
					if(!item.getChecked()){
						propertyInfoList.get(i).setNeedGenIbatisInfo(false);
						uncheckCount+=1;
					}else{
						propertyInfoList.get(i).setNeedGenIbatisInfo(true);
					}
				}
				
				/*若表格中所有行都没有被勾选，给予提示*/
				if(uncheckCount==propertyInfoList.size()){
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox
							.setMessage("您需要勾选表格中最少1行记录");
					messageBox.open(); 
					return;
				}
				GenerateOption option=new GenerateOption();
				option.setGenerateIbatisConfig(isGenIBatisConfigFile.getSelection());
				option.setGenerateIbatisCache(isGenerateCacheConfig.getSelection());
				option.setGenerateSQLDDL(isGenerateSQLDDL.getSelection());
				option.setGenerateSQLAlterAdd(isGenerateSQLAlterAdd.getSelection());
				option.setOracleScheme(isOracleScheme.getSelection());
				option.setMySQLScheme(isMySQLScheme.getSelection());
				option.setGenerateCRUDPermission(isGenerateCRUDPermission.getSelection());
				option.setPojoChineseName(txtPojoChineseName.getText());
				
				option.setSetSqlFile(isSetSqlFile.getSelection());
				option.setAddSqlFileScheme(isAddSqlFileScheme.getSelection());
				option.setNewSqlFileScheme(isNewSqlFileScheme.getSelection());
				option.setServerScheme(isServerScheme.getSelection());
				option.setLocalScheme(isLocalScheme.getSelection());
				
				Map<String,Object> returnMap=MyBatisGeneratorUtil.generate(pojoFileName.getText(), outputFolderPath.getText(), sqlFileOutputFolderPath.getText(), propertyInfoList, option);
				sqlOutput.setText(returnMap.get("SQL").toString());
			}
		});
		generateButton.setText("运行");

		
		table = new Table(group, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 203, 752, 254);

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("变量名称");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("变量类型");

		final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setWidth(100);
		newColumnTableColumn_3.setText("对应sql字段名");

		final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(100);
		newColumnTableColumn_4.setText("对应sql字段类型");

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("是否外键关联");
		
		/* table编辑器 for 数据库字段名称 */
		final TableEditor sqlFieldNameTableEditor = new TableEditor(table);  
        // The editor must have the same size as the cell and must  
        // not be any smaller than 50 pixels.  
        sqlFieldNameTableEditor.horizontalAlignment = SWT.LEFT;  
        sqlFieldNameTableEditor.grabHorizontal = true;  
        sqlFieldNameTableEditor.minimumWidth = 50;  
        
        /* table编辑器 for 数据库字段类型 */
        final TableEditor sqlFieldTypeTableEditor = new TableEditor(table);  
        // The editor must have the same size as the cell and must  
        // not be any smaller than 50 pixels.  
        sqlFieldTypeTableEditor.horizontalAlignment = SWT.LEFT;  
        sqlFieldTypeTableEditor.grabHorizontal = true;  
        sqlFieldTypeTableEditor.minimumWidth = 50;  
        
        table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control  
                Control oldFieldNameEditor = sqlFieldNameTableEditor.getEditor();  
                if (oldFieldNameEditor != null)  
                    oldFieldNameEditor.dispose(); 
                
                Control oldFieldTypeEditor = sqlFieldTypeTableEditor.getEditor();  
                if (oldFieldTypeEditor != null)  
                    oldFieldTypeEditor.dispose();
  
                // Identify the selected row  
                final TableItem item = (TableItem) e.item;  
                if (item == null)  
                    return;
                
                Text fieldTypeEditor = new Text(table, SWT.NONE);  
                fieldTypeEditor.setText(item.getText(3));  
                fieldTypeEditor.addModifyListener(new ModifyListener() {  
                    public void modifyText(ModifyEvent me) {  
                        Text text = (Text) sqlFieldTypeTableEditor.getEditor();  
                        sqlFieldTypeTableEditor.getItem().setText(3,text.getText());
                        //更新propertyInfoList内对应元素的数据库字段类型
                        for(PojoProperty property : propertyInfoList){
                        	if(StringUtils.equals(property.getVariableName(),sqlFieldTypeTableEditor.getItem().getText(0))){
                        		property.setSqlFieldType(sqlFieldTypeTableEditor.getItem().getText(3));
                        	}
                        }
                    }  
                });  
                fieldTypeEditor.selectAll();  
                fieldTypeEditor.setFocus();  
                
                Text fieldNameEditor = new Text(table, SWT.NONE);  
                fieldNameEditor.setText(item.getText(2));  
                fieldNameEditor.addModifyListener(new ModifyListener() {  
                    public void modifyText(ModifyEvent me) {  
                        Text text = (Text) sqlFieldNameTableEditor.getEditor();  
                        sqlFieldNameTableEditor.getItem().setText(2,text.getText());
                        //更新propertyInfoList内对应元素的数据库字段名称
                        for(PojoProperty property : propertyInfoList){
                        	if(StringUtils.equals(property.getVariableName(),sqlFieldTypeTableEditor.getItem().getText(0))){
                        		property.setSqlFieldName(sqlFieldTypeTableEditor.getItem().getText(2));
                        	}
                        }
                    }  
                });  
                fieldNameEditor.selectAll();  
                fieldNameEditor.setFocus();  
                
                sqlFieldNameTableEditor.setEditor(fieldNameEditor, item, 2);  
                sqlFieldTypeTableEditor.setEditor(fieldTypeEditor, item, 3);  
			}
		});

		txtPojoChineseName = new Text(group, SWT.BORDER);
		txtPojoChineseName.setBounds(560, 70, 120, 25);

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("POJO类中文名称：");
		label_2.setBounds(458, 76, 96, 12);

		sqlOutput = new Text(group_1, SWT.BORDER);
		sqlOutput.setBounds(10, 25,752, 94);
//++++++++++++++++++++++++++++
		final Button sqlButton1 = new Button(group_1, SWT.NONE);
		sqlButton1.setBounds(300, 121,140, 25);
		sqlButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
				String temp = sqlOutput.getText();
				StringSelection text = new StringSelection(temp);   
				clipboard.setContents(text,null);           
			}
		});
		sqlButton1.setText("复制SQL语句到粘帖板");
//-------------------------------
	}
}
