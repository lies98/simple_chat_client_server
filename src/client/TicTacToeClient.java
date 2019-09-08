package client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TicTacToeClient {

    public JFrame frame = new JFrame("Tic Tac Toe");
    public JLabel messageLabel = new JLabel("");
    public ImageIcon icon;
    public ImageIcon opponentIcon;

    public Square[] board = new Square[9];
    public Square currentSquare;
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;


  
    public TicTacToeClient(String serverAddress) throws Exception {

   
        socket = new Socket(serverAddress, 2049);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

       
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    out.println("MOVE " + j);}});
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }

   
    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("BIENVENUE")) {
                char mark = response.charAt(10);
                icon = new ImageIcon(mark == 'X' ? "/Users/liesamarouche/eclipse-workspace/Chatapp/res/redX.png" : "/Users/liesamarouche/eclipse-workspace/Chatapp/res/redCircle.png");
                opponentIcon  = new ImageIcon(mark == 'X' ? "/Users/liesamarouche/eclipse-workspace/Chatapp/res/blueCircle.png" : "/Users/liesamarouche/eclipse-workspace/Chatapp/res/blueX.png");
                frame.setTitle(""+ mark);
            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID")) {
                    messageLabel.setText("merci d'attendre l'adversaire");
                    currentSquare.setIcon(icon);
                    currentSquare.repaint();
                } else if (response.startsWith("HEMOVED")) {
                    int loc = Integer.parseInt(response.substring(response.indexOf(" ")+1));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("votre tour");
                } else if (response.startsWith("GAGNE")) {
                    messageLabel.setText("vous avez gagner");
                    break;
                } else if (response.startsWith("PERDU")) {
                    messageLabel.setText("vous avez perdu");
                    break;
                } else if (response.startsWith("NULL")) {
                    messageLabel.setText("c'est un nul");
                    break;
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
            }
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    public boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
            "encore une partie?",
            "rejouer",
            JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    //Graphical square in the client window.  
    @SuppressWarnings("serial")
	static class Square extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public Square() {
            setBackground(Color.white);
            add(label);
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

    

}