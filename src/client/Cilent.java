package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Cilent {
	
	private  DatagramSocket socket;
	private  InetAddress address;
	private  int port;
	private  boolean running;
	private String name;	
	
	public Cilent(String name,String address , int port) {
		try {
			this.name = name;
			this.address= InetAddress.getByName(address);
			this.port = port;
			socket = new DatagramSocket();
			running = true;
			recive();
		    send("IAM "+name);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(String message){
		
		try {
			
			if(!lstcmd(message)) {
				message = "MSG " + name +":" + message;
			}
			
			if((message.startsWith("@"))) {
				message = "PRV "+name+":"+ message.substring(1);
			}

			if(message.startsWith("IAM "))
				name = message.substring(message.indexOf(" ")+1);
			
			message += "\\e";
			byte[]buffer=message.getBytes();
			DatagramPacket pack = new DatagramPacket(buffer,buffer.length,address,port);
			socket.send(pack);
			//System.out.println("envoie du message au server" + address.getHostAddress() +":"+port);
		}catch(Exception e){
			e.printStackTrace();
			
		}

	}
	
	private void recive(){
		Thread recevoir =  new Thread("recevoir les donnees") {
			public void run() {
				try {
					while(running) {
						byte[] buffer =  new byte [1024];
						DatagramPacket pack = new DatagramPacket(buffer,buffer.length);
						socket.receive(pack);
						
						String message = new String(buffer);
						
						message = message.substring(0,message.indexOf("\\e"));

							ClWindow.setxtarea(message);
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};recevoir.start();
		

	}

	private static boolean lstcmd(String message) {
		
		if(message.startsWith("IAM ")) {
			return true;
		}
		if(message.startsWith("@"))
			return true;
		if(message.equals("BYE"))
			return true;
		if(message.equals("WHO"))
			return true;
		if(message.equals("PPC."))
			return true;

		if(message.startsWith("PPC "))
			return true;
		if(message.startsWith("PPC? "))
			return true;
		return false;
	}
	

}
