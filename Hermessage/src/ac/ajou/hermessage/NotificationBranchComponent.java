package ac.ajou.hermessage;

import java.util.Iterator;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationBranchComponent extends Activity// 일단 여기까지 통지에 대한 MM이
		// 와야함
		implements OnClickListener {
	public boolean isImmediate; // 즉시 수신 여부, 액티비티 호출시키는 부분에서 결정해줌
	HTTPProcessor httpProcessor; // HTTP Processor
	MM notificationIndMM; // 이번에 처리해야 할 통지
	String branch;
	Button branch1, branch2, branch3, branch4;
	TextView textViewSubject,textViewFrom,textViewDate,textViewExpiry,textViewSize;
	CheckBox isDeliveryReport;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notificationbranchcomponent);

		branch1 = (Button) findViewById(R.id.immediatereceive);
		branch2 = (Button) findViewById(R.id.defferedreceive);
		branch3 = (Button) findViewById(R.id.forward);
		branch4 = (Button) findViewById(R.id.reject);
		//textViewSubject = (TextView) findViewById(R.id.subjectInNotification);
		textViewFrom = (TextView) findViewById(R.id.fromInNotification);
		textViewExpiry = (TextView) findViewById(R.id.expiryInNotification);
		textViewSize = (TextView) findViewById(R.id.sizeInNotification);
		isDeliveryReport = (CheckBox) findViewById(R.id.deliveryReportInNotification);
		
		if(this==null)
		{
			Log.i("HERMESSAGE","ABNORMAL");
		}
		else
		{
			Log.i("HERMESSAGE","NORMAL");
		}
		branch1.setOnClickListener(this);//여기서 죽었음
		branch2.setOnClickListener(this);
		branch3.setOnClickListener(this);
		branch4.setOnClickListener(this);

		Intent intent = getIntent();

		notificationIndMM = (MM) intent.getSerializableExtra("mNotificationInd");
		
		if(notificationIndMM==null)
		{
			Log.e("HERMESSAGE","intent from mnotification false");
		}
		
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
	
		//textViewSubject.setText(notificationIndMM.subject);
		if(notificationIndMM.from.indexOf("/")!=-1)
			textViewFrom.setText(notificationIndMM.from.substring(0, notificationIndMM.from.indexOf("/")));
		else
			textViewFrom.setText(notificationIndMM.from);
		try
		{
			textViewExpiry.setText(formatter.format(notificationIndMM.x_mms_expiry));
		}catch(Exception e)
		{
			e.getStackTrace();
			Log.e("HERMESSAGE",e.getMessage());
		}
		textViewSize.setText(notificationIndMM.x_mms_message_size+"");
		isDeliveryReport.setChecked(notificationIndMM.x_mms_delivery_report);

		if (intent.getBooleanExtra("isImmediate",false)) {
			isImmediate = true;
		} else {
			isImmediate = false;
		}
		httpProcessor = new HTTPProcessor(this);
	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.immediatereceive:
			this.retrive();
			break;
		case R.id.defferedreceive:
			this.deffer(notificationIndMM);
			break;
		case R.id.forward:
			this.forward(notificationIndMM);
			break;
		case R.id.reject:
			this.rejection(notificationIndMM);
			break;
		default:
			break;
		}
	}
	public void retrive() {
		MM retrievedMM;
		// GET을 요청함 리스폰스 MM을 전달받음
		if(notificationIndMM==null)
		{
			Log.w("HERMESSAGE","NOTIFICATIONINDMM IS NULL");
		}
		
		retrievedMM = httpProcessor.httpReceive(notificationIndMM.x_mms_content_location);
		// GET으로 정상적으로 받아왔다면 받아온것에 대한 응답을 만들어서 보냄
		Log.i("HERMESSAGE","RETRIVED MM IN NOTIBR");
		//Log.i("HERMESSAGE","RETRIVED MM IN NOTIBR"+retrievedMM.subject);
		
		
		Log.i("HERMESSAGE","생성하는 MM");
		
		// Immediate라면 정상적으로 수신한것에 대해서 M-notifyResp.ind를 만들어서 보내야함
		if (isImmediate) {
			Log.i("HERMESSAGE","통지 받자 마자 수신 이므로 notifyResp");
			MM notifyrespindMM = new MM();
			notifyrespindMM.x_mms_message_type = "m-notifyresp-ind";
			notifyrespindMM.x_mms_transaction_ID = notificationIndMM.x_mms_transaction_ID;
			notifyrespindMM.x_mms_mms_version = "1.3";
			notifyrespindMM.x_mms_status = "Retrieved";
			notifyrespindMM.x_mms_report_allowed = isDeliveryReport.isChecked();
			// 리포트 보낼지 말지 옵션은 UI로 부터 전달받아야함
			httpProcessor.httpSend(notifyrespindMM);
		}

		else//Immediate가 아니라면 M-acknowledge.ind를 만들어서 보내야함
		{
			//통지 보관함을 통해서 온 경우임
			Log.i("HERMESSAGE","통지함을 통한 수신 이므로 acknowledge");
			MM acknowledgeindMM = new MM();
			acknowledgeindMM.x_mms_message_type = "m-acknowledge-ind";
			acknowledgeindMM.x_mms_transaction_ID = retrievedMM.x_mms_transaction_ID;
			acknowledgeindMM.x_mms_mms_version = "1.3";
			// 여기 리포트 allowed 어디서 옵션 걸어서 처리를 해 줘야하나? 즉시 수신 할때
			// 이거도 파라메터로 받아야 할라나?
			acknowledgeindMM.x_mms_report_allowed = isDeliveryReport.isChecked();
			httpProcessor.httpSend(acknowledgeindMM);
			
			//통지보관함에서 삭제해야함
			SQLiteDatabase db = new WordDBHelper(this).getWritableDatabase();
			db.delete("NotificationMessage", "MessageID = '" + notificationIndMM.x_mms_content_location +"' ", null);
			db.close();
		}

		// 통지보관함에 저장안함

		// 수신한 MM을 DB에 저장 
		

		/*
		 * 수신한 MM을 즉시 화면에 표시
		 */
		Intent intent2 = new Intent(NotificationBranchComponent.this,ShowRecvMessage.class);
		intent2.putExtra("mRetrievedConf", retrievedMM);
		PendingIntent p = PendingIntent.getActivity(this, 0, intent2, 0);
		try
		{
			p.send();
		}
		catch(Exception e)
		{
			e.getStackTrace();
			Log.i("HERMESSAGE","SHOW RECV MESSAGE FAIL");
		}
		
		//startActivityForResult(intent2, 0);
		this.finish();
		
	}

	public void rejection(MM mnotificationInd) {
		MM notifyrespindMM = new MM();
		notifyrespindMM.x_mms_message_type = "m-notifyresp-ind";
		notifyrespindMM.x_mms_transaction_ID = mnotificationInd.x_mms_transaction_ID;
		notifyrespindMM.x_mms_mms_version = "1.3";
		notifyrespindMM.x_mms_status = "Rejected";
		notifyrespindMM.x_mms_report_allowed = isDeliveryReport.isChecked();
		// 이건 화면에서 전달받아야함
		httpProcessor.httpSend(notifyrespindMM);
		this.finish();
	}

	
	public void deffer(MM mnotificationInd) {
		// notifyresp.ind 보냄 status는 Deferred
		MM notifyrespindMM = new MM();
		notifyrespindMM.x_mms_message_type = "m-notifyresp-ind";
		notifyrespindMM.x_mms_transaction_ID = mnotificationInd.x_mms_transaction_ID;
		notifyrespindMM.x_mms_mms_version = "1.3";
		notifyrespindMM.x_mms_status = "Deferred";
		notifyrespindMM.x_mms_report_allowed = isDeliveryReport.isChecked();
		// 이건 화면에서 전달받아야함

		/*
		 * DB에 저장하는 과정
		 */
		SQLiteDatabase db = new WordDBHelper(this).getWritableDatabase();
		ContentValues row = new ContentValues();
		
		row.put("MessageID", mnotificationInd.x_mms_content_location);
		if(mnotificationInd.from.indexOf("/")!=-1)
			row.put("_From", mnotificationInd.from.substring(0, mnotificationInd.from.indexOf("/")));
		else
			row.put("_From", mnotificationInd.from);
		
		row.put("DeliveryStateReq",(mnotificationInd.x_mms_delivery_report)?"Y":"N");
		
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",java.util.Locale.US);	            
		String sDate = formatter.format(mnotificationInd.x_mms_expiry);
		row.put("Expiry", sDate);
		
		row.put("Size", mnotificationInd.x_mms_message_size+"");
		row.put("TransactionID", mnotificationInd.x_mms_transaction_ID);
		//DB에러 나던거 size 넣어야 하는것일듯 
		
		db.insert("NotificationMessage", null, row);
		db.close();
		
		// 일단 받은 통지를 통지보관함에 저장해야함
		httpProcessor.httpSend(notifyrespindMM);
		// 통지보관함에 저장
		this.finish();
	}

	public void forward(MM mnotificationInd) {
		MM notifyrespindMM = new MM();
		notifyrespindMM.x_mms_message_type = "m-notifyresp-ind";
		notifyrespindMM.x_mms_transaction_ID = mnotificationInd.x_mms_transaction_ID;
		notifyrespindMM.x_mms_mms_version = "1.3";
		notifyrespindMM.x_mms_status = "Deferred";
		notifyrespindMM.x_mms_report_allowed = isDeliveryReport.isChecked();
		httpProcessor.httpSend(notifyrespindMM);
		// 이건 화면에서 전달받아야함
		
		MM forwardReqMM = new MM();
		forwardReqMM.x_mms_message_type = "m-forward-req";
		forwardReqMM.x_mms_content_location = notificationIndMM.x_mms_content_location;
		
		Intent intent = new Intent(NotificationBranchComponent.this, RecipientSubmit.class);
		intent.putExtra("mm", forwardReqMM);
		startActivityForResult(intent, 10);
		
		//전달한거 통지보관함에 저장 안함
		this.finish();
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