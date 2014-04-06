package ac.ajou.hermessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Typeface;

public class Hermessage extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	static Context mContext;
	Button messageMake, mmBox, addressConfig;

	final static int ACT_EDIT = 0;

	public static String ClientID;
	public static boolean isAcceptJpg;
	public static boolean isAcceptPng;
	public static int maxWidth;
	public static int maxHeight;
	public static int maxSize;

	Socket socket = null;
	BufferedReader bufferedReader = null;
	PrintWriter printerWriter = null;

	MM mm = new MM();
	ComponentName mService;

	NotificationManager mNotiManager;
	public static ArrayList<Activity> at = new ArrayList<Activity>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences mainPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		ClientID = mainPreference.getString("MyPhoneNumber", "01010000001")
				+ "/TYPE=PLMN";
		isAcceptJpg = mainPreference.getBoolean("ChoiceJPG", true);
		isAcceptPng = mainPreference.getBoolean("ChoicePNG", true);
		maxWidth = mainPreference.getInt("MaxWidth", 50);
		maxHeight = mainPreference.getInt("MaxHeight", 50);
		maxSize = mainPreference.getInt("MaxSize", 5242880);

		mContext = this;
		messageMake = (Button) findViewById(R.id.MessageMake);
		mmBox = (Button) findViewById(R.id.MMBox);
		addressConfig = (Button) findViewById(R.id.AddressConfig);

		TextView title = (TextView) findViewById(R.id.Hermessage);
		Typeface face = Typeface.createFromAsset(getAssets(), "ESOP.TTF");
		title.setTypeface(face);
		title.setText("Hermessage");
		title.setTextSize(50);
		messageMake.setOnClickListener(this);
		mmBox.setOnClickListener(this);
		addressConfig.setOnClickListener(this);

		TextView text1 = (TextView) findViewById(R.id.textView1);
		TextView text2 = (TextView) findViewById(R.id.textView2);
		TextView text3 = (TextView) findViewById(R.id.textView3);
		text1.setTypeface(face);
		text2.setTypeface(face);
		text3.setTypeface(face);

		new DoComplecatedJob().execute("111");
	}

	public void temp(Activity activity) {
		at.add(activity);
	}

	public void clearActivity() {
		for (int i = 0; i < at.size(); i++) {
			at.get(i).finish(); // List가 Static 이므로, Class명.변수명.get으로
								// 접근
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "환경 설정");
		menu.add(0, 2, 0, "강제 종료");
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent intent10 = new Intent(Hermessage.this, Preference.class);
			startActivity(intent10);
			break;
		case 2:
			System.exit(0);
			break;

		default:
			break;
		}

		return (itemCallback(item) || super.onOptionsItemSelected(item));
	}

	private boolean itemCallback(MenuItem item) {

		return false;
	}

	private class DoComplecatedJob extends AsyncTask<String, Integer, Long> {

		// 이곳에 포함된 code는 AsyncTask가 execute 되자 마자 UI 스레드에서 실행됨.
		// 작업 시작을 UI에 표현하거나
		// background 작업을 위한 ProgressBar를 보여 주는 등의 코드를 작성.
		@Override
		protected void onPreExecute() {
			Log.i("test start thread", "test start thread");
			super.onPreExecute();
		}

		// UI 스레드에서 AsynchTask객체.execute(...) 명령으로 실행되는 callback
		@Override
		protected Long doInBackground(String... strData) {
			Log.i("HERMESSAGE", "PUSH LISTEN START");

			while (true) {
				try {
					socket = new Socket("210.107.196.190", 8082);

					bufferedReader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()), 8 * 1024);// push in
																// socket
					printerWriter = new PrintWriter(socket.getOutputStream(),
							true);// connection send socket

					String uaRegister = "phone=" + ClientID;
					if (isAcceptJpg)
						uaRegister += ";jpg=yes";
					else
						uaRegister += ";jpg=no";
					if (isAcceptPng)
						uaRegister += ";png=yes";
					else
						uaRegister += ";png=no";
					uaRegister = uaRegister + ";width=" + maxWidth + ";height="
							+ maxHeight + ";size=" + maxSize;

					printerWriter.println(uaRegister);

					String readline;

					Properties pushEntity = new Properties();// push property

					// receiving push MM
					while (true) {
						pushEntity.clear();
						/*
						 * 푸시 두번뜨는거 아마도 이쪽 문제 일꺼 같음
						 */
						while ((readline = bufferedReader.readLine()) != null) {
							Log.i("HERMESSAGE", "RECEIVEPUSH");
							int positionOfDelimeter = readline.indexOf(':');
							if (positionOfDelimeter >= 0) {
								pushEntity
										.put(readline.substring(0,
												positionOfDelimeter).trim(),
												readline.substring(
														positionOfDelimeter + 1)
														.trim());
								System.out.println("KEY : "
										+ readline.substring(0,
												positionOfDelimeter).trim());
								System.out
										.println("VALUE :"
												+ readline
														.substring(
																positionOfDelimeter + 1)
														.trim());
							} else// PUSH 구분하는 공백이 들어올 경우
							{
								if (readline.equalsIgnoreCase("KeepAlive")) {
									printerWriter.println("Alive");
									Log.i("HERMESSAGE", "KEEPALIVE");
								} else {
									break;// END while
								}
							}
						}
						if (pushEntity.size() > 1)
							pushParser(pushEntity);

						Thread.sleep(5000);// wait 5seconds
					}
				} catch (Exception e) {
					// e.getStackTrace();
				}
			}
		}

		// onInBackground(...)에서 publishProgress(...)를 사용하면
		// 자동 호출되는 callback으로
		// 이곳에서 ProgressBar를 증가 시키고, text 정보를 update하는 등의
		// background 작업 진행 상황을 UI에 표현함.
		// (예제에서는 UI스레드의 ProgressBar를 update 함)
		@Override
		protected void onProgressUpdate(Integer... progress) {
			// progressBar.setProgress(progress[0]);
		}

		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			Log.i("test 2", "test2");
		}

		// AsyncTask.cancel(boolean) 메소드가 true 인자로
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은 여기에 구현.

		protected void onCancelled() {
			super.onCancelled();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.MessageMake:

			Intent intent = new Intent(Hermessage.this, MMMake.class);
			intent.putExtra("mm", mm);
			startActivityForResult(intent, ACT_EDIT);
			break;
		case R.id.MMBox:
			// mmbox
			Intent intent2 = new Intent(Hermessage.this, MMBox.class);
			startActivityForResult(intent2, ACT_EDIT);
			break;

		case R.id.AddressConfig:
			Intent intent3 = new Intent(Hermessage.this, AddressConfig.class);
			startActivityForResult(intent3, ACT_EDIT);

			break;
		// address configu
		default:
			break;
		}
	}

	public void pushParser(Properties pushEntity) throws Exception {
		Log.i("HERMESSAGE", "PUSHPARSINGSTART");
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss yyyy", java.util.Locale.US);

		if (pushEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase(
				"m-notification-ind")) {
			Log.i("HERMESSAGE", "M-Notification-ind");

			MM mNotificationInd = new MM();
			mNotificationInd.x_mms_message_type = pushEntity
					.getProperty("X-Mms-Message-Type");
			mNotificationInd.x_mms_transaction_ID = pushEntity
					.getProperty("X-Mms-Transaction-ID");
			mNotificationInd.x_mms_mms_version = pushEntity
					.getProperty("X-Mms-MMS-Version");
			mNotificationInd.from = pushEntity.getProperty("From");
			// mNotificationInd.subject = pushEntity.getProperty("Subject");
			mNotificationInd.x_mms_delivery_report = (pushEntity
					.getProperty("X-Mms-Delivery-Report")
					.equalsIgnoreCase("Yes")) ? true : false;
			mNotificationInd.x_mms_message_class = pushEntity
					.getProperty("X-Mms-Message-Class");
			mNotificationInd.x_mms_message_size = Integer.parseInt(pushEntity
					.getProperty("X-Mms-Message-Size"));

			// 절대 시간이면 Date Type 으로 올태고 상대시간...은 외부랑 할때만 일단 이건 절대시간으로
			mNotificationInd.x_mms_expiry = formatter.parse(pushEntity
					.getProperty("X-Mms-Expiry"));
			mNotificationInd.x_mms_content_location = pushEntity
					.getProperty("X-Mms-Content-Location");

			/*
			 * 푸시 받자마자 바로 통지에 대한 선택 다이얼 로그 띄워줌
			 */
			Intent intent2 = new Intent(Hermessage.this,
					NotificationBranchComponent.class);
			intent2.putExtra("mNotificationInd", mNotificationInd);
			intent2.putExtra("isImmediate", true);
			startActivityForResult(intent2, 0);

		} else if (pushEntity.getProperty("X-Mms-Message-Type")
				.equalsIgnoreCase("m-delivery-ind")) {

			Log.i("HERMESSAGE", "M-delivery-ind");
			MM mDiliveryInd = new MM();
			mDiliveryInd.x_mms_message_type = pushEntity
					.getProperty("X-Mms-Message-Type");
			mDiliveryInd.x_mms_mms_version = pushEntity
					.getProperty("X-Mms-MMS-Version");
			mDiliveryInd.messageID = pushEntity.getProperty("Message-ID");
			mDiliveryInd.to = pushEntity.getProperty("To");
			mDiliveryInd.date = formatter.parse(pushEntity.getProperty("Date"));
			mDiliveryInd.x_mms_status = pushEntity.getProperty("X-Mms-Status");

			Intent intent2 = new Intent(Hermessage.this, ShowReport.class);
			intent2.putExtra("pushedMM", mDiliveryInd);
			startActivityForResult(intent2, 0);

		}

		else if (pushEntity.getProperty("X-Mms-Message-Type").equalsIgnoreCase(
				"m-read-orig-ind")) {
			Log.i("HERMESSAGE", "M-read-orig-ind");
			MM mReadOrigInd = new MM();
			mReadOrigInd.x_mms_message_type = pushEntity
					.getProperty("X-Mms-Message-Type");
			mReadOrigInd.x_mms_mms_version = pushEntity
					.getProperty("X-Mms-MMS-Version");
			mReadOrigInd.messageID = pushEntity.getProperty("Message-ID");
			mReadOrigInd.to = pushEntity.getProperty("To");
			mReadOrigInd.from = pushEntity.getProperty("From");
			mReadOrigInd.date = formatter.parse(pushEntity.getProperty("Date"));
			mReadOrigInd.x_mms_read_status = pushEntity
					.getProperty("X-Mms-Read-Status");

			Intent intent2 = new Intent(Hermessage.this, ShowReport.class);
			intent2.putExtra("pushedMM", mReadOrigInd);
			startActivityForResult(intent2, 0);
		} else {
			// 오류처리
		}
		Log.i("HERMESSAGE", "PUSH PARSING END");
	}

}