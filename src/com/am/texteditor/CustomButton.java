package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class CustomButton extends JButton {
  
  private Color hoverBackgroundColor = new Color(108, 123, 139).darker();
  private Color pressedBackgroundColor = new Color(108, 123, 139).brighter();
  
  public CustomButton() {
    this(null);
  }
  
  public CustomButton(String s) {
    super(s);
    this.setBackground(new Color(108, 123, 139));
    this.setFont(new Font("Tahoma", Font.BOLD, 12));
    this.setForeground(new Color(251, 250, 250));
    super.setContentAreaFilled(false);
  }
  
  public void changeColors(Color colB, Color colF) {
    this.setBackground(colB);
    this.setForeground(colF);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    if (getModel().isPressed()) {
      g.setColor(pressedBackgroundColor);
    } else if (getModel().isRollover()) {
      g.setColor(hoverBackgroundColor);
      
    } else {
      g.setColor(getBackground());
    }
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
  
  
}