package view;

import controller.GameController;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Menus
{
    static Logger LOGGER;
    private GameController gameController;

    public void show() throws IOException
    {
        FileInputStream fis = new FileInputStream("C:\\Users\\eduar\\Desktop\\Проекты\\Snake-Online\\src\\main\\java\\log.config");
        LogManager.getLogManager().readConfiguration(fis);

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

        JButton goBack = new JButton("Go Back");
        goBack.setBounds(20, 30, 100, 30);
        goBack.setVisible(true);
        host_a_gameFrame.add(goBack);

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

        JList games = new JList(); // here will be shown started games to connect to them
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

        goBack.addActionListener(e -> {
            host_a_gameFrame.setVisible(false);
            mainMenu.setVisible(true);
        });
        createButton.addActionListener(e -> {
            host_a_gameFrame.setVisible(false);
            gameController = new GameController(Integer.parseInt(boardWidth.getText()), Integer.parseInt(boardHeight.getText()));

        });
    }
}