package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Game {


	    private Player[] board = {
	        null, null, null,
	        null, null, null,
	        null, null, null};


	    public Player currentPlayer;


	    public boolean hasWinner() {
	        return
	            (board[0] != null && board[0] == board[1] && board[0] == board[2])
	          ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
	          ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
	          ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
	          ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
	          ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
	          ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
	          ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
	    }

	
	    public boolean boardFilledUp() {
	        for (int i = 0; i < board.length; i++) {
	            if (board[i] == null) {
	                return false;
	            }
	        }
	        return true;
	    }
	    
	    public synchronized boolean legalMove(int location, Player player) {
	        if (player == currentPlayer && board[location] == null) {
	            board[location] = currentPlayer;
	            currentPlayer = currentPlayer.opponent;
	            currentPlayer.otherPlayerMoved(location);
	            return true;
	        }
	        return false;
	    }
	    public class Player extends Thread {
	        char mark;
	        Player opponent;
	        Socket socket;
	        BufferedReader input;
	        PrintWriter output;
	   
	        public Player(Socket socket, char mark) {
	            this.socket = socket;
	            this.mark = mark;
	            try {
	                input = new BufferedReader(
	                    new InputStreamReader(socket.getInputStream()));
	                output = new PrintWriter(socket.getOutputStream(), true);
	                output.println("BIENVENUE " + mark);
	                output.println("MESSAGE MERCI D'ATTENDRE L'ADVERSAIRE");
	            } catch (IOException e) {
	                System.out.println("Player died: " + e);
	            }
	        }
	      
	        public void setOpponent(Player opponent) {
	            this.opponent = opponent;
	        }

	        
	          
	        public void otherPlayerMoved(int location) {
	            output.println("HEMOVED " + location);
	            output.println(
	                hasWinner() ? "PERDU" : boardFilledUp() ? "NULL" : "");
	        }
	    
	        public void run() {
	            try {
	                
	                output.println("yosh tout le monde est là");

	           
	                if (mark == 'X') {
	                    output.println("MESSAGE votre tour");
	                }

	 
	                while (true) {
	                    String command = input.readLine();
	                    if (command.startsWith("MOVE")) {
	                        int location = Integer.parseInt(command.substring(5));
	                        if (legalMove(location, this)) {
	                            output.println("VALID");
	                            output.println(hasWinner() ? "GAGNE"
	                                         : boardFilledUp() ? "NULL"
	                                         : "");
	                        } else {
	                            output.println("MESSAGE Erreur");
	                        }
	                    } else if (command.startsWith("QUIT")) {
	                        return;
	                    }
	                }
	            } catch (IOException e) {
	                System.out.println("Connection echoue: " + e);
	            } finally {
	                try {socket.close();} catch (IOException e) {}
	            }
	        }
	    }
	

}
