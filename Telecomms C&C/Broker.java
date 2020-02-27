// Adam Ogórek 18335914
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Timer;
public class Broker extends Node{
    static final int DEFAULT_SRC_PORT=50001;
    static final int C_AND_C_PORT = 50000;
    static final String DEFAULT_DST_NODE = "localhost";
    ArrayList <WorkerData> availableWorkers;
    InetSocketAddress CAndCAddress;
    Terminal terminal;
    int currentId=1;
    int workersNotDone;
    int frameNumber;
    int totalWorkDone;
    int amountOfWork;
    byte[] currentWork;
    Timer CAndCTimer;
    int expectedFrameNumber;
    Broker() {
        try {
            availableWorkers = new ArrayList<>();
            socket = new DatagramSocket(DEFAULT_SRC_PORT);
            CAndCAddress= new InetSocketAddress(DEFAULT_DST_NODE,C_AND_C_PORT);
            //System.out.println(socket.getLocalPort());
            listener.go();
            terminal = new Terminal("Broker");
            terminal.println("waiting for packets");
            expectedFrameNumber=0;
            frameNumber=1;
        }catch (Exception e){e.printStackTrace();}
    }
    public synchronized void onReceipt(DatagramPacket packet) {
        StringContent data = new StringContent(packet);
// Determine whether the package comes from the C&C or worker
        if(packet.getPort()==C_AND_C_PORT) {
// If it starts with a '&' its an acknowledgement, no need to resend.
            if(data.toString().startsWith("&")) {
                CAndCTimer.cancel();
            }
// Else, it has to be work description, send confirmation to C&C and process work
            else {
//If the first number matches expected frame number, proceed with work
                terminal.println("received work");
                if(data.toString().startsWith((Integer.toString(expectedFrameNumber)))) {
                    String confirmation = "&2&";
                    sendConfirmation(CAndCAddress, confirmation);
                    prepSendWork(data);
                }
//Else, C&C didn't receive acknowledgement, send it again.
                else {
                    String confirmation = "&2&";
                    sendConfirmation(CAndCAddress,confirmation);
                }
            }
        }
// If its the message from worker
        else {
// '&' means that the worker is volunteering for work, add him to the list of workers and send confirmation
            if(data.toString().startsWith("&")){
                String name = data.toString().substring(1,packet.getLength());
                terminal.println("New worker: " + name);
                WorkerData newWorker = new WorkerData(currentId,name, packet.getPort());
                currentId++;
                availableWorkers.add(newWorker);
                String confirmation ="&0&";
                sendConfirmation(new InetSocketAddress(DEFAULT_DST_NODE,newWorker.getPort()),confirmation);
            }
// Else its a request, if its the same as current frame, we have to resend, if its the next one, note that this worker has done his work
            else {
                if(data.toString().startsWith(Integer.toString(frameNumber))) {
                    try {
                        InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, packet.getPort());
                        byte [] dataToSend= new byte[currentWork.length+1];
                        dataToSend[0]=(byte)(frameNumber + '0');
                        for(int i=0; i<currentWork.length; i++) {
                            dataToSend[i+1]=currentWork[i];
                        }
                        DatagramPacket work = new DatagramPacket(dataToSend, dataToSend.length, address);
                        socket.send(work);
                        Timer timer = new Timer(true);
                        timer.schedule(new TimeOut(packet,this), 200,200);
                        for(WorkerData worker : availableWorkers) {
                            worker.setTimer(timer);
                            worker.workDone = false;
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                else {
                    for (WorkerData worker : availableWorkers) {
                        if (worker.getPort() == packet.getPort()) {
                            terminal.println(worker.name);
                            worker.workDone = true;
                            worker.stopTimer();
                            workersNotDone--;
                            totalWorkDone++;
                            terminal.println(worker.name + " done " + workersNotDone);
                        }
                    }
// If all workers are done, check if the total work is done, and either confirm to C&C or send more work
                    if (workersNotDone <= 0) {
                        if(totalWorkDone >= amountOfWork) {
                            String confirmation = "&0&";
                            sendConfirmation(CAndCAddress, confirmation);
                            terminal.println("work done");
                            DatagramPacket confirmationPacket = new DatagramPacket(confirmation.getBytes(), confirmation.length(), CAndCAddress);
                            CAndCTimer = new Timer(true);
                            CAndCTimer.schedule(new TimeOut(confirmationPacket, this), 200, 200);
                            expectedFrameNumber+=1;
                            expectedFrameNumber%=2;
                        }
                        else {
                            sendWork();
                        }
                    }
                }
            }
        }
    }
    public void sendConfirmation(InetSocketAddress address, String confirmation) {
        byte [] data = confirmation.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address);
        try {
            socket.send(packet);
        }catch (Exception e){e.printStackTrace();}
    }
    public synchronized  void prepSendWork(StringContent  data) {
        try {
            totalWorkDone=0;
            String [] splitString = data.toString().split(" ");
            currentWork = splitString[2].getBytes();
            amountOfWork = Integer.parseInt(splitString[1]);
            terminal.println("Sending work");
            sendWork();
        }catch (Exception e) {e.printStackTrace();}
    }

    public void sendWork() {
        workersNotDone = 0;
        frameNumber+=1;
        frameNumber%=2;
        byte [] dataToSend= new byte[currentWork.length+1];
        dataToSend[0]=(byte)(frameNumber + '0');
        for(int i=0; i<currentWork.length; i++) {
            dataToSend[i+1]=currentWork[i];
        }
        for (int i = 0; i + totalWorkDone < amountOfWork && i < availableWorkers.size(); i++) {
            WorkerData worker = availableWorkers.get(i);
            if (worker.working && worker.workDone) {
                try {
                    InetSocketAddress address = new InetSocketAddress(DEFAULT_DST_NODE, worker.getPort());
                    DatagramPacket packet = new DatagramPacket(dataToSend, dataToSend.length, address);
                    socket.send(packet);
                    Timer timer = new Timer(true);
                    timer.schedule(new TimeOut(packet, this), 500, 500);
                    worker.setTimer(timer);
                    worker.workDone = false;
                    workersNotDone++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
