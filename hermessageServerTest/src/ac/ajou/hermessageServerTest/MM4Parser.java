package ac.ajou.hermessageServerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;




public class MM4Parser{
	
	MM4Parser()
	{
		
	}
	
	public MM parser(BufferedReader bufferedReader, String rcptTo)
	//throws InterruptedException, IOException
	{
		
		Properties mime = new Properties();
		MM mm = new MM();
		Base64Processor base64processor = new Base64Processor();
		MakeLog makelog = new MakeLog();
		String logStr = "";
		
		String line;
		int posToken;
		
		
			// mime header Parsing
			// mime header Parsing
			try {
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
					/*
					if(line.substring(0, posToken).trim().equalsIgnoreCase("Content-Type")){
						while((line = bufferedReader.readLine()).length() > 0)
						{
							posToken = line.indexOf('=');
							mime.put(
									line.substring(0, posToken).trim(),
									line.substring(posToken + 1).trim());
							
						}
						
						break;
					}*/
					try {
						if(!bufferedReader.ready())
						{	
							mime.put("To", rcptTo.substring(0,11)+"/TYPE=PLMN");
							mm.setMM(mime);
							try {
								makelog.logPrint(mm.messageID, mm.x_mms_message_type, logStr);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return mm;	
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}// end mime header parsing
			
			mime.put("To", rcptTo.substring(0,11) + "/TYPE=PLMN" );
			
			if(mime.getProperty("Content-Type") == null)
			{	
				mm.setMM(mime);
				try {
					makelog.logPrint(mm.messageID, mm.x_mms_message_type, logStr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return mm;	
			}
			
			
			
			
			//String mid = mm.makeMID();
			
			String mid = mime.getProperty("X-Mms-Message-ID").replace('\"', ' ').trim();
			
			File dir = new File("/HERMESSAGE/content/"+mid);
			dir.mkdir();
			
			File contextList = new File("/HERMESSAGE/content/" + mid +"/contentList.txt");
			FileOutputStream fosOfList = null;
			try {
				fosOfList = new FileOutputStream(contextList);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
				
				base64processor.decoder("MM41",mm, bufferedReader, mid, tempProp);
				
				String content_Location = tempProp.getProperty("Content-Location")+"\n";
				try {
					fosOfList.write(content_Location.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else
			{	
				// multipart/mixed
				String boundary = subtype[1].substring(10).replace('\"', ' ').trim();
				mime.put("boundary", boundary);
				// multipart Parsing
				try {
					while ((line = bufferedReader.readLine()) != null)
					{				
						if(line.equals("--" + boundary))
						{
							Properties tempProp = new Properties();					
							try {
								if(bufferedReader.ready() == false) {break;}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							// multipart content header parsing
							try {
								while( (line = bufferedReader.readLine()) != null)
								{
									if(line.equals("")) {break;}
									posToken = line.indexOf(':');
									tempProp.put(
											line.substring(0, posToken).trim(),
											line.substring(posToken + 1).trim());						
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							if(line == null) {break;}		
							
							// multipart Content Parsing base64decoding
							
							if( !tempProp.isEmpty() )
							{
								System.out.println("In HTTPParser  :--------------Bese64Decoding!!!");
								base64processor.decoder("MM4M",mm, bufferedReader, mid, tempProp);
								System.out.println("In HTTPParser  :--------------Bese64Decoding Complete?!!!");
							}
							if(tempProp.get("Content-Location") == null)
							{
								if(tempProp.get("Content-Type").equals("image/jpeg"))
								{
									tempProp.put("Content-Location", "image.jpg");
								}
								else if(tempProp.get("Content-Type").equals("image/png"))
								{
									tempProp.put("Content-Location", "image.png");	
								}
								else if(tempProp.get("Content-Type").equals("text/plain"))
								{
									tempProp.put("Content-Location", "context.txt");
								}
							}
							String content_Location = tempProp.getProperty("Content-Location")+"\n";
							try {
								fosOfList.write(content_Location.getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//end multipart parsing
					
			}

			try {
				fosOfList.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mm.setMM(mime);
		
		/*
		catch(IOException ioe){
			System.out.println("In MM4Parser  : -----------------In Paser -> EXception ----------");
			return null;
		}*/
		
		try {
			makelog.logPrint(mm.messageID, mm.x_mms_message_type, logStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mm;
	}
}