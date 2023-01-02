package netUtils;

import model.GameConfiguration;
import utils.Snake;

import java.net.InetAddress;
import java.util.HashMap;

public class SnakeGameAnnouncement
{
    public String gameName;
    public HashMap<Integer, SnakePlayer> players;
    public GameConfiguration configuration;
    public InetAddress addr;
    public int port;
}
