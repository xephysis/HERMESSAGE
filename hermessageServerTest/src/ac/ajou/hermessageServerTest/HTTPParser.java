package ac.ajou.hermessageServerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sun.mail.util.CRLFOutputStream;


public class HTTPParser{
	
	static final int successParsing = 1,
					nullParsing = 0,
					failParsing = -1;

			
	
	public  MM parsing(BufferedReader bufferedReader, Properties header, Properties mime)
	{
		MM mm = new MM();
		Base64Processor base64processor = new Base64Processor();
		MakeLog makelog = new MakeLog();
		String logStr = "";
		
		try{		
			String line;
			String readSubstring;
			int posToken;
			
			
			
			// Method (+ uri) Parsing
			while(   !(   (line = bufferedReader.readLine()).length() > 0   )   ) {;}
			
			posToken = line.indexOf('/');
			header.put("method", line.substring(0, posToken).trim());
			readSubstring = line.substring(posToken).trim();
			posToken = readSubstring.indexOf(' ');
			header.put("uri", readSubstring.substring(1,posToken).trim());
			// end method/uri parsing
			
			if("GET".equals(header.get("method")))
			{return null;}
			
			// http header Parsing (not use)
			while( (line = bufferedReader.readLine()).length() > 0)
			{
				posToken = line.indexOf(':');
				if(posToken != -1){
					header.put(
							line.substring(0, posToken).trim(),
							line.substring(posToken + 1).trim());
				}
			}// end http header parsing
			
			// mime header Parsing
			while ((line = bufferedReader.readLine()).length() > 0)
			{
				logStr += line + "\r\n";
				posToken = line.indexOf(':');
				if(posToken >= 0)
				{
					mime.put(
							line.substring(0, posToken).trim(),
							line.substring(posToken + 1).trim());
				}

				if(!bufferedReader.ready())
				{	
					mm.setMM(mime);
					return mm;	
				}

			}// end mime header parsing
			
			
			if(mime.getProperty("Content-Type") == null)
			{	
				mm.setMM(mime);
				makelog.logPrint(mm.messageID, mm.x_mms_message_type, logStr);
				return mm;	
			}
			
			
			
			
			String mid = mm.makeMID();
			
			File dir = new File("/HERMESSAGE/content/"+mid);
			dir.mkdir();
			
			File contextList = new File("/HERMESSAGE/content/" + mid +"/contentList.txt");
			FileOutputStream fosOfList = new FileOutputStream(contextList);
			
			String content_type = mime.getProperty("Content-Type");
			String subtype[] = content_type.split(";");
			if(content_type.indexOf(';') < 0)
			{
				//not multipart
				Properties tempProp = new Properties();
				tempProp.put("Content-Type", mime.get("Content-Type"));
				if(mime.get("Content-Location") != null)
				{
					tempProp.put("Content-Location", mime.get("Content-Location"));
					mime.put("Content-Location", mime.get("Content-Location"));
				}
				else
				{
					tempProp.put("Content-Location", "context.txt");
				}
				
				if(mime.get("Content-Transfer-Encoding") != null)
				{
					tempProp.put("Content-Transfer-Encoding", "base64");
				}
				
				base64processor.decoder("MM1", mm, bufferedReader, mid, tempProp);
				
				String content_Location = tempProp.getProperty("Content-Location")+"\n";
				fosOfList.write(content_Location.getBytes());
				
			}
			else
			{	
				// multipart/mixed
				String boundary = subtype[1].substring(10).replace('\"', ' ').trim();
				mime.put("boundary", boundary);
				// multipart Parsing
				while ((line = bufferedReader.readLine()) != null)
				{				
					if(line.equals("--" + boundary))
					{
						Properties tempProp = new Properties();					
						if(bufferedReader.ready() == false) {break;}
						// multipart content header parsing
						while( (line = bufferedReader.readLine()) != null)
						{
							if(line.equals("")) {break;}
							posToken = line.indexOf(':');
							tempProp.put(
									line.substring(0, posToken).trim(),
									line.substring(posToken + 1).trim());						
						}
						
						if(line == null) {break;}		
						
						// multipart Content Parsing base64decoding
						
						if( !tempProp.isEmpty() )
						{
							System.out.println("In HTTPParser  :--------------Bese64Decoding!!!");
							base64processor.decoder("MM1", mm, bufferedReader, mid, tempProp);
							System.out.println("In HTTPParser  :--------------Bese64Decoding Complete?!!!");
						}
						String content_Location = tempProp.getProperty("Content-Location")+"\n";
						fosOfList.write(content_Location.getBytes());
					}
					else if(line.equals("--" + boundary+"--"))
					{
						if(bufferedReader.ready() == false) {break;}
					}
				}//end multipart parsing
					
			}

			fosOfList.close();
			mm.setMM(mime);
			
			
		}
		catch(IOException ioe){
			System.out.println("In HTTPParser  : -----------------In Paser -> EXception ----------");
			return null;
		}
		
		try {
			makelog.logPrint(mm.messageID, mm.x_mms_message_type, logStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mm;
	}// end parsing
	

	
}
		
		
		
	