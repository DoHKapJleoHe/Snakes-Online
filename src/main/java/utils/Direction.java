package utils;

import java.util.Random;

public enum Direction
{
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
