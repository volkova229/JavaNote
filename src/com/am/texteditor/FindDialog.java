package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.TitledBorder;


class FindDialog extends JDialog {
  
  CustomButton findNext, cancel;
  JPanel upper, bottom, radioPanel;
  JLabel findWhatLabel, direction;
  JTextField findWhat;
  JCheckBox matchCase;
  JRadioButton up, down;
  private FindDialog fd = this;
  private TextEditor t;
  private String toFind = "";
  private static FindDialog findd = null;
  
  private boolean posDown, posUp = false;
  
  private static JOptionPane pane = null;
  
  private static Highlighter highlighter = TextEditor.textArea.getHighlighter();
  private static HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(164, 211, 238));
  
  private long eventMask = AWTEvent.KEY_EVENT_MASK;
  private AWTListener awt;
  
  private Color c1, c2;
  
  public FindDialog(TextEditor t, Color c1, Color c2) {
    super(t);
    if ( findd == null ) {
      this.t = t;
      this.c1 = c1;
      this.c2 = c2;
      makeUI();
      addStuff();
    //  addPopup();
      this.setAlwaysOnTop(true);
      this.setVisible(true);
      this.findd = this;
      Toolkit.getDefaultToolkit().addAWTEventListener(awt = new AWTListener(), eventMask);
      this.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          TextEditor.findDialog = null;
          findd = null;
          dispose();
          Toolkit.getDefaultToolkit().removeAWTEventListener(awt);
        }
      });
      this.addWindowFocusListener(new WindowFocusListener() {
        @Override
        public void windowLostFocus(WindowEvent e) {
          System.out.println(e.getOppositeWindow());
          if ( e.getOppositeWindow() != null && findd == null && e.getOppositeWindow().equals(t) );
          else if ( e.getOppositeWindow() != null && e.getOppositeWindow().equals(t)) 
            highlighter.removeAllHighlights();
        }
        @Override
        public void windowGainedFocus(WindowEvent e) {
         
        }
      });
    }
    else
      return;
  }
  
  private class AWTListener implements AWTEventListener {
    @Override
    public void eventDispatched(AWTEvent e) {
      KeyEvent k = (KeyEvent)e;
      if ( k.getKeyCode() == KeyEvent.VK_ENTER ) {
        if ( e.getID() == KeyEvent.KEY_PRESSED ) {
          if ( fd.isFocused() && findNext.isEnabled() ) {
           findOnlyOne();
          }
        }
      }
      if ( k.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        TextEditor.findDialog = null;
        findd = null;
        dispose();
      }
    }
  }

  private void makeUI() {
    this.setTitle("Find");
    this.setSize(400, 135);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
  }
  
  private void addStuff() {
    JPanel panel = new JPanel(new GridLayout(2, 1));
    
    upper = new JPanel(new FlowLayout(FlowLayout.LEADING));
    bottom = new JPanel(new FlowLayout(FlowLayout.LEADING));
    
    findWhatLabel = new JLabel("Find what:    ");
    findWhat = new JTextField(19);
    findWhat.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        
      }
      
      @Override
      public void insertUpdate(DocumentEvent e) {
          findNext.setEnabled(true);
      }
      
      @Override
      public void removeUpdate(DocumentEvent e) {
        if ( findWhat.getDocument().getLength() == 0 )
          findNext.setEnabled(false);
      }
    });
    
    findWhat.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) )
          TextEditor.popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
    
    findNext = new CustomButton("Find Next");
    findNext.setEnabled(false);
    findNext.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        findNext();
      }
    });
    
    cancel = new CustomButton("  Cancel  ");
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TextEditor.findDialog = null;
        findd = null;
        dispose();
      }
    });
    
    matchCase = new JCheckBox("Match case                      ");
    matchCase.setSelected(false);
    
    up = new JRadioButton("Up");
    down = new JRadioButton("Down");
    down.setSelected(true);
    ButtonGroup g = new ButtonGroup();
    g.add(up);
    g.add(down);
    upper.add(findWhatLabel);
    upper.add(findWhat);
    upper.add(findNext);
    bottom.add(matchCase);
    
    radioPanel = new JPanel(new GridLayout(1, 2));
    radioPanel.add(up);
    radioPanel.add(down);
    
    bottom.add(radioPanel);
    bottom.add(cancel);
    panel.add(upper);
    panel.add(bottom);
    
    colorIt();
    
    this.getContentPane().add(panel);
  }
  
  private void colorIt() {
    matchCase.setBackground(c1);
    up.setBackground(c1);
    down.setBackground(c1);
    upper.setBackground(c1);
    bottom.setBackground(c1);
    matchCase.setForeground(c2);
    up.setForeground(c2);
    down.setForeground(c2);
    findNext.changeColors(c2, c1);
    cancel.changeColors(c2, c1);
    findWhatLabel.setForeground(c2);
    radioPanel.setBackground(c1);
    TitledBorder border = new TitledBorder(BorderFactory.createEtchedBorder(), "Direction");
    border.setTitleColor(c2);
    radioPanel.setBorder(border);
  }
  
  void findNext() {
    String s = findWhat.getText();
    toFind = s;
    Document d = TextEditor.textArea.getDocument();
    if ( s.length() > d.getLength() ) {
      pane = new JOptionPane();
      pane.showMessageDialog(this, "Cannot find \"" + s + "\"");
      return;
    }
    
    if ( down.isSelected() ) {
      int x;
      if ( posUp ) 
       x = TextEditor.textArea.getCaretPosition()+1;
      else
        x = TextEditor.textArea.getCaretPosition();
      for ( int i = x; i < d.getLength()-s.length()+1; i++ ) {
        try {
          if ( matchCase.isSelected() ) {
            if ( d.getText(i, s.length()).equals(s) ) {
              highlighter.removeAllHighlights();
             highlighter.addHighlight(i, i+s.length(), painter);
              TextEditor.textArea.setCaretPosition(i+1);
              highlighter.removeAllHighlights();
              TextEditor.textArea.select(i, i+s.length());
              posDown = true;
              posUp = false;
                return;
            }
          }
          else {
            if ( d.getText(i, s.length()).equalsIgnoreCase(s) ) {
              highlighter.removeAllHighlights();
              highlighter.addHighlight(i, i+s.length(), painter);
              TextEditor.textArea.setCaretPosition(i+1);
              highlighter.removeAllHighlights();
              TextEditor.textArea.select(i, i+s.length());
              posDown = true;
              posUp = false;
                return;
            }
          }
        } catch ( BadLocationException ble ) {
          ble.printStackTrace();
          System.err.println("NEMA VEZE");
        }
      }
      JOptionPane.showMessageDialog(this, "Cannot find \"" + s + "\"");
    }
    else if ( up.isSelected() ) {
      int x;
      if ( posDown )
        x = TextEditor.textArea.getCaretPosition()-2;
      else 
        x = TextEditor.textArea.getCaretPosition()-1;
      for ( int i = x; i >= 0; i--) {
        try {
          if ( matchCase.isSelected() ) {
            if ( d.getText(i, s.length()).equals(s) ) {
              highlighter.removeAllHighlights();
              highlighter.addHighlight(i, i+s.length(), painter);
              TextEditor.textArea.setCaretPosition(i);
              posUp = true;
              posDown = false;
              return;
            }
          }
          else {
            if ( d.getText(i, s.length()).equalsIgnoreCase(s) ) {
              highlighter.removeAllHighlights();
              highlighter.addHighlight(i, i+s.length(), painter);
              TextEditor.textArea.setCaretPosition(i);
              posUp = true;
              posDown = false;
              return;
            }
          }
        } catch ( BadLocationException ble ) {
          System.err.println("NEMA VEZE");
        }
      }
      if ( s.equals(toFind) ) { 
        Highlighter.Highlight[] l =  highlighter.getHighlights();
        if ( l.length > 0 ) {
          int start = l[0].getStartOffset();
          int ending = l[0].getEndOffset();
          highlighter.removeAllHighlights();
          TextEditor.textArea.select(start, ending);
        }
        JOptionPane.showMessageDialog(this, "Cannot find \"" + s + "\"");
        return;
      }
    }
    
  }
  
  private void findOnlyOne() {
    
    String s = findWhat.getText();
    int x = TextEditor.textArea.getCaretPosition();
    System.out.println(x + " NA POCETKU");
    int r = -1;
    if ( down.isSelected() ) {
      r = TextEditor.textArea.getText().indexOf(s, x);
      System.out.println(x + " DOWN SELECTED");
      System.out.println(r + " INDEX");
    }
    else if ( up.isSelected() ) {
      r = TextEditor.textArea.getText().lastIndexOf(s, x);
      System.out.println(x + " UP SELECTED");
      System.out.println(r + " INDEX UP");
    }
    if ( r != -1 ) {
      System.out.println("Selection");
      TextEditor.textArea.select(r, r+s.length());
      return;
    }
    else {
      JOptionPane.showMessageDialog(this, "Cannot find \"" + s + "\"");
      return;
    }
  }
  
/*  private void addPopup() {
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
    
  }*/
  
}