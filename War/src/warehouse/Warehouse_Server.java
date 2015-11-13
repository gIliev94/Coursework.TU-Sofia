package warehouse;

import java.net.ServerSocket;
import java.net.Socket;

public class Warehouse_Server {

    public static final int port = 4444;

    public static void main(String[] args) {

	ServerSocket serverSocket = null;
	Socket clientSocket;
	Thread clientWorkerThread;

	try {

	    serverSocket = new ServerSocket(port);

	    System.out.println("Server is successfully started.");

	    while (true) {
		System.out.println("Waiting for clients to connect...");

		clientSocket = serverSocket.accept();

		clientWorkerThread = new Thread(new Client_Thread(clientSocket));
		clientWorkerThread.start();
		// think of new way to sync
		Thread.yield();

		System.out.println("Client connected");
	    }
	} catch (Exception e) {
	    System.out.println("Can't start server. " + e.getMessage());
	}

    }
}