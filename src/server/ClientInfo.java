package server;

import java.net.InetAddress;

public class ClientInfo {
	
	private InetAddress address;
	private int port;
	private String name;
	private int ID;
	
	
	public ClientInfo(String name,int id,InetAddress address,int port){
		
		this.address= address;
		this.ID = id;
		this.name= name;
		this.port=port;
		
	}
	
	public String GetName() {
		return name;
	}
	public int GetId() {
		return ID;
	}
	public int GetPort() {
		return port;
	}
	public InetAddress GetAddress() {
		return address;
	}
}
