

package Game;

import Engine.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Game {
    
    private boolean gameRunning;
    private final int numberPlayers;
    private final int numberInputs;
    private final int numberSets;
    private boolean[][] board;
    private final ArrayList<Integer> formula;
    private int roundNumber;
    private int playerTurn;
    private final boolean timedGame;
    private final long clockStartTime;
    private final long[] playerClocks;
    private String inputFormula;
    private String lastFormula;
    private int inputButtonState;
    private final long INPUT_BUTTON_COOLDOWN = 4l * SECOND;
    private long inputButtonCooldown;
    private boolean inputtingFormula;
    private int cursorChar;
    
    private long lastTime;
    private boolean timePaused;
    
    public static final long SECOND = 1000000000l;
    public static final long MINUTE = 60000000000l;
    
    private static final int openBracketCode = 2;
    private static final int closeBracketCode = 3;
    private static final int NOTCode = 4;
    private static final int ORCode = 5;
    private static final int ANDCode = 6;
    private static final int XORCode = 7;
    
    private static final int oneWordOpType = 1;
    private static final int twoWordOpType = 2;
    private static final int[] oneWordOps = {4};
    private static final int[] twoWordOps = {5, 6, 7};
    private static final int[] operations = {4, 5, 6, 7};
    private static final double[] opChances = {1, 1, 1, 2d / 3d};
    private static final double[] oneWordOpChances = {1};
    private static final float bracketChance = 2;
    
    private static final int NOTOrder = 0;
    private static final int OROrder = 3;
    private static final int ANDOrder = 1;
    private static final int XOROrder = 2;
    private static final int[] opOrders = {0, 1, 2, 3};
    
    public Game(int numberPlayers, int numberInputs, int numberSets, long clockStartTime, int complexity, int maxInputReferences) {
        
        this.numberPlayers = numberPlayers;
        this.numberInputs = numberInputs;
        this.numberSets = numberSets;
        this.timedGame = clockStartTime > 0;
        this.clockStartTime = clockStartTime;
        
        playerClocks = new long[numberPlayers];
        Arrays.fill(playerClocks, clockStartTime);
        
        board = new boolean[numberSets][numberInputs + 1];
        
        while (true) {
            
            ArrayList<Integer> testFormula = genFormula(complexity, numberInputs, maxInputReferences);
            
            if (isUsableFormula(testFormula, numberInputs)) {
                formula = testFormula;
                break;
            }
        }
        
        roundNumber = 1;
        
        inputFormula = "";
        lastFormula = "";
        inputButtonState = 0;
    }
    
    public void startGame() {
        
        updateBoard();
        
        gameRunning = true;
        timePaused = false;
        lastTime = System.nanoTime();
    }
    
    public void update() {
        
        if (gameRunning) {
            
            if (!timePaused) {
                
                //player clocks
                playerClocks[playerTurn] -= System.nanoTime() - lastTime;
                
                if (playerClocks[playerTurn] < 0)
                    playerClocks[playerTurn] = 0;
            }
            
            //input button cooldown
            if (inputButtonCooldown > 0)
                inputButtonCooldown -= System.nanoTime() - lastTime;
            
            if (inputButtonCooldown <= 0)
                inputButtonState = 0;
        }
        
        lastTime = System.nanoTime();
    }
    
    public void updateBoard() {
        
        for (int set = 0; set < numberSets; set++)
            board[set][numberInputs] = calcFormula(formula, Arrays.copyOfRange(board[set], 0, board[set].length - 1));
    }
    
    public void nextPlayerTurn() {
        
        playerTurn++;
        if (playerTurn >= numberPlayers)
            playerTurn = 0;
    }
    
    public void toggleInput(int set, int input) {
        
        board[set][input] = !board[set][input];
        updateBoard();
        
        nextPlayerTurn();
    }
    
    public void inputButton() {
        
        boolean incorrect = false;
        
        boolean validFormula = isValidFormula(convFormula(inputFormula), numberInputs);
        if (!validFormula) {
            if (isValidFormula(convFormula(inputFormula + ")"), numberInputs)) {
                validFormula = true;
                inputFormula += ")";
            }
        }
        
        if (validFormula && inputFormula.length() > 0) {
            
            if (compareFormula(formula, convFormula(inputFormula), numberInputs)) {
                
                gameRunning = false;
                inputButtonState = 1;
                inputtingFormula = false;
                
                Main.gameOver(playerTurn);
            }
            
            else
                incorrect = true;
        }
        
        else
            incorrect = true;
        
        if (incorrect) {
            
            inputButtonState = 2;
            inputButtonCooldown = INPUT_BUTTON_COOLDOWN;
            lastFormula = inputFormula;
            inputFormula = "";
            cursorChar = 0;
            inputtingFormula = false;
            
            nextPlayerTurn();
        }
    }
    
    private static ArrayList<Integer> genFormula(int complexity, int numberInputs, int maxInputReferences) {
        
        ArrayList<Integer> formula = new ArrayList<>();
        
        Random rand = new Random();
        
        formula.add(-(rand.nextInt(numberInputs) + 1));
        
        int currMaxInputReferences = 1;
        boolean allInputsUsed = false;
        
        mainLoop:
        for (int i = 0; i < complexity; i++) {
            
            int[] inputReferences = new int[numberInputs];
            
            ArrayList<int[]> values = new ArrayList<>();
            //put start index and end index of all values in values
            for (int a = 0; a < formula.size(); a++) {
                
                if (formula.get(a) < 0) {
                    
                    inputReferences[Math.abs(formula.get(a)) - 1]++;
                    
                    if (a == 0)
                        values.add(new int[] {a, a});
                    
                    else if (getOpType(formula.get(a - 1)) == oneWordOpType)
                        values.add(new int[] {a - 1, a});
                    
                    else
                        values.add(new int[] {a, a});
                }
                
                else if (formula.get(a) == openBracketCode) {
                    
                    int brackets = 1;
                    for (int e = a + 1; e < formula.size(); e++) {
                        
                        switch (formula.get(e)) {
                            
                            case openBracketCode:
                                brackets++;
                                break;
                            case closeBracketCode:
                                brackets--;
                                if (brackets == 0) {
                                    
                                    if (a == 0)
                                        values.add(new int[] {a, e});
                                    
                                    else if (getOpType(formula.get(a - 1)) == oneWordOpType)
                                        values.add(new int[] {a - 1, e});
                                    
                                    else
                                        values.add(new int[] {a, e});
                                }
                                break;
                        }
                    }
                }
            }
            
            //make sure all inputs have been referenced the same amount of times. Prevent inputs from getting too far ahead
            if (currMaxInputReferences < maxInputReferences) {
                
                boolean allInputsReferenced = true;
                for (int input : inputReferences)
                    if (input < currMaxInputReferences) {
                        allInputsReferenced = false;
                        break;
                    }
                
                if (allInputsReferenced)
                    currMaxInputReferences++;
            }
            
            boolean[] inputsUsed = new boolean[numberInputs];
            for (int a = 0; a < inputReferences.length; a++)
                if (inputReferences[a] >= currMaxInputReferences)
                    inputsUsed[a] = true;
            
            ArrayList<Integer> unusedInputs = new ArrayList<>(); 
            for (int a = 0; a < inputsUsed.length; a++)
                if (!inputsUsed[a])
                    unusedInputs.add(-(a + 1));
            
            if (unusedInputs.isEmpty())
                allInputsUsed = true;
            
            //random
            int[] operations;
            double[] chances;
            
            if (allInputsUsed) {
                operations = oneWordOps;
                chances = oneWordOpChances;
            }
            else {
                operations = Game.operations;
                chances = opChances;
            }
            
            //don't create brackets if the formula is only 1 big
            if (values.size() > 1) {
                chances = Arrays.copyOf(chances, chances.length + 1);
                chances[chances.length - 1] = bracketChance;
            }
            int operation = random(chances);
//            if (values.size() > 1)
//                operation = rand.nextInt(operations.length + 1);
//            else
//                operation = rand.nextInt(operations.length);
            
            //brackets
            if (operation == operations.length) {
                
                int startPos = rand.nextInt(values.size() - 1);
                int endPos = rand.nextInt(values.size() - (startPos + 1));
                
                formula.add(values.get(startPos)[0], openBracketCode);
                formula.add(values.get(endPos + startPos + 1)[1] + 2, closeBracketCode);
            }
            
            else {
                
                int opType = getOpType(operations[operation]);
                
                int pos = rand.nextInt(values.size());
                int beforeAfter = 0;
                
                if (opType == twoWordOpType)
                    beforeAfter = rand.nextInt(2);
                
                int index = values.get(pos)[beforeAfter] + beforeAfter;
                
                formula.add(index, operations[operation]);
                
                if (opType == twoWordOpType) {
                    
                    int val = rand.nextInt(unusedInputs.size());
                    
                    formula.add(index + beforeAfter, unusedInputs.get(val));
                }
            }
            
            //cleanup
            if (i == complexity - 1) {
                
                //prevent double NOTs
                boolean NOTBefore = false;
                for (int a = 0; a < formula.size(); a++) {
                    
                    if (formula.get(a) == NOTCode) {
                        
                        if (NOTBefore) {
                            
                            formula.remove(a - 1);
                            i -= 1;
                            a -= 1;
                        }
                        else
                            NOTBefore = true;
                    }
                    else
                        NOTBefore = false;
                }
                
                //prevent brackets around nothing
                boolean bracketBefore = false;
                for (int a = 0; a < formula.size() - 1; a++) {
                    
                    if (formula.get(a) == openBracketCode) {
                        
                        if (bracketBefore) {
                            
                            int brackets = 1;
                            for (int e = a + 1; e < formula.size() - 1; e++) {
                                
                                if (formula.get(e) == openBracketCode)
                                    brackets++;
                                
                                else if (formula.get(e) == closeBracketCode) {
                                    
                                    brackets--;
                                    
                                    if (brackets == 0) {
                                        
                                        if (formula.get(e + 1) == closeBracketCode) {
                                            
                                            formula.remove(e + 1);
                                            formula.remove(a - 1);
                                            i -= 1;
                                            continue mainLoop;
//                                            a -= 1;
                                        }
                                        
                                        break;
                                    }
                                }
                            }
                        }
                        else
                            bracketBefore = true;
                    }
                    else if (getOpType(formula.get(a)) != 1)
                        bracketBefore = false;
                    
                    if (formula.get(a) < 0 && a > 0) {
                        
                        if (formula.get(a - 1) == openBracketCode && formula.get(a + 1) == closeBracketCode) {
                            
                            formula.remove(a + 1);
                            formula.remove(a - 1);
                            i -= 1;
                            continue mainLoop;
//                            a -= 1;
                        }
                        
                        else if (getOpType(formula.get(a - 1)) == oneWordOpType && a > 1) {
                            
                            if (formula.get(a - 2) == openBracketCode && formula.get(a + 1) == closeBracketCode) {
                                
                                formula.remove(a + 1);
                                formula.remove(a - 2);
                                i -= 1;
                                continue mainLoop;
//                                a -= 1;
                            }
                        }
                    }
                }
                
                //remove brackets around whole formula
                if (formula.get(0) == openBracketCode && formula.get(formula.size() - 1) == closeBracketCode) {
                    
                    int brackets = 1;
                    for (int a = 1; a < formula.size(); a++) {
                        
                        if (formula.get(a) == openBracketCode)
                            brackets++;
                        else if (formula.get(a) == closeBracketCode)
                            brackets--;
                        
                        if (brackets == 0) {
                            
                            if (a == formula.size() - 1) {
                                
                                formula.remove(formula.size() - 1);
                                formula.remove(0);
                                i -= 1;
                                continue;
                            }
                            
                            else
                                break;
                        }
                    }
                    
//                    if (brackets == 0) {
//                        
//                        formula.remove(formula.size() - 1);
//                        formula.remove(0);
//                        i -= 1;
//                        continue;
//                    }
                }
            }
        }
        
        return formula;
    }
    
    private static boolean calcFormula(ArrayList<Integer> formulaArray, boolean[] inputs) {
        
        ArrayList<Integer> formula = (ArrayList)formulaArray.clone();
        
        //brackets
        for (int i = 0; i < formula.size(); i++)
            if (formula.get(i) == openBracketCode) {
                
                int brackets = 1;
                for (int a = i + 1; a < formula.size(); a++) {
                    
                    if (formula.get(a) == openBracketCode)
                        brackets++;
                    
                    else if (formula.get(a) == closeBracketCode)
                        brackets--;
                    
                    if (brackets == 0) {
                        
                        boolean res = calcFormula(new ArrayList<>(formula.subList(i + 1, a)), inputs);
                        int resInt = toInt(res);
                        
                        for (int e = a; e >= i; e--)
                            formula.remove(e);
                        
                        formula.add(i, resInt);
                        
                        break;
                    }
                }
            }
        
        //inputs
        for (int i = 0; i < formula.size(); i++)
            if (formula.get(i) < 0)
                formula.set(i, toInt(inputs[Math.abs(formula.get(i)) - 1]));
        
        //operations
        for (int order : opOrders)
            for (int i = 0; i < formula.size(); i++)
                if (getOrder(formula.get(i)) <= order) {
                    
                    switch (getOpType(formula.get(i))) {
                        
                        case oneWordOpType:
                            formula.set(i, evaluateOp(formula.get(i), formula.get(i + 1)));
                            formula.remove(i + 1);
                            break;
                        case twoWordOpType:
                            formula.set(i, evaluateOp(formula.get(i), formula.get(i - 1), formula.get(i + 1)));
                            formula.remove(i + 1);
                            formula.remove(i - 1);
                            break;
                    }
                }
        
        return toBool(formula.get(0));
    }
    
    private static boolean compareFormula(ArrayList<Integer> formula1, ArrayList<Integer> formula2, int numberInputs) {
        
        boolean[] inputs = new boolean[numberInputs];
        
        mainLoop:
        while (true) {
            
            if (!(calcFormula(formula1, inputs) == calcFormula(formula2, inputs)))
                return false;
            
            for (int i = 0; i < inputs.length; i++) {
                
                if (inputs[i]) {
                    
                    if (i == inputs.length - 1)
                        break mainLoop;
                    
                    inputs[i] = false;
                    continue;
                }
                
                inputs[i] = true;
                break;
            }
        }
        
        return true;
    }
    
    private static boolean isValidFormula(ArrayList<Integer> formula, int numberInputs) {
        
        int brackets = 0;
        
        for (int i = 0; i < formula.size(); i++) {
            
            if (getOpType(formula.get(i)) == oneWordOpType) {
                
                if (i < formula.size() - 1) {
                    
                    if (formula.get(i + 1) == closeBracketCode || getOpType(formula.get(i + 1)) == oneWordOpType || getOpType(formula.get(i + 1)) == twoWordOpType)
                        return false;
                }
                
                else
                    return false;
            }
            
            else if (getOpType(formula.get(i)) == twoWordOpType) {
                
                if (i > 0 && i < formula.size() - 1) {
                    
                    if (formula.get(i - 1) == openBracketCode || getOpType(formula.get(i - 1)) == oneWordOpType || getOpType(formula.get(i - 1)) == twoWordOpType
                        || formula.get(i + 1) == closeBracketCode || getOpType(formula.get(i + 1)) == twoWordOpType)
                        
                        return false;
                }
                
                else
                    return false;
            }
            
            else if (formula.get(i) < 0) {
                
                if (Math.abs(formula.get(i)) <= numberInputs) {
                    
                    if (i > 0)
                        if (!(formula.get(i - 1) == openBracketCode || getOpType(formula.get(i - 1)) == oneWordOpType || getOpType(formula.get(i - 1)) == twoWordOpType))
                            return false;
                    
                    if (i < formula.size() - 1)
                        if (!(formula.get(i + 1) == closeBracketCode || getOpType(formula.get(i + 1)) == twoWordOpType))
                            return false;
                }
                
                else
                    return false;
            }
            
            else if (formula.get(i) == openBracketCode) {
                
                brackets++;
                
                if (i < formula.size() - 1) {
                    if (!(formula.get(i + 1) < 0 || getOpType(formula.get(i + 1)) == oneWordOpType))
                        return false;
                }
                else
                    return false;
            }
            
            else if (formula.get(i) == closeBracketCode) {
                
                brackets--;
                
                if (i > 0) {
                    if (!(formula.get(i - 1) < 0))
                        return false;
                }
                else
                    return false;
            }
            
            else
                return false;
            
            if (brackets < 0)
                return false;
        }
        
        return brackets == 0;
    }
    
    private static boolean isUsableFormula(ArrayList<Integer> formula, int numberInputs) {
        
        if (compareFormula(formula, new ArrayList<>(Arrays.asList(0)), numberInputs) || compareFormula(formula, new ArrayList<>(Arrays.asList(1)), numberInputs))
            return false;
        
        for (int input = 0; input < numberInputs; input++)
            if (compareFormula(formula, new ArrayList<>(Arrays.asList(-(input + 1))), numberInputs))
                return false;
        
        return true;
    }
    
    public static String convFormula(ArrayList<Integer> formula) {
        
        String res = "";
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        
        for (Integer val : formula) {
            
            if (val < 0)
                res += alphabet[-val - 1];
            
            switch (val) {
                
                case NOTCode:
                    res += "!";
                    break;
                case ORCode:
                    res += " || ";
                    break;
                case ANDCode:
                    res += " && ";
                    break;
                case XORCode:
                    res += " ^^ ";
                    break;
                case openBracketCode:
                    res += "(";
                    break;
                case closeBracketCode:
                    res += ")";
                    break;
            }
        }
        
        return res;
    }
    
    public static ArrayList<Integer> convFormula(String formula) {
        
        ArrayList<Integer> res = new ArrayList<>();
        
        char[] alphabetArray = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        ArrayList<Character> alphabet = new ArrayList<>();
        for (char ch : alphabetArray)
            alphabet.add(ch);
        
        char[] capsAlphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        ArrayList<Character> capsAlphabet = new ArrayList<>();
        for (char ch : capsAlphabetArray)
            capsAlphabet.add(ch);
        
        char lastChar = (char)0;
        for (Character ch : formula.toCharArray()) {
            
            if (alphabet.contains(ch))
                res.add(-(alphabet.indexOf(ch) + 1));
            
            else if (capsAlphabet.contains(ch))
                res.add(-(capsAlphabet.indexOf(ch) + 1));
            
            switch (ch) {
                
                case '!':
                    res.add(NOTCode);
                    lastChar = '!';
                    break;
                case '|':
                    if (lastChar == '|')
                        res.add(ORCode);
                    lastChar = ch;
                    break;
                case '&':
                    if (lastChar == '&')
                        res.add(ANDCode);
                    lastChar = ch;
                    break;
                case '^':
                    if (lastChar == '^')
                        res.add(XORCode);
                    lastChar = ch;
                    break;
                case '(':
                    res.add(openBracketCode);
                    lastChar = '(';
                    break;
                case ')':
                    res.add(closeBracketCode);
                    lastChar = ')';
                    break;
                case ' ':
                    lastChar = (char)0;
                    break;
            }
        }
        
        return res;
    }
    
    private static int random(double[] chances) {
        
        double total = 0;
        for (double prob : chances)
            total += prob;
        
        double[] doubleChances = new double[chances.length];
        for (int i = 0; i < chances.length; i++)
            doubleChances[i] = chances[i] / total;
        
        Random rand = new Random();
        double randDouble = rand.nextDouble();
        
        double amt = 0;
        for (int i = 0; i < doubleChances.length; i++) {
            
            if (randDouble <= amt + doubleChances[i])
                return i;
            
            amt += doubleChances[i];
        }
        
        return 0;
    }
    
    private static int evaluateOp(int operation, int value) {
        
        boolean val = toBool(value);
        
        switch (operation) {
            
            case NOTCode:
                return toInt(!val);
        }
        
        return 0;
    }
    
    private static int evaluateOp(int operation, int value1, int value2) {
        
        boolean val1 = toBool(value1);
        boolean val2 = toBool(value2);
        
        switch (operation) {
            
            case ORCode:
                return toInt(val1 || val2);
            
            case ANDCode:
                return toInt(val1 && val2);
            
            case XORCode:
                return toInt((val1 || val2) && !(val1 && val2));
        }
        
        return 0;
    }
    
    private static int getOrder(int operation) {
        
        switch (operation) {
            
            case NOTCode:
                return NOTOrder;
            case ORCode:
                return OROrder;
            case ANDCode:
                return ANDOrder;
            case XORCode:
                return XOROrder;
        }
        
        return 0;
    }
    
    private static int getOpType(int operation) {
        
        for (int i : oneWordOps)
            if (i == operation)
                return oneWordOpType;
        
        for (int i : twoWordOps)
            if (i == operation)
                return twoWordOpType;
        
        return 0;
    }
    
    private static int toInt(boolean bool) {
        
        if (bool)
            return 1;
        else
            return 0;
    }
    
    private static boolean toBool(int val) {
        
        return val == 1;
    }

    public int getNumberPlayers() {
        
        return numberPlayers;
    }

    public int getNumberInputs() {
        
        return numberInputs;
    }

    public int getNumberSets() {
        
        return numberSets;
    }

    public boolean[][] getBoard() {
        
        return board;
    }

    public void setBoard(boolean[][] board) {
        
        this.board = board;
    }

    public ArrayList<Integer> getFormula() {
        
        return formula;
    }
    
    public int getRoundNumber() {
        
        return roundNumber;
    }
    
    public void setRoundNumber(int roundNumber) {
        
        this.roundNumber = roundNumber;
    }

    public boolean isTimedGame() {
        
        return timedGame;
    }
    
    public long getClockStartTime() {
        
        return clockStartTime;
    }
    
    public long[] getPlayerClocks() {
        
        return playerClocks;
    }

    public boolean isTimePaused() {
        
        return timePaused;
    }

    public void setTimePaused(boolean timePaused) {
        
        this.timePaused = timePaused;
        
        lastTime = System.nanoTime();
    }

    public String getInputFormula() {
        
        return inputFormula;
    }

    public void setInputFormula(String inputFormula) {
        
        this.inputFormula = inputFormula;
    }

    public String getLastFormula() {
        
        return lastFormula;
    }

    public void setLastFormula(String lastFormula) {
        
        this.lastFormula = lastFormula;
    }

    public int getPlayerTurn() {
        
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        
        this.playerTurn = playerTurn;
    }

    public int getInputButtonState() {
        
        return inputButtonState;
    }

    public void setInputButtonState(int inputButtonState) {
        
        this.inputButtonState = inputButtonState;
    }

    public boolean isGameRunning() {
        
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        
        this.gameRunning = gameRunning;
    }

    public boolean isInputtingFormula() {
        
        return inputtingFormula;
    }

    public void setInputtingFormula(boolean inputtingFormula) {
        
        this.inputtingFormula = inputtingFormula;
    }

    public int getCursorChar() {
        
        return cursorChar;
    }

    public void setCursorChar(int cursorChar) {
        
        this.cursorChar = cursorChar;
    }
}
