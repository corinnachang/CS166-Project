import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

public class ListenerThread extends Thread {
	private final int DELAY = 5000; //5 seconds
	
	private HoneyPot hp;
	private Socket socket;
	private InetAddress socketAddr; 
	private int socketPort;
	private BufferedReader input;
	private OutputStream output;
	private FileWriter log;
	private Date date;

	public ListenerThread (Socket socket, HoneyPot hp) {
		this.socket = socket;
		this.hp = hp;
	}
	
	public void run() {
		// get port and address?
		socketAddr = socket.getInetAddress();
		socketPort = socket.getPort();
		date = new Date();
		
		try {
			//Create logs directory if doesn't exist
			Files.createDirectories(Paths.get("./logs/"));
			
			//Create directory for file if first connection attempt
			String fileDirectoryName = "./logs/"+socketAddr.getHostAddress().replace(':', '_').replace('.', '_')+"/";
			Files.createDirectories(Paths.get(fileDirectoryName));
			
			//Initialize file
			String filename = fileDirectoryName+socketAddr.getHostAddress().replace(':', '_').replace('.', '_');
			int connectionAttempt = 1;
			File file = new File(filename+" ("+connectionAttempt+").txt");
			while(file.exists()) {
				connectionAttempt++;
				file = new File(filename+" ("+connectionAttempt+").txt");
			}
			log = new FileWriter(file);
			
			//Collect basic IP information
			System.out.println("Connected with: "+socket.getRemoteSocketAddress());
			System.out.println("-Info saved on "+filename+" ("+connectionAttempt+").txt");
			//System.out.println("IP: " + socketAddr);
			//System.out.println("Port: " + socketPort);
			//System.out.println(date);

			log.write("Client IP: "+socket.getRemoteSocketAddress());
			log.write("\nIP: " + socketAddr);
			log.write("\nPort: " + socketPort);
			log.write("\nConnection Attempt: "+connectionAttempt);
			log.write("\nTime connected: " + date);
			
			//Get location details based on ip address
			log.write("\nLocation details:");
			try {
				WebServiceClient geoIP2 = new WebServiceClient.Builder(712043, "M80mDGd9KoRli06x").host("geolite.info").build();	//account ID, license_key
				//License key (Yes): lpLv24T84Kwd
				//License key (No): M80mDGd9KoRli06x
				CityResponse response = geoIP2.city(socketAddr);
				//CityResponse response = geoIP2.city(InetAddress.getByName("89.250.165.81"));
				Country country = response.getCountry();
				Subdivision subdivision = response.getMostSpecificSubdivision();
				
				log.write("\n-Country: "+country.getName()+" ("+country.getIsoCode()+")");
				log.write("\n-Subdivsion (best estimated): "+subdivision.getName()+" ("+subdivision.getIsoCode()+")");
				log.write("\n-City: "+response.getCity().getName());
				log.write("\n-Postal code: "+response.getPostal().getCode());
				log.write("\n-Latitude: "+response.getLocation().getLatitude());
				log.write("\n-Longitude: "+response.getLocation().getLongitude());
				
				log.write("\nDomain details:");
				log.write("\n-User Type: "+response.getTraits().getUserType());
				log.write("\n-Domain: "+response.getTraits().getDomain());
				log.write("\n-Organization: "+response.getTraits().getOrganization());
				log.write("\n-Mobile Country Code: "+response.getTraits().getMobileCountryCode());
				log.write("\n-Mobile Network Code: "+response.getTraits().getMobileNetworkCode());

				System.out.println("-Location info written");
			} catch (AddressNotFoundException anfe) {
				System.out.println("-***Could not identify IP details: "+anfe.getMessage());
				log.write("\n-Could not identify location details: "+anfe.getMessage());
			}
			
			
			//Is this needed?
			output = socket.getOutputStream();
			//output.write("Test".getBytes());
			
		////////////////////////////////////	
			
			//output.write("HTTP/1.1 200 OK\r\n".getBytes());
			//output.write(("ContentType: text/html\r\n").getBytes());
			//output.write("\r\n".getBytes());
			
			//Scanner scanner = new Scanner(new File("server.html"));
			//String htmlString = scanner.useDelimiter("\\Z").next();
			//scanner.close();
			//output.write(htmlString.getBytes("UTF-8"));
			
		////////////////////////////////////
			
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String info;
			log.write("\n\nCollected Input Stream:");
			output.write("Enter Password: ".getBytes("UTF-8"));
			while((info = input.readLine()) != null){
				if(info.isEmpty()){
                    break;
                }
                //System.out.println(info);
    			log.write("\n"+info);
    			
				output.write("Password denied. Please try again.\n".getBytes("UTF-8"));
				output.write("Enter Password: ".getBytes("UTF-8"));
            }
		} catch (SocketException e) {
			//Connection aborted. Do nothing.
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Final logging after connection disconnected
		try {
			log.write("\nConnection duartion: "+(new Date().getTime()-date.getTime()+"ms"));
			System.out.println("-"+socketAddr+" connection duartion: "+(new Date().getTime()-date.getTime()+"ms"));
	    	System.out.println("-"+socketAddr+" connection closed");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Close everything
		try {
			System.out.println("-"+socketAddr+" log closed");
			log.write("\n*********************************\n");
			log.close();
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//Delay announcing disconnection to prevent client from making too many connections at a time
		String ipWithPort = socket.getRemoteSocketAddress().toString();
		String ip = ipWithPort.substring(0,ipWithPort.indexOf(':'));
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {}
		hp.disconnectIP(ip);
	}

}
