package ac.ajou.hermessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//수신자 지정, 설정 지정 액티비티
public class RecipientSubmit extends Activity implements OnClickListener {

	Context mContext;
	String subject, content;
	ArrayList<String> path;
	String receiver;
	boolean deliveryState, readState;
	MM mm;
	SimpleDateFormat formatter = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss yyyy", java.util.Locale.US);
	Date date;
	final static int ACT_EDIT = 0;
	EditText receiveText;
	CheckBox deliverycheck, readcheck;
	Button addressBookReference, selectinput, messageDraft, messageSubmit,
			reservationSetting, expirySetting;
	TextView reservationDate, expiryDate;
	ScrollView scrollView;
	java.util.Date reservationDateTime = new java.util.Date();
	java.util.Date expiryDateTime = new java.util.Date();
	WordDBHelper mHelper;
	int selectyear, selectmonth, selectday, selecthour, selectminute;
	Calendar calDateTime = Calendar.getInstance();
	ArrayList<String> empty = new ArrayList<String>();

	ArrayList<String> totalAddress = new ArrayList<String>();
	Boolean isReservation = false;

	int addressCount = 0;
	int addressValueCount = 0;
	int addressEditCount;
	int choiceTime = 0;

	LinearLayout linearEditText, linearButton;

	Button[] delButton = new Button[100];
	EditText[] addressEdit = new EditText[100];

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receiversubmit);

		SharedPreferences mainPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		mContext = this;

		mHelper = new WordDBHelper(this);
		expiryDateTime.setMonth(7);

		receiveText = (EditText) findViewById(R.id.receiveText);
		deliverycheck = (CheckBox) findViewById(R.id.deliverycheck);
		readcheck = (CheckBox) findViewById(R.id.readcheck);
		selectinput = (Button) findViewById(R.id.plusaddress);
		addressBookReference = (Button) findViewById(R.id.addressBookReference);
		messageDraft = (Button) findViewById(R.id.messageDraft);
		messageSubmit = (Button) findViewById(R.id.messageSubmit);
		reservationSetting = (Button) findViewById(R.id.reservationSetting);
		expirySetting = (Button) findViewById(R.id.expirySetting);
		reservationDate = (TextView) findViewById(R.id.reservationdate);
		expiryDate = (TextView) findViewById(R.id.expirydate);

		scrollView = (ScrollView) findViewById(R.id.scrollAddress);
		Intent intent = getIntent();

		deliverycheck.setChecked(mainPreference.getBoolean("SendDelivery",
				false));
		readcheck.setChecked(mainPreference.getBoolean("SendRead", false));

		mm = (MM) intent.getSerializableExtra("mm");

		linearEditText = (LinearLayout) findViewById(R.id.linearLayout4);
		linearButton = (LinearLayout) findViewById(R.id.linearLayout9);

		receiveText.setText(mm.to);
		content = mm.messageContent;
		path = mm.attachFilePath;
		// 현재 첨부파일은 하나라고 가정
		selectinput.setOnClickListener(this);
		addressBookReference.setOnClickListener(this);
		messageDraft.setOnClickListener(this);
		messageSubmit.setOnClickListener(this);
		reservationSetting.setOnClickListener(this);
		expirySetting.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "환경 설정");
		menu.add(0, 2, 0, "보관함");
		menu.add(0, 3, 0, "주소록 관리");

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences mainPreference = PreferenceManager
				.getDefaultSharedPreferences(this);
		deliverycheck.setChecked(mainPreference.getBoolean("SendDelivery",
				false));
		readcheck.setChecked(mainPreference.getBoolean("SendRead", false));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent goPreference = new Intent(RecipientSubmit.this,
					Preference.class);
			startActivity(goPreference);
			break;
		case 2:
			Intent goBox = new Intent(RecipientSubmit.this, MMBox.class);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goBox);
			break;
		case 3:
			Intent goAddress = new Intent(RecipientSubmit.this,
					AddressConfig.class);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goAddress);
			break;
		default:
			break;
		}

		return (itemCallback(item) || super.onOptionsItemSelected(item));
	}

	private boolean itemCallback(MenuItem item) {

		return false;
	}

	@Override
	public void onClick(View v) {
		SQLiteDatabase db;
		ContentValues row;
		switch (v.getId()) {
		case R.id.plusaddress:// 주소록 추가 버튼

			if (addressEditCount > 19) {
				Toast.makeText(this, "최대 동시 송신가능 숫자는 20명입니다",
						Toast.LENGTH_SHORT).show();
				return;
			}

			addressEdit[addressCount] = new EditText(this);
			addressEdit[addressCount].setHint("주소를 입력하세요");
			addressEdit[addressCount].setTextColor(0xFF000000);
			addressEdit[addressCount].setId(addressCount);
			addressEdit[addressCount]
					.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
			linearEditText.addView(addressEdit[addressCount]);
			addressEdit[addressCount].setTag("" + addressCount);
			// addressEdit[addressCount].setText("" + addressCount);

			delButton[addressCount] = new Button(this);

			delButton[addressCount]
					.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
			// delButton[addressCount].setText("삭제");
			delButton[addressCount].setBackgroundResource(R.drawable.delete);
			linearButton.addView(delButton[addressCount]);
			delButton[addressCount].setTag("" + addressCount);

			delButton[addressCount]
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Log.d("kdw getTag()",
									"" + Integer.parseInt((String) v.getTag()));

							linearButton.removeView(delButton[Integer
									.parseInt((String) v.getTag())]);
							linearEditText.removeView(addressEdit[Integer
									.parseInt((String) v.getTag())]);
							// delButton[Integer.parseInt((String) v.getTag())]
							// .setTag("delete");
							addressEdit[Integer.parseInt((String) v.getTag())]
									.setTag("delete");
							addressEditCount--;
							// addressCount--;

						}
					});

			addressCount++;
			addressEditCount++;

			break;
		case R.id.addressBookReference:// ////////주소록 참조하는 부분

			Intent intent2 = new Intent(RecipientSubmit.this,
					AddressReference.class);

			// //////addressValueCount은 실제 입력된 주소의 개수
			// /////addressCount는 입력칸의 개수
			empty.clear();
			Log.i("test1", addressCount + "");
			Log.i("test1", addressEditCount + "");
			addressValueCount = 0;
			for (int i = 0; i < addressCount; i++) {
				if (addressEdit[i].getText().toString().equals("")) {// ////빈 칸의
																		// 개수
					Log.i("test", "test");
					addressValueCount++;
					Log.i("tag", addressEdit[i].getTag() + "");
					empty.add(addressEdit[i].getTag() + "");
				}
			}

			intent2.putExtra("addresscount", addressCount + 1);
			startActivityForResult(intent2, 11);

			break;
		case R.id.messageDraft:// ///////임시보관함에 저장하는 부분

			Log.i("test", "test");
			db = mHelper.getWritableDatabase();
			deliveryState = deliverycheck.isChecked();
			readState = readcheck.isChecked();
			// insert
			row = new ContentValues();

			row.put("Receiver", receiveText.getText().toString());
			// row.put("Subject", subject);
			row.put("Content", content);
			row.put("DeliveryState", deliveryState);
			row.put("ReadState", readState);
			row.put("ContentType", deliveryState);

			// row.put("LastEditDate", value)
			GregorianCalendar today = new GregorianCalendar();

			int year = today.get(today.YEAR);
			int month = today.get(today.MONTH) + 1;
			int yoil = today.get(today.DAY_OF_MONTH);

			GregorianCalendar gc = new GregorianCalendar();

			System.out.println(gc.get(Calendar.YEAR));
			System.out.println(String.valueOf(gc.get(Calendar.MONTH) + 1));
			System.out.println(gc.get(Calendar.DATE));
			System.out.println(gc.get(Calendar.DAY_OF_MONTH));
			System.out.println(gc.get(Calendar.HOUR_OF_DAY));
			System.out.println(gc.get(Calendar.MINUTE));

			String nowTime2 = gc.get(Calendar.YEAR) + "년 "
					+ String.valueOf(gc.get(Calendar.MONTH) + 1) + "월 "
					+ gc.get(Calendar.DATE) + "일 "
					+ gc.get(Calendar.HOUR_OF_DAY) + "시 "
					+ gc.get(Calendar.MINUTE) + "분";

			row.put("LastEditDate", nowTime2);
			String attachedFilePath = "";
			for (int numOfFile = path.size(); numOfFile > 0; numOfFile--) {
				attachedFilePath += path.get(numOfFile);
			}
			row.put("ContentLocation", attachedFilePath);

			db.insert("DraftMessage", null, row);

			mHelper.close();
			db.close();
			Toast.makeText(this, "임시보관함에 저장되었습니다", Toast.LENGTH_SHORT).show();
			break;

		case R.id.messageSubmit:// /////////메시지 송신하는 부분

			if (receiveText.getText().toString().equals("")) {
				if (totalAddress.size() == 0) {
					Toast.makeText(this, "수신자를 지정해 주세요", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			totalAddress.add(receiveText.getText().toString());// ///기본 수신자 지정

			Log.i("test0", String.valueOf(addressCount));
			Log.i("test", totalAddress.get(0));
			for (int i = 0; i < addressCount; i++) {
				if (!addressEdit[i].getTag().equals("delete")) {
					totalAddress.add(addressEdit[i].getText().toString());
				}
			}
			for (int j = 0; j < totalAddress.size(); j++) {
				Log.i("address", totalAddress.get(j));
			}

			mm.to = "";

			if (totalAddress.size() == 1) {
				if (totalAddress.get(0).indexOf("@") == -1) {
					mm.to = totalAddress.get(0) + "/TYPE=PLMN";
				} else {
					mm.to = totalAddress.get(0) + "/TYPE=FQDN";
				}
			} else {
				Iterator<String> itr = totalAddress.iterator();
				while (itr.hasNext()) {
					String address = itr.next();
					if (address.indexOf("@") == -1)// 전화번호 형식
					{
						address += "/TYPE=PLMN";
					} else// 이메일 형식
					{
						address += "/TYPE=FQDN";
					}
					mm.to += address + ",";
				}
				mm.to = mm.to.substring(0, mm.to.length() - 1);
			}

			Log.i("HERMESSAGE", "TO FILED's Value is " + mm.to);

			mm.x_mms_delivery_report = deliverycheck.isChecked();
			mm.x_mms_read_report = readcheck.isChecked();

			// 제목은 옵션 내용은 없이 첨부파일만 갈 수도 있음 : 수신자만 하겠음

			Calendar now = Calendar.getInstance();
			mm.x_mms_transaction_ID = "htid"
					+ Integer.toString(now.get(Calendar.YEAR));
			mm.x_mms_transaction_ID += Integer
					.toString(now.get(Calendar.MONTH) + 1);
			mm.x_mms_transaction_ID += Integer.toString(now.get(Calendar.DATE));
			mm.x_mms_transaction_ID += Integer.toString(now
					.get(Calendar.HOUR_OF_DAY));
			mm.x_mms_transaction_ID += Integer.toString(now
					.get(Calendar.MINUTE));
			mm.x_mms_transaction_ID += Integer.toString(now
					.get(Calendar.SECOND));
			mm.x_mms_transaction_ID += (char) ((Math.random() * 26) + 97);
			mm.x_mms_transaction_ID += (char) ((Math.random() * 26) + 97);
			mm.x_mms_transaction_ID += (char) ((Math.random() * 26) + 97);

			mm.x_mms_mms_version = "1.3";
			java.util.Date nowTime = new java.util.Date();
			mm.date = nowTime;

			mm.from = ((Hermessage) Hermessage.mContext).ClientID;

			mm.x_mms_expiry = expiryDateTime;
			if (isReservation)
				mm.x_mms_delivery_time = reservationDateTime;
			else
				mm.x_mms_delivery_time = nowTime;

			mm.x_mms_delivery_report = deliverycheck.isChecked();
			mm.x_mms_read_report = readcheck.isChecked();

			mm.content_type = "application/vnd.wap.multipart.related;";

			if (content != null) {
				mm.messageContent = content;
			}

			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			HTTPProcessor httpProcessor = new HTTPProcessor(this);
			httpProcessor.httpSend(mm);

			break;
		case R.id.reservationSetting:// ////////예약 시간 선택하는 곳

			if (reservationSetting.getText().equals("예약 취소")) {
				reservationSetting.setText("예약 전송");
				isReservation = false;
				reservationDate.setText("");
			} else {
				choiceTime = 1;
				this.DialogDatePicker();
			}

			break;
		case R.id.expirySetting:// /////////만료 시간 설정
			choiceTime = 2;
			DialogDatePicker();

			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null) {
			ArrayList<String> address = data.getStringArrayListExtra("address");
			int plusAddress = address.size();

			int i = 0;
			if (receiveText.getText().toString().equals("")) {
				receiveText.setText(address.get(0));
				i = 1;
			}
			for (; i < plusAddress; i++) {

				if (empty.size() != 0) {
					for (int j = 0; j < addressCount; j++) {
						for (int k = 0; k < empty.size(); k++) {
							if (addressEdit[j].getTag().toString()
									.equals(empty.get(k))) {
								addressEdit[j].setText(address.get(i));
								i++;
							}
						}
					}
				}
				if (address.size() == empty.size()) {
					return;
				}

				addressEdit[addressCount] = new EditText(this);
				addressEdit[addressCount].setText(address.get(i));
				addressEdit[addressCount].setTextColor(0xFF000000);
				addressEdit[addressCount].setId(addressCount);
				addressEdit[addressCount]
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
				linearEditText.addView(addressEdit[addressCount]);
				addressEdit[addressCount].setTag("" + addressCount);

				delButton[addressCount] = new Button(this);

				delButton[addressCount]
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
				// delButton[addressCount].setText("삭제");
				delButton[addressCount]
						.setBackgroundResource(R.drawable.delete);
				linearButton.addView(delButton[addressCount]);
				delButton[addressCount].setTag("" + addressCount);

				delButton[addressCount]
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								Log.d("kdw getTag()",
										""
												+ Integer.parseInt((String) v
														.getTag()));

								linearButton.removeView(delButton[Integer
										.parseInt((String) v.getTag())]);
								linearEditText.removeView(addressEdit[Integer
										.parseInt((String) v.getTag())]);
								// delButton[Integer.parseInt((String)
								// v.getTag())]
								// .setTag("delete");
								addressEdit[Integer.parseInt((String) v
										.getTag())].setTag("delete");
							}
						});
				addressCount++;
			}
		}
	}

	private void DialogTimePicker() {
		TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				selecthour = hourOfDay;
				selectminute = minute;

				if (choiceTime == 1) {
					reservationDateTime.setYear(selectyear - 1900);
					reservationDateTime.setMonth(selectmonth);
					reservationDateTime.setDate(selectday);
					reservationDateTime.setHours(selecthour);
					reservationDateTime.setMinutes(selectminute);

					java.util.Date nowDate2 = new java.util.Date();
					if (reservationDateTime.before(nowDate2)) {
						Toast.makeText(mContext, "예약시간이 현재시간보다 전입니다",
								Toast.LENGTH_SHORT).show();
						// reservationDateTime = null;
						return;
					}

					formatter.format(reservationDateTime);

					reservationDate.setText(formatter
							.format(reservationDateTime));
					reservationSetting.setText("예약 취소");
					isReservation = true;

				} else if (choiceTime == 2) {

					expiryDateTime.setYear(selectyear - 1900);
					expiryDateTime.setMonth(selectmonth);
					expiryDateTime.setDate(selectday);
					expiryDateTime.setHours(selecthour);
					expiryDateTime.setMinutes(selectminute);
					java.util.Date nowDate = new java.util.Date();
					if (expiryDateTime.before(nowDate)) {
						Toast.makeText(mContext, "만료시간이 현재시간보다 전입니다",
								Toast.LENGTH_SHORT).show();
						// expiryDateTime = null;
						return;
					}

					formatter.format(expiryDateTime);

					expiryDate.setText(formatter.format(expiryDateTime));

				}
			}
		};
		TimePickerDialog alert = new TimePickerDialog(this, mTimeSetListener,
				calDateTime.get(Calendar.HOUR_OF_DAY),
				calDateTime.get(Calendar.MINUTE), true);
		alert.show();
	}

	private void DialogDatePicker() {

		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			// onDateSet method
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				String date_selected = String.valueOf(monthOfYear + 1) + " /"
						+ String.valueOf(dayOfMonth) + " /"
						+ String.valueOf(year);
				Log.i("year", year + "");
				selectyear = year;
				selectmonth = monthOfYear;
				selectday = dayOfMonth;
				DialogTimePicker();
			}
		};
		DatePickerDialog alert = new DatePickerDialog(this, mDateSetListener,
				calDateTime.get(Calendar.YEAR),
				calDateTime.get(Calendar.MONTH),
				calDateTime.get(Calendar.DAY_OF_MONTH));
		alert.show();

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