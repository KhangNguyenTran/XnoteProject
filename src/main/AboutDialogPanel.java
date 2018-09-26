package main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class AboutDialogPanel extends JDialog {
	private static final long serialVersionUID = -2683614622132727811L;
	private final Dimension DIALOG_SIZE = new Dimension(400, 200);
	private JPanel dialogPanel = new JPanel();
	private JTextArea textArea = new JTextArea();

	public AboutDialogPanel() {
		setAlwaysOnTop(true);

		textArea.setText("\n************************************************************************"
				+ "\n\tĐồ án môn" + "\n\tNgôn ngữ lập trình Java" + "\n\tSE330.I21.PMCL"
				+ "\n*************************************************************************" + "\n\n"
				+ "\n   Thực hiện: Nguyễn Trần Vĩnh Khang - 15520344"
				+ "\n                       La Ngọc Lễ    - 15520416");
		textArea.setEditable(false);
		textArea.setPreferredSize(DIALOG_SIZE);
		dialogPanel.add(textArea, BorderLayout.SOUTH);
		dialogPanel.setPreferredSize(DIALOG_SIZE);
	}

	public JPanel getMainPanel() {
		return dialogPanel;
	}
}