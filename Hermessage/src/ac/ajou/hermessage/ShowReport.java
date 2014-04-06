package ac.ajou.hermessage;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ShowReport extends Activity implements OnClickListener {
	
	MM pushedMM; // 이번에 처리해야 할 통지
	Button close;
	TextView pushedMsgInShowReport;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showreport);

		close = (Button) findViewById(R.id.closeinshowreport);
		pushedMsgInShowReport = (TextView) findViewById(R.id.pushedmsginshowreport);
	
		
		if(this==null)
		{
			Log.i("HERMESSAGE","ABNORMAL state in show report");
		}
		else
		{
			Log.i("HERMESSAGE","NORMAL state in show report");
		}
		
		close.setOnClickListener(this);//닫기 버튼 누르면 바로 M-read-Rec.ind 보내야함
				
		Intent intent = getIntent();
		pushedMM = (MM) intent.getSerializableExtra("pushedMM");
		
		if(pushedMM==null)
		{
			Log.e("HERMESSAGE","intent from retrieved MM false");
		}
		
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
	
		
		if(pushedMM.x_mms_message_type.equalsIgnoreCase("m-read-orig-ind"))
		{
			String receiver = pushedMM.from.substring(0,pushedMM.from.indexOf("/")) ;
			
			pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n메시지를 읽으셨습니다.");
		}
		else if(pushedMM.x_mms_message_type.equalsIgnoreCase("m-delivery-ind"))
		{	
			
			String receiver = pushedMM.to.substring(0, pushedMM.to.indexOf("/"));
			
			if(pushedMM.x_mms_status.equalsIgnoreCase("Expired"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n전송한 메시지가 수신되지 않고 만료되었습니다.");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n전송한 메시지가 수신되지 않고 만료되었습니다.");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Retrieved"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n통지(메시지)를 수신하셨습니다.");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n전통지(메시지)를 수신하셨습니다.");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Rejected"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n메시지를 거절하셨습니다.");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n메시지를 거절하셨습니다.");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Deferred"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n통지를 수신하셨습니다.(연기)");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n통지를 수신하셨습니다.(연기)");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Forwarded"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께서\r\n메시지를 전달하셨습니다.");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n메시지를 전달하셨습니다.");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Unreachable"))
			{
				pushedMsgInShowReport.setText(formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n메시지를 전달하지 못했습니다.");
				Log.w("HERMESSAGE","IN REPORT"+formatter.format(pushedMM.date)+"\r\n"+receiver+"님께\r\n메시지를 전달하지 못했습니다.");
			}
			else
			{
				pushedMsgInShowReport.setText("내용을 판별할 수 없는 X-Mms-Status 입니다"+pushedMM.x_mms_status);
				Log.i("HERMESSAGE","딜리버리 리포트 내용 판별 실패");
				Log.i("HERMESSAGE","내용"+pushedMM.x_mms_status);
			}
		}
		else
		{
			Log.i("HERMESSAGE","푸시 종류 판별 실패");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.closeinshowreport:
			this.close();
			break;
		default:
			break;
		}
	}
	public void close()
	{
		/*
		 * 딜리버리 혹은 리드 리포트 내용을 토대로 발신함 DB에 업데이트 해야함
		 */
		SQLiteDatabase db = new WordDBHelper(this).getWritableDatabase();
		ContentValues row = new ContentValues();
		
		if(pushedMM.x_mms_message_type.equalsIgnoreCase("m-read-orig-ind"))
		{
			row.put("ReadState","Y");
			Log.i("HERMESSAGE",pushedMM.from.substring(0,pushedMM.from.indexOf("/")));
			Log.i("HERMESSAGE",pushedMM.messageID);
			db.update("SendMessage", row, " MessageID= \"" +pushedMM.messageID+"\" AND Receiver=\""+pushedMM.from.substring(0, pushedMM.from.indexOf("/"))+"\"", null);
		}
		else if(pushedMM.x_mms_message_type.equalsIgnoreCase("m-delivery-ind"))
		{	
			Log.i("HERMESSAGE",pushedMM.to.substring(0,pushedMM.to.indexOf("/")));
			
			if(pushedMM.x_mms_status.equalsIgnoreCase("Expired"))
			{
				row.put("RejectState","Y");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Retrieved"))
			{
				row.put("DeliveryState","Y");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Rejected"))
			{
				row.put("RejectState","Y");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Deferred"))
			{
				row.put("DeliveryState","Y");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Forwarded"))
			{
				row.put("DeliveryState","Y");
			}
			else if(pushedMM.x_mms_status.equalsIgnoreCase("Unreachable"))
			{
				row.put("RejectState","Y");
			}
			else
			{
				Log.i("HERMESSAGE","딜리버리 리포트 내용 판별 실패");
			}
			db.update("SendMessage", row, " MessageID= \"" +pushedMM.messageID.trim() +"\" AND Receiver=\""+pushedMM.to.substring(0, pushedMM.to.indexOf("/"))+"\"".trim(), null);
		}
		else
		{
			Log.i("HERMESSAGE","푸시 종류 판별 실패");
		}
		
		db.close();
		Log.i("HEREMESSAGE","쇼 리포트 액티비티 종료");
		this.finish();
		
		// 받은 통지에 대해 DB에 업데이트
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
