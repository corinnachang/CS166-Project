// A Java program for a Client
import java.net.*;
import java.io.*;

public class Client
{
	// initialize socket and input output streams
	private Socket socket		 = null;
	private DataInputStream input = null;
	private DataOutputStream out	 = null;

	// constructor to put ip address and port
	public Client(String address, int port)
	{
		// establish a connection
		try
		{
			socket = new Socket(address, port);
			System.out.println("Connected: " + socket.getLocalPort());

			// takes input from terminal
			input = new DataInputStream(System.in);

			// sends output to the socket
			out = new DataOutputStream(socket.getOutputStream());
		}
		catch(UnknownHostException u)
		{
			System.out.println(u);
		}
		catch(IOException i)
		{
			System.out.println(i);
		}

		// string to read message from input
		String line = "";

		// keep reading until "Over" is input
		while (!line.equals("Over"))
		{
			try
			{
				line = input.readLine();
				out.writeUTF(line);
			}
			catch(IOException i)
			{
				System.out.println(i);
			}
		}

		// close the connection
		try
		{
			input.close();
			out.close();
			socket.close();
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}

	public static void main(String args[])
	{
		Client client = new Client("127.0.0.1", 1234);
		//Base: 127.0.0.1
		//SJSU public IP address: 2607:f380:828:fa00::4460
	}
}
/*
Compile files in Command Line:
javac -cp "../geoip2/geoip2-3.0.1.jar;../geoip2/geoip2-3.0.1.jar;../geoip2/geoip2-3.0.1-sources.jar;../geoip2/jackson-annotations-2.13.2.jar;../geoip2/jackson-core-2.13.2.jar;../geoip2/jackson-databind-2.13.2.2.jar;../geoip2/maxmind-db-2.0.0.jar;ListenerThread.java" ListenerThread.java HoneyPot.java Client.java

Run HoneyPot in Command Line:
java -cp "../geoip2/geoip2-3.0.1.jar;../geoip2/geoip2-3.0.1.jar;../geoip2/geoip2-3.0.1-sources.jar;../geoip2/jackson-annotations-2.13.2.jar;../geoip2/jackson-core-2.13.2.jar;../geoip2/jackson-databind-2.13.2.2.jar;../geoip2/maxmind-db-2.0.0.jar;." HoneyPot.java
*/
