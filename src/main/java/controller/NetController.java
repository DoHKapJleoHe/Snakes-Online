package controller;

import model.GameState;
import netUtils.*;
import utils.Direction;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class NetController
{
    private MulticastSocket publicSocket;
    private InetAddress multicastAddress;
    private DatagramSocket personalSocket;
    private int multicastPort;
    private int ms_seq = 1;

    Thread multicastListener;
    Thread peerListener;

    UnaryOperator<SnakeGameAnnouncement> announcementFunction = (e) -> e;
    UnaryOperator<SnakeJoinRequest> joinFunction = (e) -> e;
    UnaryOperator<SnakeGameState> stateFunction = (e) -> e;
    UnaryOperator<SnakeSteer> steerFunction = (e) -> e;

    private InetAddress masterAddress;
    private int masterPort;

    public NetController(String multicastAddress, int multicastPort) throws IOException {
        // joining port
        publicSocket = new MulticastSocket(multicastPort);
        InetAddress ad = InetAddress.getByName(multicastAddress);
        publicSocket.joinGroup(new InetSocketAddress(ad, multicastPort), null);

        // save parameters
        this.multicastAddress = ad;
        this.multicastPort = multicastPort;

        personalSocket = new DatagramSocket();

        multicastListener = new Thread(() -> {
            while (true)
            {
                listenOnMulticastSocket();
            }
        });
        multicastListener.start();

        peerListener = new Thread(() -> {
            while (true)
            {
                listenPeers();
            }
        });
        peerListener.start();
    }

    private void listenPeers()
    {
        byte[] buf = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buf, 4096);

        try{
            personalSocket.receive(packet);

            byte[] data = new byte[packet.getLength()];
            System.arraycopy(buf, 0, data, 0, data.length);

            Converter.MessageType msgType = Converter.getMessageType(data);

            switch (msgType)
            {
                case JOIN:
                    SnakeJoinRequest joinRequest = Converter.getJoinMessage(data);
                    joinRequest.senderPort = packet.getPort();
                    joinRequest.senderAdr = packet.getAddress();
                    joinFunction.apply(joinRequest);
                    break;
                case STATE:
                    SnakeGameState gameState = Converter.getState(data);
                    stateFunction.apply(gameState);
                    break;
                case STEER:
                    SnakeSteer snakeSteer = Converter.getSteer(data);
                    snakeSteer.address = packet.getAddress();
                    snakeSteer.port = packet.getPort();
                    steerFunction.apply(snakeSteer);
                    break;
                default:
                    System.out.println("Incorrect peer message");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void listenOnMulticastSocket()
    {
        byte[] buffer = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 4096);

        try {
            publicSocket.receive(receivePacket);

            byte[] receivedData = new byte[receivePacket.getLength()];

            System.arraycopy(receivePacket.getData(), 0, receivedData, 0,receivedData.length);
            Converter.MessageType messageType = Converter.getMessageType(receivedData);

            switch(messageType)
            {
                case ANNOUNCEMENT:
                    SnakeGameAnnouncement announcement = Converter.getAnnouncement(receivedData);
                    announcement.addr = receivePacket.getAddress();
                    announcement.port = receivePacket.getPort();
                    announcementFunction.apply(announcement);
                case DISCOVER:
                    break;
                default:
                    System.out.println("Incorrect message type!");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void joinGame(SnakeGameAnnouncement gameAnnouncement) {
        masterAddress = gameAnnouncement.addr;
        masterPort = gameAnnouncement.port;
        sendJoinMsg(gameAnnouncement.gameName);
    }

    private void sendJoinMsg(String gameName)
    {
        byte[] message = Converter.createJoinMessage(ms_seq++, gameName);

        // Create packet to send
        DatagramPacket packet = new DatagramPacket(message, message.length, masterAddress, masterPort);

        // Send
        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send JoinMsg");
        }
    }

    public void sendDiscoverMessage()
    {
        // Create proto message
        byte[] message = Converter.createDiscoverMessage(ms_seq++);

        // Create packet to send
        DatagramPacket packet = new DatagramPacket(message, message.length, multicastAddress, multicastPort);

        // Send
        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send DiscoverMsg");
        }
    }

    public void sendGameState(SnakePlayer peer, SnakeGameState gameState, HashMap<Integer, SnakePlayer> peers)
    {
        // Create proto message
        byte[] message = Converter.createStateMessage(ms_seq++, gameState, peers);

        // Create packet to send
        DatagramPacket packet = new DatagramPacket(message, message.length, peer.address, peer.port);

        // Send
        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send StateMsg");
        }
    }

    public void sendSteerMsg(Direction dir) {
        // Create proto message
        byte[] message = Converter.createSteerMsg(ms_seq++, dir);

        // Create packet to send
        DatagramPacket packet = new DatagramPacket(message, message.length, masterAddress, masterPort);

        // Send
        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send SteerMsg");
        }
    }

    public void sendAnnouncementMsg(SnakeGameAnnouncement gameAnnouncement)
    {
        // Create proto message
        byte[] message = Converter.createAnnouncementMessage(ms_seq++, gameAnnouncement);

        // Create packet to send
        DatagramPacket packet = new DatagramPacket(message, message.length, multicastAddress, multicastPort);

        // Send
        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send AnnouncementMsg");
        }
    }

    public void onReceiveAnnouncement(UnaryOperator<SnakeGameAnnouncement> func)
    {
        announcementFunction = func;
    }

    public void onJoinRequest(UnaryOperator<SnakeJoinRequest> func)
    {
        joinFunction = func;
    }

    public void onStateGet(UnaryOperator<SnakeGameState> func)
    {
        stateFunction = func;
    }

    public void onSteerGet(UnaryOperator<SnakeSteer> func)
    {
        steerFunction = func;
    }

    public void destroyFunctions()
    {
        joinFunction = (e) -> e;
        stateFunction = (e) -> e;
    }
}
