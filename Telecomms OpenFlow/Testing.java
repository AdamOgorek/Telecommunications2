
public class Testing {
	
	public static void main(String [] args) {
		Control control = new Control();
		Router [] routers = new Router[9];
		EndPoint [] nodes = new EndPoint [4];
		nodes [1] = new EndPoint (1);
		nodes [2] = new EndPoint (2);
		nodes [3] = new EndPoint (3);
		for(int i=0; i<8; i++) {
			routers[i] = new Router();
		}
		nodes[1].connect(routers[0].socket.getLocalPort());
		nodes[2].connect(routers[4].socket.getLocalPort());
		nodes[3].connect(routers[7].socket.getLocalPort());
		nodes[1].send(3);
		nodes[1].send(3);
		while (true){}
	}
}
