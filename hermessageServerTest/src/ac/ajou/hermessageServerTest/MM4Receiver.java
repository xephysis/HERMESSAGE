package ac.ajou.hermessageServerTest;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MM4Receiver implements Runnable{
	
	MMTask_Controller taskController;
	
	public MM4Receiver(MMTask_Controller _taskController){
		taskController = _taskController;
		
	}
	public void run(){

		try {
			ServerSocket welcomeSocket = null;
			Socket connectionSocket = null;
			welcomeSocket = new ServerSocket(25);
			
			while(true){
				connectionSocket = welcomeSocket.accept();
				
				System.out.println("# Connecting....");
				System.out.println(">Local address:" + connectionSocket.getLocalAddress());
				System.out.println(">Local port:" + connectionSocket.getLocalPort());
				System.out.println(">Remote address:" + connectionSocket.getInetAddress());
				System.out.println(">Remort port:" + connectionSocket.getLocalPort());
				
				Runnable runMM4Receiver = new MM4ReceiverThread(connectionSocket,taskController);
				Thread ReceiverThread = new Thread(runMM4Receiver);
				ReceiverThread.start();
			}

						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

