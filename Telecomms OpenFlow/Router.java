import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

public class Router extends Node {
    public static final int CONTROL_PORT = 50000;
    HashMap <Integer, Integer> flowTable;
    LinkedList<DataPacket> packetsToSend;
    Router () {
        try{
            socket = new DatagramSocket();
            flowTable = new HashMap<>();
            packetsToSend = new LinkedList <> ();
            listener.go();
        }catch (Exception e){e.printStackTrace();}
        start();
    }
    public void onReceipt(DatagramPacket packet) {
        try {
            GeneralPacket typedPacket = GeneralPacket.getType(packet);
            if (typedPacket instanceof DataPacket) {
                int dst = ((DataPacket) typedPacket).getDst();
                if (flowTable.get(dst) == null) {
                    InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, CONTROL_PORT);
                    packetsToSend.add((DataPacket) typedPacket);
                    RoutingPacket routingPacket = new RoutingPacket(dst);
                    socket.send(routingPacket.toDatagramPacket(address));
                }
                else {
                    int portNum = flowTable.get(dst);
                    InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, portNum);
                    socket.send(typedPacket.toDatagramPacket(address));
                }
            }
            else if (typedPacket instanceof RoutingPacket) {
                int endDst = ((RoutingPacket) typedPacket).getDst();
                int nextDst = ((RoutingPacket) typedPacket).getDst2();
                flowTable.put(endDst,nextDst);
                for (DataPacket packetToSend : packetsToSend) {
                    if(flowTable.get(packetToSend.getDst())!=null) {
                        int portNum = flowTable.get(packetToSend.getDst());
                        InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, portNum);
                        socket.send(packetToSend.toDatagramPacket(address));
                    }
                }
            }
        }catch (Exception e) {e.printStackTrace();}
    }
    private void start () {
        InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, CONTROL_PORT);
        HelloPacket packet = new HelloPacket ();
        try {
            socket.send(packet.toDatagramPacket(address));
        }catch (Exception e){e.printStackTrace();}
    }

}
