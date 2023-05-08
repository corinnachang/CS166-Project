import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI implements ActionListener{

//	private JFrame frame;
	public JPanel cards, card1, card2;
	public JButton btnNewButton, stopButton;
	JTextPane txtpnWaitingForConnection;
	
     
    public void addComponentToPane(Container pane) {
        //Create the "cards".
		card1 = new JPanel();
		card1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Honeypot");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		lblNewLabel.setBounds(182, 70, 88, 31);
		card1.add(lblNewLabel);
		
		JTextPane txtpnUseThisHoneypot = new JTextPane();
		txtpnUseThisHoneypot.setText("Use this honeypot to collect the data of anyone who tries to access your network");
		txtpnUseThisHoneypot.setBounds(127, 113, 205, 66);
		card1.add(txtpnUseThisHoneypot);
		txtpnUseThisHoneypot.setEditable(false);
		
		btnNewButton = new JButton("Start");
		btnNewButton.setBounds(175, 201, 117, 29);
		btnNewButton.addActionListener(this);
		card1.add(btnNewButton);
         
        card2 = new JPanel();
        txtpnWaitingForConnection = new JTextPane();
		card2.setLayout(null);
		txtpnWaitingForConnection.setBackground(UIManager.getColor("Button.background"));
		txtpnWaitingForConnection.setText("Waiting for connection...");
		txtpnWaitingForConnection.setBounds(155, 113, 205, 66);
		txtpnWaitingForConnection.setEditable(false);
		
		stopButton = new JButton("Stop");
		stopButton.setBounds(170, 170, 117, 29);
		stopButton.addActionListener(this);
		card2.add(stopButton);
		card2.add(txtpnWaitingForConnection);

	
         
        
        //Create the panel that contains the "cards".
        cards = new JPanel(new CardLayout());
        cards.add(card1, "Card 1");
        cards.add(card2, "Card 2");
        pane.add(cards, BorderLayout.CENTER);
    }
     
    
    public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, (String)evt.getItem());
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnNewButton) {
			CardLayout cl = (CardLayout) (cards.getLayout());
			cl.show(cards, "Card 2");
			
			new HoneyPot(this).start();
		} else if (e.getSource() == stopButton) {
			System.exit(0);
		}

	}
	
	public void updateCard(int counter) {
		txtpnWaitingForConnection.setText("Connections made: " + counter);
	}
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Honeypot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         
        //Create and set up the content pane.
        GUI demo = new GUI();
        demo.addComponentToPane(frame.getContentPane());
         
        //Display the window.
        frame.pack();
        frame.setBounds(100, 100, 450, 300);
        frame.setResizable(false);
        frame.setVisible(true);
    }
     
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
}