package ac.ajou.hermessageServerTest;


import java.io.*;
import java.net.*;
class makeMM4Message{

	DB_Management dbController;
	MakeLog makelog;

	
	public makeMM4Message(String messageIndex){
		dbController = new DB_Management();
		makelog = new MakeLog();
	}
	public String makeMM4ForwardReq(String messageIndex){
		/*
		 * X-Mms-3GPP-MMS-Version
		 * X-Mms-Message-Type
		 * X-Mms-Transaction-ID
		 * X-Mms-Message-ID
		 * To
		 * From
		 * Content-Type
		 * Date
		 * X-Mms-Expiry
		 * X-Mms-Delivery-Report
		 * X-Mms-Read-Reply
		 * Subject
		 * 
		 */
		MM4EntityMake contentEntity = new MM4EntityMake();
		String PDU = null;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
		mmHeader.x_mms_transaction_ID = mmHeader.makeTID();
		dbController.insert_TID(mmHeader.x_mms_transaction_ID, mmHeader.messageIndex);

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);

		StringBuffer strbuf = new StringBuffer();
		
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_forward.REQ").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Message-ID: \""+mmHeader.messageID).append("\"\r\n");
		strbuf.append("To: "+mmHeader.to.subSequence(0,11)+"/TYPE=PLMN").append("\r\n");
		strbuf.append("From: "+mmHeader.from.subSequence(0,11)+"/TYPE=PLMN").append("\r\n");		
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("Date: " + sDate).append("\r\n");
		sDate = formatter.format(mmHeader.x_mms_expiry);
		strbuf.append("X-Mms-Expiry: "+sDate).append("\r\n");
		strbuf.append("X-Mms-Delivery-Report: ").append(mmHeader.x_mms_delivery_report?"Yes":"No").append("\r\n");
		if (mmHeader.subject != null) {
			strbuf.append("Subject: " + mmHeader.subject).append("\r\n");

		}
		//strbuf.append("X-Mms-Ack-Request :").append(mmHeader.x_mms_ack_request).append("\r\n");
		strbuf.append("X-Mms-Read-Reply: ").append(mmHeader.x_mms_read_report?"Yes":"No").append("\r\n");
		strbuf.append("Content-Type: "+mmHeader.content_type).append("\r\n");
		if(mmHeader.content_transfer_encoding != null)	{
			strbuf.append("Content-Transfer-Encoding: ").append(mmHeader.content_transfer_encoding).append("\r\n");
		}
		if(mmHeader.content_location != null){
			strbuf.append("Content-Location: ").append(mmHeader.content_location).append("\r\n");
		}
		
		strbuf.append("\r\n");
		
		PDU = strbuf.toString() + contentEntity.make(mmHeader).toString();
		
		System.out.println("In MM4SenderThread: [MM4_forward.REQ] is Created!!!!!!!");
		try {
			makelog.logPrint(mmHeader.messageID, "MM4_forward.REQ" , PDU);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PDU;
	}
	public String makeMM4ForwardRes(String messageIndex){
		/*
		 * X-Mms-3GPP-MMS-Version
		 * X-Mms-Message-Type
		 * X-Mms-Transaction-ID;
		 * X-Mms-Message-ID
		 * X-Mms-Request-Status-Code
		 * 
		 */
		String PDU;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
				
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_forward.RES").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Message-ID: \""+mmHeader.messageID).append("\"\r\n");
		strbuf.append("X-Mms-Request-Status-Code: " +"Ok").append("\r\n");
		

		PDU = strbuf.toString();
		
		dbController.delete_mmHeader(mmHeader.messageIndex);
		
		System.out.println("In MM4SenderThread: [MM4_forward.RES] is Created!!!!!!!");
		try {
			makelog.logPrint(mmHeader.messageID, "MM4_forward.RES" , PDU);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PDU;
		
	}
	public String makeMM4DeliveryReq(String messageIndex){
		/*
		 * X-Mms-3GPP-MMS-Version
		 * X-Mms-Message-Type
		 * X-Mms-Transaction-ID
		 * X-Mms-Message-ID
		 * From
		 * To
		 * Date
		 * X-Mms-Status-Code
		 * 
		 */
		String PDU;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
		mmHeader.x_mms_transaction_ID = mmHeader.makeTID();
		dbController.insert_TID(mmHeader.x_mms_transaction_ID, mmHeader.messageIndex);
		
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);
		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_delivery_report.REQ").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Message-ID: \""+mmHeader.messageID).append("\"\r\n");
		strbuf.append("To: "+mmHeader.to).append("\r\n");
		strbuf.append("From: "+mmHeader.from).append("\r\n");
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("Date: " + sDate).append("\r\n");
		strbuf.append("X-Mms-Status-Code: " + mmHeader.x_mms_status).append("\r\n");
		
		PDU = strbuf.toString();
		
		System.out.println("In MM4SenderThread: [MM4_Delivery_report.REQ] is Created!!!!!!!");
		try {
			makelog.logPrint(mmHeader.messageID, "MM4_delivery_report.REQ" , PDU);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PDU;
		
	}
	public String makeMM4DeliveryRes(String messageIndex) {
		/*
		 *  X-Mms-3GPP-MMS-Version
		 *  X-Mms-Message-Type
		 *  X-Mms-Transaction-ID
		 *  X-Mms-Message-ID
		 *  X-Mms-Request-Status-Code
		 */
		String PDU;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_delivery_report.RES").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Message-ID: \""+mmHeader.messageID).append("\"\r\n");
		strbuf.append("X-Mms-Request-Status-Code: " + "Ok").append("\r\n");
		
		PDU = strbuf.toString();
		dbController.delete_mmHeader(mmHeader.messageIndex);
		System.out.println("In MM4SenderThread: [MM4_Delivery_report.RES] is Created!!!!!!!");
		try {
			makelog.logPrint(mmHeader.messageID, "MM4_delivery_report.RES" , PDU);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PDU;
		
	}
	public String makeMM4ReadReplyReportReq(String messageIndex){
		/*
		 *  X-Mms-3GPP-MMS-Version
		 *  X-Mms-Message-Type
		 *  X-Mms-Transaction-ID
		 *  From
		 *	To
		 *  X-Mms-Message-ID
		 *  Date
		 *  X-Mms-Read-Status
		 */
		String PDU;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
		mmHeader.x_mms_transaction_ID = mmHeader.makeTID();
		dbController.insert_TID(mmHeader.x_mms_transaction_ID, mmHeader.messageIndex);
		
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);
		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_read_reply_report.REQ").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Message-ID: \""+mmHeader.messageID).append("\"\r\n");
		strbuf.append("To: "+mmHeader.to).append("\r\n");
		strbuf.append("From: "+mmHeader.from).append("\r\n");
		String sDate = formatter.format(mmHeader.date); 
		strbuf.append("Date: " + sDate).append("\r\n");
		strbuf.append("X-Mms-Read-Status: " + "Read").append("\r\n");
		
		PDU = strbuf.toString();
		
		System.out.println("In MM4SenderThread: [MM4_Read_reply_report.REQ] is Created!!!!!!!");
		try {
			makelog.logPrint(mmHeader.messageID, "MM4_read_reply_report.REQ" , PDU);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PDU;
			
	}
	public String makeMM4ReadReplyReportRes(String messageIndex) throws IOException{
		/*
		 *  X-Mms-3GPP-MMS-Version
		 *  X-Mms-Message-Type
		 *  X-Mms-Transaction-ID
		 *  X-Mms-Request-Status-Code
		 */ 
		String PDU;
		MM mmHeader = dbController.select_mmHeader(messageIndex);
		dbController.insert_TID(mmHeader.x_mms_transaction_ID, mmHeader.messageIndex);
					
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("X-Mms-3GPP-MMS-Version: "+"6.16.0").append("\r\n");
		strbuf.append("X-Mms-Message-Type: "+ "MM4_read_reply_report.RES").append("\r\n");
		strbuf.append("X-Mms-Transaction-ID: \""+mmHeader.x_mms_transaction_ID).append("\"\r\n");
		strbuf.append("X-Mms-Request-Status-Code: " + "Ok").append("\r\n");
		
		PDU = strbuf.toString();
		dbController.delete_mmHeader(mmHeader.messageIndex);
		
		System.out.println("In MM4SenderThread: [MM4_Read_reply_report.RES] is Created!!!!!!!");
		makelog.logPrint(mmHeader.messageID, "MM4_read_reply_report.RES" , PDU);
		return PDU;
		
	}
}



public class MM4SenderThread implements Runnable{
	
	String messageIndex;
	DB_Management dbController;
	
	public MM4SenderThread(){
		
	}
	public MM4SenderThread(String _messageIndex){
		
		messageIndex = _messageIndex;
		dbController = new DB_Management();
	}
		
	public void run() {
		//MakeLog makelog = new MakeLog();
		MM mmHeader;
		int count = 0;
		String str = null;
		String destIp = null;
		String sendData = null;
		
		makeMM4Message makeMessage = new makeMM4Message(messageIndex);
		
		mmHeader = dbController.select_mmHeader(messageIndex);
		
		try {
			if(mmHeader.push_status.equals("m-notification-ind"))
			{
				sendData = makeMessage.makeMM4ForwardReq(messageIndex);
			}
			else if(mmHeader.push_status.equals("m-delivery-ind")){
				sendData = makeMessage.makeMM4DeliveryReq(messageIndex);
			} 
			else if(mmHeader.push_status.equals("m-read-orig-ind")){
				sendData = makeMessage.makeMM4ReadReplyReportReq(messageIndex);
			}
			else if(mmHeader.push_status.equals("MM4_forward.RES") && mmHeader.x_mms_ack_request){// && mmHeader.x_mms_ack_request){
				sendData = makeMessage.makeMM4ForwardRes(messageIndex);
			}
			else if(mmHeader.push_status.equals("MM4_delivery.REQ")){
				sendData = makeMessage.makeMM4DeliveryRes(messageIndex);
			}
			else if(mmHeader.push_status.equals("MM4_read_reply_report.REQ")){
				sendData = makeMessage.makeMM4ReadReplyReportRes(messageIndex);
			}
			else{
				System.out.println("In MM4SenderThread: 보낼 값이 없습니다.");
				return;
			}
		}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String rsNumber = mmHeader.to.substring(0,4);
				
		if (rsNumber.equals("0102")) {
			destIp = GROUP2_IP;
			System.out.println("*******************************2222" + destIp);

		} else if (rsNumber.equals("0103")) {
			destIp = GROUP3_IP;

		} else if (rsNumber.equals("0104")) {
			destIp = GROUP4_IP;

		} else if (rsNumber.equals("0105")) {
			destIp = GROUP5_IP;

		}
		else if(rsNumber.equals("0106"))
		{
			destIp = GROUP6_IP;
		}
		else{
			System.out.println("MM4SenderThread do not find RSnumber. Terminate MM4SenderThread");
			return;
		}

		Socket clientSocket = null;
		try {
			clientSocket = new Socket(destIp, 25);
		} catch (UnknownHostException e3) {
			e3.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		PrintWriter out = null;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		/////////////////////////////////////////
		String tempTo = mmHeader.to.substring(0, 11);
		String tempFrom = mmHeader.from.substring(0, 11);
		while (true) {

			try {
				str = in.readLine();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			System.out.println(str);

			if(str == null)
			{
				System.out.println("str is null"+str);
				
				break;				
			}
			String response;
			int index = str.indexOf(' ');
			if(index != -1)
			{
				response = str.substring(0, index).trim();
			}
			else
			{
				response = str.trim();
			}
			
			if (response.equals("220")) {
				out.println("HELO 210.107.196.190");
			}
			if (response.equals("250")) {
				if (count == 0) // 250 0k
				{
					//out.println("MAIL FROM:boy@gmail.com");
					out.println("MAIL FROM:" + tempFrom+ GROUP1_DOMAIN);
					count++;
				} else if (count == 1) // 250 Sender ok
				{
					//out.println("RCPT TO:girl@gmail.com");
					if(rsNumber.equals("0102"))
					{
						out.println("RCPT TO:" + tempTo+ GROUP2_DOMAIN);
					}
					else if(rsNumber.equals("0103"))
					{
						out.println("RCPT TO:" + tempTo+GROUP3_DOMAIN);
					}
					else if(rsNumber.equals("0104"))
					{
						out.println("RCPT TO:" + tempTo+GROUP4_DOMAIN);
					}
					else if(rsNumber.equals("0105"))
					{
						out.println("RCPT TO:" + tempTo+GROUP5_DOMAIN);
					}
					else if(rsNumber.equals("0106"))
					{
						out.println("RCPT TO:" + tempTo+"@hermessage.com");
					}
					else
					{
						out.println("RCPT TO:" + tempTo);
					}
					count++;
				} else if (count == 2) // 250 receiver ok
				{
					out.println("DATA");
					count++;
				}
				else if(count == 3)
				{
					out.println("QUIT");
				}

			}
			if (response.equals("354")) {
				out.println(sendData);
				/*
				try {
					makelog.logPrint(mmHeader.messageID, mmHeader.push_status, sendData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				//out.println("Test Data from yong");
				//out.println("\r\n.\r\n");
				out.println(".\r\n");
				System.out.println("In MM4SenderThread( 외부로 가야할 메시지 ) : 보내기 성공!!");
				
			}
			if(response.equals("221")){
				try {
					in.close();
					out.close();
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("SMTP 연결을 접속을 종료하겠습니다.");
				return;
			}
			
		}
		
	}
	
	public static final String
	GROUP1_IP = "210.107.196.190",
	GROUP2_IP = "210.107.196.193",
	GROUP3_IP = "210.107.196.184",
	GROUP4_IP = "210.107.196.185",
	GROUP5_IP = "210.107.196.204",
	GROUP6_IP = "210.107.196.189",
	
	GROUP1_DOMAIN = "@hermessage.com",
	GROUP2_DOMAIN = "@ajouwave.com",
	GROUP3_DOMAIN = "@ajoutech.com",
	GROUP4_DOMAIN = "@4jojipkyo.com",
	GROUP5_DOMAIN = "@allad.com",
	GROUP6_DOMAIN = "@hermeesage.com";

	
}
