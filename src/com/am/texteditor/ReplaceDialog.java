package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class ReplaceDialog extends JDialog {
  
  private static ReplaceDialog repla = null;
  
  private JLabel findWhatL, replaceWithL;
  private JTextField findWhat, replaceWith;
  private CustomButton findNext, replace, replaceAll, cancel;
  private JCheckBox matchCase;
  private JPanel panel, panelUp, panelMiddle, panelDown;
  private String original = "";
  private String replacement = "";
  private int ind = 0;
  private boolean originalChanged = false;
  private ReplaceDialog rdd = this;
  private TextEditor t;
  private Color c1, c2;
  
  public ReplaceDialog(TextEditor t, Color c1, Color c2) {
    super(t);
    if ( repla == null ) {
      this.t = t;
      this.c1 = c1;
      this.c2 = c2;
      makeUI();
      addStuff();
      this.setAlwaysOnTop(true);
      this.setVisible(true);
      this.repla = this;
      this.addWindowListener( new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          TextEditor.replaceDialog = null;
          repla = null;
          dispose();
        }
      });
    }
    return;
  }
  
  private void makeUI() {
    this.setTitle("Replace");
    this.setSize(350, 220);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
  }
  
  private void addStuff() {
    panelUp = new JPanel(new FlowLayout(FlowLayout.LEADING));
    
    findWhatL = new JLabel("Find what:      ");
    findWhat = new JTextField(14);
    findWhat.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
        findNext.setEnabled(true);
        replace.setEnabled(true);
        replaceAll.setEnabled(true);
        original = findWhat.getText();
        originalChanged = true;
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
        if ( findWhat.getDocument().getLength() == 0 ) {
          findNext.setEnabled(false);
          replace.setEnabled(false);
          replaceAll.setEnabled(false);
        }  
        original = findWhat.getText();
        originalChanged = true;
      }
    });
    findNext = new CustomButton("Find Next");
    findNext.setEnabled(false);
    findNext.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if ( originalChanged )
          ind = 0;
        find(ind);
        originalChanged = false;
        
      }
    });
    
    panelUp.add(findWhatL);
    panelUp.add(findWhat);
    panelUp.add(findNext);
    
    replaceWithL = new JLabel("Replace with:");
    replaceWith = new JTextField(14);
    replaceWith.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
        if ( findNext.isEnabled() ) {
          replace.setEnabled(true);
          replaceAll.setEnabled(true);
        }
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
        if ( findNext.isEnabled() ) {
          replace.setEnabled(true);
          replaceAll.setEnabled(true);
        }

      }
    });
    replace = new CustomButton(" Replace ");
    replace.setEnabled(false);
    replace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        replace(0);
      }
    });
    
    panelUp.add(replaceWithL);
    panelUp.add(replaceWith);
    panelUp.add(replace);
    
    panelMiddle = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    
    replaceAll = new CustomButton("Replace All");
    replaceAll.setEnabled(false);
    replaceAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        replaceAll();
      }
    });
    panelMiddle.add(replaceAll);
    
    matchCase = new JCheckBox("Match case                                                 ");
    matchCase.setBackground(Color.WHITE);
    cancel = new CustomButton("  Cancel  ");
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TextEditor.replaceDialog = null;
        repla = null;
        dispose();
      }
    });
    
    panelDown = new JPanel(new FlowLayout(FlowLayout.LEADING));
    panelDown.add(matchCase);
    panelDown.add(cancel);
    
    panel = new JPanel(new GridLayout(3, 1));
    
    
    panel.add(panelUp);
    panel.add(panelMiddle);
    panel.add(panelDown);
    
    colorIt();
    
    this.getContentPane().add(panel);
  }
  
  private void colorIt() {
    panelUp.setBackground(c1);
    panelMiddle.setBackground(c1);
    panelDown.setBackground(c1);
    findWhatL.setForeground(c2);
    replaceWithL.setForeground(c2);
    findNext.changeColors(c2, c1);
    replace.changeColors(c2, c1);
    replaceAll.changeColors(c2, c1);
    cancel.changeColors(c2, c1);
    matchCase.setBackground(c1);
    matchCase.setForeground(c2);
  }
  
  private void find(int j) {
    Document d = TextEditor.textArea.getDocument();
    original = findWhat.getText();
    if ( d.getLength() < original.length() ) {
      JOptionPane.showMessageDialog(this, "Cannot find \"" + original + "\"");
      return;
    }
    try {
      for ( int i = j; i < d.getLength(); i++ ) {
        if ( matchCase.isSelected() ) {
          System.out.println(d.getText(i, original.length()));
          if ( original.equals(d.getText(i, original.length())) ) {
            TextEditor.textArea.select(i, i + original.length() );
            ind = i+1; 
            return;
          }
        }
        else if ( !matchCase.isSelected() ) {
          System.out.println(d.getText(i, original.length()));
          if ( original.equalsIgnoreCase(d.getText(i, original.length())) ) {
            TextEditor.textArea.select(i, i + original.length() );
            ind = i+1; 
            return;
          }
        }
      }
      JOptionPane.showMessageDialog(this, "Cannot find \"" + original + "\"");
      ind = 0;
    } catch ( BadLocationException be ) {
      System.out.println("GRESKA");
    }
  }
  
  private void replace(int j) {
    replacement = replaceWith.getText();
    TextEditor.textArea.replaceRange(replacement, TextEditor.textArea.getSelectionStart(), TextEditor.textArea.getSelectionEnd());
  }
  
  private void replaceAll() {
    original = findWhat.getText();
    replacement = replaceWith.getText();
    if ( matchCase.isSelected() )
      TextEditor.textArea.setText(TextEditor.textArea.getText().replaceAll(original, replacement));
    else if ( !matchCase.isSelected() ) {
      TextEditor.textArea.setText(TextEditor.textArea.getText().replaceAll("(?i)"+original, replacement));
    } 
  }

}