package utils;

import java.util.Objects;

public class Point
{
    private int x;
    private int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        Point a = (Point) o;
        if(x == a.getX() && y == a.getY())
            return true;

        return (x == a.getX() && y == a.getY());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.x, this.y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }
}
