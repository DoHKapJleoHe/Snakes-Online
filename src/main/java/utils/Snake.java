package utils;

import java.util.ArrayList;

public class Snake
{
    public enum snakeState
    {
        ALIVE,
        ZOMBIE;
    }

    private int player_id;
    private ArrayList<Point> body;
    private snakeState state = snakeState.ALIVE;
    private Direction direction;
    private int score;

    public Snake(int id, Point head_pos) {
        body = new ArrayList<>();
        body.add(head_pos);
        body.add(new Point(head_pos.getX() - 1, head_pos.getY()));
        direction = Direction.setRandomDirection();
        player_id = id;
    }

    public Snake(){}

    public ArrayList<Point> getBody() {
        return this.body;
    }

    public void setBody(ArrayList<Point> body) {
        this.body = body;
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

    public int getPlayer_id() {
        return player_id;
    }

    public int getSnakeLength() {
        return body.size();
    }

    public void setState(snakeState state) {
        this.state = state;
    }

    public snakeState getState() {
        return state;
    }

    public void addScore()
    {
        this.score++;
    }

    public int getScore() {
        return score;
    }
}

