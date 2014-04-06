package ac.ajou.hermessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MM implements Serializable{
	
	public MM()
	{
		attachFilePath = new ArrayList<String>();
	}
	private static final long serialVersionUID = 7863030760452889183L;
	String 	messageID;						
	Date	date;					
	String 	to;						
	String 	from;						
	//String 	subject;				
	String 	x_mms_transaction_ID;	
	String 	x_mms_message_type;			
	String 	x_mms_mms_version;			
	Date 	x_mms_expiry;			
	Date 	x_mms_delivery_time;	
	Boolean x_mms_delivery_report;	
	Boolean x_mms_read_report;		
	String 	x_mms_response_status;	//
	int 	x_mms_message_size;		//
	String 	x_mms_content_location;	//포워딩할떄 요청하는건데 MessageID로 써야할듯 싶음 
	String	content_type;		// 
	String	x_mms_status;
	boolean	x_mms_report_allowed;
	String 	messageContent; //새로 추가
	ArrayList<String> 	attachFilePath;	//새로 추가 이거 나중에 여러개 받을 수 있는 어레이로 처리해야 할듯
	String x_mms_message_class;
	String x_mms_read_status;
	//boolean isImmediate;
	String start;
	String type;
}
