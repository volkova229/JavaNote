package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class ThemeChange {
  
  public static final Color w = new Color(251, 250, 250);
  public static final Color b = new Color(87, 87, 87);
  public static final Color y = new Color(255, 248, 220);
  public static final Color gold = new Color(205, 149, 12).darker();
  public static final Color grey = new Color(108, 123, 139);
  
  public static Color c1, c2;
  
  public static void change(JFrame c, String col) {
    
    if ( c.getClass().toString().equals("class com.am.texteditor.TextEditor")) {
      TextEditor t = (TextEditor)c;
      
      if ( col.equals("b") ) {
        c1 = b;
        c2 = w;
      }
      else if ( col.equals("w") ) {
        c1 = w;
        c2 = grey;
      }
      else if ( col.equals("y") ) {
        c1 = y;
        c2 = gold;
      }
      
      t.textArea.setBackground(c1);
      t.textArea.setForeground(c2);
      t.bottomPanel.setBackground(c1);
      t.lnColLabel.setForeground(c2);
      t.panel.setBorder(BorderFactory.createLineBorder(c2));
      t.bottomPanel.setBorder(BorderFactory.createLineBorder(c2));
      t.menubar.setBackground(c1);
      t.menubar.setForeground(c2);
      t.textArea.setSelectionColor(c1.darker());
      if ( c1.equals(b) )
        t.textArea.setSelectionColor(c2.darker());
      
      JMenu[] menus = { t.file, t.edit, t.format, t.view, t.help, t.theme};
      
      for ( JMenu menu : menus ) {
        menu.setBackground(c1);
        menu.setForeground(c2);
      }
      
      JMenuItem[] items = { t.makeNew, t.open, t.save, t.saveAs, t.exit, t.undo, t.redo, t.cut, t.copy, t.paste, t.delete,
        t.find, t.findNext, t.replace, t.goTo, t.selectAll, t.timeDate, t.undo2, t.cut2, t.copy2, t.paste2, t.delete2, 
        t.selectAll2, t.font, t.viewHelp, t.about };
      
      for ( JMenuItem item : items ) {
        item.setBackground(c1);
        item.setForeground(c2);
      }
      
      t.statusBar.setBackground(c1);
      t.statusBar.setForeground(c2);
      t.wordWrap.setBackground(c1);
      t.wordWrap.setForeground(c2);
      
      TextEditor.currentTheme = col;
    }
  }
  
  public static void change(JDialog d, String col) {
    if ( d.getClass().toString().equals("class com.am.texteditor.ConfirmSaveDialog") ) {
      ConfirmSaveDialog csd = (ConfirmSaveDialog)d;
       if ( col.equals("b") ) {
        c1 = b;
        c2 = w;
      }
      else if ( col.equals("w") ) {
        c1 = w;
        c2 = grey;
      }
      else if ( col.equals("y") ) {
        c1 = y;
        c2 = gold;
      }
      csd.panelUp.setBackground(c1);
      csd.panelDown.setBackground(c2);
    }
  }
}
