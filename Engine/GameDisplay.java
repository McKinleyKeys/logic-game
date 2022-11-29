

package Engine;

import Game.Game;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;


public class GameDisplay {
    
    private Game game;
    private int numberPlayers;
    private int numberInputs;
    private int numberSets;
    private boolean timedGame;
    
    private float boardPosX;
    private float boardPosY;
    private float boardSizeX;
    private float boardSizeY;
    private static final float boardStandardSizeY = 400;
    private float bubbleSizeX = 50;
    private float bubbleSizeY = 50;
    private final float bubbleSpace = 0.5f;
    private float scoreboardPosX;
    private float scoreboardPosY;
    private float scoreboardSizeX;
    private float scoreboardSizeY;
    private final float playerNameSpaceX = 160;
    private final float playerNameSpaceY = 100;
    private final float clockSpace = 200;
    private final float scoreboardTopLinePos = 15f / 24f;
    private final float scoreboardBottomLinePos = 2f / 11f;
    private final float scoreboardSecondLineTimedPos = 14f / 41f;
    private final float scoreboardBottomLineTimedPos = 1f / 14f;
    private float formulaDisplayPosX;
    private float formulaDisplayPosY;
    private float formulaDisplaySizeX;
    private float formulaDisplaySizeY;
    private final float inputButtonSizeX = 200;
    private final float inputButtonSizeY = 40;
    private final float inputButtonSpaceX = 2f / 3f;
    private final float inputButtonSpaceY = 7f / 12f;
    private final float formulaInputSizeX = 450;
    private final float formulaInputSizeY = 40;
    private final float formulaInputSpaceX = 20f / 21f;
    private final float formulaInputSpaceY = 7f / 12f;
    private final float formulaInputSeparation = 4;
    private final float formulaIndentation = 6;
    private final long cursorFlashingTime = Game.SECOND / 2;
    private boolean drawCursor;
    private long cursorLastFlash;
    
    private static final float[] clearColor = {1f, 1f, 1f};
    
    private static final float LINE_WIDTH = 1;
    
    private static final String FONT_STYLE = Font.MONOSPACED;
    private static Font awtFont20;
    private static TrueTypeFont font20;
    private static Font awtFont22;
    private static TrueTypeFont font22;
    private static Font awtFont24;
    private static TrueTypeFont font24;
    private Font awtFontBubble;
    private TrueTypeFont fontBubble;
    
    public GameDisplay() {
        
        initDisplay();
    }
    
    public void startGame(Game game) {
        
        this.game = game;
        
        this.numberPlayers = game.getNumberPlayers();
        this.numberInputs = game.getNumberInputs();
        this.numberSets = game.getNumberSets();
        this.timedGame = game.isTimedGame();
        
        bubbleSizeX = boardStandardSizeY / (numberSets + 1) * bubbleSpace;
        bubbleSizeY = boardStandardSizeY / (numberSets + 1) * bubbleSpace;
        
        boardPosX = Display.getWidth() / 2f - (bubbleSizeX / bubbleSpace) * ((numberInputs + 1) / 2f + 1);
        boardPosY = Display.getHeight() / 2f - (bubbleSizeY / bubbleSpace) * (numberSets + 1) / 2f;
        boardSizeX = (bubbleSizeX / bubbleSpace) * (numberInputs + 1 + 1);
        boardSizeY = (bubbleSizeY / bubbleSpace) * (numberSets + 1);
        
        scoreboardPosX = Display.getWidth() / 2f - clockSpace / 2f - playerNameSpaceX * (float)Math.ceil(numberPlayers / 2f);
        scoreboardPosY = (Display.getHeight() + boardPosY + boardSizeY) / 2f - playerNameSpaceY / 2f;
        scoreboardSizeX = clockSpace + playerNameSpaceX * numberPlayers;
        scoreboardSizeY = playerNameSpaceY;
        
        formulaDisplayPosX = Display.getWidth() / 2f - (inputButtonSizeX / inputButtonSpaceX + formulaInputSizeX / formulaInputSpaceX) / 2f;
        formulaDisplayPosY = (boardPosY / 2f) - ((formulaInputSizeY + (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f) * 2 + formulaInputSeparation) / 2f;
        formulaDisplaySizeX = inputButtonSizeX / inputButtonSpaceX + formulaInputSizeX / formulaInputSpaceX;
        formulaDisplaySizeY = (formulaInputSizeY + (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f) * 2 + formulaInputSeparation;
        
        awtFontBubble = new Font(FONT_STYLE, Font.PLAIN, (int)bubbleSizeX);
        fontBubble = new TrueTypeFont(awtFontBubble, true);
    }
    
    public void render() {
        
        glClear(GL_COLOR_BUFFER_BIT);
        glLoadIdentity();
        
        drawBoard();
        drawScoreboard();
        drawFormulaDisplay();
        
//        drawLine(Display.getWidth() / 2f, 0, Display.getWidth() / 2, Display.getHeight(), LINE_WIDTH, 1f, 0f, 0f);
//        drawLine(0, Display.getHeight() / 2f, Display.getWidth(), Display.getHeight() / 2f, LINE_WIDTH, 1f, 0f, 0f);
        
        Display.update();
        
        Display.sync(60);
    }
    
    private static void initDisplay() {
        
        try {
            
            Display.setDisplayMode(Display.getAvailableDisplayModes()[0]);
            Display.setFullscreen(true);
            Display.setVSyncEnabled(true);
            Display.create();
            Mouse.create();
            Keyboard.create();
        } 
	catch (LWJGLException e) {
		System.exit(0);
	}
        
//        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
	glShadeModel(GL_SMOOTH);        
	glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);                    
 
	glClearColor(clearColor[0], clearColor[1], clearColor[2], 0.0f);                
        glClearDepth(1);                                       
 
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
 
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
	glMatrixMode(GL_MODELVIEW);
        
        awtFont20 = new Font("Monospaced", Font.PLAIN, 20);
        font20 = new TrueTypeFont(awtFont20, true);
        awtFont22 = new Font("Monospaced", Font.PLAIN, 22);
        font22 = new TrueTypeFont(awtFont22, true);
        awtFont24 = new Font("Monospaced", Font.PLAIN, 24);
        font24 = new TrueTypeFont(awtFont24, true);
        
        Keyboard.enableRepeatEvents(true);
    }
    
    private void drawBoard() {
        
        float bubbleAreaX = bubbleSizeX / bubbleSpace;
        float bubbleAreaY = bubbleSizeY / bubbleSpace;
        float emptySpaceX = bubbleAreaX - bubbleSizeX;
        float emptySpaceY = bubbleAreaY - bubbleSizeY;
        
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        
        drawQuad(boardPosX, boardPosY, boardPosX + boardSizeX, boardPosY + boardSizeY, 0.9f, 0.9f, 0.9f);
        
        for (int set = 0; set < numberSets; set++) {
            
            for (int bubble = 0; bubble < numberInputs + 1; bubble++) {
                
                if (game.getBoard()[set][bubble])
                    drawFilledEllipse(boardPosX + (bubble + 1) * bubbleAreaX + bubbleAreaX / 2f,
                                      boardPosY + set * bubbleAreaY + bubbleAreaY / 2f,
                                      bubbleSizeX / 2f, bubbleSizeY / 2f, 0f, 1f, 0f);
                
                drawEllipse(boardPosX + (bubble + 1) * bubbleAreaX + bubbleAreaX / 2f,
                           boardPosY + set * bubbleAreaY + bubbleAreaY / 2f,
                           bubbleSizeX / 2f, bubbleSizeY / 2f, LINE_WIDTH, 0f, 0f, 0f);
                
//                drawLine(0, boardPosY + set * bubbleAreaY + bubbleAreaY / 2f - bubbleSizeY / 2f, Display.getWidth(), boardPosY + set * bubbleAreaY + bubbleAreaY / 2f - bubbleSizeY / 2f, lineThickness, 1f, 0f, 0f);
//                drawLine(0, boardPosY + set * bubbleAreaY + bubbleAreaY / 2f + bubbleSizeY / 2f, Display.getWidth(), boardPosY + set * bubbleAreaY + bubbleAreaY / 2f + bubbleSizeY / 2f, lineThickness, 1f, 0f, 0f);
            }
        }
        
        for (int set = 1; set <= numberSets; set++) {
            
            drawString(fontBubble, boardPosX + emptySpaceX / 2f, boardPosY + boardSizeY - set * bubbleAreaY - bubbleAreaY / 2f - fontBubble.getHeight() / 2f, String.valueOf(set), 0f, 0f, 0f);
        }
        
        for (int input = 0; input < numberInputs; input++) {
            
            drawString(fontBubble,
                       boardPosX + (input + 1) * bubbleAreaX + bubbleAreaX / 2f - fontBubble.getWidth(Character.toString(alphabet[input])) / 2f,
                       boardPosY + boardSizeY - bubbleAreaY / 2f - fontBubble.getHeight() / 2f,
                       Character.toString(alphabet[input]), 0f, 0f, 0f);
        }
        
        drawLine(boardPosX + (numberInputs + 1) * bubbleAreaX, boardPosY, boardPosX + (numberInputs + 1) * bubbleAreaX, boardPosY + boardSizeY, LINE_WIDTH, 0f, 0f, 0f);
        drawString(fontBubble, boardPosX + boardSizeX - bubbleAreaX / 2f - fontBubble.getWidth("*") / 2f, boardPosY + boardSizeY - bubbleAreaY / 2f - fontBubble.getHeight() / 2f, "*", 0f, 0f, 0f);
        
        drawLine(boardPosX + bubbleAreaX, boardPosY, boardPosX + bubbleAreaX, boardPosY + boardSizeY - bubbleAreaY, LINE_WIDTH, 0f, 0f, 0f);
        drawLine(boardPosX + bubbleAreaX, boardPosY + boardSizeY - bubbleAreaY, boardPosX + boardSizeX, boardPosY + boardSizeY - bubbleAreaY, LINE_WIDTH, 0f, 0f, 0f);
//        drawLine(boardPosX + boardSizeX, boardPosY, boardPosX + boardSizeX, boardPosY + boardSizeY - bubbleAreaY, lineThickness, 0f, 0f, 0f);
//        drawLine(boardPosX + bubbleAreaX, boardPosY, boardPosX + boardSizeX, boardPosY, lineThickness, 0f, 0f, 0f);
    }
    
    private void drawScoreboard() {
        
        drawQuad(scoreboardPosX, scoreboardPosY, scoreboardPosX + scoreboardSizeX, scoreboardPosY + scoreboardSizeY, 0.9f, 0.9f, 0.9f);
        
        //draw clock
        drawString(font24, scoreboardPosX + playerNameSpaceX * (float)Math.ceil(numberPlayers / 2f) + clockSpace / 2f - font24.getWidth("Game " + Main.getGameNumber()) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardTopLinePos, "Game " + Main.getGameNumber(), 0f, 0f, 0f);
        drawString(font24, scoreboardPosX + playerNameSpaceX * (float)Math.ceil(numberPlayers / 2f) + clockSpace / 2f - font24.getWidth("Round " + game.getRoundNumber()) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardBottomLinePos, "Round " + game.getRoundNumber(), 0f, 0f, 0f);
        
        //draw player scoreboards
        for (int player = 1; player <= numberPlayers; player++) {
            
            float drawClockSpace = 0;
            if (player > (float)Math.ceil(numberPlayers / 2f))
                drawClockSpace = clockSpace;
            
            //draw name
            drawString(font22, scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f - font22.getWidth("Player " + player) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardTopLinePos, "Player " + player, 0f, 0f, 0f);
            
            if (player - 1 == game.getPlayerTurn())
                drawQuad(scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f - font22.getWidth("Player " + player) / 2f - 3, scoreboardPosY + scoreboardSizeY * scoreboardTopLinePos - 1,
                         scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f + font22.getWidth("Player " + player) / 2f + 3, scoreboardPosY + scoreboardSizeY * scoreboardTopLinePos - 4, 0f, 1f, 0f);
            
            if (timedGame) {
                
                long time = game.getPlayerClocks()[player - 1];
                String playerTime = "";
                
                if (time <= 0)
                    playerTime = "00:00";
                
                else {
                    
                    int minutes = (int)(time / Game.MINUTE);
                    int seconds = (int)((time - (minutes * Game.MINUTE)) / Game.SECOND);
                    
                    if (minutes < 10)
                        playerTime = "0";
                    playerTime += minutes + ":";
                    
                    if (seconds < 10)
                        playerTime += "0";
                    playerTime += seconds;
                }
                
                //draw score
                drawString(font20, scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f - font20.getWidth(String.valueOf(Main.getPlayerScores()[player - 1])) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardSecondLineTimedPos, String.valueOf(Main.getPlayerScores()[player - 1]), 0f, 0f, 0f);
                
                //draw time
                drawString(font22, scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f - font22.getWidth(playerTime) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardBottomLineTimedPos, playerTime, 0f, 0f, 0f);
            }
            
            else
                //draw score
                drawString(font22, scoreboardPosX + playerNameSpaceX * (player - 1) + drawClockSpace + playerNameSpaceX / 2f - font22.getWidth(String.valueOf(Main.getPlayerScores()[player - 1])) / 2f, scoreboardPosY + scoreboardSizeY * scoreboardBottomLinePos, String.valueOf(Main.getPlayerScores()[player - 1]), 0f, 0f, 0f);
        }
    }
    
    private void drawFormulaDisplay() {
        
        drawQuad(formulaDisplayPosX, formulaDisplayPosY, formulaDisplayPosX + formulaDisplaySizeX, formulaDisplayPosY + formulaDisplaySizeY, 0.9f, 0.9f, 0.9f);
        
        //draw input button
        {
            if (game.getInputButtonState() == 1) {
                
                //draw green background
                drawQuad(formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY,
                         formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f, 0f, 1f, 0f);
                
                //draw "Correct"
                drawString(font24, formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX / 2f - font24.getWidth("Correct") / 2f,
                           formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY / 2f - font24.getHeight() / 2f, "Correct", 0f, 0f, 0f);
            }
            
            else if (game.getInputButtonState() == 2) {
                
                //draw red background
                drawQuad(formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY,
                         formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f, 1f, 0f, 0f);
                
                //draw "Incorrect"
                drawString(font24, formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX / 2f - font24.getWidth("Incorrect") / 2f,
                           formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY / 2f - font24.getHeight() / 2f, "Incorrect", 0f, 0f, 0f);
            }
            
            else {
                
                //draw "Input"
                drawString(font24, formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX / 2f - font24.getWidth("Input") / 2f,
                           formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY / 2f - font24.getHeight() / 2f, "Input", 0f, 0f, 0f);
            }
            
            //draw outline
            drawOpenQuad(formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY,
                         formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX, formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f, LINE_WIDTH, 0f, 0f, 0f);
        }
        
        //draw formula input
        {
            //draw white background
            if (game.isInputtingFormula())
                drawQuad(formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX, formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY,
                         formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f, formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f,  1f, 1f, 1f);
            
            //draw outline
            drawOpenQuad(formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX, formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY,
                         formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f, formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f, LINE_WIDTH, 0f, 0f, 0f);
            
            //draw formula
            if (game.getInputFormula().length() > 0)
                drawString(font22, formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX + formulaIndentation,
                           formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY / 2f - font22.getHeight() / 2f, game.getInputFormula(), 0f, 0f, 0f);
            
            //draw cursor
            if (game.isInputtingFormula()) {
                if (System.nanoTime() - cursorLastFlash >= cursorFlashingTime) {
                    drawCursor = !drawCursor;
                    cursorLastFlash = System.nanoTime();
                }
                if (drawCursor)
                    drawQuad(formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX + formulaIndentation + font22.getWidth(game.getInputFormula().substring(0, game.getCursorChar())) - 1,
                             formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY / 2f - font22.getHeight() / 2f,
                             formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX + formulaIndentation + font22.getWidth(game.getInputFormula().substring(0, game.getCursorChar())) + 1,
                             formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY / 2f + font22.getHeight() / 2f, 0f, 0f, 0f);
            }
        }
        
        //draw last formula
        if (game.getLastFormula().length() > 0)
            drawString(font22, formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX + formulaIndentation,
                       formulaDisplayPosY + (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f + formulaInputSizeY / 2f - font22.getHeight() / 2f, game.getLastFormula(), 0.5f, 0.5f, 0.5f);
        
//        drawLine(formulaDisplayPosX + inputButtonSizeX / inputButtonSpaceX, formulaDisplayPosY,
//                 formulaDisplayPosX + inputButtonSizeX / inputButtonSpaceX, formulaDisplayPosY + formulaDisplaySizeY, 1, 1f, 0f, 0f);
    }
    
    public void getInput() {
        
        //mouse inputs
        if (game.isGameRunning()) {
            
            Mouse.poll();
            
            while (Mouse.next()) {
                
                if (Mouse.getEventButtonState()) {
                    
                    if (Mouse.getEventButton() == 0) {
                        
                        //board
                        if (Mouse.getX() >= boardPosX + bubbleSizeX / bubbleSpace && Mouse.getX() <= boardPosX + boardSizeX - bubbleSizeX / bubbleSpace
                            && Mouse.getY() >= boardPosY && Mouse.getY() <= boardPosY + boardSizeY - bubbleSizeY / bubbleSpace) {
                            
                            for (int set = 0; set < numberSets; set++)
                                for (int input = 0; input < numberInputs; input++) {
                                    
                                    float inputPosX = boardPosX + 1.5f * bubbleSizeX / bubbleSpace + input * bubbleSizeX / bubbleSpace;
                                    float inputPosY = boardPosY + bubbleSizeX / bubbleSpace / 2f + set * bubbleSizeX / bubbleSpace;
                                    
                                    if (Math.sqrt(Math.pow(Mouse.getX() - inputPosX, 2) + Math.pow(Mouse.getY() - inputPosY, 2)) <= bubbleSizeX / 2f)
                                        game.toggleInput(set, input);
                                }
                        }
                        
                        //input button
                        if (Mouse.getX() >= formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f && Mouse.getX() <= formulaDisplayPosX + (inputButtonSizeX / inputButtonSpaceX - inputButtonSizeX) / 2f + inputButtonSizeX
                            && Mouse.getY() >= formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f - inputButtonSizeY && Mouse.getY() <= formulaDisplayPosY + formulaDisplaySizeY - (inputButtonSizeY / inputButtonSpaceY - inputButtonSizeY) / 2f) {
                            
                            game.inputButton();
                        }
                        
                        //formula input
                        if (Mouse.getX() >= formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX && Mouse.getX() <= formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f
                            && Mouse.getY() >= formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f - formulaInputSizeY && Mouse.getY() <= formulaDisplayPosY + formulaDisplaySizeY - (formulaInputSizeY / formulaInputSpaceY - formulaInputSizeY) / 2f) {
                            
                            if (!game.isInputtingFormula())
                                game.setInputtingFormula(true);
                            
                            if (game.getInputFormula().length() > 0) {
                                
                                String inputFormula = game.getInputFormula();
                                
                                //move cursor
                                for (int i = 1; i <= inputFormula.length(); i++) {
                                    
                                    if (Mouse.getX() <= formulaDisplayPosX + formulaDisplaySizeX - (formulaInputSizeX / formulaInputSpaceX - formulaInputSizeX) / 2f - formulaInputSizeX + formulaIndentation
                                        + font22.getWidth(inputFormula.substring(0, i - 1)) + font22.getWidth(Character.toString(inputFormula.charAt(i - 1))) / 2f) {
                                        
                                        game.setCursorChar(i - 1);
                                        break;
                                    }
                                    
                                    if (i == inputFormula.length())
                                        game.setCursorChar(i);
                                }
                            }
                            
                            drawCursor = true;
                            cursorLastFlash = System.nanoTime();
                        }
                        
                        else
                            game.setInputtingFormula(false);
                    }
                }
            }
        }
        
        //keyboard inputs
        Keyboard.poll();
        
        while (Keyboard.next()) {
            
            if (Keyboard.getEventKeyState()) {
                
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                    
                    try {
                        Display.setFullscreen(!Display.isFullscreen());
                        render();
                    }
                    catch (LWJGLException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                else if (Keyboard.getEventKey() == Keyboard.KEY_LMETA || Keyboard.getEventKey() == Keyboard.KEY_RMETA) {
                    
                    if (Main.isGameRunning())
                        game.setTimePaused(!game.isTimePaused());
                    else
                        Main.startGame();
                }
                
                if (game.isInputtingFormula()) {
                    
                    drawCursor = true;
                    cursorLastFlash = System.nanoTime();
                    
                    char ch = Keyboard.getEventCharacter();
                    int type = Character.getType(ch);
                    
                    if (!(type == Character.CONTROL || type == Character.PRIVATE_USE)) {
                        
                        String inputFormula = game.getInputFormula();
                        int cursorChar = game.getCursorChar();
                        
                        game.setInputFormula(inputFormula.substring(0,cursorChar) + ch + inputFormula.substring(cursorChar));
                        game.setCursorChar(cursorChar + 1);
                    }
                    
                    switch (Keyboard.getEventKey()) {
                        
                        case Keyboard.KEY_BACK:
                            if (game.getCursorChar() > 0) {
                                String inputFormula = game.getInputFormula();
                                int cursorChar = game.getCursorChar();
                                game.setInputFormula(inputFormula.substring(0, cursorChar - 1) + inputFormula.substring(cursorChar));
                                game.setCursorChar(cursorChar - 1);
                            }
                            break;
                            
                        case Keyboard.KEY_LEFT:
                            if (game.getCursorChar() > 0)
                                game.setCursorChar(game.getCursorChar() - 1);
                            break;
                            
                        case Keyboard.KEY_RIGHT:
                            if (game.getCursorChar() < game.getInputFormula().length())
                                game.setCursorChar(game.getCursorChar() + 1);
                            break;
                        
                        case Keyboard.KEY_RETURN:
                            game.inputButton();
                    }
                }
            }
        }
    }
    
    private static void drawLine(float x1, float y1, float x2, float y2, float width, float r, float g, float b) {
        
        glColor3f(r, g, b);
        
        glLineWidth(width);
        
        glBegin(GL_LINE_STRIP);
        {
            glVertex2f(x1, Display.getHeight() - y1);
            glVertex2f(x2, Display.getHeight() - y2);
        }
        
        glEnd();
    }
    
    private static void drawQuad(float x1, float y1, float x2, float y2, float r, float g, float b) {
        
        glColor3f(r, g, b);
        
        glBegin(GL_QUADS);
        {
            glVertex2f(x1, Display.getHeight() - y1);
            glVertex2f(x2, Display.getHeight() - y1);
            glVertex2f(x2, Display.getHeight() - y2);
            glVertex2f(x1, Display.getHeight() - y2);
        }
        
        glEnd();
    }
    
    private static void drawOpenQuad(float x1, float y1, float x2, float y2, float width, float r, float g, float b) {
        
        drawLine(x1, y1, x1, y2, width, r, g, b);
        drawLine(x1 - 1, y2, x2, y2, width, r, g, b);
        drawLine(x2, y2, x2, y1, width, r, g, b);
        drawLine(x2, y1, x1, y1, width, r, g, b);
    }
    
    private static void drawEllipse(float x, float y, float radiusX, float radiusY, float width, float r, float g, float b) {
        
        glColor3f(r, g, b);
        
        glLineWidth(width);
        
        glBegin(GL_LINE_LOOP);
 
        for (int i = 0; i <= 360; i++) {
            
            glVertex2f((float)(Math.cos(Math.toRadians(i)) * radiusX) + x, (float)(Math.sin(Math.toRadians(i)) * radiusY) + Display.getHeight() - y);
        }

        glEnd();
    }
    
    private static void drawFilledEllipse(float x, float y, float radiusX, float radiusY, float r, float g, float b) {
        
        glColor3f(r, g, b);
        
        for (int i = 0; i < 360; i++) {
            
            glBegin(GL_TRIANGLES);
            {
                glVertex2f((float)(Math.cos(Math.toRadians(i)) * radiusX) + x, (float)(Math.sin(Math.toRadians(i)) * radiusY) + Display.getHeight() - y);
                glVertex2f((float)(Math.cos(Math.toRadians(i + 120)) * radiusX) + x, (float)(Math.sin(Math.toRadians(i + 120)) * radiusY) + Display.getHeight() - y);
                glVertex2f((float)(Math.cos(Math.toRadians(i + 240)) * radiusX) + x, (float)(Math.sin(Math.toRadians(i + 240)) * radiusY) + Display.getHeight() - y);
            }
            
            glEnd();
        }
        
        drawEllipse(x, y, radiusX - 3, radiusY - 3, 4, r, g, b);
    }
    
    private static void drawString(TrueTypeFont font, float x, float y, String text, float r, float g, float b) {
        
        drawString(font, x, y, text, new Color(r, g, b));
    }
    
    private static void drawString(TrueTypeFont font, float x, float y, String text, Color color) {
        
        glEnable(GL_TEXTURE_2D);
        
        //enable transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        font.drawString(x, Display.getHeight() - y - font.getHeight(), text, color);
        
        //disable transparency
        glDisable(GL_BLEND);
        
        glDisable(GL_TEXTURE_2D);
    }
}
