package controller;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import me.ippolitov.fit.snakes.SnakesProto.*;
import model.GameConfiguration;
import model.GameState;
import netUtils.*;
import utils.*;
import utils.Direction;
import utils.Snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Converter
{
    public enum MessageType {
        NONE,
        ANNOUNCEMENT,
        DISCOVER,
        JOIN,
        STEER,
        STATE
    }

    public static MessageType getMessageType(byte[] receivedData) throws InvalidProtocolBufferException {
        GameMessage msg = GameMessage.parseFrom(receivedData);

        if(msg.hasAnnouncement()) {
            return MessageType.ANNOUNCEMENT;
        }
        else if(msg.hasJoin()) {
            return MessageType.JOIN;
        }
        else if(msg.hasDiscover()) {
            return MessageType.DISCOVER;
        }
        else if(msg.hasSteer()) {
            return MessageType.STEER;
        }
        else if(msg.hasState()) {
            return MessageType.STATE;
        }
        else {
            return MessageType.NONE;
        }
    }

    public static SnakeGameAnnouncement getAnnouncement(byte[] receivedData) throws InvalidProtocolBufferException {
        GameMessage.AnnouncementMsg announcementMsg = GameMessage.parseFrom(receivedData).getAnnouncement();
        int gameCount = announcementMsg.getGamesCount();

        SnakeGameAnnouncement gameAnnouncement = new SnakeGameAnnouncement();
        if(gameCount > 0)
        {
            GameAnnouncement protoAnnouncement = announcementMsg.getGames(0);

            gameAnnouncement.gameName = protoAnnouncement.getGameName();
            gameAnnouncement.players = getPlayers(protoAnnouncement.getPlayers());
            GameConfig conf = protoAnnouncement.getConfig();

            gameAnnouncement.configuration = new GameConfiguration(conf.getWidth(), conf.getHeight(),
                                                                    conf.getStateDelayMs(), gameAnnouncement.gameName);
        }

        return gameAnnouncement;
    }

    private static HashMap<Integer, SnakePlayer> getPlayers(GamePlayers players)
    {
        HashMap<Integer, SnakePlayer> gamePlayers = new HashMap<>();
        for(GamePlayer player : players.getPlayersList())
        {
            SnakePlayer newPlayer = new SnakePlayer();
            newPlayer.name = player.getName();
            newPlayer.playerRole = getRole(player.getRole());
            newPlayer.id = player.getId();
            newPlayer.score = player.getScore();
            gamePlayers.put(newPlayer.id, newPlayer);
        }

        return gamePlayers;
    }

    private static SnakeRole getRole(NodeRole role)
    {
        switch (role)
        {
            case NORMAL:
                return SnakeRole.NORMAL;
            case MASTER:
                return SnakeRole.MASTER;
            case VIEWER:
                return SnakeRole.VIEWER;
            case DEPUTY:
                return SnakeRole.DEPUTY;
        }

        return null;
    }

    public static SnakeJoinRequest getJoinMessage(byte[] data) throws InvalidProtocolBufferException
    {
        GameMessage.JoinMsg protoMessage = GameMessage.parseFrom(data).getJoin();
        SnakeJoinRequest joinRequest = new SnakeJoinRequest();
        joinRequest.gameName = protoMessage.getGameName();
        joinRequest.playerName = protoMessage.getPlayerName();

        if (protoMessage.hasRequestedRole())
        {
            switch (protoMessage.getRequestedRole())
            {
                case NORMAL:
                    joinRequest.role = SnakeRole.NORMAL;
                    break;
                case VIEWER:
                    joinRequest.role = SnakeRole.VIEWER;
                    break;
                default:
                    joinRequest.role = SnakeRole.NONE;
                    break;
            }
        }
        else
        {
            joinRequest.role = SnakeRole.NONE;
        }

        return joinRequest;
    }

    public static SnakeGameState getState(byte[] data) throws InvalidProtocolBufferException {
        SnakesProto.GameState protoState = GameMessage.parseFrom(data).getState().getState();

        int stateNumber = protoState.getStateOrder();

        HashMap<Integer, Snake> snakes = new HashMap<>();
        for (SnakesProto.GameState.Snake snake: protoState.getSnakesList())
        {
            // Get body
            ArrayList<Point> body = new ArrayList<>();
            for (var p: snake.getPointsList())
            {
                Point point = getPoints(p);
                body.add(point);
            }

            Snake.snakeState state = getSnakeState(snake.getState());
            int id = snake.getPlayerId();
            Direction direction = getDirection(snake.getHeadDirection());

            Snake newSnake = new Snake();
            newSnake.setPlayer_id(id);
            newSnake.setState(state);
            newSnake.setBody(body);
            newSnake.setDirection(direction);
            snakes.put(id, newSnake);
        }

        // Get foods
        ArrayList<Point> foods = new ArrayList<>();
        for (var food: protoState.getFoodsList())
        {
            foods.add(getPoints(food));
        }

        return new SnakeGameState(snakes, foods, stateNumber);
    }

    private static Snake.snakeState getSnakeState(SnakesProto.GameState.Snake.SnakeState state)
    {
        switch (state)
        {
            case ALIVE: return Snake.snakeState.ALIVE;
            case ZOMBIE: return Snake.snakeState.ZOMBIE;
        }

        return Snake.snakeState.ZOMBIE;
    }

    private static Point getPoints(SnakesProto.GameState.Coord p)
    {
        return new Point(p.getX(), p.getY());
    }

    private static utils.Direction getDirection(SnakesProto.Direction direction)
    {
        switch (direction)
        {
            case UP: return Direction.UP;
            case DOWN: return Direction.DOWN;
            case LEFT: return Direction.LEFT;
            case RIGHT: return Direction.RIGHT;
            default: return Direction.UP;
        }
    }

    public static SnakeSteer getSteer(byte[] data) throws InvalidProtocolBufferException {
        GameMessage.SteerMsg steerMsg = GameMessage.parseFrom(data).getSteer();

        SnakeSteer steer = new SnakeSteer();
        steer.snakeDir = getDirection(steerMsg.getDirection());

        return steer;
    }

    public static byte[] createJoinMessage(int ms_seq, String gameName)
    {
        GameMessage.JoinMsg joinMsg = GameMessage.JoinMsg.newBuilder()
                .setPlayerType(PlayerType.HUMAN)
                .setRequestedRole(NodeRole.NORMAL)
                .setGameName(gameName)
                .setPlayerName("Player " + new Random().nextInt(500))
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(ms_seq)
                .setJoin(joinMsg)
                .build();

        return gameMessage.toByteArray();
    }

    public static byte[] createStateMessage(int ms_seq, SnakeGameState gameState, HashMap<Integer, SnakePlayer> peers)
    {
        // Include state number
        SnakesProto.GameState protoState = SnakesProto.GameState.newBuilder()
                .setStateOrder(gameState.getStateOrder())
                .buildPartial();

        // Add snakes
        for (var snake: gameState.getSnakes().values())
        {
            SnakesProto.GameState.Snake protoSnake = SnakesProto.GameState.Snake.newBuilder()
                    .setState(snakeStateToProto(snake.getState()))
                    .setHeadDirection(snakeDirectionToProto(snake.getDirection()))
                    .setPlayerId(snake.getPlayer_id())
                    .build();

            for (var p: snake.getBody())
            {
                protoSnake = protoSnake.toBuilder().addPoints(snakePointToProto(p)).build();
            }
            protoState = protoState.toBuilder().addSnakes(protoSnake).buildPartial();
        }

        // Add foods
        for (var p: gameState.getFood())
        {
            protoState = protoState.toBuilder().addFoods(snakePointToProto(p)).buildPartial();
        }

        // Add players
        protoState = protoState.toBuilder().setPlayers(writeGamePlayers(peers)).buildPartial();

        // Enclose in state message
        GameMessage.StateMsg stateMsg = GameMessage.StateMsg.newBuilder()
                .setState(protoState)
                .build();

        // Enclose in game message
        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(ms_seq)
                .setState(stateMsg)
                .build();


        return gameMessage.toByteArray();
    }

    public static byte[] createDiscoverMessage(int ms_seq)
    {
        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(ms_seq)
                .setDiscover(GameMessage.DiscoverMsg.newBuilder().build())
                .build();

        return gameMessage.toByteArray();
    }

    private static GamePlayers writeGamePlayers(HashMap<Integer, SnakePlayer> snakePlayers)
    {
        GamePlayers gamePlayers = GamePlayers.newBuilder().buildPartial();
        for (var player: snakePlayers.entrySet()) {
            GamePlayer gamePlayer = GamePlayer.newBuilder()
                    .setId(player.getValue().id)
                    .setName(player.getValue().name)
                    .setRole(roleToProto(player.getValue().playerRole))
                    .setScore(player.getValue().score)
                    .setType(PlayerType.HUMAN)
                    .build();

            gamePlayers.toBuilder().addPlayers(gamePlayer).buildPartial();
        }
        gamePlayers.toBuilder().build();
        return gamePlayers;
    }

    public static byte[] createSteerMsg(int ms_seq, Direction dir)
    {
        GameMessage.SteerMsg steerMsg = GameMessage.SteerMsg.newBuilder()
                .setDirection(snakeDirectionToProto(dir))
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(ms_seq)
                .setSteer(steerMsg)
                .build();

        return gameMessage.toByteArray();
    }

    public static byte[] createAnnouncementMessage(int ms_seq, SnakeGameAnnouncement gameAnnouncement)
    {
        GamePlayers gamePlayers = writeGamePlayers(gameAnnouncement.players);

        GameConfiguration gameParameters = gameAnnouncement.configuration;
        GameConfig gameConfig = GameConfig.newBuilder()
                .setWidth(gameParameters.getFieldWidth())
                .setHeight(gameParameters.getFieldHeight())
                .setStateDelayMs(gameParameters.getTimeBetweenIterations())
                .build();

        GameAnnouncement protoGameAnnouncement = GameAnnouncement.newBuilder()
                .setPlayers(gamePlayers)
                .setConfig(gameConfig)
                .setGameName(gameAnnouncement.gameName)
                .build();

        GameMessage.AnnouncementMsg announcementMsg = GameMessage.AnnouncementMsg.newBuilder()
                .addGames(protoGameAnnouncement)
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(ms_seq)
                .setAnnouncement(announcementMsg)
                .build();

        return gameMessage.toByteArray();
    }

    private static SnakesProto.GameState.Coord snakePointToProto(Point p)
    {
        return SnakesProto.GameState.Coord.newBuilder()
                .setX(p.getX())
                .setY(p.getY())
                .build();
    }

    private static SnakesProto.Direction snakeDirectionToProto(Direction direction)
    {
        switch (direction) {
            case UP: return SnakesProto.Direction.UP;
            case DOWN: return SnakesProto.Direction.DOWN;
            case LEFT: return SnakesProto.Direction.LEFT;
            case RIGHT: return SnakesProto.Direction.RIGHT;
        }
        return SnakesProto.Direction.UP;
    }

    private static SnakesProto.GameState.Snake.SnakeState snakeStateToProto(Snake.snakeState state)
    {
        switch (state) {
            case ALIVE: return SnakesProto.GameState.Snake.SnakeState.ALIVE;
            case ZOMBIE: return SnakesProto.GameState.Snake.SnakeState.ZOMBIE;
        }
        return SnakesProto.GameState.Snake.SnakeState.ZOMBIE;
    }

    private static NodeRole roleToProto(SnakeRole role)
    {
        switch (role) {
            case MASTER: return NodeRole.MASTER;
            case VIEWER: return NodeRole.VIEWER;
            case DEPUTY: return NodeRole.DEPUTY;
            case NORMAL: return NodeRole.NORMAL;
        }
        return null;
    }
}
