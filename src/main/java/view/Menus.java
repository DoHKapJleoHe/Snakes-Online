package view;

import controller.GameController;
import controller.NetController;
import netUtils.SnakeGameAnnouncement;
import netUtils.SnakeRole;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;

public class Menus
{
    private GameController gameController;
    private NetController netController;
    private DefaultListModel<String> gamesModel = new DefaultListModel<>();
    private Timer timer;

    HashMap<String, SnakeGameAnnouncement> games = new HashMap<>();

    public void show() throws IOException
    {
        netController = new NetController("239.192.0.4", 9192);
        netController.onReceiveAnnouncement(this::addGameToList);

        // MAIN MENU FRAME
        JFrame mainMenu = new JFrame("Snake");
        mainMenu.setBounds(550, 150, 400, 400);
        mainMenu.getContentPane().setLayout(null);
        mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton host_a_new_gameButton = new JButton("Host a new game");
        host_a_new_gameButton.setBounds(120, 50, 150, 40);
        host_a_new_gameButton.setVisible(true);
        mainMenu.add(host_a_new_gameButton);

        JButton connect_to_a_gameButton = new JButton("Connect to a game");
        connect_to_a_gameButton.setBounds(120, 110, 150, 40);
        connect_to_a_gameButton.setVisible(true);
        mainMenu.add(connect_to_a_gameButton);

        mainMenu.setVisible(true);
        //////////////////////////////////////////////////////////////////////


        // GAME HOSTING MENU
        JFrame host_a_gameFrame = new JFrame("Host a game");
        host_a_gameFrame.setBounds(550, 150, 600, 400);
        host_a_gameFrame.getContentPane().setLayout(null);
        host_a_gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton goBack1 = new JButton("Go Back");
        goBack1.setBounds(20, 30, 100, 30);
        goBack1.setVisible(true);
        host_a_gameFrame.add(goBack1);

        JButton createButton = new JButton("Create");
        createButton.setBounds(20, 70, 100, 30);
        createButton.setVisible(true);
        host_a_gameFrame.add(createButton);

        JTextField boardWidth = new JTextField("20");
        boardWidth.setBounds(350, 30, 55, 30);
        boardWidth.setVisible(true);
        host_a_gameFrame.add(boardWidth);

        JTextField boardHeight = new JTextField("20");
        boardHeight.setBounds(350, 70, 55, 30);
        boardHeight.setVisible(true);
        //boardHeight
        host_a_gameFrame.add(boardHeight);

        JLabel Width = new JLabel("Width");
        Width.setBounds(280, 30, 50, 30);
        Width.setVisible(true);
        host_a_gameFrame.add(Width);

        JLabel Height = new JLabel("Height");
        Height.setBounds(280, 70, 50, 30);
        Height.setVisible(true);
        host_a_gameFrame.add(Height);

        // CONNECTING TO A GAME MENU
        JFrame connect_to_a_gameFrame = new JFrame("Connection to a game");
        connect_to_a_gameFrame.setBounds(550, 150, 600, 600);
        connect_to_a_gameFrame.getContentPane().setLayout(null);
        connect_to_a_gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JList gamesList = new JList(gamesModel); // here will be shown started games to connect to them
        gamesList.setVisible(true);
        gamesList.setBounds(10, 10, 200, 300);
        connect_to_a_gameFrame.add(gamesList);

        JButton goBack2 = new JButton("Go Back");
        goBack2.setBounds(250, 150, 100, 30);
        goBack2.setVisible(true);
        connect_to_a_gameFrame.add(goBack2);
        //////////////////////////////////////////////////////////////////////

        // ACTION LISTENERS
        host_a_new_gameButton.addActionListener(e -> {
            mainMenu.setVisible(false);
            host_a_gameFrame.setVisible(true);
        });

        connect_to_a_gameButton.addActionListener(e -> {
            mainMenu.setVisible(false);
            connect_to_a_gameFrame.setVisible(true);
        });

        goBack1.addActionListener(e -> {
            host_a_gameFrame.setVisible(false);
            mainMenu.setVisible(true);
        });

        createButton.addActionListener(e -> {
            host_a_gameFrame.setVisible(false);
            gameController = new GameController(Integer.parseInt(boardWidth.getText()), Integer.parseInt(boardHeight.getText()), netController,  SnakeRole.MASTER);
        });

        goBack2.addActionListener(e -> {
            connect_to_a_gameFrame.setVisible(false);
            mainMenu.setVisible(true);
        });

        gamesList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting())
            {
                connect_to_a_gameFrame.setVisible(false);
                int idx = gamesList.getSelectedIndex();
                String gameName = gamesModel.get(idx);
                SnakeGameAnnouncement gameAnnouncement = games.get(gameName);
                gameController = new GameController(gameAnnouncement.configuration.getFieldWidth(),
                        gameAnnouncement.configuration.getFieldHeight(),
                        netController, SnakeRole.NORMAL);
                netController.joinGame(gameAnnouncement);
            }
        });
    }

    private SnakeGameAnnouncement addGameToList(SnakeGameAnnouncement snakeGameAnnouncement)
    {
        if(!games.containsKey(snakeGameAnnouncement.gameName))
        {
            games.put(snakeGameAnnouncement.gameName, snakeGameAnnouncement);
            gamesModel.add(0, snakeGameAnnouncement.gameName);
        }
        else
        {
            games.replace(snakeGameAnnouncement.gameName, snakeGameAnnouncement);
        }

        return snakeGameAnnouncement;
    }

}