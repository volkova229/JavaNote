package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/** 
 * @author Aleksandra Jovanovic aleksandra.volkov229@gmail.com
 * @version 1.0
 * @since 1.0
 */

class TextEditor extends JFrame {
  
  static JMenuBar menubar;
  static JMenu file, edit, format, view, help, theme;
  static JMenuItem makeNew, open, save, saveAs, exit;
  static JMenuItem undo, redo, cut, copy, paste, delete, find, findNext, replace, goTo, selectAll, timeDate;
  static JMenuItem undo2, cut2, copy2, paste2, delete2, selectAll2;
  static JMenuItem font, viewHelp, about;
  static JCheckBoxMenuItem statusBar, wordWrap;
  static JPanel panel, bottomPanel;
  static JTextArea textArea;
   static JPopupMenu popup;
  static JLabel lnColLabel;
  
  private Color[] cols = new Color[2];
  
  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  
  private static File prefs = new File("com\\am\\texteditor\\prefs.txt");
  
  private Container c = this.getContentPane();
  
  private boolean selectedAll = false;
  private boolean saved = false;
  static boolean firstTime = true;
  private boolean changeOccured = false;
  
  private String path;
  static String fileName = "Untitled";
  private String cuttedString = "";
  
  static FindDialog findDialog;
  static ReplaceDialog replaceDialog;
  static GoToDialog goToDialog;
  
  static Caret caret;
  private UndoManager manager = new UndoManager();
  private ButtonListener l = new ButtonListener();
  
  private TextEditor t = this;
  
  public static String currentTheme = "w";
  
  public TextEditor() {
    makeUI();
    manager.setLimit(1000);
    cols = checkTheme();
    makeMenu();
    addTextField();
    addPopup();
    addKeyBindings();
    addListeners();
    ThemeChange.change(this, "w");
    this.enableDragAndDrop();
    try {
      loadPrefs();
    } catch ( FileNotFoundException fnf ) {
      fnf.printStackTrace();
    } catch ( NumberFormatException nfe ) {
      nfe.printStackTrace();
    } catch ( IllegalArgumentException iae ) {
      iae.printStackTrace();
    }catch ( IOException io ) {
      io.printStackTrace();
    }
    this.setVisible(true);
  }
  
  private void makeUI() {
    this.setTitle(fileName + " - Javapad");
    this.setSize(1400, 600);
    this.setResizable(true);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setIconImage(new ImageIcon("com\\am\\texteditor\\soyka.png").getImage());
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        try {
          savePrefs();
        } catch ( FileNotFoundException fnfe ) {
          fnfe.printStackTrace();
        } catch ( IllegalArgumentException iae ) {
          iae.printStackTrace();
        }catch ( IOException io ) {
          io.printStackTrace();
        } 
        if ( firstTime )
          System.exit(0);
        else if ( changeOccured ) {
          playSound("com\\am\\texteditor\\kitty.wav");
          cols = checkTheme();
          ConfirmSaveDialog d = new ConfirmSaveDialog(t, cols[0], cols[1]);
          int r = d.displaySaveDialog();
          if ( r == ConfirmSaveDialog.SAVE_OPTION ) {
            save();
            System.exit(0);
          }
          else if ( r == ConfirmSaveDialog.DONT_SAVE_OPTION )
            System.exit(0);
        }
        else {
          System.exit(0);
        }
      }
    });
  }
  
  private Color[] checkTheme() {
    switch ( currentTheme ) {
      case "w":
        cols[0] = ThemeChange.w;
        cols[1] = ThemeChange.grey;
        break;
      case "b":
        cols[0] = ThemeChange.b;
        cols[1] = ThemeChange.w;
        break;
      case "y":
        cols[0] = ThemeChange.y;
        cols[1] = ThemeChange.gold;
        break;
    }
    return cols;
  }
  
  private void loadPrefs() throws IOException, FileNotFoundException, NumberFormatException, IllegalArgumentException {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(prefs));
      String[] s = br.readLine().split(",");
      int width = Integer.parseInt(s[0].trim());
      int height = Integer.parseInt(s[1].trim());
      s = br.readLine().split(",");
      double locWidth = Double.parseDouble(s[0].trim());
      double locHeight = Double.parseDouble(s[1].trim());
      s = br.readLine().split(",");
      Font f = new Font(s[0].trim(), Integer.parseInt(s[1].trim()), Integer.parseInt(s[2].trim()));
      s = br.readLine().split(",");
      boolean wrap = s[0].trim().equals("true");
      boolean status = s[1].trim().equals("true");
      this.setSize(width, height);
      this.setLocation((int)locWidth, (int)locHeight);
      textArea.setFont(f);
      if ( wrap ) 
        enableWordWrap();
      else 
        disableWordWrap();
      if ( status ) 
        enableStatusBar();
      else 
        disableStatusBar();
    } finally {
      if ( br != null ) {
        try {
          br.close();
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void savePrefs() throws FileNotFoundException, IOException, IllegalArgumentException {
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new BufferedWriter(new FileWriter(prefs)));
      pw.println(this.getWidth() + ", " + this.getHeight());
      pw.println(this.getLocation().getX() + ", " + this.getLocation().getY());
      Font f = textArea.getFont();
      pw.println(f.getName() + ", " + f.getStyle() + ", " + f.getSize());
      pw.println(wordWrap.isSelected() + ", " + statusBar.isSelected());
    } finally {
      if ( pw != null ) {
        try {
          pw.close();
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void makeMenu() {
    
    menubar = new JMenuBar();
    file = new JMenu("File");
    edit = new JMenu("Edit");
    format = new JMenu("Format");
    view = new JMenu("View");
    help = new JMenu("Help");
    theme = new JMenu("Theme");
    theme.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println(cols[1]);
        new ThemeChangeDialog(t);
      }
    });
    
    makeNew = new JMenuItem("New");  
    open = new JMenuItem("Open...");
    save = new JMenuItem("Save");
    saveAs = new JMenuItem("Save as...");
    saveAs.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cols[1]));
    exit = new JMenuItem("Exit");
    
    t.setJMenuBar(menubar);
    menubar.add(file);
    menubar.add(edit);
    menubar.add(format);
    menubar.add(view);
    menubar.add(help);
    menubar.add(theme);
    
    file.add(makeNew);
    file.add(open);
    file.add(save);
    file.add(saveAs);
    file.add(exit);
    
    undo = new JMenuItem("Undo");
    undo.setEnabled(false);
    redo = new JMenuItem("Redo");
    redo.setEnabled(false);
    cut = new JMenuItem(new DefaultEditorKit.CutAction());
    cut.setText("Cut");
    cut.setEnabled(false);
    cut.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, cols[1]));
    copy = new JMenuItem(new DefaultEditorKit.CopyAction());
    copy.setText("Copy");
    copy.setEnabled(false);
    paste = new JMenuItem(new DefaultEditorKit.PasteAction());
    paste.setText("Paste");
    delete = new JMenuItem("Delete");
    delete.setEnabled(false);
    delete.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cols[1]));
    find = new JMenuItem("Find...");
    find.setEnabled(false);
    findNext = new JMenuItem("Find Next");
    findNext.setEnabled(false);
    replace = new JMenuItem("Replace...");
    goTo = new JMenuItem("Go To...");
    goTo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cols[1]));
    selectAll = new JMenuItem("Select All");
    selectAll.setEnabled(false);
    timeDate = new JMenuItem("Time/Date");
    
    edit.add(undo);
    edit.add(redo);
    edit.add(cut);
    edit.add(copy);
    edit.add(paste);
    edit.add(delete);
    edit.add(find);
    edit.add(findNext);
    edit.add(replace);
    edit.add(goTo);
    edit.add(selectAll);
    edit.add(timeDate);
    
    statusBar = new JCheckBoxMenuItem("Status Bar");
    statusBar.setSelected(true);
    
    view.add(statusBar);
    
    wordWrap = new JCheckBoxMenuItem("Word Wrap");
    wordWrap.setSelected(false);
    font = new JMenuItem("Font...");
    
    format.add(wordWrap);
    format.add(font);
    
    viewHelp = new JMenuItem("View Help");
    viewHelp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cols[1]));
    about = new JMenuItem("About Javapad");
    help.add(viewHelp);
    help.add(about);
    
  }
  
  
  private void addKeyBindings() {
    makeNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    findNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
    replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    undo2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    cut2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    copy2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    paste2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    selectAll2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
  }
  
  private void addListeners() {
    makeNew.addActionListener(l);
    open.addActionListener(l);
    save.addActionListener(l);
    saveAs.addActionListener(l);
    exit.addActionListener(l);
    undo.addActionListener(l);
    redo.addActionListener(l);
    delete.addActionListener(l);
    find.addActionListener(l);
    findNext.addActionListener(l);
    replace.addActionListener(l);
    goTo.addActionListener(l);
    selectAll.addActionListener(l);
    timeDate.addActionListener(l);
    wordWrap.addActionListener(l);
    font.addActionListener(l);
    statusBar.addActionListener(l);
    viewHelp.addActionListener(l);
    about.addActionListener(l);
    undo2.addActionListener(l);
    delete2.addActionListener(l);
    selectAll2.addActionListener(l);
    
  }
  
  private void addTextField() {
    panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    textArea = new JTextArea(1, 1);
    textArea.setFont(new Font("MONOSPACED", 0, 18));
    textArea.setSelectionColor(new Color(164, 211, 238));
    caret = textArea.getCaret();
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    this.add(scrollPane);
    
    lnColLabel = new JLabel("Ln " + 1 + ", Col " + 1);
    lnColLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
    
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
    bottomPanel.add(lnColLabel);
    
    c.add(bottomPanel, BorderLayout.SOUTH);
    
    textArea.getDocument().addDocumentListener(new DocListener());
    
    textArea.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
          popup.show(e.getComponent(), e.getX(), e.getY());
          updateButtons();
        }
      }
    });
    textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
      @Override
      public void undoableEditHappened(UndoableEditEvent e) {
        manager.addEdit(e.getEdit());
        updateButtons();
      }
    });
    
    textArea.addCaretListener(new CaretListener() {
      @Override
      public void caretUpdate(CaretEvent e) {
    //    if ( findDialog == null ) {
    //      textArea.getHighlighter().removeAllHighlights();
          
    //    }
        if ( textArea.getCaret().getMark() != textArea.getCaret().getDot() ) {
          delete.setEnabled(true);
          cut.setEnabled(true);
          copy.setEnabled(true); 
          delete2.setEnabled(true);
          cut2.setEnabled(true);
          copy2.setEnabled(true);
          if ( textArea.getCaret().getDot() == textArea.getDocument().getLength()) {
            selectedAll = true;
          }if ( textArea.getCaret().getMark() == textArea.getDocument().getLength()) {
            selectedAll = true;
          }
          if ( selectedAll ) {
            selectAll.setEnabled(false);
            selectAll2.setEnabled(false);
          }
        }
        else {
          delete.setEnabled(false);
          delete2.setEnabled(false);
          cut.setEnabled(false);
          copy.setEnabled(false); 
          cut2.setEnabled(false);
          copy2.setEnabled(false);
          if ( textArea.getDocument().getLength() != 0 ) {
            selectAll.setEnabled(true);
            selectAll2.setEnabled(true);
          }
          selectedAll = false;
        }
        lnColLabel.setText("Ln " + RXTextUtilities.getLineAtCaret(textArea) + 
                           ", Col " + countCols());
      }
    });
  }
  
  private void addPopup() {
    popup = new JPopupMenu();
    undo2 = new JMenuItem("Undo");
    undo2.setEnabled(manager.canUndo());
    cut2 = new JMenuItem(new DefaultEditorKit.CutAction());
    cut2.setText("Cut");
    cut2.setEnabled(false);
    copy2 = new JMenuItem(new DefaultEditorKit.CopyAction());
    copy2.setText("Copy");
    copy2.setEnabled(false);
    paste2 = new JMenuItem(new DefaultEditorKit.PasteAction());
    paste2.setText("Paste");
    delete2 = new JMenuItem("Delete");
    delete2.setEnabled(false);
    selectAll2 = new JMenuItem("Select All");
    selectAll2.setEnabled(false);
    
    popup.add(undo2);
    popup.add(cut2);
    popup.add(copy2);
    popup.add(paste2);
    popup.add(delete2);
    popup.add(selectAll2);
    
  }
  
  private void updateButtons() {
    undo.setEnabled(manager.canUndo());
    undo2.setEnabled(manager.canUndo());
    redo.setEnabled(manager.canRedo());
  }
  
  void newDoc() {
    saved = false;
    fileName = "Untitled";
    this.setTitle(fileName + " - Javapad");
    textArea.setText("");
    firstTime = true;
  }
  
  void open() {
    FileFilter filter = new FileNameExtensionFilter("Text file", "txt");
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(filter);
    chooser.setAcceptAllFileFilterUsed(false);
    if ( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
      textArea.setText("");
      String openPath = chooser.getSelectedFile().getAbsolutePath();
      String openFileName = chooser.getSelectedFile().getName();
      File f = new File(openPath);
      if ( f.exists() && f.canRead() ) {
        BufferedReader br = null;
        try {
          br = new BufferedReader(new FileReader(openPath));
          String line;
          while ( ( line = br.readLine() ) != null ) {
            System.out.println(line);
            textArea.append(line + "\n");
          }
          firstTime = false;
          saved = true;
          changeOccured = false;
          fileName = chooser.getSelectedFile().getName();
          t.setTitle(fileName + " - Javapad");
        } catch ( IOException io ) {
          io.printStackTrace();
        } finally {
          if ( br != null ) {
            try {
              br.close();
            } catch ( Exception e ) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
  
  void save() {
    if ( (!saved ) || firstTime) {
      PrintWriter pw = null;
      JFileChooser chooser = new JFileChooser();
      FileFilter filter = new FileNameExtensionFilter("Text file", "txt");
      chooser.setFileFilter(filter);
      chooser.setAcceptAllFileFilterUsed(false);
      if ( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
        path = chooser.getSelectedFile().getAbsolutePath();
        if ( !path.endsWith(".txt") )
          path += ".txt";
        fileName = chooser.getSelectedFile().getName();
        if ( !fileName.endsWith(".txt") )
          fileName += ".txt";
        try {
          pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
          String q = textArea.getText();
          
          String[] arr = q.split("\n");
          for ( int i = 0; i < arr.length; i++ ) {
            pw.println(arr[i]);
          }
          File f = new File(path);
          f.createNewFile();
          saved = true;
          firstTime = false;
          this.setTitle(fileName + " - Javapad");
        } catch ( IOException io ) {
          io.printStackTrace();
        } finally {
          try {
            pw.close();
          } catch ( Exception io ) {
            io.printStackTrace();
          }
        }
      }
      else
        saved = false;
    }
    else if ( saved ) {
      PrintWriter pw = null;
      try {
        pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        String q = textArea.getText();
        String[] arr = q.split("\n");
        for ( int i = 0; i < arr.length; i++ ) {
          pw.println(arr[i]);
        }
      } catch ( IOException io ) {
        io.printStackTrace();
      } finally {
        if ( pw != null ) {
          try {
           pw.close();
          } catch ( Exception io ) {
            io.printStackTrace();
          }
        }
      }
    }
    changeOccured = false;
  }
  
  private void saveAs() {
    saved = false;
    save();
  }
  
  static void delete() {
    if ( textArea.getSelectedText() != null ) {
      try {
        textArea.getDocument().remove(textArea.getSelectionStart(), 
                                      textArea.getSelectionEnd() - textArea.getSelectionStart());
      } catch ( BadLocationException e ) {
        e.printStackTrace();
      }
    }
  }
  
  private void selectAll() {
    if ( textArea.getDocument().getLength() != 0 ) {
      textArea.select(0, textArea.getDocument().getLength());
      selectedAll = true;
      selectAll.setEnabled(false);
      selectAll2.setEnabled(false);
    }
  }
  
  private void timeAndDate() {
    Date d = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("E dd.MM.yyyy 'at' hh:mm:ss", Locale.getDefault());
    if ( textArea.getCaret().getDot() != textArea.getCaret().getMark() ) {
      textArea.replaceRange(ft.format(d), textArea.getSelectionStart(), textArea.getSelectionEnd());
    }
    else
      textArea.insert(ft.format(d), textArea.getCaret().getDot());
  }
  
  private void enableWordWrap() {
    wordWrap.setSelected(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    statusBar.setSelected(false);
    statusBar.setEnabled(false); 
    bottomPanel.remove(lnColLabel);
    bottomPanel.validate();
    bottomPanel.repaint();
    c.remove(bottomPanel);
    c.validate();
    c.repaint();
    goTo.setEnabled(false);
  }
  
  private void disableWordWrap() {
    textArea.setLineWrap(false);
    textArea.setWrapStyleWord(false);
    statusBar.setSelected(true);
    statusBar.setEnabled(true);
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
    bottomPanel.setBackground(textArea.getBackground());
    bottomPanel.add(lnColLabel);
    c.add(bottomPanel, BorderLayout.SOUTH);
    c.validate();
    c.repaint();
    goTo.setEnabled(true);
  }
  
  private void disableStatusBar() {
    statusBar.setSelected(false);
    bottomPanel.remove(lnColLabel);
    bottomPanel.validate();
    bottomPanel.repaint();
    c.remove(bottomPanel);
    c.validate();
    c.repaint();
  }
  
  private void enableStatusBar() {
    statusBar.setSelected(true);
    bottomPanel.add(lnColLabel);
    c.add(bottomPanel, BorderLayout.SOUTH);
    c.validate();
    c.repaint();
  }
  
  private class ButtonListener implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
      JMenuItem b = (JMenuItem)e.getSource();
      if ( b.equals(makeNew) ) {
        if ( firstTime ) 
          newDoc();
        else if ( changeOccured ) {
         cols = checkTheme();
          ConfirmSaveDialog d = new ConfirmSaveDialog(t, cols[0], cols[1]);
          int r = d.displaySaveDialog();
          if ( r == ConfirmSaveDialog.SAVE_OPTION ) {
            save();
            newDoc();
          }
          else if ( r == ConfirmSaveDialog.DONT_SAVE_OPTION) 
            newDoc();
        }
        else if ( !changeOccured ) 
          newDoc();
      }
      else if ( b.equals(open) ) {
        if ( firstTime )
          open();
        else if ( changeOccured ) {
          cols = checkTheme();
          ConfirmSaveDialog d = new ConfirmSaveDialog(t, cols[0], cols[1]);
          int r = d.displaySaveDialog();
          if ( r == ConfirmSaveDialog.SAVE_OPTION ) {
            save();
            open();
          } else if ( r == ConfirmSaveDialog.DONT_SAVE_OPTION ) 
            open();
        }
        else
          open();
      }
      else if ( b.equals(save) ) {
        save();
      }
      else if ( b.equals(saveAs) ) {
        saveAs();
      }
      else if ( b.equals(exit) ) { 
        try {
          savePrefs();
        } catch ( FileNotFoundException fnfe ) {
          fnfe.printStackTrace();
        } catch ( IllegalArgumentException iae ) {
          iae.printStackTrace();
        }catch ( IOException io ) {
          io.printStackTrace();
        } 
        if ( firstTime )
          System.exit(0);
        else if ( changeOccured ) {
         cols = checkTheme();
          ConfirmSaveDialog d = new ConfirmSaveDialog(t, cols[0], cols[1]);
          int r = d.displaySaveDialog();
          if ( r == ConfirmSaveDialog.SAVE_OPTION ) {
            save();
            System.exit(0);
          } else if ( r == ConfirmSaveDialog.DONT_SAVE_OPTION ) 
            System.exit(0);
        }
        else
          System.exit(0);
      }
      else if ( b.equals(undo) ) {
        try {
          manager.undo();
        } catch ( CannotUndoException ue ) {
          ue.printStackTrace();
        }
      }
      else if ( b.equals(redo) ) {
        try {
          manager.redo();
        } catch ( CannotRedoException re ) {
          re.printStackTrace();
        }
      }
      else if ( b.equals(delete) ) {
        delete();
      }
      else if ( b.equals(find) ) {
        if ( replaceDialog == null ) {
          cols = checkTheme();
          findDialog = new FindDialog(t, cols[0], cols[1]);
        }
      }
      else if ( b.equals(findNext) ) {
        ///////////////////////////////////////////////////////////////////
      }
      else if (b.equals(replace) ) {
        if ( findDialog == null ) {
          cols = checkTheme();
          replaceDialog = new ReplaceDialog(t, cols[0], cols[1]);
        }
      }
      else if ( b.equals(goTo) ) {
        cols = checkTheme();
        if ( goToDialog == null ) {
          goToDialog = new GoToDialog(t, cols[0], cols[1]);
          textArea.setCaretPosition(textArea.getCaretPosition());
        }
      }
      else if ( b.equals(selectAll) ) {
        selectAll();
        selectedAll = true;
      }
      else if ( b.equals(timeDate) ) {
        timeAndDate();
      }
      else if ( b.equals(statusBar) ) {
        if ( !statusBar.isSelected() ) {
         disableStatusBar();
        }
        else if ( statusBar.isSelected() ) {
          enableStatusBar();
        }
      }
      else if ( b.equals(wordWrap) ) {
        if ( wordWrap.isSelected() ) {
          enableWordWrap();
        }
        else if ( !wordWrap.isSelected() ) {
          disableWordWrap();
        }
      }
      else if ( b.equals(font) ) {
        cols = checkTheme();
        new FontDialog(t, cols[0], cols[1]);
      }
      else if ( b.equals(undo2) ) {
        try {
          manager.undo();
        } catch ( CannotUndoException ue ) {
          
        }
      }
      else if ( b.equals(viewHelp) ) {
        cols = checkTheme();
        new HelpDialog(t, cols[0], cols[1]);
      }
      else if ( b.equals(about) ) {
        new AboutDialog(t);
      }
      else if ( b.equals(delete2) ) {
        delete();
        delete.setEnabled(false);
        delete2.setEnabled(false);
      }
      else if ( b.equals(selectAll2) ) {
        selectAll();
      }
    }
  }
  
  
  private void enableDragAndDrop() {
    
    DropTarget target = new DropTarget(textArea, new DropTargetAdapter() {
      @Override
      public void drop(DropTargetDropEvent e) { 
        if ( changeOccured ) {
          cols = checkTheme();
          ConfirmSaveDialog csd = new ConfirmSaveDialog(t, cols[0], cols[1]);
          int r = csd.displaySaveDialog();
          if ( r == ConfirmSaveDialog.SAVE_OPTION ) {
            save();
            newDoc();
          } else if ( r == ConfirmSaveDialog.DONT_SAVE_OPTION ) {
            newDoc();
          }
          else if ( r == ConfirmSaveDialog.CANCEL_OPTION )
            return;
        }
        try {
          e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
          java.util.List list = (java.util.List)e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
          File file = (File)list.get(0);
          if ( !file.getName().endsWith(".txt") ) {
            return;
          }
          if ( file.exists() && file.canRead() ) {
            BufferedReader br = null;
            try {          
              br = new BufferedReader(new FileReader(file));
              textArea.read(br, ".txt");
              path = file.getAbsolutePath();
              firstTime = false;
              saved = true;
              changeOccured = false;
              fileName = file.getName();
              t.setTitle(file.getName() + " - Javapad");
              e.dropComplete(true);
            } catch ( IOException io ) {
              e.dropComplete(false);
              io.printStackTrace();
            } finally {
              if ( br != null ) {
                try {
                  br.close();
                } catch ( Exception ex ) {
                  ex.printStackTrace();
                }
              }
            }
          }
        } catch ( Exception ex ) {
          ex.printStackTrace();
        } textArea.getDocument().addDocumentListener(new DocListener()); 
      }
    });
  }
  
  private class DocListener implements DocumentListener {
    @Override
    public void changedUpdate(DocumentEvent e) {
      
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
      find.setEnabled(true);
      findNext.setEnabled(true);
      changeOccured = true;     
      firstTime = false;
      updateButtons();
      
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
      if ( textArea.getDocument().getLength() == 0 ) {
        cut.setEnabled(false);
        copy.setEnabled(false);
        find.setEnabled(false);
        findNext.setEnabled(false);
        cut2.setEnabled(false);
        copy2.setEnabled(false);
        find.setEnabled(false);
        findNext.setEnabled(false);
        firstTime = false;
        selectAll.setEnabled(!selectedAll);
        selectAll2.setEnabled(!selectedAll);
      }
      updateButtons();
      changeOccured = true;
    }
  }
  
  private int countCols() {
    int cols = 1;
    String s = textArea.getText();
    for ( int i = 0; i < textArea.getCaretPosition(); i++ ) {
      if ( s.charAt(i) != '\n' ) 
        cols += 1;
      else if ( s.charAt(i) == '\n' )
        cols = 1;
    }
    return cols;
  }
  
  public void playSound(String soundName) {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  
  public static void main(String[] args) {
   TextEditor ttt =  new TextEditor();
  }
  
  
}