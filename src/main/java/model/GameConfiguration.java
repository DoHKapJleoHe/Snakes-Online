package model;

public class GameConfiguration
{
    private int fieldHeight;
    private int fieldWidth;
    private int timeBetweenIterations;
    private String gameName;

    public GameConfiguration(int fieldWidth, int fieldHeight, int timeBetweenIterations, String gameName)
    {
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
        this.timeBetweenIterations = timeBetweenIterations;
        this.gameName = gameName;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getTimeBetweenIterations() {
        return timeBetweenIterations;
    }
}
