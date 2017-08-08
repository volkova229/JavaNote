package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Map;

class AboutDialog extends JDialog {
  
  private JLabel sysIcon;
  private JPanel panel, bottomPanel;
  private TextEditor t;
  private JTextArea info;
  private long eventMask = AWTEvent.KEY_EVENT_MASK;
  private AWTListen awtListener;
  
  public AboutDialog(TextEditor t) {
    super(t);
    this.t = t;
    makeUI();
    addStuff();
    this.setVisible(true);
    Toolkit.getDefaultToolkit().addAWTEventListener(awtListener = new AWTListen(), eventMask);
  }
  
  private void makeUI() {
    this.setTitle("About Javapad");
    this.setSize(300, 350);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
    this.addWindowFocusListener(new WindowFocusListener() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        t.setEnabled(false);
      }
      
      @Override
      public void windowLostFocus(WindowEvent e) {
        t.setEnabled(true);
        t.requestFocus();
        Toolkit.getDefaultToolkit().removeAWTEventListener(awtListener);
      }
    });
  }
  
  private void addStuff() {
    panel = new JPanel();
    
    sysIcon = new JLabel();
    sysIcon.setIcon(logo());
    panel.add(sysIcon);
    
    bottomPanel = new JPanel();
    info = new JTextArea("Javapad" +
                             "\nVersion 1.0 ( probably never getting un update )" + 
                             "\nNo copyright. No rights reserved." + 
                             "\nNot under any trademark. Yet." + 
                             "\n" + "\n" + "\nThis product is lincenced under no terms to: " +
                             "\n" + getComputerName());
    info.setEditable(false);
    info.setFocusable(false);
    bottomPanel.add(info);
    colorIt();
    
    this.getContentPane().add(panel, BorderLayout.NORTH);
    this.getContentPane().add(bottomPanel);
  }
  
  private void colorIt() {
    panel.setBackground(Color.WHITE);
    bottomPanel.setBackground(Color.WHITE);
    info.setBackground(Color.WHITE);
    info.setForeground(Color.BLACK);
  }
  
  private String getSystem() {
    return System.getProperty("os.name");
  }
  
  private String getComputerName() {
    Map<String, String> env = System.getenv();
    if (env.containsKey("COMPUTERNAME"))
      return env.get("COMPUTERNAME");
    else if (env.containsKey("HOSTNAME"))
      return env.get("HOSTNAME");
    else
      return "Unknown Computer";
  }
  
  private ImageIcon logo() {
    String sys = getSystem();
    if ( sys.equals("Windows 7")) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\windows7.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Windows XP") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\windowsxp.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Linux") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\linux.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Mac OS X") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\macos.png").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Windows Vista") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\windowsvista.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Windows 8") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\windows8.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else if ( sys.equals("Windows 8.1") ) {
      return new ImageIcon(new ImageIcon("com\\am\\texteditor\\windows81.jpg").getImage().getScaledInstance(280, 150, Image.SCALE_DEFAULT));
    }
    else {
      
    }
    return null;
  }
  
  private class AWTListen implements AWTEventListener {
    @Override
    public void eventDispatched(AWTEvent e) {
      KeyEvent k = ( KeyEvent )e;
      System.out.println("ALALALA");
      if ( k.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        dispose();
      }
    }
  }

}