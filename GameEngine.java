import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.Math;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * GameEngine.java.
 * 
 *
 * @author Marco Gonzalez (mag0089@auburn.edu)
 * @version TODAY
 */
public class GameEngine implements WordSearchGame {

   // Instance Variables
   
   private TreeSet<String> lexicon;
   private String[][] board;
   private int columns;
   private int rows;
   private boolean[][] checked;
   private String wordSoFar;
   private ArrayList<Integer> wordPath1;
   private ArrayList<Placement> wordPath2;
   private SortedSet<String> allValidWords;
   private static final int ALL_POSITIONS = 8;
   
   // Constructor
   
   /**
    * Gives board its dimensions and values.
    * Sets up multiple arraylists;
    */
   public GameEngine() {
   
      lexicon = null;
      board = new String[4][4];
      columns = board.length;
      rows = board[0].length;
      
      board[0][0] = "E";
      board[0][1] = "E";
      board[0][2] = "C";
      board[0][3] = "A";
      board[1][0] = "A";
      board[1][1] = "L";
      board[1][2] = "E";
      board[1][3] = "P";
      board[2][0] = "H";
      board[2][1] = "N";
      board[2][2] = "B";
      board[2][3] = "O";
      board[3][0] = "Q";
      board[3][1] = "T";
      board[3][2] = "T";
      board[3][3] = "Y";
      
      unCheckAll();
   }
   
   // Methods
   
   /**
    * Loads the lexicon into a data structure for later use. 
    * 
    * @param fileName A string containing the name of the file to be opened.
    * @throws IllegalArgumentException if fileName is null
    * @throws IllegalArgumentException if fileName cannot be opened.
    */
   public void loadLexicon(String fileName) {
   
      
      if (fileName == null) {
      
         throw new IllegalArgumentException();
      }
      
      lexicon = new TreeSet<String>();
      
      try { 
         
         Scanner fileScan = new Scanner(new BufferedReader(new FileReader(new File(fileName))));
         
         while (fileScan.hasNext()) {
         
            String str = fileScan.next();
            str = str.toUpperCase();
            lexicon.add(str);
            fileScan.nextLine();
         }
         
      }
      catch (Exception e) {
            
         throw new IllegalArgumentException();
      }
   }
   
   /**
    * Determines if the given word is in the lexicon.
    * 
    * @param wordToCheck The word to validate
    * @return true if wordToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidWord(String wordToCheck) {
   
      if (wordToCheck == null) {
      
         throw new IllegalArgumentException();
      }
      
      if (lexicon == null) {
      
         throw new IllegalStateException();
      }
      
      return lexicon.contains(wordToCheck.toUpperCase());
   }
   
   /**
    * Determines if there is at least one word in the lexicon with the 
    * given prefix.
    * 
    * @param prefixToCheck The prefix to validate
    * @return true if prefixToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if prefixToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidPrefix(String prefixToCheck) {
   
      if (prefixToCheck == null) {
      
         throw new IllegalArgumentException();
      }
      
      if (lexicon == null) {
      
         throw new IllegalStateException();
      }
      
      prefixToCheck = prefixToCheck.toUpperCase();
      
      String abc = lexicon.ceiling(prefixToCheck);
      
      if (abc != null) {
      
         return abc.startsWith(prefixToCheck);
      }
      
      return false;
   }
   
   /**
    * Stores the incoming array of Strings in a data structure that will make
    * it convenient to find words.
    * 
    * @param letterArray This array of length N^2 stores the contents of the
    *     game board in row-major order. Thus, index 0 stores the contents of board
    *     position (0,0) and index length-1 stores the contents of board position
    *     (N-1,N-1). Note that the board must be square and that the strings inside
    *     may be longer than one character.
    * @throws IllegalArgumentException if letterArray is null, or is  not
    *     square.
    */
   public void setBoard(String[] letterArray) {
   
      if (letterArray == null) {
      
         throw new IllegalArgumentException();
      }
      
      int sides = (int)Math.sqrt(letterArray.length);
      
      if (letterArray.length != (sides * sides)) {
      
         throw new IllegalArgumentException();
      }
      
      columns = sides;
      rows = sides;
      board = new String[sides][sides];
      
      int count = 0;
      
      for (int i = 0; i < rows; i++) {
         
         for (int j = 0; j < columns; j++) {
         
            board[i][j] = letterArray[count];
            count++;
         }
      }
      unCheckAll();
   }
   
   
   /**
    * Creates a String representation of the board, suitable for printing to
    *   standard out. Note that this method can always be called since
    *   implementing classes should have a default board.
    */
   public String getBoard() {
   
      String boardString = "";
      
      for (String[] str1 : board) {
      
         for (String str2 : str1) {
         
            boardString = boardString + str2;
         }
      }
      
      return boardString;
   }
   
   /**
    * Determines if the given word is in on the game board. If so, it returns
    * the path that makes up the word.
    * @param wordToCheck The word to validate
    * @return java.util.List containing java.lang.Integer objects with  the path
    *     that makes up the word on the game board. If word is not on the game
    *     board, return an empty list. Positions on the board are numbered from zero
    *     top to bottom, left to right (i.e., in row-major order). Thus, on an NxN
    *     board, the upper left position is numbered 0 and the lower right position
    *     is numbered N^2 - 1.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public List<Integer> isOnBoard(String wordToCheck) {
   
      if (wordToCheck == null) {
      
         throw new IllegalArgumentException();
      }
      
      if (lexicon == null) {
      
         throw new IllegalStateException();
      }
      
      wordPath2 = new ArrayList<Placement>();
      wordToCheck = wordToCheck.toUpperCase();
      wordSoFar = "";
      wordPath1 = new ArrayList<Integer>();
      
      for (int i = 0; i < rows; i++) {
       
         for (int j = 0; j < columns; j++) {
            
            if (wordToCheck.equals(board[i][j])) {
            
               wordPath1.add((i * columns) + j);
               return wordPath1;
            }
            
            if (wordToCheck.startsWith(board[i][j])) {
               
               Placement temp = new Placement(i, j);
               wordPath2.add(temp);
               wordSoFar = board[i][j];
               dfsWordPath(i, j, wordToCheck);
               
               if (!(wordToCheck.equals(wordSoFar))) {
               
                  wordPath2.remove(temp);
               }
               else {
               
                  for (Placement p : wordPath2) {
                  
                     wordPath1.add((p.x * columns) + p.y);
                  }
                  return wordPath1;
               }
            }
         }
      }
      
      return wordPath1;
   }
   
   
   /**
    * Depth First Search for locating particular word & its path.
    */
   private void dfsWordPath(int x, int y, String wordToCheck) {
      
      Placement initial = new Placement(x, y);
      
      unCheckAll();
      pathChecked();
      
      for (Placement p : initial.adjacent()) {
         
         if (!(isChecked(p))) {
         
            check(p);
            
            if (wordToCheck.startsWith(wordSoFar + board[p.x][p.y])) {
            
               wordSoFar = wordSoFar + board[p.x][p.y];
               wordPath2.add(p);
               dfsWordPath(p.x, p.y, wordToCheck);
               
               if (wordToCheck.equals(wordSoFar)) {
               
                  return;
               }
               else {
               
                  wordPath2.remove(p);
                  
                  int end = wordSoFar.length() - board[p.x][p.y].length();
                  wordSoFar = wordSoFar.substring(0, end);
               }
            } 
         }
      
      }
      unCheckAll();
      pathChecked();
      
   }
   
   /**
   * Computes the cummulative score for the scorable words in the given set.
   * To be scorable, a word must (1) have at least the minimum number of characters,
   * (2) be in the lexicon, and (3) be on the board. Each scorable word is
   * awarded one point for the minimum number of characters, and one point for 
   * each character beyond the minimum number.
   *
   * @param words The set of words that are to be scored.
   * @param minimumWordLength The minimum number of characters required per word
   * @return the cummulative score of all scorable words in the set
   * @throws IllegalArgumentException if minimumWordLength < 1
   * @throws IllegalStateException if loadLexicon has not been called.
   */  
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
   
      if (minimumWordLength < 1) {
      
         throw new IllegalArgumentException();
      }
      
      if (lexicon == null) {
      
         throw new IllegalStateException();
      }
      
      int score = 0;
      
      for (String str : words) {
      
         score = score + (str.length() - minimumWordLength) + 1;
      }
      
      return score;
   }
   
   
   /**
    * Retrieves all valid words on the game board, according to the stated game
    * rules.
    * 
    * @param minimumWordLength The minimum allowed length (i.e., number of
    *     characters) for any word found on the board.
    * @return java.util.SortedSet which contains all the words of minimum length
    *     found on the game board and in the lexicon.
    * @throws IllegalArgumentException if minimumWordLength < 1
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public SortedSet<String> getAllValidWords(int minimumWordLength) {
   
      if (minimumWordLength < 1) {
      
         throw new IllegalArgumentException();
      }
      
      if (lexicon == null) {
      
         throw new IllegalStateException();
      }
      
      wordSoFar = "";
      wordPath2 = new ArrayList<Placement>();
      allValidWords = new TreeSet<String>();
      
      for (int i = 0; i < rows; i++) {
      
         for (int j = 0; j < columns; j++) {
         
            wordSoFar = board[i][j];
            
            if (isValidWord(wordSoFar) && wordSoFar.length() >= minimumWordLength) {
            
               allValidWords.add(wordSoFar);  
            }
            
            if (isValidPrefix(wordSoFar)) {
            
               Placement temp = new Placement(i, j);
               wordPath2.add(temp);
               dfsOneWord(i, j, minimumWordLength);
               wordPath2.remove(temp);
            }
         }
      }
      return allValidWords;
   }
   
   /**
    * Depth First Search for finding a specific word.
    */
   private void dfsOneWord(int x, int y, int min) { 
      
      Placement initial = new Placement(x, y);
      
      unCheckAll();
      pathChecked();
      
      for (Placement p : initial.adjacent()) {
      
         if (!(isChecked(p))) {
            
            check(p);
            
            if (isValidPrefix(wordSoFar + board[p.x][p.y])) {
            
               wordSoFar = wordSoFar + board[p.x][p.y];
               wordPath2.add(p);
               
               if (isValidWord(wordSoFar) && wordSoFar.length() >= min) {
               
                  allValidWords.add(wordSoFar);  
               }
               
               dfsOneWord(p.x, p.y, min);
               wordPath2.remove(p);
               
               int end = wordSoFar.length() - board[p.x][p.y].length();
               wordSoFar = wordSoFar.substring(0, end);
            }
         }
      }
      unCheckAll();
      pathChecked();
   }
   
   /**
    * Only sets the elements of wordPath2 as checked.
    */
   private void pathChecked() {
    
      for (int i = 0; i < wordPath2.size(); i++) {
      
         check(wordPath2.get(i));
      }
   }
   
   /**
    * Leaves all positions unchecked.
    */
   private void unCheckAll() {
    
      checked = new boolean[columns][rows];
      
      for (boolean[] y : checked) {
      
         Arrays.fill(y, false);
      }
   }
   
   private boolean isChecked(Placement p) {
      
      return checked[p.x][p.y];
   }
      
   private void check(Placement p) {
      
      checked[p.x][p.y] = true;
   }
      
   private boolean test(Placement p) {
      
      return (p.x >= 0) && (p.x < columns) && (p.y >= 0) && (p.y < rows);
   }
   
   // Private placement class.
   
   private class Placement {
   
      // Instance Variables
      
      int x;
      int y;
      
      // Constructor
      
      public Placement(int x, int y) {
      
         this.x = x;
         this.y = y;
      }
      
      //Methods
      
      public Placement[] adjacent() {
      
         Placement[] adj = new Placement[ALL_POSITIONS];
         Placement p;
         int count = 0;
         
         for (int z = -1; z <= 1; z++) {
         
            for (int k = -1; k <= 1; k++) {
            
               if (!((z == 0) && (k == 0))) {
               
                  p = new Placement(x + z, y + k);
                  
                  if (test(p)) {
                  
                     adj[count++] = p;
                  }
               }
            }
         }
         return Arrays.copyOf(adj, count);
      }
   }
}