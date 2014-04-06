package ac.ajou.hermessageServerTest;


import java.util.LinkedList;
import java.util.Queue;


//	After received MIU, Check state of MIU  and
//	check internalQueue and externalQueue and
//	send MIU.

class MMTask_Controller {
	Queue<String> internalQueue;
	Queue<String> externalQueue;
	DB_Management dbController;
	
	public MMTask_Controller() {
		internalQueue = new LinkedList<String>();
		externalQueue = new LinkedList<String>();
		dbController = new DB_Management();
	}
	
	
	public void queueManage(String _messageIndex){
		
		MM mmHeader;
		boolean isOurUser;
		String messageIndex = _messageIndex;
		
		String toField = null;
		
		mmHeader  =  dbController.select_mmHeader(_messageIndex);
		
		System.out.println("Task_controller's input mm_index is "+ _messageIndex+":" + " , messageType is "+ mmHeader.x_mms_message_type);

		toField  = mmHeader.to;
		isOurUser = dbController.select_userOn(toField);
		
		
		if(!toField.substring(0, 4).equals("0101"))
		{
			this.InsertExterQueue(messageIndex);
			System.out.println("this " + messageIndex +  "'s 'TO' field is "+ mmHeader.to + " So go to external Queue");
			System.out.println("In MMTask_Controller : 현재 외부 queue의 messageIndex: " + this.externalQueue);
		}
		else if(isOurUser == true)
		{
			this.InsertInterQueue(messageIndex);
			System.out.println("this " + messageIndex +  "'s 'TO' field is "+ mmHeader.to+ " So go to internal Queue");
			System.out.println("In MMTask_Controller : 현재 내부 queue의 messageIndex: " + this.internalQueue);
		}

	}
	public void InsertInterQueue(String _messageIndex) {
		internalQueue.offer(_messageIndex);
		System.out.println("internalQueue's messageIndex:" + internalQueue);
	}

	public void InsertExterQueue(String _messageIndex) {
		externalQueue.offer(_messageIndex);
		System.out.println("externalQueue's messageIndex:" + externalQueue);
	}

	public String GetInterQueue() {
		String str= null;
		
		if(this.internalQueue.isEmpty() != true)
			return internalQueue.poll();
		else{
			System.out.println("internalQueue is empty.");
			return str;
		}
	}

	public String GetExterQueue() {
		String str= null;
		if(this.externalQueue.isEmpty() != true)
			return externalQueue.poll();
		else{
			System.out.println("externalQueue is empty.");
			return str;
		}
	}
}