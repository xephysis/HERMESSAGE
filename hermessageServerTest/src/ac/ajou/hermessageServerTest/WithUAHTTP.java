package ac.ajou.hermessageServerTest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;


public class WithUAHTTP
{
	private int myTcpPort;
	private final ServerSocket myServerSocket;
	private Thread myThread;
	MMTask_Controller taskController;
	MakeLog makelog ;
	public WithUAHTTP( int port , MMTask_Controller tc) throws IOException
	{
		makelog = new MakeLog();
		
		myTcpPort = port;
		myServerSocket = new ServerSocket( myTcpPort );
		taskController = tc;
		
		myThread = new Thread( new Runnable()
			{
				public void run()
				{
					try
					{
						while( true )
							new HTTPSession( myServerSocket.accept());
					}
					catch ( IOException ioe )
					{}
				}// end run
			});
		myThread.setDaemon( true );
		myThread.start();
		
	}// end NanoHTTPD(int)
	

	private class HTTPSession implements Runnable
	{
		private Socket mySocket;
		
		public HTTPSession( Socket s )
		{
			mySocket = s;
			Thread t = new Thread( this );
			t.setDaemon( true );
			t.start();
		}// end HTTPSession(Socket)
		
		public void run()
		{
			SetResponse setResponse = new SetResponse();
			HTTPParser parser = new HTTPParser();
			SendError errorResponse = new SendError();
			HTTPEntityMake entityMake = new HTTPEntityMake();

			try
			{
				//InputStream is = mySocket.getInputStream();
				//if ( is == null) return;				
				MM mmfromParser;
				DB_Management dbController = new DB_Management();
				
				// Create a BufferedReader for parsing the header.
				BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( mySocket.getInputStream() ));
				
				Properties headerProp = new Properties();
				Properties mimeProp = new Properties();
				
				// HTTP Header Parsing
				mmfromParser = parser.parsing(bufferedReader, headerProp, mimeProp);
				String method = headerProp.getProperty("method");
				String uri = headerProp.getProperty("uri");	
				
				System.out.println("In Nano HTTPD :method / uri = " + method + uri);
				if(mmfromParser != null)
					System.out.println("In Nano HTTPD : X-mms-type :"+mmfromParser.x_mms_message_type);
				System.out.println("In Nano HTTPD : ---- Parsing Complete!!! -------");
				
				Date nowDate = new Date();
				Date mmDelivery = null;
				if (mmfromParser != null) {
					mmDelivery = new Date(mmfromParser.x_mms_delivery_time.getTime());
				}
				
				//nowDate.setTime(nowDate.getTime()+ (long)36778750); // 임시 시간 plus 
				// size calculater
				
				// m-send-req multiple Receiver
				if(mmfromParser != null && 
					"m-send-req".equals(mmfromParser.x_mms_message_type))
				{
					String TempTo = mmfromParser.to + ",";
					String []toList =  TempTo.split(",");
					
					mmfromParser.messageIndex = mmfromParser.messageID + "+" + "forSize";
					mmfromParser.x_mms_delivery_time.setTime((long)0);
					dbController.insert_mmHeader(mmfromParser);
					mmfromParser.x_mms_message_size = entityMake.make(mmfromParser, mmfromParser.messageIndex, mmfromParser).length();
					
					for(int indexTo = 0; indexTo < toList.length ; indexTo++)
					{
						MM otherMM = (MM) mmfromParser.clone();
						otherMM.messageIndex = mmfromParser.messageID + "+" + toList[indexTo];
						otherMM.push_status = "m-notification-ind";
						otherMM.x_mms_content_location = otherMM.messageIndex;
						otherMM.to = toList[indexTo];
						dbController.insert_mmHeader(otherMM);
					    System.out.println("In Nano HTTPD : MM object Created and save in DB!-MMS-type: "+ mmfromParser.x_mms_message_type 
					    		+ "-mid: " + mmfromParser.messageID + "index : " + mmfromParser.messageIndex);

					    if(mmDelivery != null && mmDelivery.before(nowDate) &&
					    		(!(otherMM.to.substring(0,4).equals("0101") && !dbController.select_userOn(otherMM.to))))	
				    	{	    	
					    	//After Send, update Deliverytime
					    	dbController.update_deliveryTime(otherMM.messageIndex,(long)0);
					    	taskController.queueManage(otherMM.messageIndex);
							System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + otherMM.messageIndex);
				    	}
					    else{
					    	dbController.update_deliveryTime(otherMM.messageIndex,(long)mmDelivery.getTime());
					    }
					    
					}
					
				}
				
				//m-forward-req 
				if(mmfromParser != null && "m-forward-req".equals(mmfromParser.x_mms_message_type))
				{	
					
					MM mmfromDB = dbController.select_mmHeader(mmfromParser.x_mms_content_location);
					
					mmfromParser.makeMID();
					mmfromParser.messageIndex = mmfromParser.messageID + "+" + mmfromParser.to;
					mmfromParser.push_status = "m-notification-ind";
					mmfromParser.x_mms_message_size = mmfromDB.x_mms_message_size;
					mmfromParser.x_mms_delivery_time.setTime((long)0);
					
					dbController.update_pushStatus(mmfromParser.x_mms_content_location, "m-delivery-ind");
					dbController.update_xMmsStatus(mmfromParser.x_mms_content_location, "Forwarded");
					taskController.queueManage(mmfromParser.x_mms_content_location);
					System.out.println("In Nano HTTPD : Input mm_index to Quere!! -Fowrarded- " + mmfromParser.messageIndex);
					
					dbController.insert_mmHeader(mmfromParser);
				    System.out.println("In Nano HTTPD : MM object Created and save in DB!-MMS-type: "+ mmfromParser.x_mms_message_type 
				    		+ "-mid: " + mmfromParser.messageID + "index : " + mmfromParser.messageIndex);	
				    				    
				    
				    System.out.println("condition 1 : " + (mmfromParser.x_mms_delivery_time != null && 
					    	mmfromParser.x_mms_delivery_time.before(nowDate)));
				    System.out.println("condition 2 : " + mmfromParser.to.substring(0,4).equals("0101") );
				    System.out.println("condition 3 : " + dbController.select_userOn(mmfromParser.to)   );
				    
				    if(mmDelivery != null && 
				    		mmDelivery.before(nowDate) &&
				    		 (!(mmfromParser.to.substring(0,4).equals("0101") && !dbController.select_userOn(mmfromParser.to))))
				    {
				    	//After Send, update Deliverytime
				    	
				    	System.out.println("In WithUAHTTP : forwrd index :" + mmfromParser.messageIndex);
				    	dbController.update_deliveryTime(mmfromParser.messageIndex,(long)0);
						taskController.queueManage(mmfromParser.messageIndex);
						System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
				    }
				    else{
				    	dbController.update_deliveryTime(mmfromParser.messageIndex,(long)mmDelivery.getTime());
				    }
				}
		
				// m-notifyresp-ind   DB 수정 : x_mms_stauts
				if(mmfromParser != null && 
					mmfromParser.x_mms_message_type.equals("m-notifyresp-ind") &&
					mmfromParser.x_mms_status != null)
				{					
					
					String mmIndex = dbController.select_TID(mmfromParser.x_mms_transaction_ID);
					dbController.update_xMmsStatus(mmIndex, mmfromParser.x_mms_status);
					MM mmfromDB = dbController.select_mmHeader(mmIndex);
					
					dbController.update_pushStatus( mmIndex, "m-delivery-ind");
					
					if(mmfromDB.to.substring(0,4).equals("0101") && !dbController.select_userOn(mmfromDB.to))
					{
						dbController.update_deliveryTime(mmIndex, (long)1);
					}
					else if(mmfromDB.x_mms_delivery_report && mmfromParser.x_mms_report_allowed)
					{	
						if(!mmfromDB.from.substring(0, 4).equals("0101"))
						{
							MM responseMM = (MM) mmfromDB.clone();
							String tempTo = responseMM.from;
							String tempFrom = responseMM.to;
							responseMM.to = tempTo;
							responseMM.from = tempFrom;
							responseMM.x_mms_message_type = "MM4_delivery_report.REQ";
							responseMM.push_status = "m-delivery-ind";
							responseMM.messageIndex += "MM4";
							String responseIndex = responseMM.messageIndex;
							dbController.insert_mmHeader(responseMM);
							taskController.queueManage(responseIndex);
						}
						else
						{
							taskController.queueManage(mmIndex);
						}
						System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
					
					}
					/////////////
					
					/////////////
				}
				
				if(mmfromParser != null && 
						mmfromParser.x_mms_message_type.equals("m-acknowledge-ind"))
					{					
						String mmIndex = dbController.select_TID(mmfromParser.x_mms_transaction_ID);
						dbController.update_xMmsStatus(mmIndex, "Retrieved");
						MM mmfromDB = dbController.select_mmHeader(mmIndex);
						
						
						dbController.update_pushStatus( mmIndex, "m-delivery-ind");
						
						if(mmfromDB.to.substring(0,4).equals("0101") && !dbController.select_userOn(mmfromDB.to))
						{
							dbController.update_deliveryTime(mmIndex, (long)1);
						}
						else if(mmfromDB.x_mms_delivery_report && mmfromParser.x_mms_report_allowed)
						{	
							if(!mmfromDB.from.substring(0, 4).equals("0101"))
							{
								MM responseMM = (MM) mmfromDB.clone();
								String tempTo = responseMM.from;
								String tempFrom = responseMM.to;
								responseMM.to = tempTo;
								responseMM.from = tempFrom;
								responseMM.x_mms_message_type = "MM4_delivery_report.REQ";
								responseMM.push_status = "m-delivery-ind";
								responseMM.messageIndex += "MM4D";
								String responseIndex = responseMM.messageIndex;
								dbController.insert_mmHeader(responseMM);
								taskController.queueManage(responseIndex);
							}
							else
							{
								taskController.queueManage(mmIndex);
							}
							System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
						}
					}
					
				
				// m-read-rec-ind
				if(mmfromParser != null && mmfromParser.x_mms_message_type.equals("m-read-rec-ind")&&
			    		(!(mmfromParser.to.substring(0,4).equals("0101") && !dbController.select_userOn(mmfromParser.to))))
				{
					
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromParser/ mid : " + mmfromParser.messageID);
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromParser/  to : " + mmfromParser.to);
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromParser/from : " + mmfromParser.from);
					
					String mmIndex = mmfromParser.messageID + "+" + mmfromParser.from;
					
					dbController.update_xMmsStatus(mmIndex, mmfromParser.x_mms_status);
					MM mmfromDB = dbController.select_mmHeader(mmIndex);
					
					dbController.update_pushStatus( mmIndex, "m-read-orig-ind");
					dbController.update_xMmsReadStatus(mmIndex, mmfromParser.x_mms_read_status);
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromDB/ mid : " + mmfromDB.messageID);
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromDB/  to : " + mmfromDB.to);
					System.out.println("In WithUAHTTP : in proccesing m-read-rec-ind -  mmfromDB/from : " + mmfromDB.from);
					if(mmfromDB.to.substring(0,4).equals("0101") && !dbController.select_userOn(mmfromDB.to))
					{
						dbController.update_deliveryTime(mmIndex, (long)1);
					}
					else if(mmfromDB.x_mms_read_report && mmfromParser.x_mms_read_status.equals("Read") )
					{
						if(!mmfromDB.from.substring(0, 4).equals("0101"))
						{
							MM responseMM = (MM) mmfromDB.clone();
							String tempTo = responseMM.from;
							String tempFrom = responseMM.to;
							responseMM.to = tempTo;
							responseMM.from = tempFrom;
							responseMM.x_mms_message_type = "MM4_read_reply_report.REQ";
							responseMM.push_status = "m-read-orig-ind";
							responseMM.messageIndex += "MM4R";
							String responseIndex = responseMM.messageIndex;
							dbController.insert_mmHeader(responseMM);
							taskController.queueManage(responseIndex);
						}
						else
						{
							taskController.queueManage(mmIndex);
						}
						System.out.println("In Nano HTTPD : Input mm_index to Quere!! <- " + mmfromParser.messageIndex);
						
						
					}
			
				}
				
				
				// Send Response 처리 ( GET / m-send-req / m-forward-req / 나머지는 200 OK만)
				Response response;
				if("GET".equals(method) ||
					( "POST".equals(method) && mmfromParser != null && (
					( mmfromParser.x_mms_message_type.equals("m-send-req")  ||
					  mmfromParser.x_mms_message_type.equals("m-forward-req") ) ) ))
				{
					response = setResponse.set( uri, method, mimeProp, mmfromParser );
					System.out.println("In Nano HTTPD : ___Created response!!____");
					
				}
				else
				{
					response = new Response();
					System.out.println("In Nano HTTPD : ___Created response only (200 OK)!!____");
				}
				
				if ( response == null )
					errorResponse.sendError( mySocket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response." );
				else{
					response.sendResponse (mySocket);
					System.out.println("In Nano HTTPD : ___Send response!!____");
				}
					
				
				
			}
			catch (IOException ioe)
			{
				try{
					errorResponse.sendError( mySocket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());					
				}
				catch (Throwable t) {}
			}
			catch (InterruptedException ie) 
			{
				// Thrown by sendError, ignore and exit the thread.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}//End void run
		
	}// end HTTPSEssion
	
	/**
	 * Stops the server.
	 */
	public void stop()
	{
		try
		{
			myServerSocket.close();
			myThread.join();
		}
		catch ( IOException ioe ) {}
		catch ( InterruptedException e ) {}
	}// end Stop                        
	
	/**
	 * Some HTTP response status codes
	 */
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

}// end NanoHTTPD