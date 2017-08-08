package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class FontDialog extends JDialog {
  
  private Container c = this.getContentPane();
  private JPanel panel, buttonPanel;
  private JComboBox<String> fonts, styles, sizes;
  private CustomButton ok, cancel;
  private JTextArea textArea;
  private String choosen = "";
  private static final String[] styleNamesArr = {"Plain", "Bold", "Italic", "Bold&Italic"};
  private static final int BOLDITALIC = Font.BOLD | Font.ITALIC;
  private static final int[] styleNoArr = { Font.PLAIN, Font.BOLD, Font.ITALIC, BOLDITALIC};
  private static final String[] sizesArr = {"8", "9", "10", "11", "12", "14", "16", "18", "20"
    , "22", "24", "26", "28", "36", "48", "72"};
  private static String[] fontsArr;
  private ComboBoxListener l = new ComboBoxListener();
  
  private String choosenFont = "";
  private int choosenSize = 0;
  private String choosenStyle = "Plain";
  
  private static FontDialog d = null;
  private TextEditor t;
  private long eventMask = AWTEvent.KEY_EVENT_MASK;
  private AWTListen awtListener;
  
  private Color c1, c2;
  
  public FontDialog(TextEditor t, Color c1, Color c2) {
    super(t);
    this.t = t;
    this.d = d;
    this.c1 = c1;
    this.c2 = c2;
    makeUI();
    addStuff();
    this.setAlwaysOnTop(true);
    this.setVisible(true);
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
    Toolkit.getDefaultToolkit().addAWTEventListener( awtListener = new AWTListen(), eventMask);
  }
  
  private void makeUI() {
    this.setTitle("Fonts");
    this.setSize(400, 300);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
    this.setResizable(false);
  }
  
  private void addStuff() {
    findFonts();
    textArea = new JTextArea(1, 1);
    textArea.setText("AaBbYyZz");
    textArea.setEditable(false);
    textArea.setLineWrap(true);         
    textArea.setWrapStyleWord(true);
    textArea.setFocusable(false);
    
    fonts = new JComboBox<String>(fontsArr);
    String selectedFont = TextEditor.textArea.getFont().getName();
    fonts.setSelectedItem(selectedFont);
    styles = new JComboBox<String>(styleNamesArr);
    int selectedStyle = TextEditor.textArea.getFont().getStyle();
    if ( selectedStyle == Font.PLAIN )
      styles.setSelectedItem("Plain");
    else if ( selectedStyle == Font.BOLD )
      styles.setSelectedItem("Bold");
    else if ( selectedStyle == Font.ITALIC )
      styles.setSelectedItem("Italic");
    else if ( selectedStyle == BOLDITALIC )
      styles.setSelectedItem("Bold&Italic");
    choosenStyle = "Plain";
    sizes = new JComboBox<String>(sizesArr);
    String selectedSize = ""+TextEditor.textArea.getFont().getSize();
    sizes.setSelectedItem(selectedSize);
    choosenSize = 36;
    
    fonts.addActionListener(l);
    sizes.addActionListener(l);
    styles.addActionListener(l);
    
    ok = new CustomButton("Ok");
    ok.requestFocusInWindow();
    ok.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TextEditor.textArea.setFont(textArea.getFont());
        dispose();
      }
    });
    
    cancel = new CustomButton("Cancel");
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    buttonPanel.add(ok);
    buttonPanel.add(cancel);
    
    panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    panel.add(fonts);
    panel.add(styles);
    panel.add(sizes);
    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
    JPanel bigPanel = new JPanel(new GridLayout(2, 1));
    bigPanel.add(panel);
    bigPanel.add(buttonPanel);
    
    colorIt();
    
    c.add(textArea, BorderLayout.CENTER);
    c.add(bigPanel, BorderLayout.SOUTH);
  }
  
  private void colorIt() {
    fonts.setBackground(c1);
    styles.setBackground(c1);
    sizes.setBackground(c1);
    fonts.setForeground(c2);
    styles.setForeground(c2);
    sizes.setForeground(c2);
    buttonPanel.setBackground(c1);
    panel.setBackground(c1);
    // bigPanel.setBackground(c1);
    c.setBackground(c1);
    ok.changeColors(c2, c1);
    cancel.changeColors(c2, c1);
    textArea.setForeground(c2);
  }
  
  private void findFonts() {
    GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    fontsArr = g.getAvailableFontFamilyNames();
  }
  
  private class ComboBoxListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComboBox box = (JComboBox)e.getSource();
      if ( box.equals(fonts) ) {
        choosenFont = (String)box.getSelectedItem();
      }
      else if ( box.equals(sizes) ) {
        choosenSize = Integer.parseInt((String)box.getSelectedItem());
      }
      else if ( box.equals(styles) ) {
        choosenStyle = (String)box.getSelectedItem();
      }
      write(choosenFont, choosenStyle, choosenSize);
    }
  }
  
  private void write(String font, String style, int size ) {
    Font f = null;
    if ( style.equals("Plain") )
      f = new Font(font, styleNoArr[0], size);
    else if ( style.equals("Bold") )
      f = new Font(font, styleNoArr[1], size);
    else if ( style.equals("Italic") )
      f = new Font(font, styleNoArr[2], size);
    else if ( style.equals("Bold&Italic") )
      f = new Font(font, styleNoArr[3], size);
    textArea.setFont(f);
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
      else if ( k.getKeyCode() == KeyEvent.VK_ENTER ) {
        TextEditor.textArea.setFont(textArea.getFont());
        dispose();
      }
    }
  }
  
  
  
}