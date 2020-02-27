// Adam Ogórek 18335914
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Timer;

public class CAndC extends Node{

    Terminal terminal;
    static final int DEFAULT_SRC_PORT = 50000;
    static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "localhost";
    Timer timer;
    int currentFrameNumber;
    InetSocketAddress dstAddress= new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_DST_PORT);
    CAndC() {
        try {
            terminal = new Terminal ("C&C");
            socket = new DatagramSocket(DEFAULT_SRC_PORT);
            currentFrameNumber=1;
            listener.go();
        }catch(Exception e) {e.printStackTrace();}
    }

    @Override
    public synchronized void onReceipt(DatagramPacket packet) {
        StringContent data = new StringContent(packet);
        String dataString=data.toString();
// "&0&" is a confirmation string from the broker, telling uss that the work was done. We need to acknowledge that we received the confirmation and then we can take new input
        if(dataString.startsWith("&0&")) {
            try {
                terminal.println("Work completed");
                byte [] dataToSend = {'&'};
                DatagramPacket confirmation = new DatagramPacket(dataToSend, dataToSend.length, dstAddress);
                socket.send(confirmation);
                this.notify();
            }catch (Exception e){e.printStackTrace();}
        }
// The only different message we can receive is the acknowledgement to work description, we don't need to resend the description then.
        else {
            timer.cancel();
        }
    }
//Get work from user, send to broker, resend if broker doesn't acknowledge.

    public synchronized void start() throws Exception {
        while(true) {
            currentFrameNumber+=1;
            currentFrameNumber%=2;
            byte[] data = null;
            DatagramPacket packet = null;
            String number = terminal.read("How many times should the work be done?");
            if(Integer.parseInt(number)>0) {
                String dataString = (terminal.read("Work to do: "));
                if (dataString.equals("quit")) {
                    System.exit(0);
                }
                String endData = currentFrameNumber + " " + number + " " + dataString;
                data = endData.getBytes();
                terminal.println("Sending work description...");
                packet = new DatagramPacket(data, data.length, dstAddress);
                socket.send(packet);
                timer = new Timer(true);
                timer.schedule(new TimeOut(packet, this), 200, 200);
                terminal.println("Work description sent");
                this.wait();
            }
        }
    }
}
