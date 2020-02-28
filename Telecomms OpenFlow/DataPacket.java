import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket extends GeneralPacket {
    DataPacket (byte [] data, int port) {
        this.data = data;
        this.port =port;
    }
    DataPacket (int dst, byte[] dataToSend) {
    	ByteBuffer temp = ByteBuffer.allocate(5 + dataToSend.length);
    	temp.put(DATA_PACKET);
        temp.putInt(dst);
        temp.put(dataToSend);
    	this.data = temp.array();
    }
    public int getDst () {
        ByteBuffer temp = ByteBuffer.wrap(Arrays.copyOfRange(data,1,5));
        int dst = temp.getInt();
        return dst;
    }

}
