package ac.ajou.hermessageServerTest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class Mail {
	String mailServer = null;
	Message message = null;

	String sender = null; 
	String senderNmae = null; 
	String subject = null;
	String content = null; 
	String receiver = null; 

	Address senderAddress = null; 
	Address[] receiverAddress = null; 
	FileReader reader;
	
	String messageID;
	
	
	 public Mail() {}
	 
	 public Mail(String mailServer){
	  setMailServer(mailServer);
	 }
	 

	public void SendMail() throws UnsupportedEncodingException,
			MessagingException {
		if (sender == null || receiver == null)
			throw new NullPointerException(
					"sender, subject,receiver is null.");

		initializeMailServer();
		initializeSender();
		initializeReceiver();

		Send();
	}

	public void initializeMailServer() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", mailServer);
		Session s = Session.getDefaultInstance(properties);
		message = new MimeMessage(s);
	}

	public void initializeSender() throws UnsupportedEncodingException {
		if (senderNmae == null) 
			senderNmae = sender;

		senderAddress = new InternetAddress(sender, MimeUtility.encodeText(
				senderNmae, "UTF-8", "B")); 
	}

	public void initializeReceiver() throws AddressException {
		ArrayList<String> receiverList = new ArrayList<String>();
		StringTokenizer stMailAddress = new StringTokenizer(receiver, ";");
		while (stMailAddress.hasMoreTokens()) {
			receiverList.add(stMailAddress.nextToken());
		}

		receiverAddress = new Address[receiverList.size()];
		for (int i = 0; i < receiverList.size(); i++) {
			receiverAddress[i] = new InternetAddress(receiverList.get(i));
		}
	}

	public void Send() throws MessagingException, UnsupportedEncodingException {
		
		
		message.setHeader("content-type", "text/html;charset=UTF-8");
		message.setFrom(senderAddress);
		message.setRecipients(Message.RecipientType.TO, receiverAddress); // �޴�
																			// ���
																			// Ÿ��
																			// (TO,
																			// CC,
																			// BCC)
		if(subject!= null)
		{
			message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
		}
		else
		{
			message.setSubject("HERMESSAGE mail");
		}
		message.setSentDate(new java.util.Date());
		//message.setContent(content, "text/html;charset=UTF-8");
		
		message = this.msgSetFile(message, messageID);
		//message.setContent("messalkejfsliejfsdf", "text/html;charset=UTF-8");
		
		
		Transport.send(message);
		System.out.println("Mail Send Success!");
	}
	
	public Message msgSetFile(Message msg, String messageID) {
		try {
			
			Multipart mp = new MimeMultipart();
			
			FileReader readerList;
			readerList = new FileReader("/HERMESSAGE/content/" + messageID + "/"
					+ "contentList.txt");
			StringBuffer listBuffer = new StringBuffer("");
			int ch = 0;
			while ((ch = readerList.read()) != -1) {
				listBuffer.append((char) ch);
			}

			String[] contentLocationList = listBuffer.toString().split("\n");

			for (int index = 0; index < contentLocationList.length; index++) {
				if (!contentLocationList[index].equals("")
						&& contentLocationList[index] != null) {
					int posPeriod = contentLocationList[index].indexOf('.');

					String extension = contentLocationList[index]
							.substring(posPeriod + 1);

					if (extension.toLowerCase().equals("txt")) { // text 파일 
						MimeBodyPart mbodyPart = new MimeBodyPart();
						
						String textFilePath = "/HERMESSAGE/content/" + messageID + "/"	+ contentLocationList[index];
						String bodyText = "";
						
						StringBuffer msgBuf = new StringBuffer();
						  File f = new File(textFilePath);

						   BufferedReader in = new BufferedReader(new FileReader(f));
						   while ((bodyText=in.readLine()) != null){
							   msgBuf.append(bodyText).append("\n");
						   }
						   in.close();
						   bodyText = msgBuf.toString();
						mbodyPart.setText(bodyText);
						mp.addBodyPart(mbodyPart);
						System.out.println("In mail.java : bodyText add " + bodyText);

					} 
					else 
					{// text 이외에 첨부파일로 
						
						File file = new File("/HERMESSAGE/content/" + messageID + "/"
								+ contentLocationList[index]);
						MimeBodyPart mbodyPart = new MimeBodyPart();
						FileDataSource fds = new FileDataSource(file);
						mbodyPart.setDataHandler(new DataHandler(fds));
						mbodyPart.setFileName(fds.getName());
						mp.addBodyPart(mbodyPart);	
						System.out.println("In mail.java : file add " + contentLocationList[index] );
					}
				}
			}
			msg.setContent(mp,"text/html;charset=UTF-8");

		}

		catch (Exception ex) {
			ex.getStackTrace();
		}
		return msg;
	}
	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setSenderName(String senderName) {
		this.senderNmae = senderName;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public void setMessageID(String messageID){
		this.messageID = messageID;
	}
	
}