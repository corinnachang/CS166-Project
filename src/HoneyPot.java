import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class HoneyPot extends Thread {
	private ServerSocket listener = null; // basically a server
	private Socket socket = null; // the port/socket that connects to the listener
	private HashSet<String> connectedIP = new HashSet<String>(); //Prevents client from making too many connections at a time
	private GUI gui;
	private int counter = 0;
	
	public HoneyPot(GUI gui) {
		System.out.println("Start honeypot");
		//this.startServer();
		this.gui = gui;
	}
	
	public void run() {
		try {
			System.out.println("Start server");
			listener = new ServerSocket(1234); //1234 is the port of the server
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
            try {
            	System.out.println("waiting for connection...");
            	socket = listener.accept(); // connect to incoming sockets
            } catch (IOException e) {
            	e.printStackTrace();
            }
   			String ipWithPort = socket.getRemoteSocketAddress().toString();
   			String ip = ipWithPort.substring(0,ipWithPort.indexOf(':'));
            if(!connectedIP.contains(ip)) {
	            // new thread for each connection
	            //System.out.println("new thread created");
	            connectedIP.add(ip);
	            gui.updateCard(++counter);
	            new ListenerThread(socket,this).start(); // runs run() in ServerThread
            } else {
            	System.out.println("(Denied: Repeat from earlier)");
            }
        }
	}
	
	//Used to prevents client from making too many connections at a time
	public void disconnectIP(String ip) {
		connectedIP.remove(ip);
		System.out.println("Removed "+ip+" from connection list");
		gui.updateCard(--counter);
	}
	
	public static void main(String args[])
    {
        //HoneyPot hp = new HoneyPot();
    }

}
