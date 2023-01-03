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
    private HashMap<Integer, Color> colors;

    public GamingField(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.setSize(width * dim, height * dim);
        this.setBackground(Color.BLACK);
        this.setVisible(true);

        this.colors = new HashMap<>();
        colors.put(0, Color.BLUE);
        colors.put(1, Color.YELLOW);
        colors.put(2, Color.GREEN);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        //g.setColor(Color.BLUE);

        for(Snake s : snakes.values())
        {
            g.setColor(colors.get(s.getPlayer_id()));
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
