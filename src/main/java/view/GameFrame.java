package view;

import model.GameConfiguration;
import model.GameState;
import utils.Point;
import utils.Snake;

import javax.swing.*;
import java.awt.event.KeyListener;

public class GameFrame extends JFrame
{
    private final int gameFieldWidth;
    private final int gameFieldHeight;
    private final int dimension = 20;
    private GamingField gamingField;

    public GameFrame(int width, int height)
    {
        this.gameFieldWidth = width;
        this.gameFieldHeight = height;

        this.setTitle("Змейка");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
        this.setBounds(10, 10, 1000, 700);
        this.setVisible(true);
        this.setFocusable(true);

        initGamingField();
    }

    private void initGamingField()
    {
        gamingField = new GamingField(gameFieldWidth, gameFieldHeight);
        this.add(gamingField);
    }

    public void updateGamingField(GameState state)
    {
        gamingField.updateField(state);
    }

    public void onArrowPress(KeyListener keyListener)
    {
        this.addKeyListener(keyListener);
    }
}



