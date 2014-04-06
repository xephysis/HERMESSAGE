package ac.ajou.hermessageServerTest;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB_Management {
	
	/*
	 * insert_mmHeader 
	 * mmHeader를 넣어서 해당 mm객체를 초기화
	 * 
	 */
	public void insert_mmHeader(MM _mmHeader) {
		
		MM mmHeader = _mmHeader;			//	mmHeader 초기화
		String query = "insert into mmdb.mmheader values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";	//	쿼리문 작성
		
		
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			java.sql.Connection Conn = null;	//	Conn 초기화
			try {
				Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PreparedStatement prepared = null;
			try {
				prepared = Conn.prepareStatement(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(1,mmHeader.messageIndex);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setLong(2, mmHeader.date.getTime());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(3, mmHeader.to);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(4, mmHeader.from);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(5, mmHeader.x_mms_transaction_ID);
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				prepared.setString(6, mmHeader.x_mms_message_type);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(7, mmHeader.x_mms_mms_version);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setLong(8, mmHeader.x_mms_expiry.getTime());
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				prepared.setLong(9, mmHeader.x_mms_delivery_time.getTime());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setBoolean(10, mmHeader.x_mms_delivery_report);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setBoolean(11, mmHeader.x_mms_read_report);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(12, mmHeader.x_mms_response_status );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setInt(13, mmHeader.x_mms_message_size);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				prepared.setString(14, mmHeader.x_mms_content_location);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(15, mmHeader.content_type);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(16, mmHeader.subject);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(17, mmHeader.content_transfer_encoding);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(18, mmHeader.content_location);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				prepared.setString(19, mmHeader.boundary);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(20, mmHeader.push_status);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(21, mmHeader.x_mms_status);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(22, mmHeader.messageID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setBoolean(23, mmHeader.x_mms_report_allowed);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setString(24, mmHeader.x_mms_read_status);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.setBoolean(25, mmHeader.x_mms_ack_request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				prepared.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	}

	/* 	select_mmHeader
	 * input: messageIndex
	 * outPut: MM
	 * 해당 MM객체를 반환.
	 */
	
	public MM select_mmHeader(String _messageIndex) {
		MM mmHeader = new MM();
		String messageIndex = _messageIndex;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			
			//String query = "select * from mmheader where mm_index =?";
			String query = "select * from mmheader where mm_index = " + "'" + messageIndex + "'";

			
			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			//PreparedStatement prepared = Conn.prepareStatement(query);
			//prepared.setString(1, messageIndex);
			//ResultSet rs = prepared.executeQuery(query);

			////System.out.println("In DB_Managemnet : DB log: mm_select Success");
			while (rs.next()) {
				messageIndex = rs.getString(1);
				Date date = new Date(rs.getLong(2));
				String to = rs.getString(3);
				String from = rs.getString(4);
				String x_mms_transaction_ID = rs.getString(5);
				String x_mms_message_type = rs.getString(6);
				String x_mms_mms_version = rs.getString(7);
				Date x_mms_expiry = new Date(rs.getLong(8));
				Date x_mms_delivery_time = new Date(rs.getLong(9));
				boolean x_mms_delivery_report = rs.getBoolean(10);
				boolean x_mms_read_report = rs.getBoolean(11);
				String x_mms_response_status = rs.getString(12);
				int x_mms_message_size = rs.getInt(13);
				String x_mms_content_location = rs.getString(14);
				String content_type = rs.getString(15);
				String subject = rs.getString(16);
				String content_transfer_encoding = rs.getString(17);
				String content_location = rs.getString(18);
				String boundary = rs.getString(19);
				String push_stauts = rs.getString(20);
				String x_mms_status = rs.getString(21);
				String messageID = rs.getString(22);	
				boolean x_mms_report_allowed = rs.getBoolean(23);
				String x_mms_read_status = rs.getString(24);
				boolean x_mms_ack_request = rs.getBoolean(25);
				
				
				mmHeader.messageIndex = messageIndex;
				mmHeader.date = date;
				mmHeader.to = to;
				mmHeader.from = from;
				mmHeader.x_mms_transaction_ID = x_mms_transaction_ID;
				mmHeader.x_mms_message_type = x_mms_message_type;
				mmHeader.x_mms_mms_version = x_mms_mms_version;
				mmHeader.x_mms_expiry = x_mms_expiry;
				mmHeader.x_mms_delivery_time = x_mms_delivery_time;
				mmHeader.x_mms_delivery_report = x_mms_delivery_report;
				mmHeader.x_mms_read_report = x_mms_read_report;
				mmHeader.x_mms_response_status = x_mms_response_status;
				mmHeader.x_mms_message_size = x_mms_message_size;
				mmHeader.x_mms_content_location = x_mms_content_location;
				mmHeader.content_type = content_type;
				mmHeader.subject = subject;
				mmHeader.content_transfer_encoding = content_transfer_encoding;
				mmHeader.content_location = content_location;
				mmHeader.boundary = boundary;
				mmHeader.push_status = push_stauts;
				mmHeader.x_mms_status = x_mms_status;
				mmHeader.messageID = messageID;		
				mmHeader.x_mms_report_allowed = x_mms_report_allowed;
				mmHeader.x_mms_read_status = x_mms_read_status;
				mmHeader.x_mms_ack_request = x_mms_ack_request;
			}
			Conn.close();
			stmt.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		

		return mmHeader;
	}

	
	/*
	 * watchExpiry
	 * input: void
	 * output: messageIndex
	 * 
	 * database를 검색하여 해당 expiry에 해당하는 db에 저장된 MM을 삭제
	 * scheduler에서는 해당 mmIndex로 db를 삭제
	 */

	public  String watchDeliveryTime() {	

		Date nowdate = new Date();				
		long now = nowdate.getTime();		
		long selectedLong = 0;
		long immediate = 0;
		MM mmHeader = null;
		
		List<Long> deliveryTimeList = new ArrayList<Long>();	
		List<String> messageIndexList = new ArrayList<String>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			String query = "select * from mmheader";
			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			//	현재 DB에 저장되어있는 모든 messageID와 deliveryTime을 가져와서 배열로 저장.
			while (rs.next()) {
				deliveryTimeList.add(rs.getLong(9));		//	선택되어진 deliveryTime을 List로 저장
				messageIndexList.add(rs.getString(1));			//	선택되어진 messageIndex를 List로 저장 
			}
			
			Long[] deliveryTimeArray = deliveryTimeList.toArray(new Long[deliveryTimeList.size()]);		//	리스트를 배열로 변환
			String[] messageIndexArray = messageIndexList.toArray(new String[messageIndexList.size()]);			//	리스트를 배열로 변환
			String userNumber;
			
			for (int i = 0; i < deliveryTimeList.size(); i++) {							 
				selectedLong = deliveryTimeArray[i];									

				//	deliveryTime Default value : 0
				if (selectedLong != immediate) {									
					if (selectedLong == now || selectedLong < now) {
						mmHeader = this.select_mmHeader(messageIndexArray[i]);
						 userNumber = mmHeader.to;

						boolean userOn;
						
						if(userNumber.substring(0,4).equals("0101")){
							userOn = this.select_userOn(userNumber);

							if (userOn == true) {
								////System.out.println("In DB_Managemnet : 현재 값은 예약 전송할 messageIndex:" + mmHeader.messageIndex);
								
								query = "UPDATE `mmdb`.`mmheader` SET `x_mms_delivery_time`='0' WHERE `mm_index`='" + mmHeader.messageIndex  +"'";
								stmt.executeUpdate(query);
								return mmHeader.messageIndex;
							} else{
								////System.out.println("In DB_Managemnet : 예약 전송을 할 값이 없습니다.");
							}
						} 
						else{
							query = "UPDATE `mmdb`.`mmheader` SET `x_mms_delivery_time`='0' WHERE `mm_index`='" + mmHeader.messageIndex  +"'";
							stmt.executeUpdate(query);
							return mmHeader.messageIndex;
						}
						
					}
				}
			}
			
			/*
			for(int i = 0; i < deliveryTimeList.size(); i++){
				selectedLong = deliveryTimeArray[i];
				if (selectedLong != immediate){
					if (selectedLong == now || selectedLong < now){
						String messageIndex = messageIndexArray[i];
						MM tempHeader = this.select_mmHeader(messageIndex);
						boolean userOn = this.select_userOn(tempHeader.to);
						if(userOn == true){
							returnDelivery[i] = messageIndexArray[i];
							////System.out.println("In DB_Managemnet : 현재 값은 예약 전송할 messageIndex:" + messageIndexArray[i]);
							query = "UPDATE `mmdb`.`mmheader` SET `x_mms_delivery_time`='0' WHERE `mm_index`='" + messageIndexArray[i]  +"'";
							stmt.executeUpdate(query);
						}
						else
						  ////System.out.println("In DB_Managemnet : 예약 전송을 할 값이 없습니다.");
						
					}
				}
				else
				  ////System.out.println("In DB_Managemnet : 예약 전송을 할 값이 없습니다.");
			}
			*/
			stmt.close();
			Conn.close();
		}catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return null;
		

	}
	
	/*
	 * watchDeliveryTime
	 * input: void
	 * output: messageID
	 * database를 검색하여 해당 deliveryTime에 해당하는 db에 저장된 MM을 가져와서
	 * 해당 messageID를 리턴
	 * scheduler에서는 해당 messageIndex를 보내면 messageIndex를 이용해서 큐에 삽입
	 */
	
	public String watchExpiry() {	

		Date nowdate = new Date();				
		long now = nowdate.getTime();		
		long selectedLong = 0;
		long expired = 0;
		
		MM mmHeader = null;

		List<Long> expiryTimeList = new ArrayList<Long>();	
		List<String> messageIndexList = new ArrayList<String>();

		try {

			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			String query = "select * from mmheader";
			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			//	현재 DB에 저장되어있는 모든 messageID와 expiryTime을 가져와서 배열로 저장.
			while (rs.next()) {
				expiryTimeList.add(rs.getLong(8));		//	선택되어진  expiryTime을 List로 저장
				messageIndexList.add(rs.getString(1));			//	선택되어진 messageIndex를 List로 저장 
			}
			
			Long[] expiryTimeArray = expiryTimeList.toArray(new Long[expiryTimeList.size()]);		//	리스트를 배열로 변환
			String[] messageIndexArray = messageIndexList.toArray(new String[messageIndexList.size()]);			//	리스트를 배열로 변환

			for (int i = 0; i < expiryTimeList.size(); i++) {							 
				selectedLong = expiryTimeArray[i];									

				//	deliveryTime Default value : 0
				if (selectedLong != expired) {									
					if (selectedLong == now || selectedLong < now) {
						//	////System.out.println(expiryTimeArray[i]);
						mmHeader = this.select_mmHeader(messageIndexArray[i]);
						String userNumber = mmHeader.to;

						boolean userOn;
						userOn = this.select_userOn(userNumber);
						if(userNumber.substring(0,4).equals("0101")){
							if (userOn == true) {
								////System.out.println("In DB_Managemnet : 현재 값은 삭제되어질  messageID입니다.");
								
								query = "UPDATE `mmdb`.`mmheader` SET `x_mms_expiry`='0' WHERE `mm_index`='" + mmHeader.messageIndex  +"'";
								stmt.executeUpdate(query);
								
								return mmHeader.messageIndex;
							} else{
								////System.out.println("In DB_Managemnet : 삭제시킬  값이 없습니다.");
							}
						}
					} else {
						////System.out.println("In DB_Managemnet : 삭제시킬  값이 없습니다.");
					}
				}
			}
			stmt.close();
			Conn.close();
		}catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}
		
	/*
	 * delete_mmHeader
	 * input: messageIndex
	 * output: void
	 * 
	 * 해당 messageIndex의 DB를 삭제
	 */
	public void delete_mmHeader(String _messageIndex) {
		
		String messageIndex = _messageIndex;
		String query = "delete  from mmHeader where mm_index = '" + messageIndex + "'";
		////System.out.println("In DB_Managemnet : DB log(delete_mmheader):" + query);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/MMdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(query);

			////System.out.println("In DB_Managemnet : delete_mmheader Success");
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * insert_TID
	 * input: tid, messageIndex
	 * output: void
	 * 
	 * tidb에 해당 tid,messageIndex를 삽입
	 */
	public void insert_TID(String _tid, String _messageIndex) {
		String tid = _tid;
		String messageIndex = _messageIndex;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			String query = "insert into mmdb.tidb values (?,?)";
			PreparedStatement prepared = Conn.prepareStatement(query);
			prepared.setString(1, tid);
			prepared.setString(2, messageIndex);
			prepared.executeUpdate();

			////System.out.println("In DB_Managemnet : insert_TID Success");
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * delete_TID
	 * input: tid
	 * output: void
	 * 
	 * 해당 tid를 키로 DB삭제
	 */

	public void delete_TID(String _tid) {
		String tid = _tid;
		String query = "delete  from tidb where tid = '" + tid + "'";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(query);

			////System.out.println("In DB_Managemnet : delete_Tid Success");
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * select_TID
	 * input: tid
	 * output: messageIndex
	 * 
	 * 해당 tid를 키로 해당 messageIndex를 리턴한다.
	 */
	
	public String select_TID(String _tid) {

		String tid = _tid;
		String messageIndex = null;
		String query = "select * from tidb where tid = " + "'" + tid + "'";
		////System.out.println("In DB_Managemnet : DB log (select_TID):" + query);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			////System.out.println("In DB_Managemnet : select_TID Success");
			
			while (rs.next()) {
				tid = rs.getString(1);
				messageIndex = rs.getString(2);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return messageIndex; 
	}

	/*
	 * select_userOn
	 * input: number
	 * output: user logon's status
	 * 
	 * 해당 번호의 유저가 logOn인지 확인
	 */
	public boolean select_userOn(String _number) {
		
		boolean userOn = false;
		String number = _number;
		String query = "select * from userdb where phone = " + "'" + number
				+ "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				userOn = rs.getBoolean(3);
				//System.out.println("In DB_Managemnet : DB log(selected Useron) Success : User's state is " + userOn );
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return userOn;
	}
	
	/*
	 * insert_userOn
	 * input: number, 설정하고자 하는 유저의 상태(true,false)
	 * output: void 
	 * 
	 * 해당 번호의 유저의 상태를 설정
	 */

	public void insert_userOn(String _number, boolean _useron) {

		String query = "UPDATE `mmdb`.`userdb` SET `UserOn`=" + _useron + " WHERE `phone`='" + _number + "'";
		//System.out.println("In DB_Managemnet : DB log(insert_userOn):" + query);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(query);

			//System.out.println("In DB_Managemnet : DB log(insert_userOn) Success");
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}	

	public void insert_userPreference(String _number,boolean usePng, boolean useJpg, int imagewidth, int imageheight,int imagesize){
		//String query = "UPDATE `mmdb`.`userdb` SET `UserOn`=" + _useron + " WHERE `phone`='" + _number + "'";
		
		String query = "UPDATE `mmdb`.`userdb` SET `png`="+usePng+", `jpg`="+useJpg+", `imagewidth`="+imagewidth+", `imageheight`="+imageheight+", `imagesize`="+imagesize+" WHERE `phone`='"+_number+"'";
		//UPDATE `mmdb`.`userdb` SET `png`=1, `jpg`=1, `imagewidth`=31, `imageheight`=31 WHERE `phone`='01010000001/TYPE=PLMN';
		//System.out.println("In DB_Managemnet : DB log(insert_userOn):" + query);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(query);

			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean getUsePng(String _number){
		
		String number = _number;
		boolean usePng = false;
		String query = "select * from userdb where phone = " + "'" + number + "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				usePng = rs.getBoolean(4);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return usePng;
	}
	public boolean getUseJng(String _number){
		
		String number = _number;
		boolean useJpg = false;
		String query = "select * from userdb where phone = " + "'" + number + "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				useJpg = rs.getBoolean(5);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return useJpg;
	}
	public int getImagewidth(String _number){
		String number = _number;
		int imagewidth = 0;
		String query = "select * from userdb where phone = " + "'" + number + "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				imagewidth = rs.getInt(6);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return imagewidth;
	}
	public int getImageheight(String _number){
		String number = _number;
		int imageheight = 0;
		String query = "select * from userdb where phone = " + "'" + number + "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				imageheight = rs.getInt(7);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return imageheight;
	}
	
	public int getImageSize(String _number){
		String number = _number;
		int imageSize = 0;
		String query = "select * from userdb where phone = " + "'" + number + "'";
		//System.out.println("In DB_Managemnet : DB log(select_userOn) :" + query);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				imageSize = rs.getInt(8);
			}
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return imageSize;
	}
	
	public void update_deliveryTime(String _messageIndex, Long number){
		String messageIndex = _messageIndex;
		
		try{
		Class.forName("com.mysql.jdbc.Driver");
		java.sql.Connection Conn;
		Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
		Statement stmt = Conn.createStatement();
		
		String query = "UPDATE `mmdb`.`mmheader` SET `x_mms_delivery_time`='"+ number + "' WHERE `mm_index`='" + messageIndex  +"'";

		stmt.executeUpdate(query);
		////System.out.println("In DB_Managemnet : DB Log:  update_deliveryTime Success");
		stmt.close();
		Conn.close();
		}catch(Exception e){
			e.getStackTrace();
		}
	}
	
	public void update_xMmsStatus(String _messageIndex, String _status){
		String messageIndex = _messageIndex;
		String status = _status;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			Statement stmt = Conn.createStatement();
			
			String query = "UPDATE `mmdb`.`mmheader` SET `x_mms_status`='"+ status + "' WHERE `mm_index`='" + messageIndex  +"'";

			stmt.executeUpdate(query);
			////System.out.println("In DB_Managemnet : DB Log: update_XMmsStatus Success");
			stmt.close();
			Conn.close();
			}catch(Exception e){
				e.getStackTrace();
			}
	}
	
	public void update_pushStatus(String _messageIndex, String _status){
		String messageIndex = _messageIndex;
		String status = _status;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			Statement stmt = Conn.createStatement();
			
			String query = "UPDATE `mmdb`.`mmheader` SET `push_status`='"+ status + "' WHERE `mm_index`='" + messageIndex  +"'";

			stmt.executeUpdate(query);
			////System.out.println("In DB_Managemnet : DB Log: update_pushStatus Success");
			stmt.close();
			Conn.close();
			}catch(Exception e){
				e.getStackTrace();
			}
	}
	
	public void update_xMmsReportAllowed(String _messageIndex , boolean _allowed){
		String messageIndex = _messageIndex;
		boolean allowed = _allowed;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			Statement stmt = Conn.createStatement();
			
			String query = "UPDATE `mmdb`.`mmheader` SET `x_mms_report_allowed`="+ allowed + " WHERE `mm_index`='" + messageIndex  +"'";

			////System.out.println(query);
			////System.out.println(query2);
			stmt.executeUpdate(query);
			////System.out.println("In DB_Managemnet : DB Log: update_xMmsReportAllowed Success");
			stmt.close();
			Conn.close();
			}catch(Exception e){
				e.getStackTrace();
			}
		
		
		
	}
	
	public void update_xMmsReadStatus(String _messageIndex, String _status){
		String messageIndex = _messageIndex;
		String status = _status;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			Statement stmt = Conn.createStatement();
			
			String query = "UPDATE `mmdb`.`mmheader` SET `x_mms_read_status`='"+ status + "' WHERE `mm_index`='" + messageIndex  +"'";			
			
			stmt.executeUpdate(query);
			////System.out.println("In DB_Managemnet : DB Log: update_xMmsReadStatus Success");
			
			stmt.close();
			Conn.close();
			}catch(Exception e){
				e.getStackTrace();
			}
	}
	
	public void update_xMmsAckRequest(String _messageIndex, boolean _ackRequest){
		String messageIndex = _messageIndex;
		boolean ackRequest = _ackRequest;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			Statement stmt = Conn.createStatement();
			
			String query = "UPDATE `mmdb`.`mmheader` SET `x_mms_ack_request`="+ ackRequest + " WHERE `mm_index`='" + messageIndex  +"'";			
			
			stmt.executeUpdate(query);
			////System.out.println("In DB_Managemnet : DB Log: update_xMmsReadStatus Success");
			
			stmt.close();
			Conn.close();
			}catch(Exception e){
				e.getStackTrace();
			}
	}
		
	public void insert_pdu(String _messageIndex, String _phone){
		String messageIndex = _messageIndex;
		String phone = _phone;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");
			String query = "insert into mmdb.pdudb values (?,?)";
			PreparedStatement prepared = Conn.prepareStatement(query);
			prepared.setString(1, messageIndex);
			prepared.setString(2, phone);
			prepared.executeUpdate();

			////System.out.println("In DB_Managemnet : insert_PDU Success");
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}
		
	
	public String[] select_pdu(String _phone){

		String phone = _phone;
		String[] messageIndexArray = null; 
		List<String> pduList = new ArrayList<String>();	
		

		String query = "select * from pdudb where phone = " + "'" + phone + "'";
		////System.out.println("In DB_Managemnet : DB log (select_pdu):" + query);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/mmdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			////System.out.println("In DB_Managemnet : select_messageIndexArray Success");
			
			
			while (rs.next()) {
				pduList.add(rs.getString(1));		
			}
			
			messageIndexArray = pduList.toArray(new String[pduList.size()]);		//	리스트를 배열로 변환

			
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return messageIndexArray; 
	}
	
	public void delete_pdu(String _phone){
		
		String phone = _phone;
		String query = "delete  from pdudb where phone = '" + phone + "'";
		////System.out.println("In DB_Managemnet : DB log(delete_pdu):" + query);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/MMdb?user=root&password=1q2w3e4r");

			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(query);

			//////System.out.println("In DB_Managemnet : delete_PDU Success");
			stmt.close();
			Conn.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}
}

