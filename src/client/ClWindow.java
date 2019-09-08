package client;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;

public class ClWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	//private JFrame frame;
	private JTextField msgField;
	private static JTextArea textArea = new JTextArea();
	private  Cilent client ;

	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClWindow window = new ClWindow();
					window.setVisible(true);
					window.setResizable(false);
					window.setTitle("Client");
					window.setBounds(100, 100, 500, 300);
					
					window.initialize();

					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void processWindowEvent(WindowEvent e) {
	    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	      client.send("BYE");
	    }
	    super.processWindowEvent(e); 

	  }

	public ClWindow() {
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
		String name = JOptionPane.showInputDialog("enter a name");
		client = new Cilent(name,"localhost",2048);

	}
	private void initialize() {

		
		
		
		
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBackground(Color.black);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setBackground(Color.black);
		
		msgField = new JTextField();
		panel.add(msgField);
		msgField.setColumns(20);
		
		JButton btnsend = new JButton("send");
		btnsend.addActionListener(e->{
			if(!msgField.getText().equals("")) {
				
				client.send(msgField.getText());
				msgField.setText("");
				
			}

		});
		panel.add(btnsend);
		JButton btns = new JButton("morpion");
		JButton btns2 = new JButton("PPC");
		
		panel.add(btns);
		btns.addActionListener(e->{
			   Thread jouer =  new Thread("listner") {
					public void run() {
						try {
							int nb = 0;
							while(true) {
								
								if(nb == 1)
									nb++;
									System.out.println("Tic Tac Toe Server is Running");
								  
		                      TicTacToeClient client = new TicTacToeClient("localhost");
		                      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		                      client.frame.setSize(600, 600);
		                      client.frame.setVisible(true);
		                      client.frame.setResizable(false);
		                      client.play();
		                      if (!client.wantsToPlayAgain()) {
		                           break;
		                      }
														
							}
							
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				};jouer.start();

		});
		panel.add(btns2);
		btns2.addActionListener(e->{
			   Thread tic =  new Thread("listner") {
				   boolean s = false;
					public void run() {
						try {
							
								JFrame frame = new JFrame();
						        JPanel panel = new JPanel();
								JButton btn0 = new JButton("pierre");
								JButton btn1 = new JButton("ciseaux");
								JButton btn2 = new JButton("feuille");
								JButton btns = new JButton("server_mode");
								JButton btns2 = new JButton("clients_mode");

								panel.add(btn0);
								panel.add(btn1);
								panel.add(btn2);
								panel.add(btns);
								panel.add(btns2);
								btns.addActionListener(e->{
									s=true;

								});
								btns2.addActionListener(e->{
									s=false;

								});
								btn0.addActionListener(e->{
									if(s)
										client.send("PPC 0");
									else
										client.send("PPC? 0");
										
									
								});
								btn1.addActionListener(e->{
									if(s)
										client.send("PPC 1");
									else
										client.send("PPC? 1");
									

								});
								btn2.addActionListener(e->{
									if(s)
										client.send("PPC 2");
									else
										client.send("PPC? 2");
									

								});

						        frame.setTitle("Fenêtre qui affiche des boutons"); //On donne un titre à l'application
								frame.setSize(320,240); //On donne une taille à notre fenêtre
								frame.setLocationRelativeTo(null); //On centre la fenêtre sur l'écran
								frame.setResizable(false); //On permet le redimensionnement
								//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit à l'application de se fermer lors du clic sur la croix
								frame.add(panel);
								frame.setVisible(true);
								         						
						
							
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				};tic.start();

		});
	}
	
	public static void setxtarea(String message) {
		textArea.setText(textArea.getText()+message + "\n");
	}

}
