package ac.ajou.hermessageServerTest;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.naming.NamingException;


public class WithExternalServer_SMTP implements Runnable {
	
	MMTask_Controller taskController;
	DB_Management dbController;
	
	
	public WithExternalServer_SMTP(MMTask_Controller _taskController){
		taskController = _taskController;
		dbController = new DB_Management();
	}
	
	public void run() {
		
		MM mmHeader;
		String mailReceiver;
		String mailSender;
		String senderName;
		String mailSubject;
		//String mailContent;
		
		Runnable runMM4Receiver = new MM4Receiver(taskController);
		Thread mm4ReceiverThread = new Thread(runMM4Receiver);
		mm4ReceiverThread
		.start();
		
				
		while (true) {
			System.out.println("---------------------------------------------SMTP is monitoring  MMTask_Controller.");
			if (taskController.externalQueue.isEmpty() != true) { 				// If MIU exists in Queue,

				int index = -1;
				String destination = null; 
				
				System.out.println("In WithExternalServer_SMTP : 외부큐에서 빠져나오기 전 상태:" + taskController.externalQueue);
				String getmessageIndex = taskController.GetExterQueue(); // Get from //
				System.out.println("In WithExternalServer_SMTP : 외부큐에서 빠져나온 상태:" + taskController.externalQueue);
				
				
				
				mmHeader = dbController.select_mmHeader(getmessageIndex);

				String to = mmHeader.to;
				if(to != null)
				{
					index = to.indexOf('=');
					destination = to.substring(index+1);
				}
				
				if(destination.equals("PLMN"))
				{
					//MM4
					System.out.println(mmHeader.messageID + "'s destination is PLMN!!!!");
					Runnable runMM4Sender = new MM4SenderThread(mmHeader.messageIndex);
					Thread mm4SenderThread = new Thread(runMM4Sender);
					mm4SenderThread.start();
				}
				else if(destination.equals("FQDN"))
				{
					//MM3
					System.out.println(mmHeader.messageID + "'s destination is FDQN!!!!");
					
					String tempReceiver = mmHeader.to;
					index = to.indexOf('/');
					mailReceiver = tempReceiver.substring(0,index);
					System.out.println("Receiver:" + mailReceiver );

					
					String tempSender = mmHeader.from;
					index = tempSender.indexOf('/');
					String tempSender2 = tempSender.substring(0,index);
					String hermessageDomain = "@hermessage.com";
					
					mailSender = tempSender2+hermessageDomain;
					//mailSender = tempSender2;

					System.out.println("Sender:" + mailSender );
					
					senderName = "HerMessage";
					mailSubject = mmHeader.subject;
					
					//	mail Content를 작성
					
					System.out.println(" = = = = = = == = = = = = = = = = =  = = = = = =");
					System.out.println(mmHeader.messageID);
					System.out.println(mailReceiver);
					System.out.println(mailSender);
					System.out.println(mailSubject);

					
					//	messageID를 이곳에 전달
					MM3 mm3 = new MM3(mmHeader.messageID,mailReceiver,mailSender,senderName,mailSubject);
					mm3.start();
				}
				else
					System.out.println(mmHeader.messageID + "do not have destination!!!!");

			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("SMTP Roop fail");
				e.printStackTrace();
			}			// Wait 5 Seconds.
		}

	}
}
