package controller;

import model.GameConfiguration;
import model.GameState;
import netUtils.*;
import utils.Direction;
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
    private int maxID = 0;

    public SnakeRole role;

    private HashMap<Integer, SnakePlayer> peers;

    public GameController(int width, int height, NetController netController, SnakeRole role)
    {
        this.role = role;
        this.netController = netController;

        config = new GameConfiguration(width, height, 1, "DemoGame");
        gameFrame = new GameFrame(width, height);
        gameState = new GameState(config, gameFrame);

        if(role == SnakeRole.MASTER)
            runAsMaster(width, height);
        else
        {
            runAsGuest(width, height);
        }
    }

    private void runAsMaster(int width, int height)
    {
        peers = new HashMap<>();

        netController.onJoinRequest(this::addPlayer);
        netController.onSteerGet(this::steerSnake);

        Snake snake = new Snake(maxID, new Point(5, 5));
        gameState.addSnake(snake);
        /*SnakePlayer master = new SnakePlayer();
        master.id = 0;
        master.name = "master";
        master.playerRole = SnakeRole.MASTER;
        master.score = 0;
        peers.put(0, master);*/

        timerForUpdate = new Timer();
        timerForUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameStateAsMaster();
            }}, 0, 500);

        timerForAnnounce = new Timer();
        timerForAnnounce.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                announce();
            }
        }, 0, 1000);

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

    private void runAsGuest(int width, int height)
    {
        netController.onStateGet(this::updateGameStateAsGuest);

        gameFrame.onArrowPress(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_LEFT) {
                    System.out.println("Left");
                    netController.sendSteerMsg(Direction.LEFT);
                } else if (code == KeyEvent.VK_RIGHT) {
                    System.out.println("Right");
                    netController.sendSteerMsg(Direction.RIGHT);
                } else if (code == KeyEvent.VK_UP) {
                    System.out.println("Up");
                    netController.sendSteerMsg(Direction.UP);
                } else {
                    System.out.println("Down");
                    netController.sendSteerMsg(Direction.DOWN);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private SnakeJoinRequest addPlayer(SnakeJoinRequest snakeJoinRequest)
    {
        System.out.println("New player added");
        SnakePlayer newPlayer = new SnakePlayer();
        newPlayer.name = snakeJoinRequest.playerName;
        newPlayer.playerRole = snakeJoinRequest.role;
        newPlayer.address = snakeJoinRequest.senderAdr;
        newPlayer.port = snakeJoinRequest.senderPort;
        newPlayer.score = 0;
        maxID++;
        newPlayer.id = maxID;

        peers.put(maxID, newPlayer);

        gameState.addSnake(new Snake(maxID, new Point(1, 1)));

        return snakeJoinRequest;
    }

    private SnakeGameState updateGameStateAsGuest(SnakeGameState snakeGameState)
    {
        gameState.setSnakes(snakeGameState.getSnakes());
        gameState.setFoods(snakeGameState.getFood());

        gameState.updateGameStateAsGuest();

        return snakeGameState;
    }

    private void announce()
    {
        SnakeGameAnnouncement gameAnnouncement = new SnakeGameAnnouncement();
        gameAnnouncement.gameName = config.getGameName();
        gameAnnouncement.configuration = this.config;
        gameAnnouncement.players = new HashMap<>(peers);

        netController.sendAnnouncementMsg(gameAnnouncement);
    }

    private void updateGameStateAsMaster()
    {
        gameState.updateState();

        SnakeGameState newGameState = new SnakeGameState(gameState.getSnakes(), gameState.getFoods(), 1);

        for(var peer : peers.values())
        {
            netController.sendGameState(peer, newGameState, peers);
        }
    }

    private SnakeSteer steerSnake(SnakeSteer steer)
    {
        int id = -1;
        for(var peer : peers.entrySet())
        {
            if (peer.getValue().address.equals(steer.address) && peer.getValue().port == steer.port)
            {
                id = peer.getKey();
            }
        }

        if(id != -1)
            gameState.updateSnakeDirection(id, steer.snakeDir.toString());

        return steer;
    }
}
