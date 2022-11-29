

package Engine;

import Game.Game;


public class Main {
    
    private static GameDisplay display;
    private static Game game;
    private static boolean gameRunning;
    private static int gameNumber;
    
    private static final int numberPlayers = 2;
    private static final int numberInputs = 3;
    private static final int numberSets = 3;
    private static final long clockStartTime = -1;
    private static final int complexity = 10;
    private static final int maxInputReferences = 2;
    
    private static int[] playerScores = new int[numberPlayers];
    
    public static void main(String[] args) {
        
        display = new GameDisplay();
        startGame();
        
        while (true) {
            
            display.getInput();
            game.update();
            display.render();
        }
    }
    
    public static void startGame() {
        
        setGameNumber(getGameNumber() + 1);
        game = new Game(numberPlayers, numberInputs, numberSets, clockStartTime, complexity, maxInputReferences);
        game.startGame();
        display.startGame(game);
        gameRunning = true;
        
        System.out.println(Game.convFormula(game.getFormula()));
    }
    
    public static void gameOver(int winner) {
        
        gameRunning = false;
        playerScores[winner]++;
    }

    public static boolean isGameRunning() {
        
        return gameRunning;
    }

    public static void setGameRunning(boolean aGameRunning) {
        
        gameRunning = aGameRunning;
    }

    public static int[] getPlayerScores() {
        
        return playerScores;
    }

    public static void setPlayerScores(int[] aPlayerScores) {
        
        playerScores = aPlayerScores;
    }

    public static int getGameNumber() {
        
        return gameNumber;
    }

    public static void setGameNumber(int aGameNumber) {
        
        gameNumber = aGameNumber;
    }
}

//        Game game = new Game(0, 0);
        
//        System.out.println(Game.convFormula(Game.genFormula(5, 3, 2)));
//        System.out.println();
//        System.out.println(Game.convFormula(Game.genFormula(5, 3, 2)));
//        System.out.println();
//        System.out.println(Game.convFormula(Game.genFormula(5, 3, 2)));
//        System.out.println();
//        System.out.println(Game.convFormula(Game.genFormula(5, 3, 2)));
//        System.out.println(Game.toString(new ArrayList<>(Arrays.asList(2, 4, -1, 5, -2, 3))));
//        System.out.println(Game.convFormula(Game.convFormula("(a || b) && c")));
        
//        System.out.println(Game.compareFormula(Game.convFormula("a ^^ b"), Game.convFormula("(a || b) && (!a || !b)"), 2));
        
//        System.out.println(Game.calcFormula(Game.convFormula("(a || b) && !(a && b)"), new boolean[] {false, false}));
        
//        System.out.println(Game.isValidFormula(Game.convFormula("a ) && b || ("), 2));
