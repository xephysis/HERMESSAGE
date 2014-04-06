package ac.ajou.hermessageServerTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

public class RSPushComponent implements Runnable {
	MMTask_Controller taskController;
	Socket clientSocket;
	String userInformation;
	String clientId;
	boolean userOn;
	DB_Management dbController;
	String getmessageIndex;
	// MM mmHeader;
	PrintWriter out;
	BufferedReader in;
	makePushMessage messageController;
	MakeLog makelog;

	public RSPushComponent(MMTask_Controller _taskController,
			Socket _clientSocket) {
		taskController = _taskController;
		clientSocket = _clientSocket;

		// /
		//userInformation = _userInformation;
		// /

		dbController = new DB_Management();
		messageController = new makePushMessage();
		makelog = new MakeLog();

		clientId = null;
	}

	public void updateUserPreference(String userInfo) {
		// String uaRegister =
		// "phone=01010000003/TYPE=PLMN;jpg=yes;png=yes;width=50;height=50";

		String userNumber;
		boolean isJpg = true;
		boolean isPng = true;
		int maxWidth = 0;
		int maxHeight = 0;
		int maxSize = 0;

		Properties uaRegistration = new Properties();// 리스폰스 파싱해서 집어 넣을 프로퍼티
		StringTokenizer tokenize = new StringTokenizer(userInfo, ";");// ; 를
																		// 구분자로
																		// 하여 파싱

		while (tokenize.hasMoreTokens()) {
			String line = tokenize.nextToken();

			int positionOfDelimeter = line.indexOf('=');
			if (positionOfDelimeter >= 0) {
				uaRegistration
						.put(line.substring(0, positionOfDelimeter).trim(),
								line.substring(positionOfDelimeter + 1).trim());

			} else {
				break;
			}
		}

		userNumber = uaRegistration.getProperty("phone");
		clientId = userNumber;

		if (uaRegistration.getProperty("jpg").equalsIgnoreCase("Yes")) {
			isJpg = true;
		} else {
			isJpg = false;
		}
		if (uaRegistration.getProperty("png").equalsIgnoreCase("Yes")) {
			isPng = true;
		} else {
			isPng = false;
		}
		
		maxSize = Integer.parseInt(uaRegistration.getProperty("size"));
		
		maxWidth = Integer.parseInt(uaRegistration.getProperty("width"));
		maxHeight = Integer.parseInt(uaRegistration.getProperty("height"));

		dbController.insert_userPreference(userNumber, isPng, isJpg, maxWidth,
				maxHeight, maxSize);
		System.out.println("\nUserPrefernce 삽입\n");

		/*
		System.out.println(isJpg);
		System.out.println(isPng);
		System.out.println(maxWidth);
		System.out.println(maxHeight);
		System.out.println(maxSize);
		*/
	}

	public void pushToUser(MM mmHeader) {
		userOn = dbController.select_userOn(clientId); // Select client of
														// destination.

		if (userOn == true) // If client is log on,
		{

			System.out
					.println("In RSPushComponenet : *****Success Socket connect*****");
			System.out.println("In RSPUSH : 내부큐에서 빠져나오기 전 상태:" + taskController.internalQueue);
			getmessageIndex = taskController.GetInterQueue(); // Get from //
			System.out.println("In RSPUSH : 내부큐에서 빠져나온 상태:" + taskController.internalQueue);
																// Queue.

			// makePushMessage messageController = new makePushMessage();
			String PDU = messageController.selectPushData(getmessageIndex);

			// keepAlive
			if (PDU != null && !clientSocket.isClosed()) {

				String responseOfClient = null;

				out.println("KeepAlive");

				try {
					Thread.sleep(5 * 1000); // 5seconds
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				try {
					responseOfClient = in.readLine();
				} catch (Exception e) {
					e.getStackTrace();
					System.out
							.println("Wait 5seconds..... But don't Receive User's respons. ");

					dbController.insert_userOn(clientId, false);
					dbController.insert_pdu(mmHeader.messageIndex, clientId);

					try {
						in.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					out.close();
					try {
						clientSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;

				}

				System.out.println("Response Message of Client : "
						+ responseOfClient);

				System.out
						.println("In RSPushComponenet :*****Push To User*************");
				System.out
						.println("In RSPushComponenet :*****content's of PDU*********");

				// 실패할 경우 DB저장 유저아이디 처리.

				if (mmHeader.x_mms_expiry.getTime() == 0) {
					// 파일 삭제
					// db 삭제
					if (mmHeader.x_mms_status.equalsIgnoreCase("Deferred")) {
						dbController.update_xMmsStatus(mmHeader.messageIndex,
								"Expired");

						dbController.update_pushStatus(mmHeader.messageIndex,
								"m-delivery-ind");
						// System.out.println("m-delivery-ind로 바뀌어야 함 :" +
						// mmHeader.push_status);

						MM temp = dbController
								.select_mmHeader(mmHeader.messageIndex);
						System.out.println("Expired로 바뀌어야 함 :"
								+ temp.x_mms_status);
						try {
							Thread.sleep(4 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String expiredPDU = messageController
								.selectPushData(mmHeader.messageIndex);
						System.out
								.println("=======================================");

						out.println(expiredPDU);
						try {
							makelog.logPrint(mmHeader.messageID,
									mmHeader.push_status, expiredPDU);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out
								.println("====================after transmission :"
										+ expiredPDU);
					}

					dbController.delete_mmHeader(getmessageIndex);
					System.out
							.println("In RSPushComponenet :만료가되어서 메시지를 삭제합니다 : ."
									+ getmessageIndex);
				} else {
					System.out.println("In RSPushComponenet : PDU: !" + PDU
							+ "!");
					out.println(PDU);
					try {
						makelog.logPrint(mmHeader.messageID, mmHeader.push_status,
								PDU);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out
						.println("In RSPushComponenet :send Message to Client:"
								+ getmessageIndex);
				System.out
						.println("In RSPushComponenet :*****Push To User Success*****");

			} else if (PDU == null) {
				MM deletemmHeader = dbController
						.select_mmHeader(getmessageIndex);
				if (deletemmHeader.x_mms_expiry.getTime() == 0) {
					dbController.delete_mmHeader(getmessageIndex);
					System.out
							.println("In RSPushComponenet :만료가되어서 메시지를 삭제합니다 : ."
									+ getmessageIndex);
				}

				// if()
				System.out
						.println("In RSPushComponenet : PDU is null / So don't send Push");
			} else {
				System.out
						.println("In RSPushComponenet : User is Log Out, So don't send Push");
			}

		}
		if (userOn == false) // If user is log out,
		{
			System.out
					.println("In RSPushComponenet :Can't push !!! Because user's state is logout!!");
			return;
		}
	}

	public void run() {

		System.out.println("In RSPushComponenet : RSPush Thread is created");
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			userInformation  = in.readLine();
		} catch (IOException e1) {
			
			e1.printStackTrace();
			System.out.println("유저의 정보를 받는 것을 실패");
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.close();
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return;
		}
		
		System.out.println("유저 정보:" + userInformation);
		this.updateUserPreference(userInformation); // User preference Update.

		dbController.insert_userOn(clientId, true);
		boolean userOn = dbController.select_userOn(clientId);

		System.out.println("InRSPush Start point " + clientId + "'s Status is "
				+ userOn);



		String[] messageIndexArray = null;
		messageIndexArray = dbController.select_pdu(clientId);

		if (messageIndexArray != null) {
			for (int i = 0; i < messageIndexArray.length; i++) {
				MM mmfromDB = dbController
						.select_mmHeader(messageIndexArray[i]);

				String PDU = messageController
						.selectPushData(messageIndexArray[i]);
				System.out.println("유저가 처음 접속했을 때 미접속된 메시지 전송할 PDU : " + PDU);
				out.println(PDU);

				try {
					makelog.logPrint(mmfromDB.messageID, mmfromDB.push_status,
							PDU);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		dbController.delete_pdu(clientId);

		while (true) {
			System.out.println("-----------------"+ clientId +"-----------------RS Pushserver is monitoring  MMTask_Controller.");

			// keepAlive
			if (dbController.select_userOn(clientId) == true) {
				String responseOfClient = null;

				out.println("KeepAlive");

				try {
					Thread.sleep(6 * 1000); // 5seconds
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				try {
					responseOfClient = in.readLine();
				} catch (Exception e) {
					e.getStackTrace();
					System.out.println("Wait 5seconds..... But don't Receive User's respons. ");

					dbController.insert_userOn(clientId, false);

						try {
							clientSocket.close();
							in.close();
							out.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}


					return;
				}
				System.out.println("현재 살아있는 유저  (" + clientId + ")의 응답:"
						+ responseOfClient);
			} else {
				try {
					clientSocket.close();
					in.close();
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
			
			// keepAlive
			if (dbController.select_userOn(clientId) == true) {
				String responseOfClient = null;

				out.println("KeepAlive");

				try {
					Thread.sleep(5 * 1000); // 5seconds
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				try {
					responseOfClient = in.readLine();
				} catch (Exception e) {
					e.getStackTrace();
					System.out.println("Wait 5seconds..... But don't Receive User's respons. ");

					dbController.insert_userOn(clientId, false);

						try {
							clientSocket.close();
							in.close();
							out.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}


					return;
				}
				System.out.println("현재 살아있는 유저  (" + clientId + ")의 응답:"
						+ responseOfClient);
			} else {
				try {
					clientSocket.close();
					in.close();
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			/*
			if (clientSocket.isClosed()) {
				System.out.println("In RSPushComponenet : User's state is Log out. Thread end");

				dbController.insert_userOn(clientId, false);
				break;
			}
			*/

			if (taskController.internalQueue.isEmpty() != true) { // 큐가 비어있지 않으면
				getmessageIndex = taskController.internalQueue.element(); // 큐의				
				
																			// 첫번째를
																			// 검사해서
				MM mmHeader = dbController.select_mmHeader(getmessageIndex); // 그
																				// 첫번째의
																				// 헤더를
																				// 가져와
				if (mmHeader.messageIndex.indexOf("forSize") > 0)
				{
					System.out.println("forSize Input -> skip mmHeader!");
				}
				else if (mmHeader.push_status.equals("m-notification-ind")&& mmHeader.to.equals(clientId)) { // notification이면

					String sender = mmHeader.from;
					String receiver = mmHeader.to; // to필드를 검사해야 한다.

					System.out.println("In RSPushComponenet : notification.ind 의 sender:"+ sender);
					System.out.println("In RSPushComponenet : notification.ind 의 receiver:"+ receiver);
					System.out.println("In RSPushComponenet : 현재 쓰레드의 주체 : "+ clientId);

					if (dbController.select_userOn(receiver) == true) { // from이
																		pushToUser(mmHeader); // 보낸다.
					} else {
						System.out.println("In RSPushComponenet : 노티피케이션을 받을 유저가 로그아웃");
						return;
						// getmessageIndex = taskController.GetInterQueue();
						// mmHeader.x_mms_delivery_time = new Date(1);
					}

				} else if (mmHeader.push_status.equals("m-delivery-ind")&& mmHeader.from.equals(clientId)) {

					String sender = mmHeader.from;
					String receiver = mmHeader.to;
					System.out.println("In RSPushComponenet : notification.ind 의 sender:"+ sender);
					System.out.println("In RSPushComponenet : notification.ind 의 receiver:"+ receiver);
					System.out.println("In RSPushComponenet : 현재 쓰레드의 주체 : "+ clientId);

					if (dbController.select_userOn(sender) == true) {
						pushToUser(mmHeader);
					} else {
						System.out
								.println("In RSPushComponenet : 딜리버리를 받을 유저가 로그아웃");
						return;
						// getmessageIndex = taskController.GetInterQueue();
						// mmHeader.x_mms_delivery_time = new Date(1);
					}

				} else if (mmHeader.push_status.equals("m-read-orig-ind")
						&& mmHeader.from.equals(clientId)) {

					String sender = mmHeader.from;
					String receiver = mmHeader.to; // to필드를 검사해야 한다.

					System.out
							.println("In RSPushComponenet : notification.ind 의 sender:"
									+ sender);
					System.out
							.println("In RSPushComponenet : notification.ind 의 receiver:"
									+ receiver);
					System.out.println("In RSPushComponenet : 현재 쓰레드의 주체 : "
							+ clientId);

					if (dbController.select_userOn(sender) == true) {
						pushToUser(mmHeader);
					} else {
						System.out.println("In RSPushComponenet : 리드리포트를 받을 유저가 로그아웃");
						return;
						// getmessageIndex = taskController.GetInterQueue();
						// mmHeader.x_mms_delivery_time = new Date(1);
					}

				}

			} else {
				// System.out.println("---------------------------------------------In RSPushComponenet : 큐가 비어있음.");
			}
		}

	}
}
