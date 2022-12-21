package model;

import utils.Food;
import utils.Point;
import utils.Snake;
import view.GameFrame;

import java.util.ArrayList;

public class GameState
{
    private GameConfiguration config;
    private GameFrame gameFrame;

    private int state_order;
    private ArrayList<Snake> snakes = new ArrayList<>();
    private ArrayList<Food> foods = new ArrayList<>();
    private int player_num;

    public GameState(GameConfiguration config, GameFrame frame){
        this.config = config;
        this.gameFrame = frame;
        //this.gameFrame = new GameFrame(config.getFieldWidth(), config.getFieldHeight());
    }

    public ArrayList<Snake> getSnakes() {
        return snakes;
    }

    public void addSnake(Snake newSnake)
    {
        this.snakes.add(newSnake);
        this.player_num++;
    }

    public void moveSnakes()
    {
        for(Snake s : snakes)
        {
            Point head = s.getBody().get(0);
            int x = head.getX();
            int y = head.getY();

            if(s.getDirection().equals(Snake.Direction.RIGHT))
            {
                Point temp = new Point(x + 1, y);
                s.getBody().add(0, temp);
                s.getBody().remove(s.getSnakeLength() - 1);
            }
            else if(s.getDirection().equals(Snake.Direction.LEFT))
            {
                Point temp = new Point(x - 1, y);
                s.getBody().add(0, temp);
                s.getBody().remove(s.getSnakeLength() - 1);
            }
            else if(s.getDirection().equals(Snake.Direction.UP))
            {
                Point temp = new Point(x, y - 1);
                s.getBody().add(0, temp);
                s.getBody().remove(s.getSnakeLength() - 1);
            }
            else
            {
                Point temp = new Point(x, y + 1);
                s.getBody().add(0, temp);
                s.getBody().remove(s.getSnakeLength() - 1);
            }
        }
    }

    public void updateState()
    {
        moveSnakes();
        gameFrame.updateGamingField(this);
    }

    public void updateSnakeDirection(int snake_id, String newDir)
    {
        if(newDir.equals("LEFT"))
        {
            snakes.get(snake_id).setDirection(Snake.Direction.LEFT);
        }
        else if(newDir.equals("RIGHT"))
        {
            snakes.get(snake_id).setDirection(Snake.Direction.RIGHT);
        }
        else if(newDir.equals("UP"))
        {
            snakes.get(snake_id).setDirection(Snake.Direction.UP);
        }
        else
        {
            snakes.get(snake_id).setDirection(Snake.Direction.DOWN);
        }
    }
}

