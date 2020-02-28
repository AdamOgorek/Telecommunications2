import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public abstract class GeneralPacket {
    public static final byte HELLO_PACKET = 0;
    public static final byte ROUTING_PACKET = 1;
    public static final byte DATA_PACKET = 2;
    protected byte [] data;
    protected int port;
    public static GeneralPacket getType (DatagramPacket packet) {
        GeneralPacket content = null;
        byte type = packet.getData()[0];
        switch (type) {
            case HELLO_PACKET :
                content = new HelloPacket(packet.getData(),packet.getPort());
                break;
            case ROUTING_PACKET:
                content = new RoutingPacket(packet.getData(),packet.getPort());
                break;
            case DATA_PACKET :
                content = new DataPacket (packet.getData(),packet.getPort());
                break;
                default:

        }
        return content;
    }
    public byte [] getData() {
        return this.data;
    }
    public int getPort() {
        return this.port;
    }
    public int getLength() {
        return this.data.length;
    }
    public DatagramPacket toDatagramPacket(InetSocketAddress address) {
        return new DatagramPacket(this.data, this.data.length, address);
    }
}
