import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RoutingPacket extends GeneralPacket {
    RoutingPacket (byte [] data, int port) {
        this.data=data;
        this.port = port;
    }
    RoutingPacket(int dst) {
        this.data = new byte[5];
        this.data[0] = ROUTING_PACKET;
        ByteBuffer temp = ByteBuffer.allocate(4);
        temp.putInt(dst);
        byte [] tempByteArr = temp.array();
        for(int i=0; i<tempByteArr.length; i++) {
            data[i+1]=tempByteArr[i];
        }
    }
    RoutingPacket(int endDst, int nxtDst) {
        this.data = new byte[9];
        this.data[0] = ROUTING_PACKET;
        ByteBuffer temp = ByteBuffer.allocate(4);
        temp.putInt(endDst);
        byte [] tempByteArr = temp.array();
        for(int i=0; i<tempByteArr.length; i++) {
            data[i+1]=tempByteArr[i];
        }
        temp = ByteBuffer.allocate(4);
        temp.putInt(nxtDst);
        tempByteArr = temp.array();
        for(int i=0; i<tempByteArr.length; i++) {
            data[i+5]=tempByteArr[i];
        }
    }
    public int getDst() {
        ByteBuffer temp = ByteBuffer.wrap(Arrays.copyOfRange(data,1,5));
        int dst = temp.getInt();
        return  dst;
    }
    public int getDst2() {
        ByteBuffer temp = ByteBuffer.wrap(Arrays.copyOfRange(data,5,9));
        int dst = temp.getInt();
        return  dst;
    }
}
