package controller;

import netUtils.SnakeGameAnnouncement;
import netUtils.SnakeGameState;
import netUtils.SnakeJoinRequest;
import netUtils.SnakeSteer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.function.UnaryOperator;

public class NetController
{
    MulticastSocket publicSocket;
    InetAddress multicastAddress;
    DatagramSocket datagramSocket;
    int multicastPort;
    int mes_seq = 1;

    Thread multicastListener;

    UnaryOperator<SnakeGameAnnouncement> announcementFunction = (e) -> e;
    UnaryOperator<SnakeJoinRequest> joinFunction = (e) -> e;
    UnaryOperator<SnakeGameState> stateFunction = (e) -> e;
    UnaryOperator<SnakeSteer> steerFunction = (e) -> e;

    InetAddress masterAddress;
    int masterPort;
    Thread peerListener;

    public NetController(String multicastAddress, int multicastPort) throws IOException {
        // joining port
        publicSocket = new MulticastSocket(multicastPort);
        InetAddress ad = InetAddress.getByName(multicastAddress);
        publicSocket.joinGroup(new InetSocketAddress(ad, multicastPort), null);

        // save parameters
        this.multicastAddress = ad;
        this.multicastPort = multicastPort;

        datagramSocket = new DatagramSocket();

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

    }

    private void listenOnMulticastSocket()
    {
    }
}
