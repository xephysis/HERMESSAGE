package ac.ajou.hermessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowRecvMessage extends Activity implements OnClickListener {

	public boolean isImmediate; // 즉시 수신 여부, 액티비티 호출시키는 부분에서 결정해줌
	HTTPProcessor httpProcessor; // HTTP Processor
	boolean reportAllowed; // 통지에 대한 확인 리포트에 응답을 요청하는지 여부

	MM retrievedMM; // 이번에 처리해야 할 통지
	Button close;
	TextView textViewSubject, textViewFrom, textViewDate, textViewContext;
	CheckBox isReadReport;
	LinearLayout fileListLayout;
	ArrayList<TextView> fileList = new ArrayList<TextView>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showrecvmessage);

		close = (Button) findViewById(R.id.closeInShowRecvMessage);

		//textViewSubject = (TextView) findViewById(R.id.subjectInShowRecvMessage);
		textViewFrom = (TextView) findViewById(R.id.fromInShowRecvMessage);
		textViewDate = (TextView) findViewById(R.id.dateInShowRecvMessage);
		textViewContext = (TextView) findViewById(R.id.contentInShowRecvMessage);
		textViewDate = (TextView) findViewById(R.id.dateInShowRecvMessage);
		isReadReport = (CheckBox) findViewById(R.id.readReportInShowRecvMessage);
		close = (Button) findViewById(R.id.closeInShowRecvMessage);
		fileListLayout =  (LinearLayout) findViewById(R.id.attachfilelistlinearlayout);

		if (this == null) {
			Log.i("HERMESSAGE", "ABNORMAL state in show recv");
		} else {
			Log.i("HERMESSAGE", "NORMAL state in show recv");
		}

		close.setOnClickListener(this);// 닫기 버튼 누르면 바로 M-read-Rec.ind 보내야함

		Intent intent = getIntent();

		retrievedMM = (MM) intent.getSerializableExtra("mRetrievedConf");

		if (retrievedMM == null) {
			Log.e("HERMESSAGE", "intent from retrieved MM false");
		}
		
		isReadReport.setEnabled(retrievedMM.x_mms_read_report);

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"EEE MMM dd HH:mm:ss yyyy", java.util.Locale.US);

		//textViewSubject.setText(retrievedMM.subject);
		textViewFrom.setText(retrievedMM.from.substring(0, retrievedMM.from.indexOf("/")));
		try {
			textViewDate.setText(formatter.format(retrievedMM.date));
		} catch (Exception e) {
			e.getStackTrace();
		}

		if (retrievedMM.messageContent == null) {
			textViewContext.setText("내용이 없는 메시지 입니다.");
		} else {
			textViewContext.setText(retrievedMM.messageContent);
		}
		isReadReport.setChecked(retrievedMM.x_mms_read_report);

		if (retrievedMM.attachFilePath.size() != 0) {
			Iterator<String> itr = retrievedMM.attachFilePath.iterator();
			while (itr.hasNext()) {
				final String filepath = itr.next();
				/*
				 * 여기서만 사용하기 때문에 파이널로 사용
				 */
				File attachFile = new File(filepath);

				if (attachFile.exists()) {
					long L = attachFile.length();
					Log.i("HERMESSAGE", "첨부된 파일 용량 : " + L + " bytes : "
							+ attachFile.getAbsoluteFile());
					TextView file = new TextView(this);
					file.setText(filepath.substring(filepath.lastIndexOf("/") + 1)
							+ " : " + L + " bytes");
					file.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Log.i("HERMESSAGE", filepath);
							/*
							 * 정상적으로만 뜨면 화면 보여줘도 됨
							 */
						}
					});

					fileListLayout.addView(file);
					/*
					 * 여기에 온 클릭 리스너 추가?
					 */
				}
			}
		}
		httpProcessor = new HTTPProcessor(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.closeInShowRecvMessage:
			this.close();
			break;
		default:
			break;
		}
	}

	public void close() {
		if (isReadReport.isChecked())// m-read-rec-ind 보내야함
		{
			MM readrecind = new MM();
			readrecind.x_mms_message_type = "m-read-rec-ind";
			readrecind.x_mms_mms_version = "1.3";
			readrecind.messageID = retrievedMM.messageID;
			readrecind.to = retrievedMM.from;
			readrecind.from = retrievedMM.to;
			readrecind.date = new Date();
			readrecind.x_mms_read_status = "Read";
			httpProcessor.httpSend(readrecind);
		}
		// 여기서 받은 MM DB에 저장은 이미 되어 있음
		// 그리고 액티비티 클로즈
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
