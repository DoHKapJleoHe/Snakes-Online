package controller;

import model.GameConfiguration;
import model.GameState;
import netUtils.SnakeGameAnnouncement;
import netUtils.SnakePlayer;
import netUtils.SnakeSteer;
import utils.Point;
import utils.Snake;
import view.GameFrame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameController
{
    private GameConfiguration config;
    private NetController netController;
    private GameState gameState;
    private GameFrame gameFrame;
    private Timer timerForUpdate;
    private Timer timerForAnnounce;

    private HashMap<Integer, SnakePlayer> peers;

    public GameController(int width, int height) {
        config = new GameConfiguration(width, height, config.getTimeBetweenIterations(), "DemoGame");
        gameFrame = new GameFrame(width, height);
        gameState = new GameState(config, gameFrame);

        Snake snake = new Snake(0, new Point(5, 5));
        gameState.addSnake(snake);
        timerForUpdate = new Timer();
        int updTime = config.getTimeBetweenIterations();
        timerForUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameState();
            }
        }, updTime, updTime);

        timerForAnnounce = new Timer();
        timerForAnnounce.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    announce();
            }
        }, 0, 100);

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

    private void announce()
    {
        SnakeGameAnnouncement gameAnnouncement = new SnakeGameAnnouncement();
        gameAnnouncement.gameName = config.getGameName();
        gameAnnouncement.configuration = this.config;
        gameAnnouncement.players = new HashMap<>(peers);

        netController.sendAnnouncementMsg(gameAnnouncement);
    }

    private void updateGameState() {
        gameState.updateState();
    }

    private SnakeSteer SteerSnake(SnakeSteer steer)
    {
        int id = -1;

        for(var peer : peers.entrySet())
        {
            if(peer.getValue().address.equals(steer.address) && peer.getValue().port == steer.port)
            {
                id = peer.getKey();
            }
        }

        if(id != -1)
            
    }
}
