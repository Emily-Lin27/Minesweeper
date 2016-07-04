/*
 * Name: Gemma Zhang and Emily Lin
 * Description: Minesweeper
 * Date: 09/05/16
 */

// PLEASE USE ECLIPSE TO RUN THIS PROGRAM AS IT DOES NOT SEEM TO WORK ON DR. JAVA

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory; 
import javax.swing.JOptionPane;

public class Minesweeper extends JFrame implements ActionListener{
  // Public variables
  public static int numBombs = 10;  // number of bombs as seen on the GUI
  public static int time = 0; // initiates the timer 
  public static Timer myTimer; // Timer 
  public static Boolean bombTracker[][] = new Boolean[16][30]; 
  public static String level = "B"; 
  
  // GUI- related public variables 
  public static JButton[][] board = new JButton[8][8]; 
  public static JButton faceButton = new JButton(); // Button with the face 
  public static JPanel boardPanel = new JPanel(), dialogPane;
  public static JLabel bombLabel = new JLabel("" + numBombs);  
  JLabel timeLabel = new JLabel("" + time);  
  public static boolean gameOver = false; 
  public GridLayout grid = new GridLayout(board.length, board[0].length);
  
  public static int clearedSpots = (board.length*board[0].length) - 10; // determines number of spots to be cleared  
  
  // Menu variables
  public JMenuBar menuBar; 
  public JMenu menuG, menuH, subMenu; 
  public JMenuItem menuItem, menuItemPa, menuItemUn, menuItemR;
  public JRadioButtonMenuItem rbMenuItem;
  public JDialog insDialog, aboutDialog; 
  
  //labels for dialog boxes 
  JTextArea ins, about;
  
  // Images/ Icons 
  public static Icon redFlag = new ImageIcon("Red Flag.png"); 
  public static Icon bomb = new ImageIcon("Bomb.png"); 
  public static Icon smileyFace = new ImageIcon("Smiley Face.png"); 
  public static Icon shockedFace = new ImageIcon("Shocked Face.png"); 
  public static Icon deadFace = new ImageIcon("Dead Face.png");
  public static Icon bombWithCross = new ImageIcon("Bomb with Cross.png");
  public static Icon sunglasses = new ImageIcon("Sunglasses.png");
  
  static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
  
  public Minesweeper(){
    try {
      UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // Basic setup
    setTitle("Minesweeper");
    setSize(750, 720);
    setResizable(false);
    setLocation(300,0); 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    
    // Menu 
    menuBar = new JMenuBar();
    
    //Game menu  
    menuG = new JMenu("Game");
    menuG.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menuG);  //add game menu to bar 
    
    //add items into menu 
    menuItem = new JMenuItem("New Game", KeyEvent.VK_N);
    menuG.add(menuItem); 
    menuItem.addActionListener(this);
    
    //submenu for levels 
    subMenu = new JMenu("Levels");
    ButtonGroup myGroup = new ButtonGroup();
    rbMenuItem = new JRadioButtonMenuItem("Beginner");
    rbMenuItem.setSelected(true);
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
    
    menuItemPa = new JMenuItem("Pause",
                               KeyEvent.VK_P);
    menuG.add(menuItemPa);   
    menuItemPa.addActionListener(this);
    
    menuItemUn = new JMenuItem("Unpause",
                               KeyEvent.VK_P);
    menuG.add(menuItemUn);   
    menuItemUn.addActionListener(this);
    menuItemUn.setEnabled(false); 
    
    menuItemR = new JMenuItem("Resume",
                             KeyEvent.VK_R);
    menuG.add(menuItemR);   
    menuItemR.addActionListener(this);
    /*
     menuItem = new JMenuItem("Scoreboard",
     KeyEvent.VK_S);
     menuG.add(menuItem);   
     menuItem.addActionListener(this);
     */
    menuItem = new JMenuItem("Exit",
                             KeyEvent.VK_E);
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
    
    // Panels
    JPanel headerPanel = new JPanel();
    
    // Layouts
    GridLayout grid = new GridLayout(board.length, board[0].length); // CHANGE THE SIZE SO ITS DYNAMIC!!
    BoxLayout box = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
    BoxLayout boxLayout = new BoxLayout(headerPanel, BoxLayout.X_AXIS);
    SpringLayout spring = new SpringLayout(); 
    
    // Setting Layouts
    setLayout(box);
    boardPanel.setLayout(grid);
    headerPanel.setLayout(spring);
    
    // Sets size of headerPanel
    headerPanel.setMinimumSize(new Dimension(750, 50));
    headerPanel.setPreferredSize(new Dimension(750, 50));
    headerPanel.setMaximumSize(new Dimension(750, 50));
    
    //JLabel
    JLabel remainingBombs = new JLabel("Remaining Bombs:"); 
    JLabel timeElapsedLabel = new JLabel("Time elasped: "); 
    
    //BombLabel sizes
    bombLabel.setMinimumSize(new Dimension(70, 50));
    bombLabel.setPreferredSize(new Dimension(70, 50));
    bombLabel.setMaximumSize(new Dimension(70, 50));
    
    // MenuBar sizes
    menuBar.setMinimumSize(new Dimension(750, 30));
    menuBar.setPreferredSize(new Dimension(750, 30));
    menuBar.setMaximumSize(new Dimension(750, 30));
    
    // Sets size of Facebutton
    faceButton.setMinimumSize(new Dimension(50, 40));
    faceButton.setPreferredSize(new Dimension(50, 40));
    faceButton.setMaximumSize(new Dimension(50,40));
    
    // Sets characteristics of faceButton 
    faceButton.setIcon(smileyFace);
    
    //Timer-related Code
    myTimer = new Timer(1000, new ActionListener(){
      public void actionPerformed (ActionEvent e){
        time++; 
        timeLabel.setText(""+ time);
      }
    }); 
    
    // Adds JButtons to boardPanel in 2D array manner 
    fill(board);
    
    // ActionListener for FaceButton
    faceButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){ 
        gameOver = false; 
        faceButton.setIcon(smileyFace);
        emptyBoard(); // empties out the board 
        resetTracker();
        if (level.equals("B")){
          clearedSpots = (board.length*board[0].length) - 10; 
          addBombs(10); // adds appropriate number of bombs 
          numBombs = 10;
        } else if (level.equals("I")){
          clearedSpots = (board.length*board[0].length) - 40; 
          addBombs(40); // adds appropriate number of bombs 
          numBombs = 40;
        } else if (level.equals("E")){
          clearedSpots = (board.length*board[0].length) - 99; 
          addBombs(99); // adds appropriate number of bombs 
          numBombs = 99;
        }
        
        for (int i = 0; i < board.length; i++){
          for (int k = 0; k < board[0].length; k++){
            board[i][k].setBorder(BorderFactory.createBevelBorder(0));
          }
        }
        time = 0; 
        timeLabel.setText("" + time);
        bombLabel.setText("" + numBombs);
      }
    });
    
    // fills the board array
    addBombs(10);  
    
    // Adds components to headerPanel
    headerPanel.add(remainingBombs); 
    headerPanel.add(Box.createHorizontalStrut(5)); // creates spaces in panel
    headerPanel.add(bombLabel); 
    headerPanel.add(faceButton);
    headerPanel.add(timeElapsedLabel); 
    headerPanel.add(Box.createHorizontalStrut(5));
    headerPanel.add(timeLabel); 
    
    // Add panels to frame
    add(menuBar);   //add the menu bar to top of frame 
    add(headerPanel); 
    add(boardPanel); 
    
    // Layout of HeaderPanel
    spring.putConstraint(SpringLayout.WEST, timeElapsedLabel, 620, SpringLayout.WEST, headerPanel);
    spring.putConstraint(SpringLayout.WEST, timeLabel, 715, SpringLayout.WEST, headerPanel);
    spring.putConstraint(SpringLayout.EAST, faceButton, 390, SpringLayout.WEST, headerPanel);
    spring.putConstraint(SpringLayout.WEST, bombLabel, 135, SpringLayout.WEST, headerPanel);
    spring.putConstraint(SpringLayout.WEST, remainingBombs, 10, SpringLayout.WEST, headerPanel);
    
    spring.putConstraint(SpringLayout.NORTH, timeElapsedLabel, 10, SpringLayout.NORTH, headerPanel);
    spring.putConstraint(SpringLayout.NORTH, timeLabel, 10, SpringLayout.NORTH, headerPanel);
    spring.putConstraint(SpringLayout.NORTH, bombLabel, -7, SpringLayout.NORTH, headerPanel);
    spring.putConstraint(SpringLayout.NORTH, remainingBombs, 10, SpringLayout.NORTH, headerPanel);
    
    
    //monitor if close cross button is pressed 
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        if (JOptionPane.showConfirmDialog(getContentPane(),
                                          "Are you sure to close this window?", "Closing?", 
                                          JOptionPane.YES_NO_OPTION,
                                          JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
          
          try {
            PrintWriter output = new PrintWriter (new File ("game.txt")); 
                   
            if (gameOver == true)
            {
              output.println("A"); 
            }
            else 
            {
            //output level (board dimension)
            output.println(level); 
            
            //output info of bomb locations 
            for (int r = 0; r < board.length; r++){
              for (int c = 0; c < board[0].length; c++){
                if (Boolean.TRUE.equals(bombTracker[r][c])) //if there is a bomb out put the row and col numbers 
                {
                  output.println(r + " " + c); 
                }
              }
            }
            
            //output symbols for grid 
            for (int r = 0; r < board.length; r++){
              for (int c = 0; c < board[0].length; c++){
                if (!Boolean.FALSE.equals(bombTracker[r][c]) && board[r][c].getIcon() != redFlag)
                {
                  output.print("n "); 
                } //if enabled and unflagged cell 
                else if (board[r][c].getIcon() == redFlag)
                {
                  output.print("f "); 
                } //if flagged cell 
                else if (Boolean.FALSE.equals(bombTracker[r][c]) && board[r][c].getText().equals(""))
                {
                  output.print("0 "); 
                } //if diabled cell with no bomb surounding 
                else 
                {
                  output.print(board[r][c].getText() + " "); 
                } //if diabled cell with some bombs surrounding 
              } //inner for loop
              output.println("");
            } //outer for loop 
            
            output.println(time); //output time 
            output.println(numBombs); //output num of bombs remaining 
            
            }
            
            output.close(); 
          } catch (IOException x){
            System.err.println(x); 
          } //end try catch 
          
          System.exit(0); 
        } //end if yes is clicked 
      } 
    }); //end listener 
    
    
    // Sound-related variables
    setVisible(true); //set frame visible 
  }
  
  public static void addBombs(int numBombs){
    Random rand = new Random(); 
    int r, c; 
    for (int i = 0; i < numBombs; i++){
      r = rand.nextInt(board.length);
      c = rand.nextInt(board[0].length); 
      if (Boolean.TRUE.equals(bombTracker[r][c])){ // if this spot already has a bomb 
        i--; 
      } else {
        bombTracker[r][c] = true; 
      }
    }
  }
  
  public void reveal(){
    faceButton.setIcon(deadFace);
    gameOver = true; 
    myTimer.stop(); 
    for (int r = 0; r < board.length; r++){
      for (int c = 0; c < board[0].length; c++){
        if (board[r][c].isEnabled() == true){
          UIManager.put("Button.disabledText", Color.BLACK);
          board[r][c].setEnabled(false); // makes each button unclickable
          board[r][c].setOpaque(true);
          if (Boolean.TRUE.equals(bombTracker[r][c])){
            board[r][c].setIcon(bomb); // sets the icon of the bomb
            board[r][c].setDisabledIcon(bomb); // ensures that the icon is not greyed out when the button is disabled
          }
        } else if (!(board[r][c].getIcon() == null)){
          board[r][c].setDisabledIcon(bombWithCross);
        }
      }
    }
    invalidate(); 
    revalidate();
    repaint();
    time = 0;
  }
  
  public void emptyBoard(){
    for (int r = 0; r < board.length; r++){
      for (int c = 0; c < board[0].length; c++){
        board[r][c].setText(""); 
        board[r][c].setIcon(null); // removes icon
        board[r][c].setEnabled(true); // re-enables the buttons
      }
    } 
    invalidate(); 
    revalidate();
    repaint();
  }
  
  public void fill(JButton[][] board){
    for (int i = 0; i < board.length; i++){
      for (int k = 0; k < board[0].length; k++){
        board[i][k] = new JButton(); // creates a button to store within the array 
        board[i][k].setOpaque(true);
        board[i][k].setBackground(Color.GRAY); 
        board[i][k].setBorder(BorderFactory.createBevelBorder(0));
        boardPanel.add(board[i][k]); // adds button onto panel 
        final int r = i, c = k;
        
        board[i][k].addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            menuItemR.setEnabled(false);  //disable  resume 
            if (Boolean.TRUE.equals(bombTracker[r][c])){ // user hits a bomb   
              reveal(); 
              executor.schedule(new Runnable() {
                public void run() {
                  try {
                    File explosionFile = new File ("Explosion.wav"); 
                    Clip explosionClip =  AudioSystem.getClip();
                    explosionClip.open(AudioSystem.getAudioInputStream(explosionFile));
                    explosionClip.start(); 
                    Thread.sleep(500);
                    explosionClip.close();
                  } catch (Exception E){
                    System.out.println(E.getMessage()); 
                  }
                }
              }, delay, TimeUnit.MILLISECONDS);
              
            } else { // user hits an empty spot 
              if (myTimer.isRunning() == false){ // starts the timer when the first button is clicked 
                myTimer.start(); 
              }
              delay = 0;
              check(board, r, c);
              if (clearedSpots == 0){ // if the user wins
              
                gameOver = true; // changes the game 
                faceButton.setIcon(sunglasses); // changes face
                myTimer.stop(); // stops the timer 
                executor.schedule(new Runnable() { 
                  public void run() {
                    try { // sound-related code as it releases the cheering sound 
                      File cheeringFile = new File ("Cheering.wav"); 
                      Clip cheeringClip =  AudioSystem.getClip();
                      cheeringClip.open(AudioSystem.getAudioInputStream(cheeringFile));
                      cheeringClip.start(); 
                      Thread.sleep(1100);
                      cheeringClip.close();
                    } catch (Exception E){
                      System.out.println(E.getMessage()); 
                    }
                  }
                }, delay, TimeUnit.MILLISECONDS);
        
              }
            }
          }
        });
        
        board[i][k].addMouseListener(new MouseListener(){
          public void mouseClicked(MouseEvent e){
            if (e.getButton() == MouseEvent.BUTTON3){ // if the user right-clicks on the button
              if (board[r][c].getIcon() != null && board[r][c].getIcon() == redFlag){ // removes the red flag 
                board[r][c].setBorder(BorderFactory.createBevelBorder(0));
                board[r][c].setEnabled(true);
                board[r][c].setIcon(null);
                numBombs++;
                bombLabel.setText("" + numBombs);
              } else if (board[r][c].isEnabled() == true){
                // places the red flag 
                board[r][c].setEnabled(false); // changes if the button can be clicked 
                board[r][c].setOpaque(true);
                board[r][c].setIcon(redFlag); // changes the icon on the button 
                board[r][c].setDisabledIcon(redFlag); // adds the red flag onto the JButton
                board[r][c].setBorder(BorderFactory.createBevelBorder(1)); //get rid of the bevel 
                numBombs--; // decreases the number of bombs 
                bombLabel.setText(""+ numBombs); // changes the number of bombs 
              }
            } 
          }
          
          @Override
          public void mousePressed(MouseEvent e) {
            if (gameOver == false){
              faceButton.setIcon(shockedFace); // changes the face 
            }
          }
          
          @Override
          public void mouseReleased(MouseEvent e) {
            if (gameOver == false){ 
              faceButton.setIcon(smileyFace); // changes the face 
            } 
          }
          
          @Override
          public void mouseEntered(MouseEvent e) {
          }
          
          @Override
          public void mouseExited(MouseEvent e) {
          }
        });
      }
    }
  }
  
  public void restart(int r, int c, int bombs){
    faceButton.setIcon(smileyFace); // resets the face
    myTimer.stop(); // stops the timer
    emptyBoard(); // empties the board
    resetTracker(); //reset tracker for bombs 
    boardPanel.removeAll(); // removes all components from the board 
    board = new JButton[r][c]; // creates a new array
    grid = new GridLayout(board.length, board[0].length); // creates a new gridlayout
    boardPanel.setLayout(grid); // sets the gridLayout
    fill(board); // fills the board 
    numBombs = bombs; // determines the number of bombs 
    bombLabel.setText("" + numBombs); // sets the text 
    addBombs(bombs); // adds bombs to the grid 
    // repainting and refreshing 
    invalidate(); 
    revalidate();
    repaint();
  }
  
  static int delay = 0;
  public static void check (JButton[][] board, int r, int c){ // checks how many bombs are surrounding location clicked 
    int n = 0;
    clearedSpots--;
    
    // how many bombs surround the spot 
    if (r > 0 && Boolean.TRUE.equals(bombTracker[r - 1][c])){ // N
      n++; 
    }
    if (r > 0 && c < board[0].length - 1 && Boolean.TRUE.equals(bombTracker[r - 1][c + 1])){ // NE
      n++; 
    }
    if (c < board[0].length - 1 && Boolean.TRUE.equals(bombTracker[r][c + 1])){ // E
      n++; 
    }
    if (r < board.length - 1 && c < board[0].length - 1 && Boolean.TRUE.equals(bombTracker[r + 1][c + 1])){ // SE
      n++; 
    }
    if (r < board.length - 1 && Boolean.TRUE.equals(bombTracker[r + 1][c])){ // S
      n++; 
    }
    if (r < board.length - 1 && c > 0 && Boolean.TRUE.equals(bombTracker[r + 1][c - 1])){ // SW
      n++; 
    }
    if (c > 0 && Boolean.TRUE.equals(bombTracker[r][c - 1])){ // W
      n++; 
    }
    if (r > 0 && c > 0 && Boolean.TRUE.equals(bombTracker[r - 1][c - 1])){ // NW
      n++; 
    }
    if (n != 0){ // changes value of text on button to correspond with # of surrounding bombs and sets it to unclickable
      board[r][c].setText(Integer.toString(n));
      board[r][c].setOpaque(true);
      bombTracker[r][c] = false; 
      board[r][c].setBorder(BorderFactory.createBevelBorder(1));
      board[r][c].setEnabled(false);
      
      final int nv = n;
      executor.schedule(new Runnable() {
        @Override
        public void run() {
          switch (nv) {
            // changes the color of the text such that each number has its own color 
            case 1:
              //board[r][c].setForeground(Color.BLUE);
              UIManager.put("Button.disabledText", Color.BLUE);
              break;
            case 2:
              UIManager.put("Button.disabledText", Color.GREEN);
              break;
            case 3:
              UIManager.put("Button.disabledText", Color.RED);
              break;
            case 4:
              UIManager.put("Button.disabledText", Color.LIGHT_GRAY);
              break;
            case 5:
              UIManager.put("Button.disabledText", Color.MAGENTA);
              break;
            case 6:
              UIManager.put("Button.disabledText", Color.CYAN);
              break;
            case 7:
              UIManager.put("Button.disabledText", Color.ORANGE);
              break;
            default:
              UIManager.put("Button.disabledText", Color.YELLOW);
          }
          
          board[r][c].repaint();
        }
      }, delay += 10, TimeUnit.MILLISECONDS);
    }
    if (n == 0){ // if there are no bombs surrounding the grid 
      board[r][c].setEnabled(false); // sets the unclickable
      board[r][c].setOpaque(true);
      bombTracker[r][c] = false;
      board[r][c].setBorder(BorderFactory.createBevelBorder(1));
      
      if (r > 0 && board[r - 1][c].isEnabled() == true) { // checks to make sure the button isn't already checked 
        check(board, r - 1, c); // N
      }
      if (r > 0 && c < board[0].length - 1 && board[r - 1][c + 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board, r - 1, c + 1); // NE
      }
      if (c < board[0].length - 1 && board[r][c + 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board, r, c + 1); // E
      }
      if (r < board.length -1 && c < board[0].length -1 && board[r + 1][c + 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board,r + 1, c + 1); // SE
      }
      if (r < board.length - 1 && board[r + 1][c].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board,r + 1, c); // S
      }
      if (r < board.length - 1 && c > 0 && board[r + 1][c - 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board,r + 1, c - 1); // SW
      }
      if (c > 0 && board[r][c - 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board,r, c - 1); // W
      }
      if (r > 0 && c > 0 && board[r - 1][c - 1].isEnabled() == true){ // checks to make sure the button isn't already checked 
        check(board,r - 1, c - 1); // NW
      }
    }
  }
  
  public static void resetTracker()
  {
    for (int i = 0; i < 16; i++)
    {
      for (int j = 0; j < 30; j++)
      {
        bombTracker[i][j] = null; 
      }
    }
  }
  
  public void actionPerformed (ActionEvent e) {
    String command = e.getActionCommand();      //Get information from the action event...
    if (command.equals("New Game")){
      faceButton.setIcon(smileyFace);
      emptyBoard(); // empties out the board 
      resetTracker();
      gameOver = false;
      if (level.equals("B")){
        clearedSpots = (board.length*board[0].length) - 10; 
        addBombs(10); // adds appropriate number of bombs 
        numBombs = 10;
      } else if (level.equals("I")){
        clearedSpots = (board.length*board[0].length) - 40; 
        addBombs(40); // adds appropriate number of bombs 
        numBombs = 40;
      } else if (level.equals("E")){
        clearedSpots = (board.length*board[0].length) - 99; 
        addBombs(99); // adds appropriate number of bombs 
        numBombs = 99;
      }
      time = 0;
      timeLabel.setText("" + time);
      bombLabel.setText("" + numBombs);
    } 
    else if (command.equals("Pause")){
      menuItemPa.setEnabled(false); 
      menuItemUn.setEnabled(true);
      
      myTimer.stop(); 
      
      for (int r = 0; r < board.length; r++){
        for (int c = 0; c < board[0].length; c++){
          board[r][c].setEnabled(false); // re-enables the buttons
        }
      } 
    } 
    else if (command.equals("Resume")){
      menuItemR.setEnabled(false);  
      try{
        Scanner input = new Scanner(new File ("game.txt")); 
        
        while(input.hasNext()){ 
          level = input.next(); //get level 
          
          if (level.equals("A"))
          {
            insDialog = new JDialog(this,"Notification",true);
            insDialog.setSize(new Dimension(250, 100)); //dimensions
            insDialog.setLocation(550, 100); //opening location
            insDialog.setResizable(false); //not resizable
            insDialog.setModal(true);       
            
            ins = new JTextArea(140,140); 
            ins.setText("Sorry, your game could not be loaded as you either lost or won the previous game."); 
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
            
            level = "B"; 
          }
          else
          {
            if (level.equals("I"))
            {
              restart(16,16,40); 
            } 
            else if (level.equals("E"))
            {
              restart(16,30,99); 
            } 
            
            resetTracker(); //reset bombTracker 
            
            //add bomb locations to tracker 
            for (int i = 0; i < numBombs; i++)
            {
              int ro = Integer.parseInt(input.next()); 
              int co = Integer.parseInt(input.next()); 
              
              bombTracker[ro][co] = true; 
            }
            
            //set cells of board 
            for (int r = 0; r < board.length; r++){
              for (int c = 0; c < board[0].length; c++){
                String cell = input.next(); //get input cell symbol 
                
                if (cell.equals("n")){ //if enabled and not flagged 
                  
                } 
                else if (cell.equals("f")) //if flagged 
                {
                  board[r][c].setEnabled(false); // changes if the button can be clicked 
                  board[r][c].setOpaque(true);
                  board[r][c].setIcon(redFlag); // changes the icon on the button 
                  board[r][c].setDisabledIcon(redFlag); // adds the red flag onto the JButton
                  board[r][c].setBorder(BorderFactory.createBevelBorder(1)); //get rid of the bevel 
                }
                else if (cell.equals("0")) //if disabled with no bombs surrounding  
                {
                  board[r][c].setBorder(BorderFactory.createBevelBorder(1)); //get rid of the bevel 
                  board[r][c].setEnabled(false); // changes if the button can be clicked 
                  board[r][c].setOpaque(true);
                  bombTracker[r][c] = false; 
                  
                  clearedSpots--; 
                } 
                else //if disabled with some bombs surrounding 
                {
                  clearedSpots--; 
                  board[r][c].setEnabled(false); // changes if the button can be clicked                 
                  // changes the color of the text such that each number has its own color 
                  /////////////////////////////////////////////////////////////////////////////////
                  final int i = r,j = c;
                  executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                      // changes the color of the text such that each number has its own color 
                      if (cell.equals("1")){
                        //board[r][c].setForeground(Color.BLUE);
                        UIManager.put("Button.disabledText", Color.BLUE);
                      } 
                      else if (cell.equals("2")){
                        UIManager.put("Button.disabledText", Color.GREEN);
                      } 
                      else if (cell.equals("3")){
                        UIManager.put("Button.disabledText", Color.RED);
                      } 
                      else if (cell.equals("4")){
                        UIManager.put("Button.disabledText", Color.LIGHT_GRAY);
                      } 
                      else if (cell.equals("5")){
                        UIManager.put("Button.disabledText", Color.MAGENTA);
                      } 
                      else if (cell.equals("6")){
                        UIManager.put("Button.disabledText", Color.CYAN);
                      } 
                      else if (cell.equals("7")){
                        UIManager.put("Button.disabledText", Color.ORANGE);
                      }
                      else {
                        UIManager.put("Button.disabledText", Color.YELLOW);
                      }
                      
                      board[i][j].repaint();
                    }
                  }, delay += 10, TimeUnit.MILLISECONDS);
                  
                  board[r][c].setOpaque(true);
                  board[r][c].setText(cell); 
                  board[r][c].setBorder(BorderFactory.createBevelBorder(1)); //get rid of the bevel 
                  bombTracker[r][c] = false; 
                } //end if 
              }
            } //end for 
            
            time = Integer.parseInt(input.next()); 
            timeLabel.setText("" + time); 
            
            numBombs = Integer.parseInt(input.next());
            bombLabel.setText("" + numBombs); 
          }          
        }
        input.close(); 
      } catch (IOException ex){
        System.err.println(ex);
      }
    }
    else if (command.equals("Unpause")){
      menuItemPa.setEnabled(true); 
      menuItemUn.setEnabled(false);
      
      myTimer.restart(); 
      
      for (int r = 0; r < board.length; r++){
        for (int c = 0; c < board[0].length; c++){
          if (Boolean.FALSE.equals(bombTracker[r][c])) //&& !board[r][c].getText().equals(""))
            board[r][c].setEnabled(true); // re-enables the buttons
        }
      } 
    }
    else if (command.equals("Instructions")){
      insDialog = new JDialog(this,"Game Instructions",true);
      insDialog.setSize(new Dimension(270, 240)); //dimensions
      insDialog.setLocation(550, 100); //opening location
      insDialog.setResizable(false); //not resizable
      insDialog.setModal(true);   
      
      ins = new JTextArea(20,30); 
      ins.setText("   The purpose of the game is to open all the cells of the board which do not contain a bomb. You lose if you set off a bomb cell. \n \n   Every non-bomb cell you open will tell you the total number of bombs in the eight neighboring cells. Once you are sure that a cell contains a bomb, you can right-click to put a flag it on it as a reminder. Once you have flagged all the bombs around an open cell, you can quickly open the remaining non-bomb cells by shift-clicking on the cell."); 
      ins.setWrapStyleWord(true);
      ins.setLineWrap(true);
      ins.setOpaque(false);
      ins.setEditable(false);
      ins.setFocusable(false);
      ins.setBackground(UIManager.getColor("Label.background"));
      ins.setFont(UIManager.getFont("Label.font"));
      //ins.setBorder(UIManager.getBorder("Label.border"));
      
      insDialog.add(ins); 
      insDialog.setVisible(true); 
    } 
    else if (command.equals("About")){
      insDialog = new JDialog(this,"About",true);
      insDialog.setSize(new Dimension(270, 240)); //dimensions
      insDialog.setLocation(550, 100); //opening location
      insDialog.setResizable(false); //not resizable
      insDialog.setModal(true);       
      
      ins = new JTextArea(140,140); 
      ins.setText("   This Minesweeper game was developed by Emily Lin and Gemma Zhang. This game was developed as a Grade 12 Computer Science project. \n\n   Minesweeper was initially developed by Microsoft Corp. as a functionality of a previous version of the Windows Operating System. There are many variations for Minesweeper which usually involve different minefields and may have more than one mine per cell. \n\n   All rights reserved. "); 
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
    } 
    else if (command.equals("Beginner")){
      level = "B";   //keep track of level selected 
      restart(8,8,10); 
    } 
    else if (command.equals("Intermediate")){
      level = "I";    //keep track of level selected
      restart(16,16,40); 
    } 
    else if (command.equals("Expert")){
      level = "E";    //keep track of level selected
      restart(16,30,99); 
    } 
    else {
      processEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
      
    }
  }
  
  public static void main(String[] args){
    Minesweeper frame = new Minesweeper(); 
  }
}