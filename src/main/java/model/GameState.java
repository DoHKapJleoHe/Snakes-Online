package model;

import utils.Direction;
import utils.Point;
import utils.Snake;
import view.GameFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameState
{
    private GameConfiguration config;
    private GameFrame gameFrame;

    private int state_order;
    private HashMap<Integer ,Snake> snakes = new HashMap<>();
    private ArrayList<Point> foods = new ArrayList<>();
    private int player_num;

    //TODO: find a free place for food spawning

    public GameState(GameConfiguration config, GameFrame frame){
        this.config = config;
        this.gameFrame = frame;

        spawnFood();
    }

    public HashMap<Integer, Snake> getSnakes() {
        return snakes;
    }

    public void addSnake(Snake newSnake)
    {
        this.snakes.put(newSnake.getPlayer_id(), newSnake);
        this.player_num++;
    }

    public void moveSnakes()
    {
        for(Snake s : snakes.values())
        {
            Point head = s.getBody().get(0);
            int x = head.getX();
            int y = head.getY();

            if(s.getDirection().equals(Direction.RIGHT))
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
                    s.addScore();
                }
            }
            else if(s.getDirection().equals(Direction.LEFT))
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
            else if(s.getDirection().equals(Direction.UP))
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

        return new Point(x, y);
    }

    public void updateState()
    {
        state_order++;
        checkDeath();
        moveSnakes();
        gameFrame.updateGamingField(this, foods);
    }

    public void updateSnakeDirection(int snake_id, String newDir)
    {
        if(newDir.equals("LEFT"))
        {
            snakes.get(snake_id).setDirection(Direction.LEFT);
        }
        else if(newDir.equals("RIGHT"))
        {
            snakes.get(snake_id).setDirection(Direction.RIGHT);
        }
        else if(newDir.equals("UP"))
        {
            snakes.get(snake_id).setDirection(Direction.UP);
        }
        else
        {
            snakes.get(snake_id).setDirection(Direction.DOWN);
        }
    }

    private void locatePlace()
    {

    }

    private void spawnFood()
    {
        Random r = new Random();
        int x = r.nextInt(config.getFieldWidth());
        int y = r.nextInt(config.getFieldHeight());

        Point food = new Point(x, y);

        foods.add(food);
    }

    private void checkDeath()
    {
        for(Snake snake1 : snakes.values())
        {
            Point s1_head = snake1.getBody().get(0);
            for(Snake snake2 : snakes.values())
            {
                ArrayList<Point> s2_body = snake2.getBody();
                Point s2_head = snake2.getBody().get(0);

                for(Point s2_body_segment : s2_body)
                {
                    if(snake1.getPlayer_id() == snake2.getPlayer_id())
                    {
                        if(s2_body_segment == s2_head)
                        {
                            continue;
                        }

                        if(s1_head.equals(s2_body_segment))
                        {
                            killSnake(snake1.getPlayer_id());
                        }
                    }
                    else
                    {
                        if(s1_head.equals(s2_body_segment))
                        {
                            killSnake(snake1.getPlayer_id());
                            //addScore(snake2);
                        }
                    }
                }
            }
        }
    }

    private void killSnake(int snakeID)
    {
        Snake snake = snakes.remove(snakeID);

        // spawn food with 50% chance
        for(Point p : snake.getBody())
        {
            if(1 == new Random().nextInt(2))
            {
                foods.add(p);
            }
        }
    }
}

