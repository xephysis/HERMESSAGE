package ac.ajou.hermessageServerTest;

class MessageScheduler implements Runnable {
	
	MMTask_Controller taskController;
	DB_Management dbController;
	
	public MessageScheduler(MMTask_Controller _taskController){
		taskController = _taskController;
		dbController = new DB_Management();
	}
	
	public void run(){
		//	무한루프를 돌고 있는 도중에 
		//	expiry 와  x_mms_delivery_time을 수시로 비교를 해야 한다. 
		//	현재 시간을 long형으로 바꾸어 현재 보다 같거나 작으면 
		// 	디비에서 꺼낸 다음에 큐로 전송한다.

		while(true)
		{
			//System.out.println("---------------------------------------------Scheduler is checking DataBase ");
			//	만료된 messageID를 검사
			String messageIndex = dbController.watchExpiry();			//	만료된 메시지 index가 있는지 검사
			if(messageIndex != null)					
			{				
				taskController.queueManage(messageIndex);
				System.out.println("In MessageScheculer : 삭제되어야 할 MM을 큐로 삽입 함 ");
				System.out.println(messageIndex);
				System.out.println("In MessageScheculer : External Queue:" + taskController.externalQueue);
				System.out.println("In MessageScheculer : internal Queue:" + taskController.internalQueue);
				
			}
			
			//	예약전송 messageID를 검사
			messageIndex = dbController.watchDeliveryTime();			//	만료된 메시지 처리 후 예약 전송
			if(messageIndex != null)	
			{
				taskController.queueManage(messageIndex);				//	큐에 삽입
				System.out.println("In MessageScheculer : External Queue:" + taskController.externalQueue);
				System.out.println("In MessageScheculer : internal Queue:" + taskController.internalQueue);
				System.out.println("In MessageScheculer : 예약전송 할 MM을 큐로 삽입 함");
			}

			try {
				Thread.sleep(5*1000);								//	sleep
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}		
	}
}
