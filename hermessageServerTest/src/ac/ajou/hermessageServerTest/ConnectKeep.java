package ac.ajou.hermessageServerTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket; 
import java.net.Socket; 

//import java.io.PrintWriter;


public class ConnectKeep extends Thread
{   
	MMTask_Controller taskController;
	ServerSocket connectKeepSocket;
	
	public ConnectKeep(MMTask_Controller _taskController){
		taskController = _taskController;
	}
	
    public void run()
    {
		try {
			connectKeepSocket = new ServerSocket(8082);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
    	
    	while(true)
    	{
	    	try
	    	{
		    	
		    	//
		    	Socket clientSocket = null;        
		        
		        System.out.println("Listening Second Test");
		        while((clientSocket = connectKeepSocket.accept())!=null)
		        {
		 
		        	
					//BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					// PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					
					//String userInformation = null;
					//userInformation  = in.readLine();	// User의 정보를 넣어줌.
		        	
	        	
		        	Runnable runPush = new RSPushComponent(taskController, clientSocket);
		        	Thread thread = new Thread(runPush);
		        	thread.start();        	
		        }
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		try {
					Thread.sleep(5000);
				} catch (Exception e1) {
					e1.printStackTrace();
				}//wait 5seconds
	    	}
    	}
    } 
} 

