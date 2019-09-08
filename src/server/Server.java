package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.ArrayList;


public class Server {
	
	
	private static DatagramSocket socket;
	
	private static boolean running;
	private static int ClientId ;
	
	private static  ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
	private static int nb =0;
	private static  ArrayList<ClientInfo> jeu = new ArrayList<ClientInfo>();
	private static int r1=-1;
	private static int r2 =-1;
	private static int j1 = -1;
	private static int j2 = -1;
	
	
	
	public static void start(int port){
		
		try {
			socket = new DatagramSocket(port);
			running = true;
			listen();
			System.out.println("le server a demarré sur le port "+ port);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	private static void broadcast(String message){
		
		for(ClientInfo cl : clients) {
			send(message,cl.GetAddress(),cl.GetPort());
		}

	}
	private static void send(String message,InetAddress address ,int port){
		
		try {
			message += "\\e";
			byte[]buffer=message.getBytes();
			DatagramPacket pack = new DatagramPacket(buffer,buffer.length,address,port);
			socket.send(pack);
			//System.out.println("le message a etait envoye au client :" + address.getHostAddress() +":"+port);
		}catch(Exception e){
			e.printStackTrace();
			
		}

	}
	private static void listen(){
		Thread recevoir =  new Thread("listner") {
			public void run() {
				try {
					while(running) {
						byte[] buffer =  new byte [1024];
						DatagramPacket pack = new DatagramPacket(buffer,buffer.length);
						socket.receive(pack);
						
						String message = new String(buffer);
						
						message = message.substring(0,message.indexOf("\\e"));
						
						
						if(!inlstcommandes(message,pack)){
							broadcast(message);
							
						}
						
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};recevoir.start();
		

	}
	private static boolean hasname(ArrayList<ClientInfo> clients,String name) {
		
		for (ClientInfo i : clients) {
			if(i.GetName().equals(name)) {
				return true;
			}
		}
		return false;
		
	}

	private static boolean inlstcommandes(String message,DatagramPacket pack) {
		String name;

		//name = message.substring(message.indexOf(" ")+1);
	    if(message.startsWith("IAM ")) {
	    	name = message.substring(message.indexOf(" ")+1);
			if (name.equals("null")) {
				send("entrez un nom",pack.getAddress(),pack.getPort());
			}
			else if(!name.contains(" ")) {
				if (clients.isEmpty()) {
					clients.add(new ClientInfo(name,ClientId++,pack.getAddress(),pack.getPort()));
					broadcast(name +" est parmis nous");
					
				}
				else {
					if(hasname(clients,name)) {
						send("erreur ce pseudo est déja pris",pack.getAddress(),pack.getPort());
					}
					else {
						clients.add(new ClientInfo(name,ClientId++,pack.getAddress(),pack.getPort()));
						broadcast(name +" est parmis nous");
					}
				}
				

			}
			else if(name.contains(" ")) {
				send("votre pseudo ne doit pas contenir d'espace",pack.getAddress(),pack.getPort());
			}
			else if (name.equals("")) {
				send("erreur",pack.getAddress(),pack.getPort());
			}
			
			return true;
							
		}
		else if(message.startsWith("PRV ")) {
			//prv lies : 
            String src = message.substring(message.indexOf(" ")+1,message.indexOf(":")+1);
            
			String msg = message.substring(message.indexOf(":")+1);
			
			System.out.println(msg);
			String ame = msg.substring(0,msg.indexOf(" "));
			
			System.out.println(ame);
			 if(hasname(clients,ame)) {
				 for(ClientInfo i : clients) {
					 
					 if(i.GetName().equals(ame)) {
						 send(src +msg.substring(msg.indexOf(" ") + 1),i.GetAddress(),i.GetPort());
						 send("votre message a était envoyé à "+ame,pack.getAddress(),pack.getPort());
					 }

				 }

			 }
			 else {
				 send("ce nom n'existe pas",pack.getAddress(),pack.getPort());
			 }
	      return true;
			 
		}
		else if(message.equals("BYE")) {
			 send("au revoir",pack.getAddress(),pack.getPort());
			 
			 for(ClientInfo i : clients) {
				 if(i.GetAddress().equals(pack.getAddress()) && i.GetPort() == pack.getPort()) {
					 String nam  =  i.GetName();
					 clients.remove(i);
					 if(clients.size() >= 1) {
					 broadcast(nam+" nous a quitter");
					 }
					 break;
					 

					
				 }
				 
				     
			 }
			 
			 
			 return true;
			 
		}
		else if(message.equals("WHO")) {
			 String noms = "";
			
			 for(ClientInfo i : clients) {
				 
				
				 
				 noms += i.GetName() + " ";
					
				 }
			 
			 send("ce trouve dans ce chat :" + noms ,pack.getAddress(),pack.getPort());
				 
			
			return true;
		}
		else if(message.startsWith("MSG ")) {
			String msg = message.substring(message.indexOf(" ")+1);
			broadcast(msg);
			return true;
		}
		else if(message.equals("PPC.")) {
			String arme[] = {"la pierre", "les ciseaux", "la feuille"};
			 for (int i=0;i<3;i++){
				send(i + ". " + arme[i],pack.getAddress(),pack.getPort());
			 }
			
			return true;
		}
		else if(message.startsWith("PPC ")) {
			String arme[] = {"la pierre", "les ciseaux", "la feuille"};
			int resultat[] = {0, 2, 1};
			int nb = Integer.valueOf(message.substring(message.indexOf(" ")+1));
			System.out.println(nb);
			if(nb>2 || nb < 0) {
				send("entrez un nombre entre 0 et 2",pack.getAddress(),pack.getPort());
			}
			else {
				j1 = nb;
				j2 = (int)(Math.random()*3);
				send("J'ai choisi "+ arme[j2],pack.getAddress(),pack.getPort());
				
				 if ( j1 == j2 ) { send("On a choisi la meme chose",pack.getAddress(),pack.getPort()); }
				 else {
						int i = j1 + j2 - 1;

					
						 if (j1 == resultat[i]) {
							 send("Tu gagnes",pack.getAddress(),pack.getPort());
						  } else {
							 send("tu perds",pack.getAddress(),pack.getPort());
						}
				 }
				


			}
			 			
			return true;
		}
		else if(message.startsWith("PPC? ")) {
			nb++;

			
			jeu.add(new ClientInfo("jouer"+nb,ClientId++,pack.getAddress(),pack.getPort()));
			if(nb == 1) {
				  send("vous etes le jouer numéro "+nb+" il manque un jouer",pack.getAddress(),pack.getPort());
				  for(ClientInfo c : clients) {
					  if(c.GetAddress() != pack.getAddress() && c.GetPort() != pack.getPort())
						  send("une partie de ppc a été lancer lance la commande PPC? (un nombre entre 0 et 2) pour jouer",c.GetAddress(),c.GetPort());
				  }
				  r1 = Integer.valueOf(message.substring(message.indexOf(" ")+1));
				  if(r1 == 0)
					  send("vous avez choisi la pierre",pack.getAddress(),pack.getPort());
				  if(r1 == 1)
					  send("vous avez choisi les ciseaux",pack.getAddress(),pack.getPort());
				  if(r1 == 2)
					  send("vous avez choisi la feuille",pack.getAddress(),pack.getPort());
			}
			if(nb==2) {
	
				nb = 0;
				int resultat[] = {0, 2, 1};
				
				for(ClientInfo i : jeu) {
					send("c'est parti",i.GetAddress(),i.GetPort());
				}
				r2 = Integer.valueOf(message.substring(message.indexOf(" ")+1));
				  if(r2 == 0)
					  send("vous avez choisi la pierre",pack.getAddress(),pack.getPort());
				  if(r2 == 1)
					  send("vous avez choisi les ciseaux",pack.getAddress(),pack.getPort());
				  if(r2 == 2)
					  send("vous avez choisi la feuille",pack.getAddress(),pack.getPort());
				System.out.println(r1+" "+r2);
				if ( r1 == r2 ) {
					for(ClientInfo i : jeu) {
						send("vous avez choisie la meme chose",i.GetAddress(),i.GetPort());
					}
				}else {
					int i = r1 + r2 - 1;
					  if(r1 == 0)
						  send("votre adversaire a choisi la pierre",jeu.get(1).GetAddress(),jeu.get(1).GetPort());
					  if(r1 == 1)
						  send("votre adversaire a choisi les ciseaux",jeu.get(1).GetAddress(),jeu.get(1).GetPort());
					  if(r2== 2)
						  send("votre adversaire a choisi la feuille",jeu.get(1).GetAddress(),jeu.get(1).GetPort());
					  
					  if(r2 == 0)
						  send("votre adversaire a choisi la pierre",jeu.get(0).GetAddress(),jeu.get(0).GetPort());
					  if(r2 == 1)
						  send("votre adversaire a choisi les ciseaux",jeu.get(0).GetAddress(),jeu.get(0).GetPort());
					  if(r2 == 2)
						  send("votre adversaire a choisi la feuille",jeu.get(0).GetAddress(),jeu.get(0).GetPort());

					 if (r2 == resultat[i]) {
						 send("Tu gagnes",pack.getAddress(),pack.getPort());
						 send("Tu perds",jeu.get(0).GetAddress(),jeu.get(0).GetPort());
					  }else {
						 send("tu perds",pack.getAddress(),pack.getPort());
						 send("Tu gagnes",jeu.get(0).GetAddress(),jeu.get(0).GetPort());
					}

				}

				

			}

			
			return true;
		}
	    
			
									
		return false;
	}


	public static void stop(){
		
		running = false;

	}

}
