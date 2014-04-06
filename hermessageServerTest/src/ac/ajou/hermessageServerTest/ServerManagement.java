package ac.ajou.hermessageServerTest;

import java.io.IOException;
import java.util.Date;

public class ServerManagement
{
	public ServerManagement ()
	{
		
	}
	public static void main( String[] args )
	{	
		MMTask_Controller taskController = new MMTask_Controller();
		
		int port = 8080;
		if ( args.length > 0 )
			port = Integer.parseInt( args[0] );

		try
		{
			new WithUAHTTP(port,taskController );
		}
		catch( IOException ioe )
		{
			System.err.println( "Couldn't start server:\n" + ioe );
			System.exit( -1 );
		}
		

		//	Scheduler Start 
		
		Runnable runScheduler = new MessageScheduler(taskController);
		Thread schedulerThread = new Thread(runScheduler);
		schedulerThread.start();
		
		Runnable smtpRun = new WithExternalServer_SMTP(taskController);
		Thread smtpThread = new Thread(smtpRun);
		smtpThread.start();
		
		//	Connection Start
		ConnectKeep connectKeep = new ConnectKeep(taskController);
		connectKeep.start();
		
		
		try { System.in.read(); } catch( Throwable t ) {}
		
		
		
	}//end main
}
