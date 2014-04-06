package ac.ajou.hermessageServerTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;



public class HTTPEntityMake{
	
	HTTPEntityMake(){
		
	}
	
	StringBuffer make(MM mm_fromDB, String messageIndex, MM mmfromParser) //HTTP 스트링 엔티티를 만들기 위해서 전달받은 MM을 스트링 빌더로 변환 
	{
		Base64Processor base64processor = new Base64Processor();
		
		int posPlus = messageIndex.indexOf('+');
		String mid = messageIndex.substring(0,posPlus);
		DB_Management dbController = new DB_Management();
		StringBuffer httpEntityString = new StringBuffer();
		
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);
		String sDate = formatter.format(mmfromParser.date);
		
		httpEntityString.append("X-Mms-Message-Type: m-retrieve-conf").append(CRLF_code);
		
		if(mm_fromDB.x_mms_status != null && mm_fromDB.x_mms_status.equals("Deferred"))
		{
			String tempTID = mm_fromDB.makeTID();
			dbController.insert_TID(tempTID, mm_fromDB.messageIndex);
			httpEntityString.append("X-Mms-Transaction-ID: ").append(tempTID).append(CRLF_code);
		}
		
		httpEntityString.append("X-Mms-MMS-Version: ").append(mm_fromDB.x_mms_mms_version).append(CRLF_code);
		httpEntityString.append("Message-ID: ").append(mid).append(CRLF_code);
		httpEntityString.append("Date: ").append(sDate).append(CRLF_code);
		httpEntityString.append("From: ").append(mmfromParser.from).append(CRLF_code);
		httpEntityString.append("To: ").append(mmfromParser.to).append(CRLF_code);
		if(mm_fromDB.subject != null) {httpEntityString.append("Subject: ").append(mm_fromDB.subject).append(CRLF_code);}
		httpEntityString.append("X-Mms-Delivery-Report: ").append((mm_fromDB.x_mms_delivery_report ? "Yes" : "No")).append(CRLF_code);
		httpEntityString.append("X-Mms-Read-Report: ").append((mm_fromDB.x_mms_read_report ? "Yes" : "No")).append(CRLF_code);
	
		httpEntityString.append("Content-Type: ").append(mm_fromDB.content_type).append(CRLF_code);	
		
		if(mm_fromDB.content_transfer_encoding != null)	{
			httpEntityString.append("Content-Transfer-Encoding: ").append(mm_fromDB.content_transfer_encoding).append(CRLF_code);
		}
		if(mm_fromDB.content_location != null){
			httpEntityString.append("Content-Location: ").append(mm_fromDB.content_location).append(CRLF_code);
		}
		
		httpEntityString.append(CRLF_code);
		
		
		FileReader readerList = null;
		try {
			readerList = new FileReader("/HERMESSAGE/content/" + mid + "/" + "contentList.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer listBuffer = new StringBuffer("");
		int ch = 0;
		try {
			while((ch = readerList.read()) != -1)
			{
				listBuffer.append((char)ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			readerList.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] contentLocationList = listBuffer.toString().split("\n");
		String type;
		
		if(mm_fromDB.content_type.substring(0, 9).equals("multipart"))
		{
			String boundary = mm_fromDB.boundary.replace('\"', ' ').trim();			
			base64processor.multipartEncoder("MM1M", httpEntityString, mid, boundary ,contentLocationList);		
		}
		else
		{
			base64processor.encoder("MM11", httpEntityString, mid, contentLocationList[0]);
		}

		return httpEntityString;
	}
	
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