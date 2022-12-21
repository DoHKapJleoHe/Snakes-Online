package controller;

import model.GameConfiguration;
import model.GameState;
import utils.Point;
import utils.Snake;
import view.GameFrame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private GameConfiguration config;
    private GameState gameState;
    private GameFrame gameFrame;
    private Timer timer;

    public GameController(int width, int height) {
        config = new GameConfiguration(width, height);
        gameFrame = new GameFrame(width, height);
        gameState = new GameState(config, gameFrame);

        Snake snake = new Snake(1, new Point(5, 5));
        gameState.addSnake(snake);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameState();
            }
        }, 0, 500);

        gameFrame.onArrowPress(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_LEFT) {
                    System.out.println("Left");
                    gameState.updateSnakeDirection(0, "LEFT");
                } else if (code == KeyEvent.VK_RIGHT) {
                    System.out.println("Right");
                    gameState.updateSnakeDirection(0, "RIGHT");
                } else if (code == KeyEvent.VK_UP) {
                    System.out.println("Up");
                    gameState.updateSnakeDirection(0, "UP");
                } else {
                    System.out.println("Down");
                    gameState.updateSnakeDirection(0, "DOWN");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void updateGameState() {
        gameState.updateState();
    }
}
