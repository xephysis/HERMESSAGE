package ac.ajou.hermessageServerTest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MM4EntityMake{
	
	
	public StringBuffer make(MM mmHeader){
		Base64Processor base64processor = new Base64Processor();
		StringBuffer entityString = new StringBuffer();
		
		FileReader readerList = null;
		try {
			readerList = new FileReader("/HERMESSAGE/content/" + mmHeader.messageID + "/" + "contentList.txt");
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
		
		if(mmHeader.content_type.substring(0, 9).equals("multipart"))
		{
			String boundary = mmHeader.boundary.replace('\"', ' ').trim();			
			base64processor.multipartEncoder("MM4M", entityString, mmHeader.messageID, boundary ,contentLocationList);		
		}
		else
		{
			base64processor.encoder("MM41",entityString, mmHeader.messageID, contentLocationList[0]);
		}

		return entityString;

	}
}