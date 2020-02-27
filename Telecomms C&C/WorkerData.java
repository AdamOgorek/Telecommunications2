// Adam Ogórek 18335914
import java.util.Timer;
public class WorkerData {
    String name;
    int port;
    boolean working;
    boolean workDone;
    int ID;
    Timer timeout;
    WorkerData(int ID, String name, int port) {
        this.name = name;
        this.port=port;
        this.ID=ID;
        working=true;
        workDone=true;
    }
    public void setTimer(Timer timer) {
        this.timeout=timer;
    }
    public void stopTimer() {
        timeout.cancel();
        timeout=null;
    }
    public int getPort() {
        return this.port;
    }
}
