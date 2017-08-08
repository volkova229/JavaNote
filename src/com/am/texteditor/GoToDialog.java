package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class GoToDialog extends JDialog {

  private static GoToDialog gtd = null;
  
  private CustomButton goTo, cancel;
  private JTextField text;
  private JLabel label;
  private Container c = this.getContentPane();
  private JPanel buttonPanel, upperPanel;
  private TextEditor t;
  private Color c1, c2;
  
  public GoToDialog(TextEditor t, Color c1, Color c2) {
      super(t);
    if ( gtd == null ) {
      this.t = t;
      this.c1 = c1;
      this.c2 = c2;
      makeUI();
      addStuff();
      addListeners();
      this.setVisible(true);
      this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
      this.addWindowFocusListener(new WindowFocusListener() {
        @Override
        public void windowGainedFocus(WindowEvent e) {
          t.setEnabled(false);
        }
        
        @Override
        public void windowLostFocus(WindowEvent e) {
          t.setEnabled(true);
          t.requestFocus();
          TextEditor.textArea.requestFocusInWindow();
          TextEditor.textArea.setCaretPosition(TextEditor.textArea.getCaretPosition());
        }
      });
    }
  }
  
  
  private void makeUI() {
    this.setTitle("Go To Line");
    this.setSize(301, 130);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
  }
  
  private void addStuff() {
    FlowLayout f = new FlowLayout();
    f.setAlignment(FlowLayout.LEADING);
    upperPanel = new JPanel(f);
    
    label = new JLabel("Line number:             ");
    text = new JTextField(26);
    
    upperPanel.add(label);
    upperPanel.add(text);
    
    goTo = new CustomButton("Go To");
    cancel = new CustomButton("Cancel");
    
    FlowLayout g = new FlowLayout();
    g.setAlignment(FlowLayout.TRAILING);
    buttonPanel = new JPanel(g);
    
    buttonPanel.add(goTo);
    buttonPanel.add(cancel);
    colorIt();
    c.add(upperPanel);
    c.add(buttonPanel, BorderLayout.SOUTH);
  }
  
  private void colorIt() {
    upperPanel.setBackground(c1);
    buttonPanel.setBackground(c1);
    label.setForeground(c2);
    goTo.changeColors(c2, c1);
    cancel.changeColors(c2, c1);
    
  }
  
  private void addListeners() {
    text.addKeyListener(new KeyAdapter() {
      
      @Override
      public void keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == 8 )
          return;
        if ( e.getKeyCode() == 10 ) {
          goTo();
          return;
        }
        if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        TextEditor.goToDialog = null;
          dispose();
          return;
        }
        if (!( ( e.getKeyCode() >= 48 && e.getKeyCode() <= 57 ) ||  (e.getKeyCode() >= 96 && e.getKeyCode() <= 105 ))) {
          JOptionPane.showMessageDialog(null, "Unacceptable character", "", JOptionPane.ERROR_MESSAGE);
          text.setText("1");
          text.selectAll();
        }
        else {
          return;
        }
      }
    });
    
    goTo.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goTo();
      }
    });
    
    cancel.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TextEditor.goToDialog = null;
        gtd = null;
        dispose();
      }
    });
    
  }
  
  private void goTo() {
    int n;
    try {
    n = Integer.parseInt(text.getText());
    } catch ( NumberFormatException nfe ) {
      text.setText("1");
      text.selectAll();
      return;
    }
    if ( n <= RXTextUtilities.getLines(TextEditor.textArea) ) { 
      RXTextUtilities.gotoStartOfLine(TextEditor.textArea, n);
      int r = TextEditor.textArea.getCaretPosition();
        TextEditor.goToDialog = null;
      gtd = null;
      dispose();
    }
    else {
      JOptionPane.showMessageDialog(this, "The line number is beyond the total number of lines");
    }
  }
  

}