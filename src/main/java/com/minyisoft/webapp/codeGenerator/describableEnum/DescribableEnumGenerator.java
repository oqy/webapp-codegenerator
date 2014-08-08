package com.minyisoft.webapp.codeGenerator.describableEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DescribableEnumGenerator {

	private Button button;
	private Text remark6;
	private Text remark5;
	private Text remark4;
	private Text remark3;
	private Text remark2;
	private Text remark1;
	private Text value6;
	private Text value5;
	private Text value4;
	private Text value3;
	private Text value2;
	private Text value1;
	private Text variableName6;
	private Text variableName5;
	private Text variableName4;
	private Text variableName3;
	private Text variableName2;
	private Text variableName1;
	private Text txtClassName;
	protected Shell shell;
	private Text txtModuleName;
	private Button chkIntEnum;
	private Button chkStringEnum;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DescribableEnumGenerator window = new DescribableEnumGenerator();
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
		shell.setSize(500, 380);
		shell.setText("整形枚举类生成器");

		final Label label = new Label(shell, SWT.NONE);
		label.setText("请输入枚举类名，程序将自动加上Enum后缀：");
		label.setBounds(10, 38, 240, 12);

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(10, 87, 174, 12);
		label_1.setText("请输入枚举信息：");

		txtClassName = new Text(shell, SWT.BORDER);
		txtClassName.setTabs(0);
		txtClassName.setBounds(10, 56, 240, 25);

		final Label label_1_1 = new Label(shell, SWT.NONE);
		label_1_1.setBounds(10, 105, 87, 12);
		label_1_1.setText("枚举变量名");

		final Label label_1_1_1 = new Label(shell, SWT.NONE);
		label_1_1_1.setBounds(174, 105, 87, 12);
		label_1_1_1.setText("\u5BF9\u5E94\u503C");

		final Label label_1_1_1_1 = new Label(shell, SWT.NONE);
		label_1_1_1_1.setBounds(340, 105, 87, 12);
		label_1_1_1_1.setText("文字说明");

		variableName1 = new Text(shell, SWT.BORDER);
		variableName1.setTabs(1);
		variableName1.setBounds(10, 123, 146, 25);

		variableName2 = new Text(shell, SWT.BORDER);
		variableName2.setTabs(4);
		variableName2.setBounds(10, 154, 146, 25);

		variableName3 = new Text(shell, SWT.BORDER);
		variableName3.setTabs(7);
		variableName3.setBounds(10, 185, 146, 25);

		variableName4 = new Text(shell, SWT.BORDER);
		variableName4.setTabs(10);
		variableName4.setBounds(11, 216, 146, 25);

		variableName5 = new Text(shell, SWT.BORDER);
		variableName5.setTabs(13);
		variableName5.setBounds(11, 247, 146, 25);

		variableName6 = new Text(shell, SWT.BORDER);
		variableName6.setTabs(16);
		variableName6.setBounds(11, 278, 146, 25);

		value1 = new Text(shell, SWT.BORDER);
		value1.setTabs(2);
		value1.setBounds(173, 123, 146, 25);

		value2 = new Text(shell, SWT.BORDER);
		value2.setTabs(5);
		value2.setBounds(173, 154, 146, 25);

		value3 = new Text(shell, SWT.BORDER);
		value3.setBounds(173, 185, 146, 25);

		value4 = new Text(shell, SWT.BORDER);
		value4.setTabs(11);
		value4.setBounds(174, 216, 146, 25);

		value5 = new Text(shell, SWT.BORDER);
		value5.setTabs(14);
		value5.setBounds(174, 247, 146, 25);

		value6 = new Text(shell, SWT.BORDER);
		value6.setTabs(17);
		value6.setBounds(174, 278, 146, 25);

		remark1 = new Text(shell, SWT.BORDER);
		remark1.setTabs(3);
		remark1.setBounds(335, 123, 146, 25);

		remark2 = new Text(shell, SWT.BORDER);
		remark2.setTabs(6);
		remark2.setBounds(335, 154, 146, 25);

		remark3 = new Text(shell, SWT.BORDER);
		remark3.setTabs(9);
		remark3.setBounds(335, 185, 146, 25);

		remark4 = new Text(shell, SWT.BORDER);
		remark4.setTabs(12);
		remark4.setBounds(336, 216, 146, 25);

		remark5 = new Text(shell, SWT.BORDER);
		remark5.setTabs(15);
		remark5.setBounds(336, 247, 146, 25);

		remark6 = new Text(shell, SWT.BORDER);
		remark6.setTabs(18);
		remark6.setBounds(336, 278, 146, 25);

		chkIntEnum = new Button(shell, SWT.RADIO);
		chkIntEnum.setSelection(true);
		chkIntEnum.setBounds(270, 57, 69, 16);
		chkIntEnum.setText("\u6574\u5F62\u679A\u4E3E");

		chkStringEnum = new Button(shell, SWT.RADIO);
		chkStringEnum.setBounds(360, 57, 109, 16);
		chkStringEnum.setText("\u5B57\u7B26\u578B\u679A\u4E3E");

		button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				if (StringUtils.isBlank(txtClassName.getText()) || StringUtils.isBlank(txtModuleName.getText())) {
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox.setMessage("模块名及枚举类目均不能为空！");
					messageBox.open();
					return;
				}

				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("enumName", txtClassName.getText());
				inputMap.put("modelName", txtModuleName.getText());

				boolean isIntEnum = chkIntEnum.getSelection();
				List<DescribableEnumSpecInfo<?>> enumsList = new ArrayList<DescribableEnumSpecInfo<?>>();
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName1, value1, remark1,
						isIntEnum));
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName2, value2, remark2,
						isIntEnum));
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName3, value3, remark3,
						isIntEnum));
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName4, value4, remark4,
						isIntEnum));
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName5, value5, remark5,
						isIntEnum));
				enumsList.add(DescribableEnumGeneratorUtil.createCoreEnumSpecInfo(variableName6, value6, remark6,
						isIntEnum));
				for (int i = enumsList.size() - 1; i >= 0; i--) {
					if (enumsList.get(i) == null) {
						enumsList.remove(i);
					}
				}
				inputMap.put("enumInfoList", enumsList);
				inputMap.put("isIntEnum", isIntEnum);
				DescribableEnumGeneratorUtil.generate(inputMap);

				MessageBox messageBox = new MessageBox(shell, SWT.OK);
				messageBox.setMessage("生成类文件成功！");
				messageBox.open();

				clearText();
			}
		});
		button.setBounds(174, 309, 146, 22);
		button.setText("生成整形枚举信息");

		txtModuleName = new Text(shell, SWT.BORDER);
		txtModuleName.setText("shop");
		txtModuleName.setBounds(111, 7, 87, 25);

		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(10, 10, 240, 12);
		label_2.setText("\u8BF7\u8F93\u5165\u6A21\u5757\u540D\u79F0\uFF1A");

		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(265, 38, 216, 12);
		label_3.setText("\u8BF7\u6307\u5B9A\u679A\u4E3E\u7C7B\u578B\uFF1A");

		shell.setTabList(new Control[] { txtClassName, variableName1, value1, remark1, variableName2, value2, remark2,
				variableName3, value3, remark3, variableName4, value4, remark4, variableName5, value5, remark5,
				variableName6, value6, remark6, button });
	}

	private void clearText() {
		txtClassName.setText("");

		variableName1.setText("");
		value1.setText("");
		remark1.setText("");

		variableName2.setText("");
		value2.setText("");
		remark2.setText("");

		variableName3.setText("");
		value3.setText("");
		remark3.setText("");

		variableName4.setText("");
		value4.setText("");
		remark4.setText("");

		variableName5.setText("");
		value5.setText("");
		remark5.setText("");

		variableName6.setText("");
		value6.setText("");
		remark6.setText("");
	}
}