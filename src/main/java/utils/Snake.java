package utils;

import java.util.ArrayList;
import java.util.Random;

public class Snake
{
    public enum Direction{
        LEFT,
        RIGHT,
        UP,
        DOWN;

        public static Direction setRandomDirection()
        {
            Direction[] directions = values();
            return directions[new Random().nextInt(directions.length)];
        }
    }

    private enum snakeState
    {
        ALIVE,
        ZOMBIE;
    }

    private int player_id;
    private ArrayList<Point> body;
    private snakeState state = snakeState.ALIVE;
    private Direction direction;

    public Snake(int id, Point head_pos) {
        body = new ArrayList<>();
        body.add(head_pos);
        body.add(new Point(head_pos.getX() - 1, head_pos.getY()));
        direction = Direction.setRandomDirection();
        player_id = id;
    }

    public ArrayList<Point> getBody() {
        return this.body;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public int getSnakeLength() {
        return body.size();
    }
}

