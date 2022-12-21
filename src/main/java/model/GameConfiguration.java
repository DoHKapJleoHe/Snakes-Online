package model;

public class GameConfiguration
{
    private final int fieldHeight;
    private final int fieldWidth;
    //private int timeBetweenIterations;

    public GameConfiguration(int fieldWidth, int fieldHeight)
    {
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }


}
