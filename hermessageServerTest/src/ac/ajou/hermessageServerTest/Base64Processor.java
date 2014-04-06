package ac.ajou.hermessageServerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class Base64Processor{
	
	Base64Processor(){
		
	}
	
	public void decoder(String type, MM mm, BufferedReader reader, String messageID, Properties ContentHeader)
	{
		
		String line;
		String location = null;
		
		if(ContentHeader.getProperty("Content-Location") != null)
		{
			location = ContentHeader.getProperty("Content-Location");
		}
		else if(ContentHeader.getProperty("Content-Type").equals("text/plain"))
		{
			location = "context.txt";
		}
		else if(ContentHeader.getProperty("Content-Type").equals("image/jpeg"))
		{
			location = "image.jpg";
		}
		else if(ContentHeader.getProperty("Content-Type").equals("image/png"))
		{
			location = "image.png";
		}
		
		
		
		File f = new File("/HERMESSAGE/content/" + messageID +"/"+location);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ContentHeader.getProperty("Content-Transfer-Encoding") != null)
		{
			// base64 encoding
			try {
				while((line = reader.readLine()).length() > 0)
				{
					byte [] decodedByte = Base64.decode(line,Base64.CRLF);
					fos.write(decodedByte);

					if(!reader.ready())
					{
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				while((line = reader.readLine()).length() > 0)
				{
					fos.write(line.getBytes());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void encoder(String type, StringBuffer httpEntityString, String mid, String filename)
	{
		if(!type.equals("MM41") && !type.equals("MM11"))
		{
			int posPeriod = filename.lastIndexOf('.');
			String extension = filename.substring(posPeriod+1);
			
			if(extension.equals("jpg") || extension.equals("jpeg"))
				httpEntityString.append("Content-Type: image/jpeg").append(CRLF_code).append(CRLF_code);
			else if(extension.equals("png"))
				httpEntityString.append("Content-Type: image/png").append(CRLF_code).append(CRLF_code);
			else if(extension.equals("gif"))
				httpEntityString.append("Content-Type: image/gif").append(CRLF_code).append(CRLF_code);
			else if(extension.equals("txt"))
				httpEntityString.append("Content-Type: text/plain").append(CRLF_code).append(CRLF_code);
		}
		
		FileReader reader = null;
		try {
			reader = new FileReader("/HERMESSAGE/content/" + mid + "/" + filename);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File inputFile = new File( "/HERMESSAGE/content/"+mid+"/"+ filename);
		InputStream is = null;
		try {
			is = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long length = inputFile.length();
		if (length > Integer.MAX_VALUE) {
            // File is too large
        }
		byte[] bytes = new byte[(int)length];
	    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        try {
			while (offset < bytes.length
			       && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(bytes.toString());
        	httpEntityString.append(Base64.encodeToString(bytes,Base64.CRLF));

	}
	
	public void multipartEncoder(String type, StringBuffer httpEntityString, String mid, String boundary, String[] contentLocationList)
	{
		for(int index = 0; index < contentLocationList.length; index++)
		{	
			if(!contentLocationList[index].equals("") && contentLocationList[index] != null)
			{
				
				if(index>0 )
				{
					httpEntityString.append(CRLF_code);
				}
				httpEntityString.append("--").append(boundary).append(CRLF_code); 
				httpEntityString.append("Content-Transfer-Encoding: base64").append(CRLF_code);
				httpEntityString.append("Content-Location: ").append(contentLocationList[index]).append(CRLF_code);	
				
			
				
				encoder(type, httpEntityString, mid, contentLocationList[index]);
			
			}
			
		}	 
		
		httpEntityString.append(CRLF_code).append("--").append(boundary).append("--").append(CRLF_code);
	
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