package model;

import utils.Point;
import utils.Snake;
import view.GameFrame;

import java.util.ArrayList;
import java.util.Random;

public class GameState
{
    private GameConfiguration config;
    private GameFrame gameFrame;

    private int state_order;
    private ArrayList<Snake> snakes = new ArrayList<>();
    private ArrayList<Point> foods = new ArrayList<>();
    private int player_num;

    //TODO: find a free place for food spawning

    public GameState(GameConfiguration config, GameFrame frame){
        this.config = config;
        this.gameFrame = frame;

        spawnFood();
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
                Point temp = checkPoint(new Point(x + 1, y));
                s.getBody().add(0, temp);

                if(!onFood(temp))
                {
                    s.getBody().remove(s.getSnakeLength() - 1);
                }
                else
                {
                    foods.remove(0);
                    spawnFood();
                }
            }
            else if(s.getDirection().equals(Snake.Direction.LEFT))
            {
                Point temp = checkPoint(new Point(x - 1, y));
                s.getBody().add(0, temp);

                if(!onFood(temp))
                {
                    s.getBody().remove(s.getSnakeLength() - 1);
                }
                else
                {
                    foods.remove(0);
                    spawnFood();
                }
            }
            else if(s.getDirection().equals(Snake.Direction.UP))
            {
                Point temp = checkPoint(new Point(x, y - 1));
                s.getBody().add(0, temp);

                if(!onFood(temp))
                {
                    s.getBody().remove(s.getSnakeLength() - 1);
                }
                else
                {
                    foods.remove(0);
                    spawnFood();
                }
            }
            else
            {
                Point temp = checkPoint(new Point(x, y + 1));
                s.getBody().add(0, temp);

                if(!onFood(temp))
                {
                    s.getBody().remove(s.getSnakeLength() - 1);
                }
                else
                {
                    foods.remove(0);
                    spawnFood();
                }
            }
        }
    }

    private boolean onFood(Point temp)
    {
        int p_x = temp.getX();
        int p_y = temp.getY();

        // case for one food
        return p_x == foods.get(0).getX() && p_y == foods.get(0).getY();
    }

    // mb rename to "getPoint" ?
    private Point checkPoint(Point temp)
    {
        // i added 1 to width and height because my field is drawn by specific way:
        // each point creates a square width height and width, that equals to "dim"
        int width = config.getFieldWidth() + 1;
        int height = config.getFieldHeight() + 1;

        int x = (temp.getX() < 0) ? (width - 1) : (temp.getX() % width);
        int y = (temp.getY() < 0) ? (height - 1) : (temp.getY() % height);

        /*if(temp.getX() > width)
        {
            return new Point(0, temp.getY());
        }
        else if(temp.getX() < 0)
        {
            return new Point(width, temp.getY());
        }
        else if(temp.getY() > height)
        {
            return new Point(temp.getX(), 0);
        }
        else
        {

        }*/
        return new Point(x, y);
    }

    public void updateState()
    {
        moveSnakes();
        state_order++;
        gameFrame.updateGamingField(this, foods);
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

    private void locatePlace()
    {
        /*for(int i = 0; i < config.getFieldHeight(); i++)
        {
            for(int j = 0; j < config.getFieldWidth(); j++)
            {
                Point freePlace = new Point(i, j);
            }
        }*/
    }

    private void spawnFood()
    {
        Random r = new Random();
        int x = r.nextInt(config.getFieldWidth());
        int y = r.nextInt(config.getFieldHeight());

        Point food = new Point(x, y);

        foods.add(food);
    }
}

