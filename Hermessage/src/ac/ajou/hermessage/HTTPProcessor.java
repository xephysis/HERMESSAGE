package ac.ajou.hermessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

public class HTTPProcessor {
	public static String ServerAddr = "http://210.107.196.190:8080";
	int transactionId;
	int messageId;
	Context context;
	WordDBHelper mHelper;
	
	public HTTPProcessor(Context _context)
	{
		context = _context;
	}
	
	/* 전달받은 MM을 고대로 httpPost에 전달하고 전달받은 리턴값을 통해 response를 확인해서 다시 전송하거나 함
	 * 
	 * 
	 */
	public void httpSend(MM postMM)
	{
		MM responseMm;
		
		if(postMM.x_mms_message_type.equalsIgnoreCase("m-send-req"))
		{
			//1.일차로 여기서 post 하고 난 이후에 타임아웃 걸어서 리스폰스 오나 안오나 확인해야 함 
			responseMm = httpPost(postMM);//m-send-conf
			if(responseMm.x_mms_message_type.equalsIgnoreCase("m-send-conf"))//m-send-req가 정상적으로 이루어짐
			{
				//전송에 대한 트랜잭션 아이디 확인 
				if(postMM.x_mms_transaction_ID.equalsIgnoreCase(responseMm.x_mms_transaction_ID))
				{
					Log.w("HERMESSAGE","RESPONSE MATCHED");
					//정상적으로 리스폰스가 온 경우 "MM"이 정상적으로 전송되었습니다 라는 토스트? 혹은 팝업?
					
					//그리고 발신함 DB에 저장해야함
					SQLiteDatabase db = new WordDBHelper(this.context).getWritableDatabase();
					ContentValues row = new ContentValues();
					
					
					StringTokenizer tokenize = new StringTokenizer(postMM.to,",");
			    	
			    	while(tokenize.hasMoreTokens())//to 필드 여러명 처리
					{
			    		String receiver = tokenize.nextToken(",");
			    		
			    		row.put("Receiver",receiver.substring(0, receiver.indexOf("/")));
			    		row.put("MessageID", responseMm.messageID);
			    		
			    		//row.put("Subject", postMM.subject);
						//row.put("ContentType", value) 이거 필요 없음 
						row.put("DeliveryState","N");
						row.put("ReadState", "N");
						row.put("RejectState", "N");
						
						StringBuilder attachFiles = new StringBuilder();
						Iterator<String> iter = postMM.attachFilePath.iterator();
						while(iter.hasNext())
						{
							attachFiles.append(iter.next()).append(";");
						}
						row.put("ContentType", "null");
						row.put("ContentLocation", attachFiles.toString());
							
						java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
						String sDate = formatter.format(postMM.date); 
						row.put("Date", sDate);
						
						row.put("Content",postMM.messageContent);
						
						db.insert("SendMessage", null, row);
					
					}db.close();
				}
				else
				{
					Log.w("HERMESSAGE","RESPONSE NOT MATCHED");
					//일단 정상적으로 리스폰스가 안왔음 전송 실패
				}
			}
			else//리스폰스가 일치하지 않음, 엄한 리스폰스가 날라온경우 
			{
				Log.w("HERMESSAGE","OTHER RESPONSE RECEIVED");
				//무시
			}
			//전송하고 트랜잭션 아이디까지 유지해야함 
		}
		else if(postMM.x_mms_message_type.equalsIgnoreCase("m-forward-req"))
		{
			responseMm = httpPost(postMM);//m-forward -conf
			if(responseMm.x_mms_message_type.equalsIgnoreCase("m-forward-conf"))//m-send-req가 정상적으로 이루어짐
			{
				//일단 tid 확인해야함
			}
			else//m-forward-req가 정상적으로 이루어 지지 않음 
			{
				
			}
			//전송하고 트랜잭션 아이디까지 유지해야함 
		}
		else if(postMM.x_mms_message_type.equalsIgnoreCase("m-notifyresp-ind")|
				postMM.x_mms_message_type.equalsIgnoreCase("m-acknowledge-ind")|
				postMM.x_mms_message_type.equalsIgnoreCase("m-read-rec-ind"))
		{
			Log.w("HERMESSAGE","POST IND SUC");
			httpPost(postMM);
		}
		else
		{
			Log.w("HERMESSAGE","POST IND FAIL");
		}
	}
	
	/* MM객체의 내용을 토대로 HTTP PDU 생성하고 
	 * 전송시 TID를 관리하여 Reliable한 통신 유지 
	 */
	public MM httpPost(MM postMM)
	{
		try{
			//Http 헤더 생성
			HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ServerAddr);
            httpPost.addHeader("Content-Type","application/vnd.wap.mms-message");
            
            //Http Entity 생성 - MMS(MIME)헤더 부분 
            
            StringEntity stringEntity = new StringEntity(HTTPEntityMake(postMM).toString());
            
            httpPost.setEntity(stringEntity);//HTTP POST로 보낼 HTTP의 BODY ENTITY
            
            if(postMM.x_mms_message_type.equalsIgnoreCase("m-notifyresp-ind")|
    				postMM.x_mms_message_type.equalsIgnoreCase("m-acknowledge-ind")|
    				postMM.x_mms_message_type.equalsIgnoreCase("m-read-rec-ind"))
    		{
    			Log.w("HERMESSAGE","POST IND SUC");
    			httpclient.execute(httpPost);
    			return null;
    		}

            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity postResponse = response.getEntity();
            MM responseMM = HTTPEntityParse(postResponse);//여기서 익샙션으로 넘어감 리턴값으로
        	return responseMM;
        	
		}catch (Exception e) {
			Log.i("HERMESSAGE",e.getMessage());
			return null;
		}
	}
	
	/* HTTP GET을 요청하기 위해 
	 * httpGet 메소드를 호출하여 
	 * 전송을 요청한 MM을 처리 해야 하는데 어떻게 할지는 잘 고민해 ??? 
	 */
	
	public MM httpReceive(String reqMessageId)
	{
		MM retrievedMM=httpGet(reqMessageId);
		
		if(!reqMessageId.equalsIgnoreCase(retrievedMM.messageID+"+"+retrievedMM.to))
		{
			//요청한거 아니고 엉뚱한거 보냈으므로 재전송 요청
		}
		else//정상 수신
		{
			SQLiteDatabase db = new WordDBHelper(this.context).getWritableDatabase();
			ContentValues row = new ContentValues();
			
			row.put("_From", retrievedMM.from.substring(0,retrievedMM.from.indexOf("/")));
			//row.put("Subject", retrievedMM.subject);
			
			StringBuilder attachFiles = new StringBuilder();
			Iterator<String> iter = retrievedMM.attachFilePath.iterator();
			while(iter.hasNext()) 
			{
				attachFiles.append(iter.next()).append(";");
			}
			
			row.put("ContentType", "null");
			
			
			if(attachFiles.length()==0 || attachFiles == null)
			{
				row.put("ContentLocation", "null");
			}
			else
			{
				row.put("ContentLocation", attachFiles.toString());
			}
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
			String sDate = formatter.format(retrievedMM.date); 
			row.put("Date", sDate);
			
			
			if(retrievedMM.messageContent!=null)
			{
				row.put("Content",retrievedMM.messageContent);
			}
			else
			{
				row.put("Content","내용이 없는 메시지 입니다.");
			}
				//여기서 지금 Content는 일단 파일로 저장되어 있음
			db.insert("ReceiveMessage", null, row);
			db.close();
			
			/*
			 * 읽은 후에 처음으로 읽었으므로 read report 요청한지 여부를 확인한 후에
			 * 액티비티에 띄워서 체크를 받은거 그대로 해 놓고 닫을때 다시 read orig를 해 줘야할지 말지 결정해야함 
			 * 
			 */
		}
		//받은 MM을 DB에 저장해야 할듯
		return retrievedMM;
	}
	
	/* 전달받은 mid를 통해서 HTTP get을 요청하고 
	 * R/S로 부터 전달받은 PDU를 파싱? 여기서 하던가 아니면 적당하 다뤄서 
	 * 리씨브 함수로 돌려줌 
	 * 
	 */
	public MM httpGet(String reqMessageId)
	{
		try {
	        HttpClient client = new DefaultHttpClient();  
	        
	        
	        String getURL = ServerAddr+"/"+reqMessageId;
	       
	        HttpGet get = new HttpGet(getURL);
	        HttpResponse responseGet = client.execute(get);  
	        
	        HttpEntity resEntityGet = responseGet.getEntity(); 
	        
	        if (resEntityGet != null) 
	        {  
	        	MM  retrieved1MM = HTTPEntityParse(resEntityGet);
	        	return retrieved1MM;
	        }
	        else
	        {
	        	return null;
	        }
		}catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}

	StringBuffer HTTPEntityMake(MM mm) throws Exception//HTTP 스트링 엔티티를 만들기 위해서 전달받은 MM을 스트링 빌더로 변환 
	{
		 StringBuffer httpEntityString = new StringBuffer();
		 
		 if(mm.x_mms_message_type.equalsIgnoreCase("m-acknowledge-ind"))
		 {
				httpEntityString.append("X-Mms-Message-Type: "+mm.x_mms_message_type).append("\r\n");
				httpEntityString.append("X-Mms-Transaction-ID: "+mm.x_mms_transaction_ID).append("\r\n");
				httpEntityString.append("X-Mms-MMS-Version: "+mm.x_mms_mms_version).append("\r\n");
				httpEntityString.append("X-Mms-Report-Allowed: "+ (mm.x_mms_report_allowed ? "Yes" : "No")).append("\r\n");
		 }
		 else if(mm.x_mms_message_type.equalsIgnoreCase("m-notifyresp-ind"))
		 {
			 	httpEntityString.append("X-Mms-Message-Type: "+mm.x_mms_message_type).append("\r\n");
			 	httpEntityString.append("X-Mms-Transaction-ID: "+mm.x_mms_transaction_ID).append("\r\n");
			 	httpEntityString.append("X-Mms-MMS-Version: "+mm.x_mms_mms_version).append("\r\n");
			 	httpEntityString.append("X-Mms-Status: "+mm.x_mms_status).append("\r\n");
			 	
			 	httpEntityString.append("X-Mms-Report-Allowed: "+ (mm.x_mms_report_allowed ? "Yes" : "No")).append("\r\n");
		 }
		 else if(mm.x_mms_message_type.equalsIgnoreCase("m-read-rec-ind"))
		 {
			 	httpEntityString.append("X-Mms-Message-Type: "+mm.x_mms_message_type).append("\r\n");
			 	httpEntityString.append("X-Mms-MMS-Version: "+mm.x_mms_mms_version).append("\r\n");
			 	httpEntityString.append("Message-ID: "+mm.messageID).append("\r\n");
			 	httpEntityString.append("To: "+mm.to).append("\r\n");
			 	httpEntityString.append("From: "+mm.from).append("\r\n");
			 	httpEntityString.append("From: "+mm.from).append("\r\n");
			 	httpEntityString.append("X-Mms-Read-Status: " + mm.x_mms_read_status).append("\r\n");
		 }
		 else if(mm.x_mms_message_type.equalsIgnoreCase("m-forward-req"))
		 {
			 	httpEntityString.append("X-Mms-Message-Type: "+mm.x_mms_message_type).append("\r\n");
			 	httpEntityString.append("X-Mms-Transaction-ID: "+mm.x_mms_transaction_ID).append("\r\n");
			 	httpEntityString.append("X-Mms-MMS-Version: "+mm.x_mms_mms_version).append("\r\n");
			 	
			 	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
				String sDate = formatter.format(mm.date); 
				httpEntityString.append("Date: "+sDate).append("\r\n");
				httpEntityString.append("From: "+mm.from).append("\r\n");
				httpEntityString.append("To: "+mm.to).append("\r\n");
				sDate = formatter.format(mm.x_mms_expiry); 
				httpEntityString.append("X-Mms-Expiry: "+sDate).append("\r\n");
				sDate = formatter.format(mm.x_mms_delivery_time);
				httpEntityString.append("X-Mms-Delivery-Time: "+sDate).append("\r\n");
				httpEntityString.append("X-Mms-Delivery-Report: ").append(mm.x_mms_delivery_report?"Yes":"No").append("\r\n");
				httpEntityString.append("X-Mms-Read-Report: ").append(mm.x_mms_read_report?"Yes":"No").append("\r\n");
				httpEntityString.append("X-Mms-Content-Location: "+mm.x_mms_content_location).append("\r\n");
		 }
		 else if(mm.x_mms_message_type.equalsIgnoreCase("m-send-req"))
         {
	            httpEntityString.append("X-Mms-Message-Type: "+mm.x_mms_message_type).append("\r\n");
				httpEntityString.append("X-Mms-Transaction-ID: "+mm.x_mms_transaction_ID).append("\r\n");
				httpEntityString.append("X-Mms-MMS-Version: "+mm.x_mms_mms_version).append("\r\n");
				java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
				String sDate = formatter.format(mm.date); 
				httpEntityString.append("Date: "+sDate).append("\r\n");
				httpEntityString.append("From: "+mm.from).append("\r\n");
				httpEntityString.append("To: "+mm.to).append("\r\n");
				//httpEntityString.append("Subject: "+mm.subject).append("\r\n");
				sDate = formatter.format(mm.x_mms_expiry); 
				httpEntityString.append("X-Mms-Expiry: "+sDate).append("\r\n");
				sDate = formatter.format(mm.x_mms_delivery_time);
				httpEntityString.append("X-Mms-Delivery-Time: "+sDate).append("\r\n");
				httpEntityString.append("X-Mms-Delivery-Report: ").append(mm.x_mms_delivery_report?"Yes":"No").append("\r\n");
				httpEntityString.append("X-Mms-Read-Report: ").append(mm.x_mms_read_report?"Yes":"No").append("\r\n");
				
				Log.i("HERMESSAGE",mm.messageContent);
				Log.i("HERMESSAGE",mm.attachFilePath.size()+"");
				
				if(mm.attachFilePath.size()==0)//텍스트만 오는경우 
				{
					httpEntityString.append("Content-Type: text/plain").append("\r\n");
					httpEntityString.append("Content-Transfer-Encoding: base64").append("\r\n").append("\r\n");
					httpEntityString.append(Base64.encodeToString(mm.messageContent.getBytes(),Base64.CRLF)).append("\r\n");
				}
				else if(mm.messageContent.equalsIgnoreCase("")&&mm.attachFilePath.size()==1)//이미지 하나만 오는경우
				{
					//httpEntityString.append("Content-Type: ").append("image/" +mm.attachFilePath.get(0).substring(mm.attachFilePath.get(0).lastIndexOf('.')+1)).append("\r\n");
					
					if(mm.attachFilePath.get(0).substring(mm.attachFilePath.get(0).lastIndexOf('.')+1).equals("jpg"))
					{
						httpEntityString.append("Content-Type: ").append("image/jpeg").append("\r\n");
					}
					else
					{
						httpEntityString.append("Content-Type: ").append("image/png").append("\r\n");
					}
					
					
					httpEntityString.append("Content-Location: ").append(mm.attachFilePath.get(0).substring(mm.attachFilePath.get(0).lastIndexOf('/')+1,mm.attachFilePath.get(0).length())).append("\r\n");
					httpEntityString.append("Content-Transfer-Encoding: base64").append("\r\n");
					httpEntityString.append("\r\n");
					
					if(mm.attachFilePath.get(0).substring(mm.attachFilePath.get(0).lastIndexOf('.')+1).equalsIgnoreCase("jpg")
							||
							mm.attachFilePath.get(0).substring(mm.attachFilePath.get(0).lastIndexOf('.')+1).equalsIgnoreCase("png") 
							)
					{
						File inputFile = new File(mm.attachFilePath.get(0));
						InputStream is = new FileInputStream(inputFile);
						long length = inputFile.length();
						if (length > Integer.MAX_VALUE) {
						// File is too large
						}
						byte[] bytes = new byte[(int)length];
						 
						// Read in the bytes
						int offset = 0;
						int numRead = 0;
						while (offset < bytes.length
						&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
						offset += numRead;
						}
						is.close();
						 
						httpEntityString.append(Base64.encodeToString(bytes,Base64.CRLF)).append("\r\n");
					}
				}
				
				else if(mm.attachFilePath.size() > 0)//텍스트와 이미지(1개 이상)가 있는경우
				{
					httpEntityString.append("Content-Type: multipart/mixed;").append("boundary=\"1stgroup\"").append("\r\n").append("\r\n");
					httpEntityString.append("--1stgroup").append("\r\n");
					
					if(mm.messageContent != "")
					{
						httpEntityString.append("Content-Location: ").append("context.txt").append("\r\n");
						httpEntityString.append("Content-Transfer-Encoding: ").append("base64").append("\r\n");
						httpEntityString.append("Content-Type: ").append("text/plain").append("\r\n").append("\r\n");
						httpEntityString.append(Base64.encodeToString(mm.messageContent.getBytes(),Base64.CRLF)).append("\r\n");
						httpEntityString.append("--1stgroup").append("\r\n");
					}
					for(int countOfMultipart=mm.attachFilePath.size();countOfMultipart>0;countOfMultipart--)
					{
						Log.i("HERMESSAGE","COM"+countOfMultipart);
						Log.i("HERMESSAGE","SIZE"+mm.attachFilePath.size());
						
						String thisPartFilePath = mm.attachFilePath.get(mm.attachFilePath.size()-countOfMultipart);
						
						httpEntityString.append("Content-Transfer-Encoding: ").append("base64").append("\r\n");
						
						httpEntityString.append("Content-Location: ").append(thisPartFilePath.substring(thisPartFilePath.lastIndexOf('/')+1,thisPartFilePath.length())).append("\r\n");
						
						if(thisPartFilePath.substring(thisPartFilePath.lastIndexOf('.')+1).equals("jpg"))
						{
							httpEntityString.append("Content-Type: ").append("image/jpeg").append("\r\n");
						}
						else
						{
							httpEntityString.append("Content-Type: ").append("image/png").append("\r\n");
						}
						
						httpEntityString.append("\r\n");
						if(thisPartFilePath.substring(thisPartFilePath.lastIndexOf('.')+1).equalsIgnoreCase("jpg")
								||
								thisPartFilePath.substring(thisPartFilePath.lastIndexOf('.')+1).equalsIgnoreCase("png") 
								)
						{
							File inputFile = new File(thisPartFilePath);
							InputStream is = new FileInputStream(inputFile);
							long length = inputFile.length();
							if (length > Integer.MAX_VALUE) {
							// File is too large
							}
							byte[] bytes = new byte[(int)length];
							 
							// Read in the bytes
							int offset = 0;
							int numRead = 0;
							while (offset < bytes.length
							&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
							offset += numRead;
							}
							is.close();
							 
							httpEntityString.append(Base64.encodeToString(bytes,Base64.CRLF)).append("\r\n");
						}
						httpEntityString.append("--1stgroup--").append("\r\n");
					}
				}
				
		   }	
		return httpEntityString;
	}

	//여기서 멀티파트까지 포함 되어있다면 파일로 만들어서 줘야함
	MM HTTPEntityParse(HttpEntity httpEntity) throws Exception//POST나 RESPONSE가 오면 파싱해서 MM객체로 돌려줌 
	{
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
		
		MM mm = new MM();
    	InputStream is = httpEntity.getContent();
    	BufferedInputStream bis = new BufferedInputStream(is);
    	
    	ByteArrayBuffer baf = new ByteArrayBuffer(5*1024*1024);
    	int current = 0;
    	while((current = bis.read()) != -1){
    		baf.append((byte)current);
    	}
    	
    	boolean isMultipart = true;
    	Properties responseEntity = new Properties();//리스폰스 파싱해서 집어 넣을 프로퍼티
    	String tempString = new String(baf.toByteArray());
    	StringTokenizer stk = new StringTokenizer(tempString,"\r\n", false);
    	StringBuffer boundary;
    	StringBuffer encodedString = new StringBuffer();
    	
    	
    	if(tempString.indexOf("multipart/mixed")!=-1)//멀티파트로 이루어짐
		{
			int boundaryBeg = tempString.indexOf("\"");
			int boundaryEnd = tempString.indexOf("\"",boundaryBeg+1);
			
			boundary = new StringBuffer(tempString.subSequence(boundaryBeg+1,boundaryEnd));
		
			int begPosOfBoundary=tempString.indexOf("--"+boundary);
			int endPosOfBoundary=0;
			
			while( begPosOfBoundary != -1)//멀티파트 쪼개서 어레이 리스트에 넣음
			{
				if((endPosOfBoundary = tempString.indexOf("--"+boundary,begPosOfBoundary+boundary.length()+1))==-1)
				{
					break;
				}
				multipartDecode(mm,tempString.substring(begPosOfBoundary+boundary.length()+2, endPosOfBoundary));
				begPosOfBoundary = endPosOfBoundary;
			}
		}
    	else
    	{
    		isMultipart = false;
    	}
    	
    	while(stk.hasMoreTokens()) 
    	{
    		String line = stk.nextToken();     
    	   
    		int positionOfDelimeter = line.indexOf(':');
    		if(positionOfDelimeter >= 0 )
    		{
    			responseEntity.put(line.substring(0, positionOfDelimeter).trim(),line.substring(positionOfDelimeter+1).trim());
    			Log.i("HERMESSAGE",line.substring(0, positionOfDelimeter).trim());
    			Log.i("HERMESSAGE",line.substring(positionOfDelimeter+1).trim());
    		}
    		else if(!isMultipart)//구분자가 없는경우 누적함? 멀티파트 아닐때만 누적
    		{
    			encodedString.append(line).append("\r\n");
    		}
    		else
    		{
    			break;
    		}
    	}
    	
    	if(responseEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase("m-retrieve-conf"))
    	{
	    	File dir = new File("/mnt/sdcard/hermessage/"+mm.messageID);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			else
			{
				Log.i("HERMESSAGE","MAKR DIR FAILED");
			}
			Log.i("HERMESSAGE","파일 저장위치 확인"+"/mnt/sdcard/hermessage/"+responseEntity.getProperty("Message-ID")+"/");
    	
			
			if(!isMultipart)//1. 텍스트만 있는 경우, 2. 이미지만 있는경우
			{
				byte [] decodedByte = Base64.decode(encodedString.toString(),Base64.CRLF);//안드로이드에서 지원되는 클래스임
				
				if(responseEntity.getProperty("Content-Type").equalsIgnoreCase("image/jpeg")
						||responseEntity.getProperty("Content-Type").equalsIgnoreCase("image/png"))
				{
					writeFile(decodedByte,"/mnt/sdcard/hermessage/"+responseEntity.getProperty("Message-ID")+"/"+responseEntity.getProperty("Content-Location"));
					mm.attachFilePath.add("/sdcard/hermessage/"+responseEntity.getProperty("Message-ID")+"/"+responseEntity.getProperty("Content-Location"));
					Log.i("HERMESSAGE","파일 저장 완료");
				}
				else//텍스트만 오는 경우
				{
					mm.messageContent = new String(decodedByte);
					
				}
			}
    	}
		
    	if(responseEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase("m-send-conf"))
    	{
        	mm.x_mms_message_type = responseEntity.getProperty("X-Mms-Message-Type");
        	mm.x_mms_transaction_ID = responseEntity.getProperty("X-Mms-Transaction-ID");	
        	mm.x_mms_mms_version = responseEntity.getProperty("X-Mms-MMS-Version");			
        	mm.x_mms_response_status = responseEntity.getProperty("X-Mms-Response-Status");//
        	mm.messageID = responseEntity.getProperty("Message-ID");						
    	}
    	else if(responseEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase("m-forward-conf"))
		{
    		mm.x_mms_message_type = responseEntity.getProperty("X-Mms-Message-Type");
        	mm.x_mms_transaction_ID = responseEntity.getProperty("X-Mms-Transaction-ID");	
        	mm.x_mms_mms_version = responseEntity.getProperty("X-Mms-MMS-Version");			
        	mm.x_mms_response_status = responseEntity.getProperty("X-Mms-Response-Status");//
        	mm.x_mms_content_location = responseEntity.getProperty("X-Mms-Content-Location");
        	mm.messageID = responseEntity.getProperty("Message-ID");						
		}
    	else if(responseEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase("m-retrieve-conf"))//get에 대한 처리
    	{
    		mm.x_mms_message_type = responseEntity.getProperty("X-Mms-Message-Type");
    		if(responseEntity.getProperty("X-Mms-Transaction-ID")!=null)
    		{
    			mm.x_mms_transaction_ID = responseEntity.getProperty("X-Mms-Transaction-ID");	
    		}
    		mm.messageID = responseEntity.getProperty("Message-ID");						
    		mm.date = formatter.parse(responseEntity.getProperty("Date"));
    		mm.from = responseEntity.getProperty("From");
    		mm.to = responseEntity.getProperty("To");
    		//mm.subject = responseEntity.getProperty("Subject");
    		mm.x_mms_delivery_report = responseEntity.getProperty("X-Mms-Delivery-Report").equalsIgnoreCase("Yes")?true:false;
    		mm.x_mms_read_report = responseEntity.getProperty("X-Mms-Read-Report").equalsIgnoreCase("Yes")?true:false;
    		mm.content_type  = responseEntity.getProperty("Content-Type");
    	}
    	else
    	{
    		//에러처리
    	}
		return mm;
	}
	
	public void multipartDecode(MM mm,String partString)
	{
		StringTokenizer st= new StringTokenizer(partString,"\r\n");
		Properties multipartHeader = new Properties();
		StringBuffer encodedString = new StringBuffer();
		
		while(st.hasMoreTokens())
		{
			String tmpString = st.nextToken();
			int positionOfDelimeter = tmpString.indexOf(':');
			if(positionOfDelimeter >= 0 )
    		{
				multipartHeader.put(tmpString.substring(0, positionOfDelimeter).trim(),tmpString.substring(positionOfDelimeter+1).trim());
    			Log.i("HERMESSAGE",tmpString.substring(0, positionOfDelimeter).trim());
    			Log.i("HERMESSAGE",tmpString.substring(positionOfDelimeter+1).trim());
    		}
			else
			{
				if(multipartHeader.getProperty("Content-Transfer-Encoding").equalsIgnoreCase("base64"))
				{
					encodedString.append(tmpString).append("\r\n");
				}
			}
		}//헤더 부분 읽어오기
		
		if(multipartHeader.getProperty("Content-Transfer-Encoding").equalsIgnoreCase("base64"))
		{
			byte [] decodedByte = Base64.decode(encodedString.toString(),Base64.CRLF);//안드로이드에서 지원되는 클래스임
			try {
				Log.i("HERMESSAGE","FILE 확장자 TEST "+multipartHeader.getProperty("Content-Location").substring(multipartHeader.getProperty("Content-Location").lastIndexOf(".")+1));
				if( multipartHeader.getProperty("Content-Location").substring(multipartHeader.getProperty("Content-Location").lastIndexOf(".")+1).equalsIgnoreCase("txt") )
				{
					mm.messageContent = new String(decodedByte);
					Log.i("HERMESSAGE","메시지 내용"+mm.messageContent);
				}
				else
				{
					writeFile(decodedByte,"/mnt/sdcard/hermessage/"+mm.messageID+"/"+multipartHeader.getProperty("Content-Location"));
					mm.attachFilePath.add("/sdcard/hermessage/"+mm.messageID+"/"+multipartHeader.getProperty("Content-Location"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("HERMESSAGE",e.getMessage());
			}
		}
	}
	
	public void writeFile(byte[] data, String fileName) throws IOException
	{
        File file = new File(fileName);
        file.createNewFile();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        }
        catch (FileNotFoundException e) {
        	Log.e("HERMESSAGE",e.getMessage());
        	e.getStackTrace();
        }
        catch (Exception e) {
        	Log.e("HERMESSAGE",e.getMessage());
        	e.getStackTrace();
        }
	}
	
	class WordDBHelper extends SQLiteOpenHelper {
		public WordDBHelper(Context context) {
			super(context, "db.sqlite", null, 1);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE dic ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "eng TEXT, han TEXT);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS dic");
			onCreate(db);
		}
	}
}





