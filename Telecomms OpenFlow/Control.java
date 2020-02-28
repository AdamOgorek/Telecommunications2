import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class Control extends Node {
    HashMap <Integer, Integer> routersTable;
    HashMap <Integer, Integer> routerPorts;
    HashMap <Integer, Integer[]> routingTables;
    public static final int PORT = 50000;
    public static final Integer [] DST1TABLE = {-1, 0, 1, 1, 3, 4, 7, 2};
    public static final Integer [] DST2TABLE = {1, 3, 3, 4, -1, 4, 5, 6};
    public static final Integer [] DST3TABLE = {1, 2, 7, 2, 5, 6, 7, -1};
    int nextRouterID;
    Control () {
        try {
            routersTable = new HashMap<>();
            routerPorts = new HashMap<>();
            nextRouterID = 0;
            socket = new DatagramSocket(PORT);
            routingTables = new HashMap <> ();
            routingTables.put(1, DST1TABLE);
            routingTables.put(2, DST2TABLE);
            routingTables.put(3, DST3TABLE);
            listener.go();
        }catch (Exception e) {e.printStackTrace();}
    }
    @Override
    public void onReceipt(DatagramPacket packet) {
        GeneralPacket typedPacket = GeneralPacket.getType(packet);
        try {
            if (typedPacket instanceof HelloPacket) {
                routersTable.put(typedPacket.getPort(), nextRouterID);
                routerPorts.put(nextRouterID++, typedPacket.getPort());
            } else if (typedPacket instanceof RoutingPacket) {
                int dst = ((RoutingPacket) typedPacket).getDst();
                Integer[] table = routingTables.get(dst);
                int srcPort = typedPacket.getPort();
                int currentNode = routersTable.get(srcPort);
                int nextDstPort=0;
                while(currentNode!=-1) {
                    int nextDst = table[currentNode];
                    if(nextDst!= -1) {
	                    nextDstPort = routerPorts.get(nextDst);
	                    RoutingPacket routingPacket = new RoutingPacket(dst,nextDstPort);
	                    InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE,srcPort);
	                    socket.send(routingPacket.toDatagramPacket(address));
                    }
                    else {
                    	switch(dst) {
                    	case 1:
                    		nextDstPort = NODE1;
                    		break;
                    	case 2:
                    		nextDstPort = NODE2;
                    		break;
                    	case 3:
                    		nextDstPort = NODE3;
                    		break;
                    	}
                    	RoutingPacket routingPacket = new RoutingPacket(dst,nextDstPort);
	                    InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE,srcPort);
	                    socket.send(routingPacket.toDatagramPacket(address));
                    }
                    currentNode = nextDst;
                    srcPort = nextDstPort;
                }
            }
        }catch (Exception e) {e.printStackTrace();}
    }
}
