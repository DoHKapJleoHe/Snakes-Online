package netUtils;

import utils.Point;
import utils.Snake;

import java.util.ArrayList;
import java.util.HashMap;

public class SnakeGameState
{
    private HashMap<Integer, Snake> snakes;
    private ArrayList<Point> food;
    private int stateOrder;

    public SnakeGameState(HashMap<Integer, Snake> snakes, ArrayList<Point> food, int stateOrder)
    {
        this.snakes = snakes;
        this.food = food;
        this.stateOrder = stateOrder;
    }

    public ArrayList<Point> getFood() {
        return food;
    }

    public HashMap<Integer, Snake> getSnakes() {
        return snakes;
    }

    public int getStateOrder() {
        return stateOrder;
    }
}
