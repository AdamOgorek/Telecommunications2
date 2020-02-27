// Adam Ogórek 18335914
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

public class StringContent implements PacketContent {
	String string;
	
	public StringContent(DatagramPacket packet) {
		byte[] buf;
		
		buf= packet.getData();
		byte[] data= Arrays.copyOf(buf,packet.getLength());
		string = new String(data);
	}
	
	public StringContent(String string) {
		this.string = string;
	}
	
	public String toString() {
		return string;
	}

	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet= null;
		try {
			byte[] data= string.getBytes();
			packet= new DatagramPacket(data, data.length);
		}
		catch(Exception e) {e.printStackTrace();}
		return packet;
	}
}
