package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.EtchedBorder;

class ThemeChangeDialog extends JDialog {

  private JPanel panel, whiteP, blackP, yellowP;
  private MouseListen listener;
  private TextEditor t;
  private ConfirmSaveDialog csd;
  
  public ThemeChangeDialog(TextEditor t) {
    super(t);
    this.t = t;
    this.csd = csd;
    makeUI();
    addStuff();
    this.setVisible(true);
  }
  
  private void makeUI() {
    this.setTitle("Themes");
    this.setSize(300, 100);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocation((int)t.getLocation().getX()+10, (int)t.getLocation().getX()-60);
    this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
  }
  
  private void addStuff() {
    panel = new JPanel(new GridLayout(1, 3));
    whiteP = new JPanel();
    blackP = new JPanel();
    yellowP = new JPanel();
    
    whiteP.setBackground(new Color(251, 250, 250));
    blackP.setBackground(new Color(87, 87, 87));
    yellowP.setBackground(new Color(255, 236, 139));
    
    whiteP.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    blackP.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    yellowP.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    
    listener = new MouseListen();
    
    whiteP.addMouseListener(listener);
    blackP.addMouseListener(listener);
    yellowP.addMouseListener(listener);
    
    panel.add(whiteP);
    panel.add(blackP);
    panel.add(yellowP);
    
    this.getContentPane().add(panel);
  }
  
  private class MouseListen extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
      System.out.println(e);
      JPanel src = ( JPanel )e.getSource();
      if ( src.equals(whiteP) ) {
        ThemeChange.change(t, "w");
      }
      else if ( src.equals(blackP) ) {
        ThemeChange.change(t, "b");
      }
      else if ( src.equals(yellowP) ) {
        ThemeChange.change(t, "y");
      }
      if ( e.getClickCount() == 2 )
        dispose();
    }
  }
  
  
}