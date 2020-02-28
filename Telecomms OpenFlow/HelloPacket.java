import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HelloPacket extends GeneralPacket {
    HelloPacket (byte [] data, int port) {
        this.data = data;
        this.port = port;
    }
    HelloPacket () {
        this.data = new byte [1];
        this.data[0] = HELLO_PACKET;
    }
}
