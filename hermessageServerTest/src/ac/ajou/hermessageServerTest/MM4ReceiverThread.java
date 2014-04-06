package ac.ajou.hermessageServerTest;

//Server
import java.io.*;
import java.net.*;

public class MM4ReceiverThread implements Runnable {
	
	public Socket connectionSocket;
	public boolean isend = false;
	public MMTask_Controller taskController;
	
	public MM4ReceiverThread(Socket _connectionSocket, MMTask_Controller _taskController ){
		connectionSocket = _connectionSocket;
		taskController = _taskController;
	}

	public void run() {
		
		System.out.println("Receiver Thread Start");
		
		MM4Parser mm4parser = new MM4Parser();
		MM4ProcessMIME mm4Processer = new MM4ProcessMIME();
		
		String from = null;
		String to = null;
		String data = null;
		StringBuffer strbuf = new StringBuffer();
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			out = new PrintWriter(connectionSocket.getOutputStream(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.println("220 Hermessage.com SMTP MailWatcher"); 
		try {

			while (true) {
				String cmd = in.readLine();
				String tempcmd = cmd.substring(0, 4);
				System.out.println("받은 명령어:" + cmd);

				if (tempcmd.equals("HELO") || tempcmd.equals("helo")) {
					out.println("250 OK");
					

					while (true) {
						cmd = in.readLine();
						System.out.println("받은 명령어:" + cmd);
						int index = cmd.indexOf(':');
						String parsedCmd = cmd.substring(0, index + 1);
						String host = cmd.substring(index + 1);

						if (parsedCmd.equals("MAIL FROM:")|| parsedCmd.equals("mail from:")) {
							out.println("250 " + "Sender <" + host + "> Ok");
							
							
							from = host;
							while (true) {
								cmd = in.readLine();
								System.out.println("받은 명령어:" + cmd);
								index = cmd.indexOf(':');
								parsedCmd = cmd.substring(0, index + 1);
								host = cmd.substring(index + 1);

								if (parsedCmd.equals("RCPT TO:")|| parsedCmd.equals("rcpt to:")) {
									out.println("250 " + "Recipient <" + host+ "> Ok");
									to = host;
									while (true) {
										cmd = in.readLine();
										System.out.println("받은 명령어:" + cmd);
										if (cmd.equals("DATA")|| cmd.equals("data")) {
											out.println("354 Start mail input; end with \"<CRLF>.<CRLF>\"");
											while (true) 
											{
												char tempChar;
												tempChar=(char)in.read();
												strbuf.append(tempChar);
												
												if(tempChar=='\n')
												{
													if(strbuf.indexOf("\r\n.\r\n")!=-1)//찾았음
													{
														out.println("250 OK");
														cmd = in.readLine();
														System.out.println("마지막 명령어"+cmd);
														
														if(cmd.equalsIgnoreCase("quit"))
														{
															out.print("221 Hermessage Service closing transmission channel");
															
															System.out.println("지금까지 쌓인 데이터\n"+strbuf);
															
															String mimeStr = strbuf.toString().substring(0,strbuf.toString().length() - 3);
															BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("mm4.txt")));
															bw.write(mimeStr);
															bw.close();
															
															BufferedReader mimeReader = new BufferedReader(new InputStreamReader(new FileInputStream("mm4.txt")));
															System.out.println("In MM4ReceiverThread : before parsing!!!");
															MM mm = mm4parser.parser(mimeReader, to);
															System.out.println("In MM4ReceiverThread : after parsing!!!");
															mimeReader.close();
															mm4Processer.process(mm, taskController);
															
															in.close();
															out.close();
															connectionSocket.close();
															System.out.println("SMTP thread 종료");
															return;
														}
												
													}
												}
												
											}
										} else
											out.println("500 Syntax error, unrecognized command ["+ cmd + "]");
									}

								} else
									out.println("500 Syntax error, unrecognized command ["+ cmd + "]");
							}
						} else
							out.println("500 Syntax error, unrecognized command ["+ cmd + "]");
					}
				} else
					out.println("500 Syntax error, unrecognized command ["+ cmd + "]");
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}
}

