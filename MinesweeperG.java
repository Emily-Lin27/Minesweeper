/* Minesweeper 
 * April 2016 
 */ 

/* New Game 
 * Levels
 * Pause 
 * Exit 
 */ 

import java.util.Scanner; 
import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 

public class MinesweeperG extends JFrame implements ActionListener{
  
  JMenuBar menuBar; 
  JMenu menuG, menuH, subMenu; 
  JMenuItem menuItem;
  JRadioButtonMenuItem rbMenuItem;
  
  JDialog insDialog, aboutDialog; 
  
  //labels for dialog boxes 
  JTextArea ins, about; 
  
  public MinesweeperG(){
    menuBar = new JMenuBar();
    
    //Game menu  
    menuG = new JMenu("Game");
    menuG.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menuG);  //add game menu to bar 
    
    //add items into menu 
    menuItem = new JMenuItem("New Game",
                             KeyEvent.VK_N);
    menuG.add(menuItem); 
    menuItem.addActionListener(this);
    
    //submenu for levels 
    subMenu = new JMenu("Levels");
    ButtonGroup myGroup = new ButtonGroup();
    rbMenuItem = new JRadioButtonMenuItem("Beginner");
    //rbMenuItem.setSelected(true);
    myGroup.add(rbMenuItem);  //make sure others are unselected if one is selected 
    subMenu.add(rbMenuItem);
    rbMenuItem.addActionListener(this);
    rbMenuItem = new JRadioButtonMenuItem("Intermediate");
    myGroup.add(rbMenuItem); 
    subMenu.add(rbMenuItem);
    rbMenuItem.addActionListener(this);
    rbMenuItem = new JRadioButtonMenuItem("Expert");
    myGroup.add(rbMenuItem); 
    subMenu.add(rbMenuItem);
    rbMenuItem.addActionListener(this);
    menuG.add(subMenu); 
    
    menuItem = new JMenuItem("Pause",
                             KeyEvent.VK_P);
    menuG.add(menuItem);   
    menuItem.addActionListener(this);
    
    menuItem = new JMenuItem("Exit",
                             KeyEvent.VK_P);
    menuG.add(menuItem); 
    menuItem.addActionListener(this);
    
    
    //Help menu 
    menuH = new JMenu("Help");
    menuBar.add(menuH);
    menuItem = new JMenuItem("Instructions",
                             KeyEvent.VK_I);
    menuH.add(menuItem); 
    menuItem.addActionListener(this);
    
    menuItem = new JMenuItem("About",
                             KeyEvent.VK_A);
    menuH.add(menuItem); 
    menuItem.addActionListener(this);
    
    add(menuBar, BorderLayout.NORTH);   //add the menu bar to top of frame 
    
    setSize(450,450); 
    setLocation(300,100); 
    setResizable(false); 
    setTitle("Minesweeper");
    setVisible(true); 
  }
  
  public void actionPerformed (ActionEvent e) {
    String command = e.getActionCommand();      //Get information from the action event...
    System.out.println(command); 
    if (command.equals("New Game"))
    {
      
    } else if (command.equals("Pause"))
    {
      
    } else if (command.equals("Instructions"))
    {
      insDialog = new JDialog(this,"Game Instructions",true);
      insDialog.setSize(new Dimension(270, 240)); //dimensions
      insDialog.setLocation(350, 100); //opening location
      insDialog.setResizable(false); //not resizable
      insDialog.setModal(true);      
      //insDialog.setLayout(new FlowLayout());   
      
      ins = new JTextArea(140,140); 
      ins.setText("The purpose of the game is to open all the cells of the board which do not contain a bomb. You lose if you set off a bomb cell. \n \n Every non-bomb cell you open will tell you the total number of bombs in the eight neighboring cells. Once you are sure that a cell contains a bomb, you can right-click to put a flag it on it as a reminder. Once you have flagged all the bombs around an open cell, you can quickly open the remaining non-bomb cells by shift-clicking on the cell."); 
      ins.setWrapStyleWord(true);
      ins.setLineWrap(true);
      ins.setOpaque(false);
      ins.setEditable(false);
      ins.setFocusable(false);
      ins.setBackground(UIManager.getColor("Label.background"));
      ins.setFont(UIManager.getFont("Label.font"));
      ins.setBorder(UIManager.getBorder("Label.border"));
      
      insDialog.add(ins); 
      
      insDialog.setVisible(true); 
    } else if (command.equals("About"))
    {
      
    } else if (command.equals("Beginner"))
    {
      
    } else if (command.equals("Intermediate"))
    {
      
    } else if (command.equals("Expert"))
    {
      
    } else {
      processEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
  }
  
  public static void main (String [] args){
    MinesweeperG frame1 = new MinesweeperG();
  }
}