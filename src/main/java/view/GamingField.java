package view;

import model.GameState;
import utils.Point;
import utils.Snake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GamingField extends JPanel
{
    private final int width;
    private final int height;
    private final int dim = 20;
    private HashMap<Integer, Snake> snakes;
    private ArrayList<Point> food;

    public GamingField(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.setSize(width * dim, height * dim);
        this.setBackground(Color.BLACK);
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(Color.BLUE);

        for(Snake s : snakes.values())
        {
            for(Point p : s.getBody())
            {
                g.fillRect(p.getX()*dim, p.getY()*dim, dim, dim);
            }
        }

        g.setColor(Color.RED);

        for(Point p : food)
        {
            g.fillRect(p.getX()*dim, p.getY()*dim, dim, dim);
        }
    }

    public void updateField(HashMap<Integer, Snake> snakes, ArrayList<Point> foods)
    {
        this.snakes = snakes;
        this.food = foods;
        repaint();
    }
}
