package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class EnterAction extends AbstractAction {
  public EnterAction(String text, String desc) {
    super(text);
    putValue(SHORT_DESCRIPTION, desc);
  }
  
  @Override
  public void actionPerformed(ActionEvent ae) {
    JOptionPane.showMessageDialog(null, "BINGO, you SAW me.");
  }
}
