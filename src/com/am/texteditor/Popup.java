package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Map;

class Popup extends JPopupMenu {
  
  private JMenuItem undo, redo, cut, copy, paste, delete, selectAll;
  private Color c1, c2;
  private JFrame parentFrame;
  private JDialog parentDialog;
  
  public Popup(Color c1, Color c2) {
    this.c1 = c1;
    this.c2 = c2;
    init();
    this.setLocation(200, 200);
    this.setVisible(true);
  }
  
  public Popup(JFrame frame, Color c1, Color c2) {
    this(c1, c2);
    this.parentFrame = frame;
  }
  
  public Popup(JDialog dialog, Color c1, Color c2) {
    this(c1, c2);
    this.parentDialog = dialog;
  }
  
  private class Listener implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
      if ( parentFrame != null && parentFrame.getClass().toString().equals("class com.am.texteditor.TextEditor") ) {
        TextEditor t = (TextEditor)parentFrame;
        if ( e.getSource().equals(delete) ) {
          
        }
        else if ( e.getSource().equals(selectAll) ) {
        
        }
        else if ( e.getSource().equals(undo) ) {
        
        }
        else if ( e.getSource().equals(redo) ) {
        
        }
      }
      
      else if ( parentDialog != null && parentFrame.getClass().toString().equals("class com.am.texteditor.TextEditor")) {
    //    if ( parentDialog.getTitle().equals("Find") ) 
     //     FindDialog t = (FindDialog)parentDialog;
      //  else if ( parentDialog.getTitle().equals("Replace") ) 
     //     ReplaceDialog t = (ReplaceDialog)parentDialog;
    //    else if ( parentDialog.getTitle().equals("Go To Line") ) 
     //     GoToDialog t = (GoToDialog)parentDialog;
          
      }
    }
  }
  
  private void init() {
    undo = new JMenuItem("Undo");
    redo = new JMenuItem("Redo");
    cut = new JMenuItem(new DefaultEditorKit.CutAction());
    cut.setText("Cut");
    copy = new JMenuItem(new DefaultEditorKit.CopyAction());
    copy.setText("Copy");
    paste = new JMenuItem(new DefaultEditorKit.PasteAction());
    paste.setText("Paste");
    delete = new JMenuItem("Delete");
    selectAll = new JMenuItem("Select All");
    
    colorIt();
    
    this.add(undo);
    this.add(redo);
    this.add(cut);
    this.add(copy);
    this.add(paste);
    this.add(selectAll);
  }
  
  private void colorIt() {
    redo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, c2));
    paste.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, c2));
    undo.setBackground(c1);
    redo.setBackground(c1);
    cut.setBackground(c1);
    copy.setBackground(c1);
    paste.setBackground(c1);
    delete.setBackground(c1);
    selectAll.setBackground(c1);
    undo.setForeground(c2);
    redo.setForeground(c2);
    cut.setForeground(c2);
    copy.setForeground(c2);
    paste.setForeground(c2);
    delete.setForeground(c2);
    selectAll.setForeground(c2);
  }
  
  public static void main(String[] args) {
    new Popup(ThemeChange.w, ThemeChange.grey);
  }
  
}