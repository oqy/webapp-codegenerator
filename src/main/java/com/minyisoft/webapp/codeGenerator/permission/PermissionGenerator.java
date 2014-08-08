package com.minyisoft.webapp.codeGenerator.permission;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.minyisoft.webapp.codeGenerator.config.CodeGeneratorConfig;
import com.minyisoft.webapp.core.model.CoreBaseInfo;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;


public class PermissionGenerator {

	private Text txtPojoPermissionValue;
	private Text txtPermissionName;
	private Text txtOutputFileName;
	private Text txtPojoName;
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PermissionGenerator window = new PermissionGenerator();
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
		shell.setSize(416, 292);
		shell.setText("权限信息生成器");

		final Group group = new Group(shell, SWT.NONE);
		group.setText("功能选项卡");
		group.setBounds(10, 10, 388, 238);

		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 22,211, 12);
		label.setText("请选择需要生成权限的java pojo类：");

		txtPojoName = new Text(group, SWT.BORDER);
		txtPojoName.setBounds(10, 40,300, 25);

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
		        		String className=CodeGeneratorConfig.CLASS_FULL_NAME_PREFIX+StringUtils.substringBetween(selectedFile, CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_JAVA_FILE_ROOT, ".java").replace('\\', '.');
		        		String simpleName=className.substring(className.lastIndexOf('.')+1);
		        		if(simpleName.endsWith("Info")){
		        			simpleName=simpleName.substring(0,simpleName.length()-4);
		        		}
		        		txtPojoName.setText(className);
		        		txtOutputFileName.setText(CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_RESOURCE_FILE_ROOT+StringUtils.substringBetween(selectedFile, CodeGeneratorConfig.TARGET_PROJECT_PACKAGE_JAVA_FILE_ROOT, "model")+"security\\permission\\"+simpleName+".permission");
		        		txtPojoPermissionValue.setText(simpleName+":");
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
		label_1.setBounds(10, 73,192, 12);
		label_1.setText("请选择权限文件的输出路径：");

		txtOutputFileName = new Text(group, SWT.BORDER);
		txtOutputFileName.setBounds(10, 91,300, 25);

		final Button browseButton2 = new Button(group, SWT.NONE);
		browseButton2.setBounds(316, 89,48, 22);
		browseButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				DirectoryDialog folderdlg=new DirectoryDialog(shell);
		        //设置文件对话框的标题
		        folderdlg.setText("配置文件输出目录选择");
		        //设置初始路径
		        folderdlg.setFilterPath(txtOutputFileName.getText());
		        //设置对话框提示文本信息
		        folderdlg.setMessage("请选择相应的文件夹");
		        //打开文件对话框，返回选中文件夹目录
		        String selecteddir=folderdlg.open();
		        if(!StringUtils.isBlank(selecteddir)){
		        	txtOutputFileName.setText(selecteddir);
		        }                 
			}
		});
		browseButton2.setText("浏览");

		txtPermissionName = new Text(group, SWT.BORDER);
		txtPermissionName.setBounds(75, 132, 235, 25);

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("权限名称：");
		label_2.setBounds(10, 140, 96, 12);

		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(10, 170, 59, 12);
		label_2_1.setText("权限简码：");

		txtPojoPermissionValue = new Text(group, SWT.BORDER);
		txtPojoPermissionValue.setBounds(75, 161, 235, 25);

		final Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(final SelectionEvent arg0) {
				MessageBox messageBox = null;
				if (StringUtils.isBlank(txtPojoName.getText())
						|| StringUtils.isBlank(txtOutputFileName.getText())
						|| StringUtils.isBlank(txtPermissionName.getText())
						|| StringUtils.isBlank(txtPojoPermissionValue.getText())) {
					messageBox = new MessageBox(shell, SWT.OK);
					messageBox.setMessage("请选择pojo类、设置权限文件输出路径、输入权限名称及简码");
					messageBox.open(); 
					return;
				}
				
				try {
					Class<?> modelClazz=Class.forName(txtPojoName.getText());
					if(!CoreBaseInfo.class.isAssignableFrom(modelClazz)){
						messageBox = new MessageBox(shell, SWT.OK);
						messageBox.setMessage("指定的java类并非继承自"+CoreBaseInfo.class.getName());
						messageBox.open(); 
						return;
					}
					
					ObjectUuidUtils.registerModelClass(PermissionInfo.class);
					ObjectUuidUtils.registerModelClass((Class<? extends IModelObject>)modelClazz);
					
					StringBuffer sb = new StringBuffer(ObjectUuidUtils.createObjectID(PermissionInfo.class));
					sb.append("=").append(txtPojoPermissionValue.getText());
					sb.append(",").append(txtPermissionName.getText());
					sb.append(",").append(ObjectUuidUtils.getClassShortKey((Class<? extends IModelObject>)modelClazz));
					sb.append("\\n");
					
					Files.append(sb, new File(txtOutputFileName.getText()), Charsets.UTF_8);
					
					messageBox = new MessageBox(shell, SWT.OK);
					messageBox.setMessage("权限文件输出成功");
					messageBox.open(); 
				} catch (Exception e) {
					e.printStackTrace();
					
					messageBox = new MessageBox(shell, SWT.OK);
					messageBox.setMessage("权限文件输出失败");
					messageBox.open(); 
				}
			}
		});
		button.setText("生成权限信息");
		button.setBounds(75, 192, 235, 32);
	}

}
