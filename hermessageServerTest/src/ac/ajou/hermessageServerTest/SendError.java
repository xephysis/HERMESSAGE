package ac.ajou.hermessageServerTest;

import java.io.ByteArrayInputStream;
import java.net.Socket;

// Client ??Error Meesage ë¥?ë³´ë‚´ì£¼ëŠ” Method -> Responseë¡?ë³´ë‚´ì¤?‹¤.
public class SendError
{
	/**
	 * Returns an error message as a HTTP response and throws
	 * InterruptedException to stop further request processing.
	 */
	public void sendError(Socket socket, String status, String msg ) throws InterruptedException
	{
		Response errorResponse = new Response();
		errorResponse.sendResponse(socket, status, "text/pline", null, new ByteArrayInputStream( msg.getBytes()));
		throw new InterruptedException();
	} // end sendError
}