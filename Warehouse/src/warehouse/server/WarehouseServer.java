package warehouse.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import warehouse.client.ClientThread;

/**
 * A very simple server/GUI for the warehouse.
 * 
 * @author Georgi Iliev
 *
 */
public class WarehouseServer {

    private static final Logger LOG = Logger.getLogger(WarehouseServer.class);

    public static final int port = 8888;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    public static void main(String[] args) {

	Thread clientWorkerThread;

	try {

	    serverSocket = new ServerSocket(port);

	    Dimension display = Toolkit.getDefaultToolkit().getScreenSize();
	    JFrame panel = new JFrame();
	    JButton button = new JButton();
	    setupGUI(display, panel, button);

	    button.setText("Server starting at port: " + port + " ...");
	    LOG.info("Server starting at port: " + port + " ...");

	    Thread.sleep(2000);

	    notifyForSuccessfulStart(button);

	    while (true) {
		LOG.info("Waiting for clients to connect...");

		clientSocket = serverSocket.accept();

		clientWorkerThread = new Thread(new ClientThread(clientSocket));
		clientWorkerThread.start();
		clientWorkerThread.join();

		LOG.info("Client connected");
	    }
	} catch (SocketException se) {
	    LOG.warn("Server stopped!");
	    JOptionPane.showMessageDialog(null, "Server stopped!", "INFO", JOptionPane.INFORMATION_MESSAGE);
	    return;
	} catch (Exception e) {
	    LOG.error("Problem occurred: ", e);
	    JOptionPane.showMessageDialog(null, "Problem occurred: " + e.getLocalizedMessage(), "ERROR",
		    JOptionPane.ERROR_MESSAGE);
	}
    }

    private static void notifyForSuccessfulStart(JButton button) throws InterruptedException {
	button.setText("STARTED: Press 'ENTER' or click to stop the server");
	LOG.info("Server has been successfully started.");
	button.setBackground(Color.GREEN);
	button.setBackground(button.getBackground().darker());
	button.setBackground(button.getBackground().darker());
    }

    private static void setupGUI(Dimension display, JFrame panel, JButton button) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	panel.setTitle("SERVER");
	panel.setIconImage(new ImageIcon(new WarehouseServer().getClass().getResource("/server.png")).getImage());
	panel.setBounds(display.width / 2, display.height / 2, 450, 250);
	panel.setVisible(true);

	button.setBounds(display.width / 2, display.height / 2, 450, 250);
	button.setFont(new Font("Courier New", Font.BOLD, 14));
	button.setBackground(Color.BLACK);
	button.setForeground(Color.WHITE);
	panel.add(button);

	button.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    LOG.warn("Stopping server...");
		    panel.dispose();
		    serverSocket.close();
		} catch (Exception exc) {
		    LOG.error("Problem occurred: ", exc);
		    JOptionPane.showMessageDialog(null, "Problem occurred: " + exc.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}

	    }
	});
	panel.getRootPane().setDefaultButton(button);
    }
}