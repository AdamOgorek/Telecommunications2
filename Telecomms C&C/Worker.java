// Adam Ogórek 18335914
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.lang.Math;
import java.util.Timer;
public class Worker extends Node{
    static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "localhost";
    int srcPort;
    Terminal terminal;
    String name;
    int expectedFrameNumber;
    Timer timer;
    InetSocketAddress dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_DST_PORT);
    Worker() {
        try {
            socket = new DatagramSocket();
            srcPort = socket.getLocalPort();
            listener.go();
            terminal = new Terminal ("Worker");
            expectedFrameNumber=0;
            this.start();
        }catch (Exception e) {e.printStackTrace();}
    }
    @Override
    public synchronized void onReceipt(DatagramPacket packet) {
        StringContent data = new StringContent(packet);
        //terminal.println("received");
// "&0& means that the broker acknowledges workers "ready for work" message, no need to resend
        if(data.toString().startsWith("&0&")) {
            timer.cancel();
        }
// If something else, it has to be work description, check if its the expected frame and either do work and send ack, or send a request for other frame
        else {
            if (data.toString().startsWith(Integer.toString(expectedFrameNumber))) {
                work(data.toString().substring(1));
            } else {
                sendConfirmation();
            }
            this.notify();
        }
    }
    public void work(String workload) {
        terminal.println(workload);
        expectedFrameNumber++;
        expectedFrameNumber%=2;
        sendConfirmation();
    }
// Sends a request for next expected frame to the broker
    public void sendConfirmation(){
        String confirmation = Integer.toString(expectedFrameNumber);
            byte[] data = confirmation.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
            try {
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public synchronized void start() throws Exception {
        try {
            name = "&" + terminal.read("Please input a name:");
            byte[] data = name.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
            socket.send(packet);
            timer = new Timer(true);
            timer.schedule(new TimeOut(packet,this),500, 500);
        }catch (Exception e) {e.printStackTrace();}
    }

    public synchronized void check() {
        while(true) {
        String temp = terminal.read("Work?");
        if (temp.charAt(0) == 'N') {
            System.out.println("No");
        }
    }
    }
}
