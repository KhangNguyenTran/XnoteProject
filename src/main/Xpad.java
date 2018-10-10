package main;

import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JRadioButtonMenuItem;

public class Xpad {

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private FindAndReplaceDialog findAndReplaceDialog;
	private UndoManager undoManager;
	private JTextPane statusTextPane;
	private GoToDialog goToDialog;
	private boolean wordWrap;
	private String[] saveDir = null;
	private boolean isTextModified[] = null;
	/*****************************************/

	private JPopupMenu popupMenu;
	private JRadioButtonMenuItem rdbtnmntmStatusBar;
	private JRadioButtonMenuItem rdbtnmntmWordWrap;
	private JMenuItem mntmCut;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JMenuItem mntmRedo;
	private JMenuItem mntmUndo;
	private JMenuItem mntmDelete;
	private JMenuItem mntmPrint;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmSaveFile;
	private JMenuItem mntmGoTo;
	private JMenuItem mntmFind;
	private JMenuItem mntmReplace;
	private JMenuItem mntmTimedate;
	private JMenuItem mntmFont;
	private JMenuItem mntmSelecteAll;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Xpad window = new Xpad();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Xpad() {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		saveDir = new String[11];
		wordWrap = false;
		isTextModified = new boolean[11];
		/********************************************************/
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setFont(new Font("Caliri", Font.PLAIN, 12));
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setMinimumSize(new Dimension(350, 200));
		frame.setBounds(100, 100, 550, 400);
		frame.setTitle("Xpad");
		frame.setLocationRelativeTo(frame.getParent());// Appearance location

		/*********************************************************/
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.setBounds(0, 22, 534, 339);

		/*********************************************************/
		 JMenuItem pmNewTab;
		 JMenuItem pmSaveFile;
		 JMenuItem pmOpenFile;
		 JMenuItem pmCloseTab;
		 JMenuItem pmCloseAll;
		 JMenuItem pmExit;
		 
		 JMenuItem mntmCloseAll;
		 JMenuItem mntmCloseTab;
		 JMenuItem mntmExit;
		 JMenuItem mntmOpenFile;
		 JMenuItem mntmNewTab;
		 JMenuItem mntmNew;		 
		/*********************************************************/
		goToDialog = new GoToDialog();
		goToDialog.setLocationRelativeTo(frame);
		goToDialog.setTabbedPaneFrom(tabbedPane);
		goToDialog.setVisible(false);

		/******************** Make first tab *********************/
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("*New tab", null, scrollPane, null);
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
		textArea.addKeyListener(new UpdateTextModifedListener());
		textArea.addCaretListener(new UpdateStatusCaretListener());
		textArea.addMouseListener(new MouseReleaseListener());
		scrollPane.setViewportView(textArea);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_0);

		/*********** Add Cut, Copy, Paste, Undo, Redo ************/
		scrollPane.setTransferHandler(new TransferHandler(null));
		textArea.setDragEnabled(true);
		undoManager = new UndoManager();
		textArea.getDocument().addUndoableEditListener(undoManager);

		/******************* Font chooser ************************/
		FontChooser fontChooser = new FontChooser();

		/*********************************************************/

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 534, 22);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If there are no tab. Open one.
				if (tabbedPane.getTabRunCount() < 1) {
					JScrollPane scrollPane = new JScrollPane();
					JTextArea textArea = new JTextArea();
					scrollPane.setViewportView(textArea);
					textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
					textArea.setLineWrap(wordWrap);
					scrollPane.setTransferHandler(new TransferHandler(null));
					textArea.setDragEnabled(true);
					textArea.addKeyListener(new UpdateTextModifedListener());
					textArea.addCaretListener(new UpdateStatusCaretListener());
					textArea.addMouseListener(new MouseReleaseListener());
					tabbedPane.addTab("*New tab", null, scrollPane, null);
					tabbedPane.setVisible(true);
					/*****************************/
					enableEveryThing();
				} else {
					Xpad window = new Xpad();
					window.frame.setVisible(true);
				}
			}
		});
		KeyStroke controlN = KeyStroke.getKeyStroke("control N");
		mntmNew.setAccelerator(controlN);
		mnFile.add(mntmNew);

		mntmNewTab = new JMenuItem("New tab");
		mntmNewTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numberOfTabs = tabbedPane.getTabCount();
				if (numberOfTabs < 10) {
					JScrollPane scrollPane = new JScrollPane();
					JTextArea textArea = new JTextArea();
					scrollPane.setViewportView(textArea);
					textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
					textArea.setLineWrap(wordWrap);
					scrollPane.setTransferHandler(new TransferHandler(null));
					textArea.setDragEnabled(true);
					textArea.addKeyListener(new UpdateTextModifedListener());
					textArea.addCaretListener(new UpdateStatusCaretListener());
					textArea.addMouseListener(new MouseReleaseListener());
					if (numberOfTabs == 0) {
						tabbedPane.addTab("*New tab", null, scrollPane, null);
						tabbedPane.setVisible(true);
					} else
						tabbedPane.addTab("*New tab " + (numberOfTabs), null, scrollPane, null);
					tabbedPane.setSelectedIndex(numberOfTabs % 10);
					tabbedPane.setMnemonicAt(numberOfTabs, KeyEvent.VK_0 + numberOfTabs % 10);
					/************** Enable all the disable features ****************/
					enableEveryThing();
				}
			}
		});
		KeyStroke controlT = KeyStroke.getKeyStroke("control T");
		mntmNewTab.setAccelerator(controlT);
		mnFile.add(mntmNewTab);

		mntmOpenFile = new JMenuItem("Open File");
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (tabbedPane.getTabCount() > 0) {

						JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
						JTextArea textArea_ = (JTextArea) viewport_.getView();
						textArea_.setLineWrap(wordWrap);
						if (textArea_.getText().isEmpty()) { // Replce current tab if no text inserted
							saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
							tabbedPane.setVisible(true);
							isTextModified[tabbedPane.getSelectedIndex()] = false;
						} else {
							if (saveDir[tabbedPane.getSelectedIndex()] == null) { // Ask for save
								int response = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to "
										+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
								if (response == JOptionPane.YES_OPTION) {
									saveFileAction(textArea_);
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);

								} else if (response == JOptionPane.NO_OPTION) {
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
								}

							} else { // Text has been changed, we need to save the new text.
								if (isTextModified[tabbedPane.getSelectedIndex()]) {
									int response = JOptionPane.showConfirmDialog(frame,
											"Do you want to save changes to "
													+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
									if (response == JOptionPane.YES_OPTION) {

										File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
										BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
										outFile.write(textArea_.getText());
										outFile.flush();
										outFile.close();

										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
									} else if (response == JOptionPane.NO_OPTION) {
										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
									}

								} else { // No changes in text, no need to save again
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
								}
							}
						}

					} else {
						JScrollPane scrollPane = new JScrollPane();
						JTextArea textArea = new JTextArea();
						textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
						textArea.setLineWrap(wordWrap);
						scrollPane.setViewportView(textArea);
						tabbedPane.addTab("", null, scrollPane, null);
						tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
						textArea.addKeyListener(new UpdateTextModifedListener());
						textArea.addCaretListener(new UpdateStatusCaretListener());
						textArea.addMouseListener(new MouseReleaseListener());
						// Get the directory of the currently opened file
						saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea);
						isTextModified[tabbedPane.getSelectedIndex()] = false;
					}
					/*****************************/
					enableEveryThing();
				} catch (Exception e1) {
					// Do nothing
				}
			}
		});
		KeyStroke controlO = KeyStroke.getKeyStroke("control O");
		mntmOpenFile.setAccelerator(controlO);
		mnFile.add(mntmOpenFile);

		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);

		mntmSaveFile = new JMenuItem("Save");
		mntmSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// current tab already isn't saved
					if (saveDir[tabbedPane.getSelectedIndex()] == null) {
						JViewport viewport = scrollPane.getViewport();
						JTextArea textArea = (JTextArea) viewport.getView();
						saveDir[tabbedPane.getSelectedIndex()] = saveFileAction(textArea);
					} else {// current tab already has saved
						File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
						BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
						JViewport viewport = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
						JTextArea textArea = (JTextArea) viewport.getView();
						outFile.write(textArea.getText());
						outFile.flush();
						outFile.close();
					}

					isTextModified[tabbedPane.getSelectedIndex()] = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		KeyStroke controlS = KeyStroke.getKeyStroke("control S");
		mntmSaveFile.setAccelerator(controlS);
		mnFile.add(mntmSaveFile);

		mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JViewport viewport = scrollPane.getViewport();
					JTextArea textArea = (JTextArea) viewport.getView();
					saveDir[tabbedPane.getSelectedIndex()] = saveFileAction(textArea);
					isTextModified[tabbedPane.getSelectedIndex()] = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		mnFile.add(mntmSaveAs);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		mntmPrint = new JMenuItem("Print");
		mntmPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int tmp_ = 0;
				if (frame.isAlwaysOnTop()) {
					tmp_++;
					frame.setAlwaysOnTop(false);
				}
				PrinterJob pjob = PrinterJob.getPrinterJob();
				PageFormat pf = pjob.defaultPage();
				pjob.setPrintable(null, pf);

				if (pjob.printDialog()) {
					try {
						pjob.print();
					} catch (PrinterException e1) {
						e1.printStackTrace();
					}
				}
				if (tmp_ == 1)
					frame.setAlwaysOnTop(true);
			}
		});
		KeyStroke controlP = KeyStroke.getKeyStroke("control P");
		mntmPrint.setAccelerator(controlP);
		mnFile.add(mntmPrint);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		KeyStroke controlQ = KeyStroke.getKeyStroke("control Q");
		mntmExit.setAccelerator(controlQ);

		mntmCloseTab = new JMenuItem("Close Tab");
		mntmCloseTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() > 0) {
					JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
					JTextArea textArea_ = (JTextArea) viewport_.getView();
					if (textArea_.getText().isEmpty()) { // Delete if no text inserted
						saveDir[tabbedPane.getSelectedIndex()] = null;
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					} else {
						boolean alwaysOnTop_lock = false;
						if (frame.isAlwaysOnTop()) {
							alwaysOnTop_lock = true;
							frame.setAlwaysOnTop(false);
						}
						/**********************************/
						if (saveDir[tabbedPane.getSelectedIndex()] == null) { // Ask for save
							try {
								int response = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to "
										+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
								if (response == JOptionPane.YES_OPTION) {
									saveFileAction(textArea_);
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = null;
									tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
								} else if (response == JOptionPane.NO_OPTION) {
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = null;
									tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else { // Text has been changed, we need to save the new text.
							if (isTextModified[tabbedPane.getSelectedIndex()]) {
								try {
									int response = JOptionPane.showConfirmDialog(frame,
											"Do you want to save changes to "
													+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
									if (response == JOptionPane.YES_OPTION) {

										File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
										BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
										outFile.write(textArea_.getText());
										outFile.flush();
										outFile.close();

										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = null;
										tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
									} else if (response == JOptionPane.NO_OPTION) {
										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = null;
										tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
									}
								} catch (IOException e1) {
									// Do nothing
								}
							} else { // No changes in text, no need to save again
								saveDir[tabbedPane.getSelectedIndex()] = null;
								tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
							}
						}
						/**********************************/
						if (alwaysOnTop_lock)
							frame.setAlwaysOnTop(true);
						/********************* Request Focus on previous tab *********************/
						if(tabbedPane.getSelectedIndex() > 0) {
							viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
							textArea_ = (JTextArea) viewport_.getView();
							textArea_.requestFocusInWindow();							
						}
						updateStatus(1, 1);
					}
					if (tabbedPane.getTabCount() == 0) {
						disableEveryThing();
						tabbedPane.setVisible(false);
					}
				} else {
					disableEveryThing();
					JOptionPane.showMessageDialog(frame, "No tab left");
					tabbedPane.setVisible(false);
				}
			}
		});
		KeyStroke controlW = KeyStroke.getKeyStroke("control W");
		mntmCloseTab.setAccelerator(controlW);
		mnFile.add(mntmCloseTab);

		mntmCloseAll = new JMenuItem("Close All");
		mntmCloseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() <= 0)
					JOptionPane.showMessageDialog(frame, "No tab left");
				disableEveryThing();
			}
		});
		KeyStroke controShiftlW = KeyStroke.getKeyStroke("control shift W");
		mntmCloseAll.setAccelerator(controShiftlW);
		mnFile.add(mntmCloseAll);
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		mntmUndo = new JMenuItem("Undo");
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotUndoException cannotUndoException) {
					// Do nothing
				}
			}
		});
		KeyStroke controlZ = KeyStroke.getKeyStroke("control Z");
		mntmUndo.setAccelerator(controlZ);
		mnEdit.add(mntmUndo);

		mntmRedo = new JMenuItem("Redo");
		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotRedoException cannotRedoException) {
					// Do nothing
				}
			}
		});
		KeyStroke controlY = KeyStroke.getKeyStroke("control Y");
		mntmRedo.setAccelerator(controlY);
		mnEdit.add(mntmRedo);

		JSeparator separator_4 = new JSeparator();
		mnEdit.add(separator_4);

		mntmCut = new JMenuItem(new DefaultEditorKit.CutAction());
		mntmCut.setText("Cut");
		KeyStroke controlX = KeyStroke.getKeyStroke("control X");
		mntmCut.setAccelerator(controlX);
		mnEdit.add(mntmCut);

		mntmCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
		mntmCopy.setText("Copy");
		KeyStroke controlC = KeyStroke.getKeyStroke("control C");
		mntmCopy.setAccelerator(controlC);
		mnEdit.add(mntmCopy);

		mntmPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
		mntmPaste.setText("Paste");
		KeyStroke controlV = KeyStroke.getKeyStroke("control V");
		mntmPaste.setAccelerator(controlV);
		mnEdit.add(mntmPaste);

		mntmDelete = new JMenuItem("Delete");
		mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mntmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int startI = textArea.getSelectionStart();
				int endI = textArea.getSelectionEnd();
				if (startI == endI) // Delete the previous character if selection==null
					textArea.select(startI - 1, endI);
				textArea.replaceSelection("");
			}
		});
		mnEdit.add(mntmDelete);

		JSeparator separator_5 = new JSeparator();
		mnEdit.add(separator_5);

		mntmFind = new JMenuItem("Find");
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (findAndReplaceDialog == null) {
					findAndReplaceDialog = new FindAndReplaceDialog();
					findAndReplaceDialog.findConfigIndex();
					findAndReplaceDialog.getContentPane().add(findAndReplaceDialog.getMainPanel());
					findAndReplaceDialog.setSelectedTab(0); // open Find tab first
					findAndReplaceDialog.setLocationRelativeTo(frame.getParent());
					findAndReplaceDialog.setVisible(true);
				} else {
					findAndReplaceDialog.setSelectedTab(0); // open Find tab first
					findAndReplaceDialog.findConfigIndex();
					findAndReplaceDialog.setVisible(true);
				}
			}
		});
		KeyStroke controlF = KeyStroke.getKeyStroke("control F");
		mntmFind.setAccelerator(controlF);
		mnEdit.add(mntmFind);

		mntmReplace = new JMenuItem("Replace");
		mntmReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (findAndReplaceDialog == null) {
					findAndReplaceDialog = new FindAndReplaceDialog();
					findAndReplaceDialog.replaceConfigIndex();
					findAndReplaceDialog.getContentPane().add(findAndReplaceDialog.getMainPanel());
					findAndReplaceDialog.setSelectedTab(1); // open Replace tab first
					findAndReplaceDialog.setLocationRelativeTo(frame.getParent());
					findAndReplaceDialog.setVisible(true);
				} else {
					findAndReplaceDialog.setSelectedTab(1); // open Replace tab first
					findAndReplaceDialog.replaceConfigIndex();
					findAndReplaceDialog.setVisible(true);
				}
			}
		});
		KeyStroke controlR = KeyStroke.getKeyStroke("control R");
		mntmReplace.setAccelerator(controlR);
		mnEdit.add(mntmReplace);

		mntmGoTo = new JMenuItem("Go to");
		mntmGoTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goToDialog.requestFocusInWindow();
				if (!goToDialog.isVisible()) {
					goToDialog.setVisible(true);
				}
			}
		});
		KeyStroke controlG = KeyStroke.getKeyStroke("control G");
		mntmGoTo.setAccelerator(controlG);
		mnEdit.add(mntmGoTo);

		JSeparator separator_3 = new JSeparator();
		mnEdit.add(separator_3);

		mntmSelecteAll = new JMenuItem("Select All");
		mntmSelecteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
				JTextArea textArea_ = (JTextArea) viewport_.getView();
				textArea_.selectAll();
			}
		});
		KeyStroke controlA = KeyStroke.getKeyStroke("control A");
		mntmSelecteAll.setAccelerator(controlA);
		mnEdit.add(mntmSelecteAll);

		mntmTimedate = new JMenuItem("Time/Date");
		mntmTimedate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
				JTextArea textArea_ = (JTextArea) viewport_.getView();
				int index = textArea_.getSelectionEnd();
				long tmp = System.currentTimeMillis();
				Date d = new Date(tmp);

				textArea_.setSelectionStart(index);
				textArea_.replaceSelection(d.toString());
			}
		});
		KeyStroke f5 = KeyStroke.getKeyStroke("F5");
		mntmTimedate.setAccelerator(f5);
		mnEdit.add(mntmTimedate);

		JMenu mnNewMenu = new JMenu("Format");
		menuBar.add(mnNewMenu);

		JRadioButtonMenuItem rdbtnmntmAlwaysOnTop = new JRadioButtonMenuItem("Always On Top");
		rdbtnmntmAlwaysOnTop.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!frame.isAlwaysOnTop())
					frame.setAlwaysOnTop(true);
				else
					frame.setAlwaysOnTop(false);
			}
		});
		KeyStroke altT = KeyStroke.getKeyStroke("alt T");
		rdbtnmntmAlwaysOnTop.setAccelerator(altT);
		rdbtnmntmAlwaysOnTop.setSelected(true);
		mnNewMenu.add(rdbtnmntmAlwaysOnTop);

		rdbtnmntmWordWrap = new JRadioButtonMenuItem("Word Wrap");
		rdbtnmntmWordWrap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
				JTextArea textArea_ = (JTextArea) viewport_.getView();
				if (!wordWrap) {
					textArea_.setLineWrap(true);
					rdbtnmntmStatusBar.setEnabled(false);
					if (rdbtnmntmStatusBar.isSelected()) {
						statusTextPane.setVisible(false);
					}
					wordWrap = true;
				} else {
					textArea_.setLineWrap(false);
					rdbtnmntmStatusBar.setEnabled(true);
					if (rdbtnmntmStatusBar.isSelected()) {
						statusTextPane.setVisible(true);
					}
					wordWrap = false;
				}
			}
		});
		KeyStroke altW = KeyStroke.getKeyStroke("alt W");
		rdbtnmntmWordWrap.setAccelerator(altW);
		mnNewMenu.add(rdbtnmntmWordWrap);

		JSeparator separator_7 = new JSeparator();
		mnNewMenu.add(separator_7);

		mntmFont = new JMenuItem("Font");
		mntmFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!fontChooser.isVisible()) {
					int result = fontChooser.showDialog(frame);
					if (result == FontChooser.OK_OPTION) {
						Font font = fontChooser.getSelectedFont();
						JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
						JTextArea textArea_ = (JTextArea) viewport_.getView();
						textArea_.setFont(font);
					}
				} else {
					JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
					JTextArea textArea_ = (JTextArea) viewport_.getView();
					Font font = textArea_.getFont();
					fontChooser.setSelectedFont(font);
					int result = fontChooser.showDialog(frame);
					if (result == FontChooser.OK_OPTION) {
						font = fontChooser.getSelectedFont();
						textArea_.setFont(font);
					}
				}
			}
		});
		mnNewMenu.add(mntmFont);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		rdbtnmntmStatusBar = new JRadioButtonMenuItem("Status Bar");
		rdbtnmntmStatusBar.setSelected(true);
		rdbtnmntmStatusBar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (statusTextPane.isVisible())
					statusTextPane.setVisible(false);
				else
					statusTextPane.setVisible(true);
			}
		});
		KeyStroke altS = KeyStroke.getKeyStroke("alt S");
		rdbtnmntmStatusBar.setAccelerator(altS);
		mnView.add(rdbtnmntmStatusBar);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		
		/********************* Popup Menu *************************/
		popupMenu = new JPopupMenu();
		Action cutAction = new DefaultEditorKit.CutAction();
		cutAction.putValue(Action.NAME, "Cut");
		Action copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.NAME, "Copy");
		Action pasteAction = new DefaultEditorKit.PasteAction();
		pasteAction.putValue(Action.NAME, "Paste");
		pmNewTab = new JMenuItem("New tab");
		pmSaveFile = new JMenuItem("Save this tab");
		pmOpenFile = new JMenuItem("Open");
		pmCloseTab = new JMenuItem("Close this tab");
		pmCloseAll = new JMenuItem("Close All");
		pmExit = new JMenuItem("Exit");
		pmNewTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numberOfTabs = tabbedPane.getTabCount();
				if (numberOfTabs < 10) {
					JScrollPane scrollPane = new JScrollPane();
					JTextArea textArea = new JTextArea();
					scrollPane.setViewportView(textArea);
					textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
					textArea.setLineWrap(wordWrap);
					textArea.addKeyListener(new UpdateTextModifedListener());
					textArea.addCaretListener(new UpdateStatusCaretListener());
					textArea.addMouseListener(new MouseReleaseListener());
					if (numberOfTabs == 0) {
						tabbedPane.addTab("*New tab", null, scrollPane, null);
						tabbedPane.setVisible(true);
					} else
						tabbedPane.addTab("*New tab " + (numberOfTabs), null, scrollPane, null);
					tabbedPane.setSelectedIndex(numberOfTabs % 10);
					tabbedPane.setMnemonicAt(numberOfTabs, KeyEvent.VK_0 + numberOfTabs % 10);
					/************** Enable all the disable features ****************/
					enableEveryThing();
				}
			}
		});
		pmSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (tabbedPane.getTabCount() < 1) { // No tab currently run
						// popup info diaglog showed up
					} else {// current tab already isn't saved
						if (saveDir[tabbedPane.getSelectedIndex()] == null) {
							JViewport viewport = scrollPane.getViewport();
							JTextArea textArea = (JTextArea) viewport.getView();
							saveDir[tabbedPane.getSelectedIndex()] = saveFileAction(textArea);
						} else {// current tab already has saved
							File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
							BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
							JViewport viewport = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
							JTextArea textArea = (JTextArea) viewport.getView();
							outFile.write(textArea.getText());
							outFile.flush();
							outFile.close();
						}
					}
					isTextModified[tabbedPane.getSelectedIndex()] = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		pmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (tabbedPane.getTabCount() > 0) {
						JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
						JTextArea textArea_ = (JTextArea) viewport_.getView();
						textArea_.setLineWrap(wordWrap);
						if (textArea_.getText().isEmpty()) { // Replce current tab if no text inserted
							saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
							tabbedPane.setVisible(true);
							isTextModified[tabbedPane.getSelectedIndex()] = false;
						} else {
							if (saveDir[tabbedPane.getSelectedIndex()] == null) { // Ask for save
								int response = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to "
										+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
								if (response == JOptionPane.YES_OPTION) {
									saveFileAction(textArea_);
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);

								} else if (response == JOptionPane.NO_OPTION) {
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
								}

							} else { // Text has been changed, we need to save the new text.
								if (isTextModified[tabbedPane.getSelectedIndex()]) {
									int response = JOptionPane.showConfirmDialog(frame,
											"Do you want to save changes to "
													+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
									if (response == JOptionPane.YES_OPTION) {

										File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
										BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
										outFile.write(textArea_.getText());
										outFile.flush();
										outFile.close();

										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
									} else if (response == JOptionPane.NO_OPTION) {
										isTextModified[tabbedPane.getSelectedIndex()] = false;
										saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
									}

								} else { // No changes in text, no need to save again
									saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea_);
								}
							}
						}

					} else {
						JScrollPane scrollPane = new JScrollPane();
						JTextArea textArea = new JTextArea();
						textArea.setFont(new Font("Caliri", Font.PLAIN, 12));
						textArea.setLineWrap(wordWrap);
						scrollPane.setViewportView(textArea);
						tabbedPane.addTab("", null, scrollPane, null);
						tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
						textArea.addKeyListener(new UpdateTextModifedListener());
						textArea.addCaretListener(new UpdateStatusCaretListener());
						textArea.addMouseListener(new MouseReleaseListener());
						// Get the directory of the currently opened file
						saveDir[tabbedPane.getSelectedIndex()] = openFileAction(textArea);
						isTextModified[tabbedPane.getSelectedIndex()] = false;
					}
					/*****************************/
					enableEveryThing();
				} catch (Exception e1) {
					// Do nothing
				}
			}
		});
		pmCloseTab.addActionListener(new ActionListener() { 
			//this listener attached to every textArea, if tabCount <= 0 this won't appear
			public void actionPerformed(ActionEvent e) {
				JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
				JTextArea textArea_ = (JTextArea) viewport_.getView();
				if (textArea_.getText().isEmpty()) { // Delete if no text inserted
					saveDir[tabbedPane.getSelectedIndex()] = null;
					tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
				} else {
					boolean alwaysOnTop_lock = false;
					if (frame.isAlwaysOnTop()) {
						alwaysOnTop_lock = true;
						frame.setAlwaysOnTop(false);
					}
					/**********************************/
					if (saveDir[tabbedPane.getSelectedIndex()] == null) { // Ask for save
						try {
							int response = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to "
									+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
							if (response == JOptionPane.YES_OPTION) {
								saveFileAction(textArea_);
								isTextModified[tabbedPane.getSelectedIndex()] = false;
								saveDir[tabbedPane.getSelectedIndex()] = null;
								tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
							} else if (response == JOptionPane.NO_OPTION) {
								isTextModified[tabbedPane.getSelectedIndex()] = false;
								saveDir[tabbedPane.getSelectedIndex()] = null;
								tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else { // Text has been changed, we need to save the new text.
						if (isTextModified[tabbedPane.getSelectedIndex()]) {
							try {
								int response = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to "
										+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
								if (response == JOptionPane.YES_OPTION) {

									File file = new File(saveDir[tabbedPane.getSelectedIndex()]);
									BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
									outFile.write(textArea_.getText());
									outFile.flush();
									outFile.close();

									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = null;
									tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
								} else if (response == JOptionPane.NO_OPTION) {
									isTextModified[tabbedPane.getSelectedIndex()] = false;
									saveDir[tabbedPane.getSelectedIndex()] = null;
									tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
								}
							} catch (IOException e1) {
								// Do nothing
							}
						} else { // No changes in text, no need to save again
							saveDir[tabbedPane.getSelectedIndex()] = null;
							tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
						}
					}
					/**********************************/
					if (alwaysOnTop_lock)
						frame.setAlwaysOnTop(true);
					/********************* Request Focus on previous tab *********************/
					viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
					textArea_ = (JTextArea) viewport_.getView();
					textArea_.requestFocusInWindow();
					updateStatus(1, 1);
				}
				if (tabbedPane.getTabCount() == 0) {
					disableEveryThing();
					tabbedPane.setVisible(false);
				}
			}
		});
		pmCloseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableEveryThing();
			}
		});
		pmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		popupMenu.add(cutAction);
		popupMenu.add(copyAction);
		popupMenu.add(pasteAction);
		popupMenu.addSeparator();
		popupMenu.add(pmNewTab);
		popupMenu.add(pmSaveFile);
		popupMenu.add(pmOpenFile);
		popupMenu.addSeparator();
		popupMenu.add(pmCloseTab);
		popupMenu.add(pmCloseAll);
		popupMenu.add(pmExit);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		statusTextPane = new JTextPane();
		statusTextPane.setFont(new Font("Tahoma", Font.PLAIN, 12));
		menuBar.add(statusTextPane);
		statusTextPane.setDisabledTextColor(Color.BLACK);
		statusTextPane.setEditable(false);
		statusTextPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		updateStatus(1, 1);
	}

	/********************************************************************/

	class FindPanel extends JPanel {
		private static final long serialVersionUID = 7285325943932883548L;
		private JTextField findField;

		private boolean matchCase = false;
		private int index = 0; // index and pre_index will be set up in configIndex
		private int pre_index = 0;
		private int cursorI = 0; // index of current text index in selected tab's textArea
		private int mode = 2; // Up, Down, Whole File
		private int numberOfNewline = 0;

		public FindPanel() {

			ButtonGroup bg = new ButtonGroup();

			JButton btnFindNext = new JButton("Find Next");
			btnFindNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAction();
				}
			});
			btnFindNext.setBounds(333, 19, 97, 25);

			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAndReplaceDialog.setVisible(false);
				}
			});
			btnCancel.setBounds(333, 55, 97, 25);
			setLayout(null);

			JPanel Direction = new JPanel();
			Direction.setBorder(new TitledBorder(null, "", TitledBorder.CENTER, TitledBorder.TOP, null, null));
			Direction.setBounds(230, 103, 200, 45);
			Direction.setLayout(new BorderLayout(0, 0));

			JLabel lblDirection = new JLabel(" Direction:");
			Direction.add(lblDirection, BorderLayout.NORTH);

			JRadioButton rdbtnUp = new JRadioButton("Up    ");
			rdbtnUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 0;
				}
			});
			Direction.add(rdbtnUp, BorderLayout.WEST);

			JRadioButton rdbtnDown = new JRadioButton("Down");
			rdbtnDown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 1;
				}
			});
			Direction.add(rdbtnDown, BorderLayout.CENTER);

			JRadioButton rdbtnAllFile = new JRadioButton("Whole File");
			rdbtnAllFile.setSelected(true);
			rdbtnAllFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 2;
				}
			});
			Direction.add(rdbtnAllFile, BorderLayout.EAST);
			bg.add(rdbtnUp);
			bg.add(rdbtnDown);
			bg.add(rdbtnAllFile);

			add(Direction);
			add(btnFindNext);
			add(btnCancel);

			JCheckBox chckbxMatchCase = new JCheckBox("Match case");
			chckbxMatchCase.setBounds(6, 125, 97, 23);
			chckbxMatchCase.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (!matchCase)
						matchCase = true;
					else
						matchCase = false;
				}
			});
			add(chckbxMatchCase);

			JPanel panel = new JPanel();
			panel.setBounds(6, 19, 320, 23);
			add(panel);
			panel.setLayout(new BorderLayout(0, 0));

			JLabel lblFindWhat = new JLabel("Find what:     ");
			panel.add(lblFindWhat, BorderLayout.WEST);

			findField = new JTextField();
			panel.add(findField, BorderLayout.CENTER);
			findField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) { // If Enter is pressed, do find operation
					if (e.getKeyCode() == 10) {
						findAction();
					}
				}
			});
			findField.setColumns(10);
		}

		private void findAction() {
			JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
			JTextArea textArea_ = (JTextArea) viewport_.getView();
			String findText = findField.getText();
			String textArea = textArea_.getText();
			int textArea_length = textArea_.getText().length();

			if (!matchCase) {
				findText = findText.toLowerCase();
				textArea = textArea_.getText().toLowerCase();
			}

			if (index > -1)
				index = textArea.indexOf(findText, index + 1);
			else {
				// index <= -1
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText
						+ "\"\nEnd of file was reached\nAttempting to find \"" + findText + "\" again");
				index = textArea.indexOf(findText, 0);
			}

			if (index == -1 && mode != 2) { // findText NOT FOUND && not mode 2
				if (pre_index >= textArea_length)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText
							+ "\"\nEnd of file was reached\nAttempting to find \"" + findText + "\" again");
				else if (mode == 0 && pre_index < index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
				else if (mode == 1 && pre_index > index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nEnd of file was reached");
				pre_index = index;
				return;
			}

			if (mode == 0 && (index > cursorI || pre_index >= textArea_length)) { // Up
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
			} else if (mode == 1 && (index < cursorI || pre_index >= textArea_length)) { // Down
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nEnd of file was reached");
			} else if (mode == 2) { // Whole File
				if (index <= -1) {
					index = textArea.indexOf(findText, 0);
				}
				if (findText.length() >= Math.abs(index - pre_index)) {
					index = textArea.indexOf(findText, index + findText.length());
				}
			}

			numberOfNewline = strCount(textArea, "\r\n", index);
			index -= numberOfNewline;

			textArea_.setSelectionStart(index);
			textArea_.setSelectionEnd(index + findText.length());

			if (!(mode == 2 && pre_index > index))
				index += numberOfNewline;

			pre_index = index;

		}

		public void configIndex() {
			JViewport viewport = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
			JTextArea textArea_ = (JTextArea) viewport.getView();
			cursorI = textArea_.getText().length();

			if (textArea_.getSelectedText() != null)
				findField.setText(textArea_.getSelectedText());

			switch (mode) {
			case 0:
				index = 0;
				pre_index = index - 1;
				break;
			case 1:
				index = cursorI;
				pre_index = index - 1;
				break;
			case 2:
				index = cursorI - 1;
				pre_index = index - 1;
				break;
			}
		}

	}

	class ReplacePanel extends JPanel {
		private static final long serialVersionUID = -1144086439427313811L;
		private JTextField findField;
		private JTextField replaceField;

		private boolean matchCase = false;
		private int index = 0; // index and pre_index will be set up in configIndex
		private int pre_index = 0;
		private int cursorI = 0; // index of current text index in selected tab's textArea
		private int mode = 2; // Up, Down, Whole File
		private int numberOfNewline = 0;

		public ReplacePanel() {
			ButtonGroup bg = new ButtonGroup();

			JButton btnFindNext = new JButton("Find Next");
			btnFindNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAction();
				}
			});
			btnFindNext.setBounds(333, 19, 97, 25);

			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAndReplaceDialog.setVisible(false);
				}
			});
			btnCancel.setBounds(333, 123, 97, 25);
			setLayout(null);

			JPanel Direction = new JPanel();
			Direction.setBorder(new TitledBorder(null, "", TitledBorder.CENTER, TitledBorder.TOP, null, null));
			Direction.setBounds(127, 100, 200, 40);
			Direction.setLayout(new BorderLayout(0, 0));

			JLabel lblDirection = new JLabel(" Direction:     ");
			Direction.add(lblDirection, BorderLayout.NORTH);

			JRadioButton rdbtnUp = new JRadioButton("Up     ");
			rdbtnUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 0;
				}
			});
			Direction.add(rdbtnUp, BorderLayout.WEST);

			JRadioButton rdbtnDown = new JRadioButton("Down");
			rdbtnDown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 1;
				}
			});
			Direction.add(rdbtnDown, BorderLayout.CENTER);

			JRadioButton rdbtnWholeFile = new JRadioButton("Whole File");
			rdbtnWholeFile.setSelected(true);
			rdbtnWholeFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mode = 2;
				}
			});
			Direction.add(rdbtnWholeFile, BorderLayout.EAST);
			bg.add(rdbtnUp);
			bg.add(rdbtnDown);
			bg.add(rdbtnWholeFile);

			add(Direction);
			add(btnFindNext);
			add(btnCancel);

			JCheckBox chckbxMatchCase = new JCheckBox("Match case");
			chckbxMatchCase.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (!matchCase)
						matchCase = true;
					else
						matchCase = false;
				}
			});
			chckbxMatchCase.setBounds(6, 125, 97, 23);
			add(chckbxMatchCase);

			JPanel findTextPanel = new JPanel();
			findTextPanel.setBounds(6, 19, 320, 23);
			add(findTextPanel);
			findTextPanel.setLayout(new BorderLayout(0, 0));

			JLabel lblFindWhat = new JLabel("Find what:     ");
			findTextPanel.add(lblFindWhat, BorderLayout.WEST);

			findField = new JTextField();
			findField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) { // If Enter is pressed, do find
					if (e.getKeyCode() == 10) {
						findAction();
					}
				}
			});
			findTextPanel.add(findField, BorderLayout.CENTER);
			findField.setColumns(10);

			JPanel replaceTextPanel = new JPanel();
			replaceTextPanel.setBounds(6, 55, 320, 23);
			add(replaceTextPanel);
			replaceTextPanel.setLayout(new BorderLayout(0, 0));

			JLabel lblReplaceWith = new JLabel("Replace with:");
			replaceTextPanel.add(lblReplaceWith, BorderLayout.WEST);

			replaceField = new JTextField();
			replaceField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) { // If Enter is pressed, do find
					if (e.getKeyCode() == 10) {
						findAndReplaceAction();
					}
				}
			});
			replaceField.setColumns(10);
			replaceTextPanel.add(replaceField, BorderLayout.CENTER);

			JButton btnReplace = new JButton("Replace");
			btnReplace.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAndReplaceAction();
				}
			});
			btnReplace.setBounds(333, 55, 97, 25);
			add(btnReplace);

			JButton btnReplaceAll = new JButton("Replace All");
			btnReplaceAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
					JTextArea textArea_ = (JTextArea) viewport_.getView();
					String text = textArea_.getText();
					String findText = findField.getText();
					String replaceText = replaceField.getText();
					textArea_.setText(text.replace(findText, replaceText));
				}
			});
			btnReplaceAll.setBounds(333, 90, 97, 25);
			add(btnReplaceAll);
		}

		private void findAndReplaceAction() {
			JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
			JTextArea textArea_ = (JTextArea) viewport_.getView();
			String findText = findField.getText(); // target text to be replaced
			String replaceText = replaceField.getText(); // replacement text
			String textArea = textArea_.getText();
			int textArea_length = textArea_.getText().length();

			if (!matchCase) {
				findText = findText.toLowerCase();
				textArea = textArea_.getText().toLowerCase();
			}
			index = textArea.indexOf(findText, index);
			if (index == -1 && mode != 2) { // findText NOT FOUND && not mode 2
				if (pre_index >= textArea_length)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText
							+ "\"\nEnd of file was reached\nAttempting to find \"" + findText + "\" again");
				else if (mode == 0 && pre_index < index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
				else if (mode == 1 && pre_index > index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nEnd of file was reached");
				else
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"");
				pre_index = index;
				return;
			} else if (index != -1)
				index += findText.length();

			numberOfNewline = strCount(textArea, "\r\n", index);
			index -= numberOfNewline;
			if (mode == 0 && (index > cursorI || pre_index >= textArea_length)) { // Up
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
			} else if (mode == 1 && (index < cursorI || pre_index >= textArea_length)) { // Down
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Can't find \"" + findText + "\"\nEnd of file was reached");
			} else if (mode == 2 && pre_index > index && index <= -1) { // Whole File
				if (pre_index == cursorI - 2) { // DEBUG: pre_index == cursorI - 2 :the initial value of pre_index is
												// cursorI -2
					index = textArea.indexOf(findText, 0) + findText.length();
				} else if (index <= -1) {
					// the whole file is searched and replaced.
					JOptionPane.showMessageDialog(this, "Can't find \"" + findText + "\"\nEnd of file was reached");
					return;
				}
			}

			textArea_.setSelectionStart(index - findText.length());
			textArea_.setSelectionEnd(index);
			textArea_.replaceSelection(replaceText);
			index = textArea_.getSelectionEnd();
			textArea_.setSelectionStart(index - replaceText.length());
			textArea_.setSelectionEnd(index);

			if (!(mode == 2 && pre_index > index))
				index += numberOfNewline;
			if (index >= textArea_length - findText.length() && mode == 2) {// In mode2 ,if Out_of_length,
																			// reset index
				index = 0;
			} else
				pre_index = index;

		}

		private void findAction() {
			JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
			JTextArea textArea_ = (JTextArea) viewport_.getView();
			String findText = findField.getText();
			String textArea = textArea_.getText();
			int textArea_length = textArea_.getText().length();

			if (!matchCase) {
				findText = findText.toLowerCase();
				textArea = textArea_.getText().toLowerCase();
			}

			if (index > -1)
				index = textArea.indexOf(findText, index + 1);
			else {
				// index <= -1
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText
						+ "\"\nEnd of file was reached\nAttempting to find \"" + findText + "\" again");
				index = textArea.indexOf(findText, 0);
			}

			if (index == -1 && mode != 2) { // findText NOT FOUND && not mode 2
				if (pre_index >= textArea_length)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText
							+ "\"\nEnd of file was reached\nAttempting to find \"" + findText + "\" again");
				else if (mode == 0 && pre_index < index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
				else if (mode == 1 && pre_index > index)
					JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nEnd of file was reached");
				pre_index = index;
				index = 0;
				return;
			}

			if (mode == 0 && (index > cursorI || pre_index >= textArea_length)) { // Up
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nText cursor was reached");
			} else if (mode == 1 && (index < cursorI || pre_index >= textArea_length)) { // Down
				index = pre_index;
				JOptionPane.showMessageDialog(this, "Cannot find \"" + findText + "\"\nEnd of file was reached");
			} else if (mode == 2) { // Whole File
				if (index <= -1) {
					index = textArea.indexOf(findText, 0);
				}
				if (findText.length() >= Math.abs(index - pre_index)) {
					index = textArea.indexOf(findText, index + findText.length());
				}
			}

			numberOfNewline = strCount(textArea, "\r\n", index);
			index -= numberOfNewline;

			textArea_.setSelectionStart(index);
			textArea_.setSelectionEnd(index + findText.length());

			if (!(mode == 2 && pre_index > index))
				index += numberOfNewline;

			pre_index = index;

		}

		public void configIndex() {
			JViewport viewport = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
			JTextArea textArea_ = (JTextArea) viewport.getView();
			cursorI = textArea_.getText().length();

			if (textArea_.getSelectedText() != null)
				findField.setText(textArea_.getSelectedText());

			switch (mode) {
			case 0:
				index = 0;
				pre_index = index - 1;
				break;
			case 1:
				index = cursorI;
				pre_index = index - 1;
				break;
			case 2:
				index = cursorI - 1;
				pre_index = index - 1;
				break;
			}
		}
	}

	class FindAndReplaceDialog extends JDialog {
		private static final long serialVersionUID = -7102882142097239529L;
		private JTabbedPane dialogPanel = new JTabbedPane(JTabbedPane.TOP);
		private FindPanel findPanel = new FindPanel();
		private ReplacePanel replacePanel = new ReplacePanel();

		public FindAndReplaceDialog() {
			setTitle("Find & Replace");
			setPreferredSize(new Dimension(450, 215));
			setModalityType(ModalityType.APPLICATION_MODAL);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setResizable(false);
			setAlwaysOnTop(true);

			dialogPanel.addTab("Find", null, findPanel, null);
			dialogPanel.addTab("Replace", null, replacePanel, null);

			pack();
		}

		public void findConfigIndex() {
			findPanel.configIndex();
		}

		public void replaceConfigIndex() {
			replacePanel.configIndex();
		}

		public JTabbedPane getMainPanel() {
			return dialogPanel;
		}

		public void setSelectedTab(int s) { // 0: Find
											// 1: Replace
			dialogPanel.setSelectedIndex(s);
		}

		public void getSelectedTab() {// 0: Find, 1: Replace
			dialogPanel.getSelectedIndex();
		}
	}

	class UpdateStatusCaretListener implements CaretListener {
		public void caretUpdate(CaretEvent e) {
			JTextArea editArea = (JTextArea) e.getSource();
			int linenum = 1;
			int columnnum = 1;
			try {
				int caretpos = editArea.getCaretPosition();
				linenum = editArea.getLineOfOffset(caretpos);
				columnnum = 1 + caretpos - editArea.getLineStartOffset(linenum);
				linenum += 1;
			} catch (Exception ex) {
				// Do nothing
			}
			updateStatus(linenum, columnnum);
		}
	}

	class UpdateTextModifedListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			if ((e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a') || (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A')
					|| (e.getKeyChar() <= '9' && e.getKeyChar() >= '0')
					|| (e.getKeyChar() == '.' || e.getKeyChar() == ',' || e.getKeyChar() == ';'
							|| e.getKeyChar() == ':')
					|| e.getKeyChar() == '\'' || e.getKeyChar() == '\"' || e.getKeyChar() == '{'
					|| e.getKeyChar() == '[' || e.getKeyChar() == ']' || e.getKeyChar() == '}' || e.getKeyChar() == '|'
					|| e.getKeyChar() == '\\' || e.getKeyChar() == '-' || e.getKeyChar() == '_' || e.getKeyChar() == '+'
					|| e.getKeyChar() == '=' || e.getKeyChar() == '/' || e.getKeyChar() == '*' || e.getKeyChar() == '~'
					|| e.getKeyChar() == '!' || e.getKeyChar() == '@' || e.getKeyChar() == '#' || e.getKeyChar() == '$'
					|| e.getKeyChar() == '%' || e.getKeyChar() == '^' || e.getKeyChar() == '&' || e.getKeyChar() == '('
					|| e.getKeyChar() == ')') {
				isTextModified[tabbedPane.getSelectedIndex()] = true;
			}
		}
	}

	class MouseReleaseListener extends MouseAdapter {
		public void mouseReleased(MouseEvent Me) {
			if (Me.isPopupTrigger()) {
				popupMenu.show(Me.getComponent(), Me.getX(), Me.getY());
			}

		}
	}

	/********************************************************************/
	/**
	 * update status operation
	 */
	private void updateStatus(int linenumber, int columnnumber) {
		statusTextPane.setText("Line: " + linenumber + " Column: " + columnnumber);
	}

	/**
	 * open file operation
	 */
	private String openFileAction(JTextArea textArea) throws IOException {
		String dir = null;
		File file;
		BufferedReader inputFile = null;
		FileReader inputReader = null;
		JFileChooser openwindow = new JFileChooser();

		openwindow.setDialogTitle("Open");
		openwindow.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		openwindow.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		openwindow.setAcceptAllFileFilterUsed(false);

		if (openwindow.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			if (openwindow.getSelectedFile().isFile()) {
				file = openwindow.getSelectedFile();
				dir = file.getPath();
				inputReader = new FileReader(dir);
				inputFile = new BufferedReader(inputReader);

				JViewport viewport_ = ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport();
				JTextArea textArea_ = (JTextArea) viewport_.getView();
				tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());

				int eachChar;
				String wholeText = "";
				while ((eachChar = inputFile.read()) != -1) {
					wholeText += (char) eachChar;
				}
				textArea_.setText(wholeText);
			} else {
				JOptionPane.showMessageDialog(frame, "No file specified");
			}
		}

		return dir;
	}

	/**
	 * save file operation
	 */
	private String saveFileAction(JTextArea textArea) throws IOException {
		File file;
		String dir = null;
		BufferedWriter outputFile = null;
		String[] fileNameExtensionList = { ".txt" };

		JFileChooser savewindow = new JFileChooser();
		savewindow.setDialogTitle("Save As");
		savewindow.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter textfilter1 = new FileNameExtensionFilter(".txt", "txt");
		savewindow.addChoosableFileFilter(textfilter1);
		savewindow.setAcceptAllFileFilterUsed(false);
		if (savewindow.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) { // Save button pressed
			if (savewindow.getSelectedFile().isFile()) {
				file = savewindow.getSelectedFile();
				boolean alwaysOnTop_lock = false;
				if (frame.isAlwaysOnTop()) {
					alwaysOnTop_lock = true;
					frame.setAlwaysOnTop(false);
				}
				int response = JOptionPane.showConfirmDialog(frame,
						file.getName() + " is already exists.\nDo you want to replace it?");
				if (response == JOptionPane.YES_OPTION) {
					tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
					dir = file.getPath();// get the path of the current tab
					outputFile = new BufferedWriter(new FileWriter(file));
					outputFile.write(textArea.getText());
				}
				if (alwaysOnTop_lock)
					frame.setAlwaysOnTop(true);

			} else {
				file = savewindow.getSelectedFile();
				if (checkInvalid(file.toString(), fileNameExtensionList)) { // if the selected file doesn't end with
																			// any
																			// extension file name in the list,
					file = new File(savewindow.getSelectedFile().getPath() + ".txt"); // this will add .txt to the
																						// selected file.
					file.createNewFile();
					tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
					dir = file.getPath();// get the path of the current tab
					outputFile = new BufferedWriter(new FileWriter(file));
					outputFile.write(textArea.getText());
				}
			}

			outputFile.flush();
			outputFile.close();
		}

		return dir;
	}

	private void disableEveryThing() {
		/***************
		 * Remove saveDir, disable Goto, Find, Replace, status bar, Save, Save As
		 ***************/
		mntmPaste.setEnabled(false);
		mntmCut.setEnabled(false);
		mntmCopy.setEnabled(false);
		mntmRedo.setEnabled(false);
		mntmUndo.setEnabled(false);
		mntmDelete.setEnabled(false);
		mntmPrint.setEnabled(false);
		mntmSaveAs.setEnabled(false);
		mntmSaveFile.setEnabled(false);
		mntmGoTo.setEnabled(false);
		mntmFind.setEnabled(false);
		mntmReplace.setEnabled(false);
		mntmTimedate.setEnabled(false);
		mntmSelecteAll.setEnabled(false);
		mntmFont.setEnabled(false);
		rdbtnmntmWordWrap.setEnabled(false);
		rdbtnmntmStatusBar.setEnabled(false);
		/************* Set Status Bar invicible *****************/
		statusTextPane.setVisible(false);
		/************** Delete save directory list **************/
		for (int i = 9; i >= 0; i--)
			saveDir[i] = null;
		/******************** Delete all tab ********************/
		tabbedPane.removeAll();
		tabbedPane.setVisible(false);
	}

	private void enableEveryThing() {
		mntmPaste.setEnabled(true);
		mntmCut.setEnabled(true);
		mntmCopy.setEnabled(true);
		mntmRedo.setEnabled(true);
		mntmUndo.setEnabled(true);
		mntmDelete.setEnabled(true);
		mntmPrint.setEnabled(true);
		mntmSaveAs.setEnabled(true);
		mntmSaveFile.setEnabled(true);
		mntmGoTo.setEnabled(true);
		mntmFind.setEnabled(true);
		mntmReplace.setEnabled(true);
		mntmTimedate.setEnabled(true);
		mntmSelecteAll.setEnabled(true);
		mntmFont.setEnabled(true);
		rdbtnmntmWordWrap.setEnabled(true);
		rdbtnmntmStatusBar.setEnabled(true);

		statusTextPane.setVisible(rdbtnmntmStatusBar.isSelected());
	}

	private boolean checkInvalid(String str, String[] substr) {
		int i = substr.length - 1;
		int count = 0;
		while (i >= 0) {
			if (!str.endsWith(substr[i]))
				count++;
			i--;
		}
		return (count == substr.length) ? true : false;
	}

	private int strCount(String str, String substr, int toIndex) {
		// count the times substr appear in str, return 0 if str doesn't contain substr
		int count_ = 0;
		int index = 0;
		do {
			index = str.indexOf(substr, index) + substr.length();
			if (index == substr.length() - 1 || (index > toIndex && toIndex != -1))
				return count_;
			count_++;
		} while (true);
	}
}
