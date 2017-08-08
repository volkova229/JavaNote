package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class ConfirmSaveDialog extends JDialog {
  
  private TextEditor t; 
  
   JPanel panelUp, panelDown;
  private JLabel label;
  private CustomButton dontSave, cancel;
  private CustomButton save;
  private ConfirmSaveDialog co = this;
  
  public static final int SAVE_OPTION = 1;
  public static final int DONT_SAVE_OPTION = 2;
  public static final int CANCEL_OPTION = 3;
  public static int option;
  
  public static Color c1, c2;
  
  private Listener l = new Listener();
  
  private long eventMask = AWTEvent.KEY_EVENT_MASK;
  
  public ConfirmSaveDialog(TextEditor t, Color c1, Color c2) {
    super(t);
    this.c1 = c1;
    this.c2 = c2;
    this.t = t;
    makeUI();
    addStuff();
    this.setAlwaysOnTop(true);
    this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    this.setVisible(true);
  }
  
  private void makeUI() {
    this.setTitle("Javapad");
    this.setSize(350, 100);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
  }
  
  private void addStuff() {
    panelUp = new JPanel(new FlowLayout(FlowLayout.LEADING));
    panelDown = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    label = new JLabel("Do you want to save changes to " + TextEditor.fileName + "?");
    label.setFont(new Font("Tahoma", Font.BOLD, 12));
    save = new CustomButton("Save");
    dontSave = new CustomButton("Don't save");
    cancel = new CustomButton("Cancel");
    
    save.addActionListener(l);
    dontSave.addActionListener(l);
    cancel.addActionListener(l);
    save.addKeyListener(new KeyListen());
    dontSave.addKeyListener(new KeyListen());
    cancel.addKeyListener(new KeyListen());
    
    colorIt(c1, c2);
    
    panelUp.add(label);
    panelDown.add(save);
    panelDown.add(dontSave);
    panelDown.add(cancel);
    
    this.getContentPane().add(panelUp);
    this.getContentPane().add(panelDown, BorderLayout.SOUTH);
  }
  
  private void colorIt(Color c1, Color c2) {
    panelUp.setBackground(c1);
    panelDown.setBackground(c1);
    label.setForeground(c2);
    save.changeColors(c2, c1);
    dontSave.changeColors(c2, c1);
    cancel.changeColors(c2, c1);
  }
  
  public static int displaySaveDialog() {
    return option;
  }
  
  private class Listener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JButton b = (JButton)e.getSource();
      if ( b.equals(save) ) 
        option = 1;
      else if ( b.equals(dontSave) ) 
        option = 2;
      else if ( b.equals(cancel) ) 
        option = 3;
      displaySaveDialog();
      dispose();
    }
  }
  
  private class KeyListen extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      if ( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
        if ( dontSave.isFocusOwner() ) 
          cancel.requestFocus();
        else if ( save.isFocusOwner() )
          dontSave.requestFocus();
      }
      else if ( e.getKeyCode() == KeyEvent.VK_LEFT ) {
        if ( cancel.isFocusOwner() )
          dontSave.requestFocus();
        else if ( dontSave.isFocusOwner() )
          save.requestFocus();
      }
      else if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
        if ( save.isFocusOwner() )
          option = 1;
        else if ( dontSave.isFocusOwner() )
          option = 2;
        else if ( cancel.isFocusOwner() )
          option = 3;
        displaySaveDialog();
        dispose();
      }
      else if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        displaySaveDialog();
        dispose();
      }
    }
  }
}