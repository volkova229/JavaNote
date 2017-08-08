package com.am.texteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;


class HelpDialog extends JDialog {
  
  // It's a singleton :D

  private static HelpDialog hd = null;
  private JPanel panel;
  private JTree tree;
  private JLabel label;
  private JTextArea whatArea;
  private DefaultMutableTreeNode what, top, howToOpen, clickToOpen, howToChangeFont, howToCutEtc, howToTime, howToFind,
    helpWrap, helpGoTo;
  private JSplitPane splitPane;
  
  private JTextArea textArea;
  private TextEditor t;
  private Color c1, c2;
  
  public HelpDialog(TextEditor t, Color c1, Color c2) {
    super(t);
    if ( hd == null ) {
      this.t = t;
      this.c1 = c1;
      this.c2 = c2;
      makeUI();
      makeStuff();
      this.setVisible(true);
      this.hd = this;
    }
      return;
  }
  
  private void makeUI() {
    this.setTitle("Help - Javapad");
    this.setSize(800, 450);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocationRelativeTo(t);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        hd = null;
        dispose();
      }
    });
  }
  
  private void makeStuff() {
    FlowLayout f = new FlowLayout();
    f.setAlignment(FlowLayout.LEADING);
    panel = new JPanel(f);
    panel.setSize(400, 400);
    
    top = new DefaultMutableTreeNode("Javapad");
    createNodes(top);
    tree = new JTree(top);
    tree.setShowsRootHandles(true);
    tree.collapseRow(0);
    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.addTreeSelectionListener(new Listener());
    
    label = new JLabel();
    label.setText("Frequently asked questions about Javapad");
    label.setFont(new Font("Tahoma", 0, 20));
    
    panel.add(label);
    panel.add(tree);
    
    textArea = new JTextArea(55, 55);
    textArea.setFont(new Font("MONOSPACED", 0, 12));
    textArea.setEditable(false);
    textArea.setFocusable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    
    JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
    panel2.add(textArea);
    panel2.setSize(400, 400);
    
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, panel2); 
    colorIt();
    this.getContentPane().add(splitPane);
  }
  
  private void colorIt() {
    panel.setBackground(c1);
    textArea.setBackground(c1);
    textArea.setForeground(c2);
    label.setForeground(c2);
    tree.setBackground(c1);
    class MyTreeCellRenderer extends DefaultTreeCellRenderer {
      
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                    boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        String node = (String) ((DefaultMutableTreeNode) value).getUserObject();
        setForeground(c2);
        setOpaque(true);
        return this;
      }
    }
    tree.setCellRenderer(new MyTreeCellRenderer());
  }
  
  private void createNodes(DefaultMutableTreeNode top) {
    
    whatArea = new JTextArea();
    whatArea.setEditable(false);
    
    what = new DefaultMutableTreeNode("What is Javapad?");
    top.add(what);
    howToOpen = new DefaultMutableTreeNode("How to open Javapad?");
    top.add(howToOpen);
    clickToOpen = new DefaultMutableTreeNode("Click to open Javapad");
    howToOpen.add(clickToOpen);
    howToChangeFont = new DefaultMutableTreeNode("How do I change the font style and size?");
    top.add(howToChangeFont);
    howToCutEtc = new DefaultMutableTreeNode("How do I cut, copy, paste or delete text?");
    top.add(howToCutEtc);
    howToTime = new DefaultMutableTreeNode("How do I insert the time and date in a document?");
    top.add(howToTime);
    howToFind = new DefaultMutableTreeNode("How do I find and replace specific characters or words?");
    top.add(howToFind);
    helpWrap = new DefaultMutableTreeNode("How to enable word wrap?");
    top.add(helpWrap);
    helpGoTo = new DefaultMutableTreeNode("How do I go to a specific line in a Notepad document?");
    top.add(helpGoTo);
  }
  
  private class Listener implements TreeSelectionListener {
    @Override
    public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
      if ( node.equals(top) ) {
     //   tree.expandPath(tree.getSelectionPath());
      }
      else if ( node.equals(what) )
        textArea.setText("Javapad is a basic text-editing program and it's most commonly used to view or edit text files." + 
                         "\nA text file is a file type typically identified by the .txt file name extension.");
      else if ( node.equals(howToOpen) ) {
        tree.expandPath(tree.getSelectionPath());
        textArea.setText("");
      }
      else if ( node.equals(clickToOpen) ) {
        hd = null;
        dispose();
        t.requestFocus();
      }
      else if ( node.equals(howToChangeFont) )
        textArea.setText("1. Click the Format menu, and then click Font." +
                         "\n2. Make your selections in the Font, Font style, and Size boxes." + 
                         "\n3. An example of how your font will look appears under Sample." +
                         "\n4. When you are finished making font selections, click OK.");
      else if ( node.equals(howToCutEtc) ) 
        textArea.setText("-To cut text so you can move it to another location, select the text, click the Edit menu, and then click Cut." +
                         "\n-To copy text so you can paste it in another location, select the text, click the Edit menu, and then click Copy." +
                         "\n-To paste text you have cut or copied, click the location in the file where you want to paste the text, click the Edit menu, and then click Paste." +
                         "\n-To delete text, select it, click the Edit menu, and then click Delete." + 
                         "\n-To undo your last action, click the Edit menu, and then click Undo.");
      else if ( node.equals(howToTime) )
        textArea.setText("1. Click the location in the document where you want to add the time and date." + 
                         "\n2. Click the Edit menu, and then click Time/Date.");
      else if ( node.equals(howToFind) )
        textArea.setText("To find specific characters or words: " + 
                         "\n1. Click the Edit menu, and then click Find." + 
                         "\n2. In the Find what box, type the characters or words you want to find." + 
                         "\n3. Under Direction, click Up to search from the current cursor position to the top of the document, or click Down to search from the cursor position to the bottom of the document." + 
                         "\n4. Click Find Next." + 
                         
                         "\n\nTo replace specific characters or words: " +
                         "\n1. Click the Edit menu, and then click Replace." + 
                         "\n2. In the Find what box, type the characters or words you want to find." +
                         "\n3. In the Replace with box, type the replacement text." + 
                         "\n4. Click Find Next, and then click Replace." +
                         "\n5. To replace all instances of the text, click Replace All.\n" +
                         "\nTip" + 
                         "\n-To find or replace only text that matches the use of uppercase and lowercase characters specified in the Find what box, select the Match case check box.");
      else if ( node.equals(helpWrap) ) 
        textArea.setText("To see all of your text without scrolling, click the Format menu, and then click Word Wrap.");
      else if  ( node.equals(helpGoTo) )
        textArea.setText("You can go to a specific line in a Notepad document even if the document doesn't display line numbers. Lines are counted down the left margin beginning at the top of the document." + 
                         "\n1.Click the Edit menu, and then click Go To." +
                         "\n2.In the Line number box, type the line number you want the cursor to jump to, and then click Go To.\n" +
                         "\nNote: " +
                         "\n-The Go To command isn't available when word wrapping is enabled. To turn word wrapping off, click the Format menu, and then click Word Wrap.");
    }
  }
  
}