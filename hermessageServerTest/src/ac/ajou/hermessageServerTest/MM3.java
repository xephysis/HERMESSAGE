package ac.ajou.hermessageServerTest;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class MM3 {
	
	String messageID;
	String mailReceiver;
	String mailSender;
	String senderName;
	String mailSubject;
	String mailContent;

	//	messageID를 여기서 받고 나서 
	public MM3(String _messageID, String _mailReceiver, String _mailSender, String _senderName, String _mailSubject)
	{
		messageID = _messageID;
		mailReceiver = _mailReceiver;
		mailSender = _mailSender;
		senderName = _senderName;
		mailSubject = _mailSubject;
	}
	
	public String getDomain(String mailName){
		int index = mailName.indexOf("@");
		String domain = mailName.substring(index+1);
		return domain;
	}

	
	public String doLookup(String hostName) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = null;
		try {
			ictx = new InitialDirContext(env);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Attributes attrs = null;
		try {
			attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Attribute attr = attrs.get("MX");

		Object obj = null;
		try {
			obj = attr.get(0);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = obj.toString();

		String mailServer = str.substring(3, str.length() - 1);
		System.out.println(mailServer);

		return mailServer;
	}
	
	
	public void start() {
		
		String domain = getDomain(mailReceiver);
		String mailServer = doLookup(domain);
		
		System.out.println("mail Domail "+domain);
		System.out.println("mail Server: "+mailServer);

		Mail mail = new Mail();
		mail.setMailServer(mailServer);
		mail.setMessageID(messageID);
		mail.setSender(mailSender);
		mail.setSenderName(senderName);
		mail.setReceiver(mailReceiver);
		mail.setSubject(mailSubject);
		
		try {
			mail.SendMail();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		
	}
	

}
