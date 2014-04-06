package ac.ajou.hermessageServerTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;


public class MM implements Cloneable{
	
	String 	messageIndex;
	
	String 	messageID;					// MM4 : x_mms_message_type			
	Date	date;						
	String 	to;						
	String 	from;						
	String 	subject;				
	String 	x_mms_transaction_ID;		
	String 	x_mms_message_type;			
	String 	x_mms_mms_version;			// MM4 : x_mms_3gpp_mms_version   : 6.16.0
	Date 	x_mms_expiry;			
	Date 	x_mms_delivery_time;	
	Boolean x_mms_delivery_report;	
	Boolean x_mms_read_report;			// MM4 : x_mms_read_reply
	String 	x_mms_response_status;		// MM4 : x_mms_request_status_code
	int 	x_mms_message_size;		
	String	x_mms_content_location;	
	Boolean	x_mms_report_allowed;
	String	x_mms_status;				// MM4 : x_mms_mm_status_code
	String	x_mms_read_status;
	String	content_type;			

	String content_transfer_encoding;	// = base64;
	String content_location;
	String boundary;
	String push_status;
	Boolean x_mms_ack_request;
	
	public MM(){
		messageIndex = null;
		messageID = null;
		date = null;					
		to = null;						
		from = null;						
		subject = null;
		x_mms_transaction_ID = null;	
		x_mms_message_type = null;			
		x_mms_mms_version = null;			
		x_mms_expiry = null;			
		x_mms_delivery_time = null;	
		x_mms_delivery_report = false;	
		x_mms_read_report = false;		
		x_mms_response_status = null;	
		x_mms_message_size = 0;		
		x_mms_content_location = null;
		x_mms_report_allowed = true;
		x_mms_status = null;
		x_mms_read_status = null;
		content_type = null;
		content_transfer_encoding = null;
		content_location = null;
		boundary = null;
		push_status = null;
		x_mms_ack_request = false;

	}
	
	public Object clone(){
		MM copiedMM = null;
		try {
			copiedMM = (MM)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copiedMM;
	}

	public MM setMM(Properties p)
	{
		@SuppressWarnings("rawtypes")	
		Enumeration e = p.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = ((String)e.nextElement());
			String val_l = value.toLowerCase();
			if("message-id".equals(val_l) || "x-mms-message-id".equals(val_l))		
																{ messageID = p.getProperty(value).replace('\"', ' ').trim();}
			else if("date".equals(val_l))						{ date = makeDate(p.getProperty(value));}
			else if("to".equals(val_l))							{ to = p.getProperty(value);}
			else if("from".equals(val_l))						{ from = p.getProperty(value);}
			else if("subject".equals(val_l))					{ subject = p.getProperty(value);}
			else if("x-mms-transaction-id".equals(val_l))		{ x_mms_transaction_ID = p.getProperty(value).replace('\"', ' ').trim();}
			else if("x-mms-message-type".equals(val_l))			{ x_mms_message_type = p.getProperty(value);}
			else if("x-mms-mms-version".equals(val_l) || "x-mms-3gpp-mms-version".equals(val_l))		
																{ x_mms_mms_version = p.getProperty(value);}
			else if("x-mms-expiry".equals(val_l))				{ x_mms_expiry = makeDate(p.getProperty(value));}
			else if("x-mms-delivery-time".equals(val_l))		{ x_mms_delivery_time = makeDate(p.getProperty(value));}
			else if("x-mms-delivery-report".equals(val_l))		{ x_mms_delivery_report = "Yes".equals(p.getProperty(value));}
			else if("x-mms-read-report".equals(val_l) || "x-mms-read-reply".equals(val_l))		
																{ x_mms_read_report = "Yes".equals(p.getProperty(value));}
			else if("x-mms-response-status".equals(val_l) || "x-mms-request-status-code".equals(val_l) )
																{ x_mms_response_status = p.getProperty(value);}
			else if("x-mms-message-size".equals(val_l))			{ x_mms_message_size = Integer.parseInt(p.getProperty(value));}
			else if("x-mms-content-location".equals(val_l))		{ x_mms_content_location = p.getProperty(value);}
			else if("x-mms-status".equals(val_l) || "x-mms-mm-status-code".equals(val_l))
																{ x_mms_status = p.getProperty(value);}
			else if("x-mms-report-allowed".equals(val_l))		{ x_mms_report_allowed = "Yes".equals(p.getProperty(value));}			
			else if("content-type".equals(val_l))				{ content_type = p.getProperty(value);}
			else if("x-mms-read-status".equals(val_l))			{ x_mms_read_status = p.getProperty(value);}
			else if("content-transfer-encoding".equals(val_l))	{ content_transfer_encoding = p.getProperty(value);}
			else if("content-location".equals(val_l))			{ content_location = p.getProperty(value);}
			else if("boundary".equals(val_l))					{ boundary = p.getProperty(value);}
			else if("x_mms_ack_request".equals(val_l))			{ x_mms_report_allowed = "Yes".equals(p.getProperty(value));}
		}	
		
		if(date == null)
		{
			date = new Date();
		}
		
		if(x_mms_delivery_time == null )
		{
			x_mms_delivery_time = new Date();
		}
		if(x_mms_expiry == null)
		{	
		    Calendar cal = new GregorianCalendar();
		    cal.add(Calendar.DATE, 1);

		    x_mms_expiry = cal.getTime();
		    
		}
		return this;
	}// end createMM
	
	public Date makeDate(String t)
	{
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);
		try {
			date = formatter.parse(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println(formatter.format(date));
		
		return date;
	}// end makeDate
	
	public String getDateString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);
		String sDate = formatter.format(date);
		
		return sDate;
	}
	
	public String makeTID()
	{
		String _tid = makeID("htid");
		return _tid;
	}
	public String makeMID()
	{
		String _mid = makeID("hmid");
		messageID = _mid;
		
		return _mid;
	}
	private String makeID(String id_type)
	{
		String _id = new String();
		Calendar now = Calendar.getInstance();
		
		// ex ) hm20110605231537eft  (hm+date+random-3char)
		_id += id_type + Integer.toString(now.get(Calendar.YEAR));
		_id +=			 Integer.toString(now.get(Calendar.MONTH) + 1);
		_id +=			 Integer.toString(now.get(Calendar.DATE));
		_id +=			 Integer.toString(now.get(Calendar.HOUR_OF_DAY));
		_id +=			 Integer.toString(now.get(Calendar.MINUTE));
		_id +=			 Integer.toString(now.get(Calendar.SECOND));
		_id +=			 (char)((Math.random() * 26) + 97) ;
		_id +=			 (char)((Math.random() * 26) + 97);
		_id +=			 (char)((Math.random() * 26) + 97);		
		return _id;
	} // end makeID
	
}
