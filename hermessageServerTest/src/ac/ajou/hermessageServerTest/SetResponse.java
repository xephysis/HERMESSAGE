package ac.ajou.hermessageServerTest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;



public class SetResponse{
	
	SetResponse() {
		
	}
	
	public Response set( String uri, String method, Properties mime, MM mmfromParser) 
	{
		
		MakeLog makelog = new MakeLog();
		
		HTTPEntityMake entityMake = new HTTPEntityMake();
		System.out.println( "In Nano HTTPD : method /uri : " + method + " '" + uri + "' " );

		@SuppressWarnings("rawtypes")
		Enumeration e = mime.propertyNames();

		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			System.out.println( "In Nano HTTPD :   PRM: '" + value + "' = '" +
					mime.getProperty( value ) + "'" );
		}
		String data = "";
		
		// uri  "/" 처리
		System.out.println("In Nano HTTPD : uri = mid : " + uri);			
		DB_Management dbController = new DB_Management();
		MM mm_fromDB = dbController.select_mmHeader(uri);
		
		if("GET".equals(method))
		{
			//send M-Retrieve.conf
			/*
			if(mm_fromDB.x_mms_status != null && mm_fromDB.x_mms_status.equals("Deferrend"))
			{
				data = entityMake.make(mm_fromDB, uri, mm_fromDB).toString();
			}
			else
			{
				data = entityMake.make(mm_fromDB, uri, mmfromParser).toString();	
			}*/
			try {
				data = entityMake.make(mm_fromDB, uri, mm_fromDB).toString();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			int data_len = data.length();
			
			Response r = new Response(HTTP_OK,"application/vnd.wap.mms-message",data);
			r.addHeader("Content-Length", Integer.toString(data_len) );
			
			System.out.println("In Nano HTTPD : Created M-Retrieve.con !!!!!");
			try {
				makelog.logPrint(mm_fromDB.messageID, "m-retrieve-conf", data);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return r;
			
		}
		else if("POST".equals(method) && mmfromParser.x_mms_message_type.equals("m-send-req"))
		{
			//send M-Send.conf
			//return create
			
			data += "X-Mms-Message-Type: " + "m-send-conf" + CRLF_code;
			data += "X-Mms-Transaction-ID: "+mmfromParser.x_mms_transaction_ID + CRLF_code;
			data += "X-Mms-MMS-Version: " + mmfromParser.x_mms_mms_version + CRLF_code;
			data += "X-Mms-Response-Status: " + "Ok" + CRLF_code;
			data += "Message-ID: " + mmfromParser.messageID + CRLF_code;			
			
			int data_len = data.length();
			
			Response r = new Response(HTTP_OK,"application/vnd.wap.mms-message",data);
			r.addHeader("Content-Length: ", Integer.toString(data_len) );
			
			System.out.println("In Nano HTTPD : Created M-send-conf !!!!!");
			try {
				makelog.logPrint(mmfromParser.messageID, "m-send-conf", data);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return r;
			
		}
		else if("POST".equals(method) && mmfromParser.x_mms_message_type.equals("m-forward-req"))
		{
			//send M-Forward.conf -- 
			
			data += "X-Mms-Message-Type: " + "m-forward-conf" + CRLF_code;
			data += "X-Mms-Transaction-ID: "+mmfromParser.x_mms_transaction_ID + CRLF_code;
			data += "X-Mms-MMS-Version: " + mmfromParser.x_mms_mms_version + CRLF_code;
			data += "X-Mms-Response-Status: " + "Ok" + CRLF_code;
			data += "Message-ID: " + mmfromParser.messageID + CRLF_code;		
			data += "X-Mms-Content-Location: " + mmfromParser.x_mms_content_location + CRLF_code;
				
			int data_len = data.length();
			
			Response r = new Response(HTTP_OK,"application/vnd.wap.mms-message",data);
			r.addHeader("Content-Length", Integer.toString(data_len) );
			
			System.out.println("In Nano HTTPD : Created M-Forward.conf !!!!!");
			try {
				makelog.logPrint(mmfromParser.messageID, "m-forward-conf", data);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return r;
		}
		else 
		{
			// ERROR : GET /POST 
		}
		
		return new Response();
		
		//return serveFile( uri, header, new File("."), true, mm );
	}// end Response serve
	
	public static final String
	HTTP_OK = "200 OK",
	HTTP_REDIRECT = "301 Moved Permanently",
	HTTP_FORBIDDEN = "403 Forbidden",
	HTTP_NOTFOUND = "404 Not Found",
	HTTP_BADREQUEST = "400 Bad Request",
	HTTP_INTERNALERROR = "500 Internal Server Error",
	HTTP_NOTIMPLEMENTED = "501 Not Implemented";

	/**
	 * Common mime types for dynamic content
	 */
	public static final String
		MIME_PLAINTEXT = "text/plain",
		MIME_HTML = "text/html",
		MIME_DEFAULT_BINARY = "application/octet-stream",
		MIME_XML = "text/xml";
	
	public static final String
		CRLF_code = "\r\n";

}