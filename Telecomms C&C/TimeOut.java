// Adam Ogórek 18335914
import java.net.DatagramPacket;
import java.util.TimerTask;
import java.util.Timer;
public class TimeOut extends TimerTask {
    DatagramPacket packet;
    Node owner;
    TimeOut(DatagramPacket packet, Node owner) {
        this.packet=packet;
        this.owner=owner;
    }
    public void run() {
        owner.resend(packet);
    }
}
