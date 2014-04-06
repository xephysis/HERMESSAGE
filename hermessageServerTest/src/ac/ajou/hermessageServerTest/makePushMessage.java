package ac.ajou.hermessageServerTest;

public class makePushMessage {
	
	DB_Management dbController;
	
	makePushMessage(){
		dbController = new DB_Management();
	}
	public String  selectPushData(String _messageIndex){
		String messageIndex = _messageIndex;	
		String PDU = null;
		MM mmHeader;
		
		mmHeader = dbController.select_mmHeader(messageIndex);
		
		if(mmHeader == null)
			return null;
		
		if(mmHeader.push_status.equals("m-notification-ind"))
		{
			
			PDU = this.createNotificationInd(mmHeader);
			System.out.println("In makePushMessage : Making m-notification-ind is success");
		}
		
		else if(mmHeader.push_status.equals("m-delivery-ind"))
		{
			if(mmHeader.x_mms_delivery_report == true)
			{
				
				PDU = this.createDeliveryInd(mmHeader);
				System.out.println("In makePushMessage : Making m-delivery-ind is success");
			}			
		}
		
		else if(mmHeader.push_status.equals("m-read-orig-ind"))
		{
			
			if(mmHeader.x_mms_read_report == true)
			{
				//	but, need to other Algorithm for checking type "to,from"
				
				PDU = this.createReadOrigInd(mmHeader);
				System.out.println("In makePushMessage : Making m-read-orig-ind is success");
			}
			
		}
		else
		{
			System.out.println("In makePushMessage : 나중에 처리");
		}
		return PDU;
		
	}
	
	public String createNotificationInd(MM _mmHeader){
		// M-Notification.ind
		
		String PDU;
		MM mmHeader = _mmHeader;		
		mmHeader.x_mms_transaction_ID = mmHeader.makeTID();
		dbController.insert_TID(mmHeader.x_mms_transaction_ID, mmHeader.messageIndex);
		System.out.println("In makePushMessage : When Notifications Ind ... input TID db");
		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-Message-Type: "+"m-notification-ind").append("\r\n");
		strbuf.append("X-Mms-MMS-Version: "+mmHeader.x_mms_mms_version).append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: "+mmHeader.x_mms_transaction_ID).append("\r\n");
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("From: "+mmHeader.from).append("\r\n");
		if(mmHeader.subject != null) {strbuf.append("Subject: "+mmHeader.subject).append("\r\n");}
		sDate = formatter.format(mmHeader.x_mms_expiry); 
		strbuf.append("X-Mms-Expiry: "+sDate).append("\r\n");
		sDate = formatter.format(mmHeader.x_mms_delivery_time);
		strbuf.append("X-Mms-Delivery-Report: ").append(mmHeader.x_mms_delivery_report?"Yes":"No").append("\r\n");
		strbuf.append("X-Mms-Message-Class: ").append("Personal").append("\r\n");
		strbuf.append("X-Mms-Message-Size: ").append(mmHeader.x_mms_message_size).append("\r\n");
		strbuf.append("X-Mms-Content-Location: ").append(mmHeader.x_mms_content_location).append("\r\n").append("\r\n");
		PDU = strbuf.toString();
		
		
		System.out.println("In makePushMessage : [M-notification-ind] Careted!!!!!!!");
		return PDU;
	
	}
	
	public String createDeliveryInd(MM _mmHeader){
		
		String PDU;
		MM mmHeader = _mmHeader;		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-Message-Type: "+"m-delivery-ind").append("\r\n");
		strbuf.append("X-Mms-MMS-Version: "+mmHeader.x_mms_mms_version).append("\r\n");
		strbuf.append("Message-ID: "+mmHeader.messageID).append("\r\n");
		strbuf.append("To: "+mmHeader.to).append("\r\n");
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	  
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("Date: "+sDate).append("\r\n");
		strbuf.append("X-Mms-Status: "+mmHeader.x_mms_status).append("\r\n").append("\r\n");
		PDU = strbuf.toString();
		
		
		System.out.println("In makePushMessage : [m-delivery-ind] Careted!!!!!!!");
		return PDU;
		
	}
	
	public String createReadOrigInd(MM _mmHeader){
		// M-Read-orig.ind .
		
		String PDU;
		MM mmHeader = _mmHeader;		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-Message-Type: "+"m-read-orig-ind").append("\r\n");
		strbuf.append("X-Mms-MMS-Version: "+mmHeader.x_mms_mms_version).append("\r\n");
		strbuf.append("Message-ID: " + mmHeader.messageID).append("\r\n");
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("Date: "+sDate).append("\r\n");
		strbuf.append("From: "+mmHeader.to).append("\r\n");
		strbuf.append("To: "+mmHeader.from).append("\r\n");		
		strbuf.append("X-Mms-Read-Status: ").append(mmHeader.x_mms_read_status).append("\r\n").append("\r\n");
		PDU = strbuf.toString();
		
		System.out.println("In makePushMessage : [m-read-orig-ind] Careted!!!!!!!");
		return PDU;
	}
	
}
