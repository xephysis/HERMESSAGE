package ac.ajou.hermessageServerTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

/**
 * HTTP response. Return one of these from serve().
 */
public class Response {
	/**
	 * Default constructor: response = HTTP_OK, data = mime = 'null'
	 */
	public Response() {
		this.status = "200 OK";
	}

	/**
	 * Basic constructor.
	 */
	public Response(String status, String mimeType, InputStream data) {
		this.status = status;
		this.mimeType = mimeType;
		this.data = data;
	}

	/**
	 * Convenience method that makes an InputStream out of given text.
	 */
	public Response(String status, String mimeType, String txt) {
		this.status = status;
		this.mimeType = mimeType;
		try {
			this.data = new ByteArrayInputStream(txt.getBytes("UTF-8"));
		} catch (java.io.UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	}

	/**
	 * Adds given line to the header.
	 */
	public void addHeader(String name, String value) {
		header.put(name, value);
	}

	/**
	 * HTTP status code after processing, e.g. "200 OK", HTTP_OK
	 */
	public String status;

	/**
	 * MIME type of content, e.g. "text/html"
	 */
	public String mimeType;

	/**
	 * Data of the response, may be null.
	 */
	public InputStream data;

	/**
	 * Headers for the HTTP response. Use addHeader() to add lines.
	 */
	public Properties header = new Properties();
	
	public void sendResponse( Socket socket, String in_status, String in_mime, Properties in_header, InputStream in_data )
	{
		status = in_status;
		mimeType = in_mime;
		header = in_header;
		data = in_data;
		
		sendResponse(socket);
	}
	public void sendResponse( Socket socket)
	{
		try
		{
			if ( status == null )
				throw new Error( "sendResponse(): Status can't be null." );

			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter( out );
			pw.print("HTTP/1.0 " + status + " \r\n");

			if ( mimeType != null )
				pw.print("Content-Type: " + mimeType + CRLF);

			if ( header != null )
			{
				Enumeration<Object> e = header.keys();
				
				while ( e.hasMoreElements())
				{
					String key = (String)e.nextElement();
					String value = header.getProperty( key );
					pw.print( key + ": " + value + CRLF);
				}
			}

			pw.print(CRLF);
			pw.flush();

			//data.
			if ( data != null )
			{
				byte[] buff = new byte[2048];
				while (true)
				{
					int read = data.read( buff, 0, 2048 );
					if (read <= 0)
						break;
					out.write( buff, 0, read );
				}
			}
			out.flush();
			out.close();
			if ( data != null )
				data.close();
			
			
			System.out.println("In Nano HTTPD : Send Response !!!!!");
		}
		catch( IOException ioe )
		{
			// Couldn't write? No can do.
			try { socket.close(); } catch( Throwable t ) {}
		}
	} // end sendResponse
	
	public static final String
		CRLF = "\r\n";
	
} // end Response Class