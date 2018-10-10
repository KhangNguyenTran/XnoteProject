package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;

public class GoToDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTabbedPane tabbedPane;

	/*
	 * public static void main(String[] args) { try { GoToDialog dialog = new
	 * GoToDialog(); dialog.setVisible(true); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */
	/**
	 * Create the dialog.
	 */
	public GoToDialog() {
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		/*****************************************************/
		setTitle("Go to line");
		setBounds(100, 100, 305, 140);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			textField = new JTextField();
			PlainDocument docTextField = (PlainDocument) textField.getDocument();
			docTextField.setDocumentFilter(new IntFilter());			
			textField.setBounds(10, 38, 266, 20);
			contentPanel.add(textField);
			textField.setColumns(10);
		}
		textField.setFocusAccelerator('L');

		JLabel lblLineNumber = new JLabel("Line number:");
		lblLineNumber.setBounds(10, 11, 109, 20);
		lblLineNumber.setFont(new Font("Caliri", Font.PLAIN, 12));
		contentPanel.add(lblLineNumber);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton gotoButton = new JButton("Go To");
				gotoButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						gotoLine(Integer.parseInt(textField.getText()) - 1);
					}
				});				
				gotoButton.setActionCommand("Goto");
				buttonPane.add(gotoButton);
				getRootPane().setDefaultButton(gotoButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void gotoLine(int line) {
		JViewport viewport = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
		JTextArea textArea_ = (JTextArea) viewport.getView();
		try {
			int maxLineNum = textArea_.getLineCount();
			if (maxLineNum <= line)
				JOptionPane.showMessageDialog(tabbedPane.getRootPane(), "Out of range\nTotal lines is " + maxLineNum);
			else {
				int characterCounter = 0;
				String subString[] = textArea_.getText().split("\n");
				for (int i = 0; i < line; i++) {
					characterCounter += subString[i].length() + 1;
				}
				textArea_.setCaretPosition(characterCounter);
				textArea_.requestFocus();
				setVisible(false);
			}
		} catch (Exception e) {
			// Do nothing
		}
	}

	public void setTabbedPaneFrom(JTabbedPane thisTabbedPane) {
		tabbedPane = thisTabbedPane;
	}
}
