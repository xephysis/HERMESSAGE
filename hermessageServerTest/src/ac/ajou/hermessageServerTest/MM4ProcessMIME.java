package ac.ajou.hermessageServerTest;

import java.sql.SQLException;
import java.util.Date;


public class MM4ProcessMIME{
	
	MM4ProcessMIME (){
		
	}
	
	public void process(MM mmfromParser, MMTask_Controller taskController){
		
		DB_Management dbController = new DB_Management();		
		HTTPEntityMake entityMake = new HTTPEntityMake();
		if(mmfromParser == null)
		{
			System.out.println("In MM4ProcessMIME : mmfromParser = null!!!!");
			return;
		}
		
		if(mmfromParser.x_mms_message_type.equals("MM4_forward.REQ"))
		{
			
			mmfromParser.messageIndex = mmfromParser.messageID + "+" + "forSize";
			mmfromParser.x_mms_delivery_time.setTime((long)0);
			dbController.insert_mmHeader(mmfromParser);
			try {
				mmfromParser.x_mms_message_size = entityMake.make(mmfromParser, mmfromParser.messageIndex ,mmfromParser).length();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			MM responseMM = null;
			responseMM = (MM) mmfromParser.clone();
			String tempTo = responseMM.from;
			String tempFrom = responseMM.to;
			
			mmfromParser.messageIndex = mmfromParser.messageID + "+" + mmfromParser.to;
			mmfromParser.push_status = "m-notification-ind";
			mmfromParser.x_mms_content_location = mmfromParser.messageIndex;
			
			responseMM.messageIndex = mmfromParser.messageID + "+" + mmfromParser.from;
			responseMM.to = tempTo;
			responseMM.from = tempFrom;
			responseMM.push_status = "MM4_forward.RES";
		    responseMM.x_mms_response_status = "Ok";
		    
			dbController.insert_mmHeader(mmfromParser);
			dbController.insert_mmHeader(responseMM);
		     	
	    	//After Send, update Deliverytime
			
	    	dbController.update_deliveryTime(mmfromParser.messageIndex,(long)0);
	    	if(!dbController.select_userOn(mmfromParser.to))
	    	{
	    		dbController.update_deliveryTime(responseMM.messageIndex,(long)1);
	    	}
	    	else
	    	{
	    		taskController.queueManage(mmfromParser.messageIndex);
	    	}
	    	
	    	taskController.queueManage(responseMM.messageIndex);
			System.out.println("In MM4ProcessMIME : Finish Proccesing MM4_forward.REQ");
		    
		}
		
		else if(mmfromParser.x_mms_message_type.equals("MM4_forward.RES"))
		{
			if(mmfromParser.x_mms_response_status.equals("Ok"))
			{
				System.out.println("In MM4ProcessMIME : MM4_forward.RES 정상적으로 수신");
			}
			else
			{
				System.out.println("In MM4ProcessMIME : MM4_forward.RES's M-Mms-Request-Status-Code: " + mmfromParser.x_mms_response_status);
			}		
		}
		
		else if(mmfromParser.x_mms_message_type.equals("MM4_delivery_report.REQ"))
		{
			MM responseMM = null;
			responseMM = (MM) mmfromParser.clone();
			
			String tempTo = responseMM.from;
			String tempFrom = responseMM.to;
			
			String mmIndex = mmfromParser.messageID + "+" + mmfromParser.from;
			dbController.update_xMmsStatus(mmIndex, mmfromParser.x_mms_status);

			MM mmfromDB = dbController.select_mmHeader(mmIndex);
			
			dbController.update_pushStatus( mmIndex, "m-delivery-ind");
			
			responseMM.messageIndex = mmfromParser.messageID + "+MM4D" + mmfromParser.to;
			responseMM.x_mms_response_status = "Ok";
			responseMM.to = tempTo;
			responseMM.from = tempFrom;
			
			dbController.insert_mmHeader(responseMM);
			dbController.update_deliveryTime(responseMM.messageIndex,(long)0);
			taskController.queueManage(responseMM.messageIndex);
			
			if(!dbController.select_userOn(mmfromDB.to))
			{
				dbController.update_deliveryTime(mmIndex, (long)1);
			}
			else if(mmfromDB.x_mms_delivery_report && mmfromParser.x_mms_report_allowed)
			{	
				taskController.queueManage(mmIndex);
				System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
			}
			
			System.out.println("In MM4ProcessMIME : Finish Proccesing MM4_delivery_report.REQ");
			
		}
		
		else if(mmfromParser.x_mms_message_type.equals("MM4_delivery_report.RES"))
		{
			if(mmfromParser.x_mms_response_status.equals("Ok"))
			{
				System.out.println("In MM4ProcessMIME : MM4_delivery_report.RES 정상적으로 수신");
			}
			else
			{
				System.out.println("In MM4ProcessMIME : MM4_delivery_report.RES M-Mms-Request-Status-Code: " + mmfromParser.x_mms_response_status);
			}	
			
		}
		
		else if(mmfromParser.x_mms_message_type.equals("MM4_read_reply_report.REQ"))
		{
			MM responseMM = null;
			responseMM = (MM) mmfromParser.clone();
			String tempTo = responseMM.from;
			String tempFrom = responseMM.to;
			
			String mmIndex = mmfromParser.messageID + "+" + mmfromParser.from;
			dbController.update_xMmsStatus(mmIndex, mmfromParser.x_mms_status);

			MM mmfromDB = dbController.select_mmHeader(mmIndex);
			
			dbController.update_pushStatus( mmIndex, "m-read-orig-ind");
			
			responseMM.messageIndex = mmfromParser.messageID + "+MM4R" + mmfromParser.to;
			responseMM.x_mms_response_status = "Ok";
			responseMM.to = tempTo;
			responseMM.from = tempFrom;
			
			dbController.insert_mmHeader(responseMM);
			dbController.update_deliveryTime(responseMM.messageIndex,(long)0);
			taskController.queueManage(responseMM.messageIndex);
			
			if(!dbController.select_userOn(mmfromDB.to))
			{
				dbController.update_deliveryTime(mmIndex, (long)1);
			}
			else if(mmfromDB.x_mms_delivery_report && mmfromParser.x_mms_report_allowed)
			{	
				taskController.queueManage(mmIndex);
				System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
			}
			
			System.out.println("In MM4ProcessMIME : Finish Proccesing MM4_delivery_report.REQ");
		}
		
		else if(mmfromParser.x_mms_message_type.equals("MM4_read_reply_report.RES"))
		{
			if(mmfromParser.x_mms_response_status.equals("Ok"))
			{
				System.out.println("In MM4ProcessMIME : MM4_read_reply_report.RES 정상적으로 수신");
			}
			else
			{
				System.out.println("In MM4ProcessMIME : MM4_read_reply_report.RES M-Mms-Request-Status-Code: " + mmfromParser.x_mms_response_status);
			}	
			
		}
		
	}
}