import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
public class EndPoint extends Node {
	InetSocketAddress defaultRouter;
	EndPoint (int number) {
		try {
			switch (number) {
			case 1:
				socket = new DatagramSocket(NODE1);
				break;
			case 2:
				socket = new DatagramSocket(NODE2);
				break;
			case 3:
				socket = new DatagramSocket(NODE3);
				break;
			}
			listener.go();
		}catch (Exception e) {e.printStackTrace();}
	}
	public void connect(int routerPort) {
		defaultRouter = new InetSocketAddress("localhost", routerPort);
	}
	public void onReceipt(DatagramPacket packet) {
		System.out.println("received " + packet.getData());
	}
	
	public void send(int dst) {
		try {
			System.out.println("Sending");
			DataPacket packet = new DataPacket (dst, ("hello " + dst).getBytes());
			socket.send(packet.toDatagramPacket(defaultRouter));
		}catch(Exception e) {e.printStackTrace();}
	}
}
