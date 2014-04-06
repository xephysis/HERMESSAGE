package ac.ajou.hermessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import ac.ajou.hermessage.MMBoxReceive.Info;
import ac.ajou.hermessage.MMBoxReceive.MyListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MMBoxNotification extends Activity implements OnClickListener {
	private Cursor mCursor;
	ArrayList<Info> message = new ArrayList<Info>();
	Info messageInfo[] = new Info[20];
	SimpleDateFormat formatter = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss yyyy", java.util.Locale.US);
	Button messageDelete;

	HashMap<String, String> checkedList2 = new HashMap<String, String>();
	int[] checkedPosition = new int[100];

	WordDBHelper mHelper;

	MyListAdapter MyAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmbox);

		mHelper = new WordDBHelper(this);

		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM NotificationMessage", null);
		// mCursor.moveToFirst();
		int i = 0;
		mCursor.moveToLast();

		if (mCursor.getCount() != 0) {
			do {
				messageInfo[i] = new Info();
				messageInfo[i]._id = mCursor.getString(mCursor
						.getColumnIndex("_id"));
				messageInfo[i].MessageID = mCursor.getString(mCursor
						.getColumnIndex("MessageID"));
				messageInfo[i].From = mCursor.getString(mCursor
						.getColumnIndex("_From"));
				// messageInfo[i].Subject = mCursor.getString(mCursor
				// .getColumnIndex("Subject"));
				messageInfo[i].DeliveryStateReq = mCursor.getString(mCursor
						.getColumnIndex("DeliveryStateReq"));
				messageInfo[i].Expiry = mCursor.getString(mCursor
						.getColumnIndex("Expiry"));
				messageInfo[i].Size = mCursor.getString(mCursor
						.getColumnIndex("Size"));
				message.add(messageInfo[i]);
			} while (mCursor.moveToPrevious());
		}
		mCursor.close();
		Typeface face = Typeface.createFromAsset(getAssets(), "LOVE.TTF");

		messageDelete = (Button) findViewById(R.id.messageDelete);
		messageDelete.setTypeface(face);
		messageDelete.setOnClickListener(this);

		MyAdapter = new MyListAdapter(this, R.layout.listview_notification,
				message);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 1:
			MyListAdapter MyAdapter;
			mHelper = new WordDBHelper(this);

			SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db.rawQuery("SELECT * FROM NotificationMessage", null);
			// mCursor.moveToFirst();
			message.clear();
			int i = 0;
			mCursor.moveToLast();

			if (mCursor.getCount() != 0) {
				do {
					messageInfo[i] = new Info();
					messageInfo[i]._id = mCursor.getString(mCursor
							.getColumnIndex("_id"));
					messageInfo[i].MessageID = mCursor.getString(mCursor
							.getColumnIndex("MessageID"));
					messageInfo[i].From = mCursor.getString(mCursor
							.getColumnIndex("_From"));
					// messageInfo[i].Subject = mCursor.getString(mCursor
					// .getColumnIndex("Subject"));
					messageInfo[i].DeliveryStateReq = mCursor.getString(mCursor
							.getColumnIndex("DeliveryStateReq"));
					messageInfo[i].Expiry = mCursor.getString(mCursor
							.getColumnIndex("Expiry"));
					messageInfo[i].Size = mCursor.getString(mCursor
							.getColumnIndex("Size"));
					message.add(messageInfo[i]);
				} while (mCursor.moveToPrevious());
			}

			MyAdapter = new MyListAdapter(this, R.layout.listview_notification,
					message);

			ListView MyList;
			MyList = (ListView) findViewById(R.id.list);
			MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			MyList.setAdapter(MyAdapter);
			mCursor.close();
			break;
		default:

			break;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.messageDelete:
			Iterator itr = checkedList2.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				// String value = checkedList2.get(key);

				SQLiteDatabase db;
				db = mHelper.getWritableDatabase();

				Cursor mCursor;
				mCursor = db
						.rawQuery("SELECT * FROM NotificationMessage", null);
				mCursor.moveToPosition(Integer.parseInt(key) - 1);

				Date expiredDate;
				try {
					expiredDate = formatter.parse(mCursor.getString(mCursor
							.getColumnIndex("Expiry")));

					if (expiredDate.after(new Date())) {
						MM notifyRespMm = new MM();
						notifyRespMm.x_mms_message_type = "m-notifyresp-ind";
						notifyRespMm.x_mms_mms_version = "1.3";
						notifyRespMm.x_mms_status = "Rejected";
						notifyRespMm.x_mms_report_allowed = true;
						notifyRespMm.x_mms_transaction_ID = mCursor
								.getString(mCursor
										.getColumnIndex("TransactionID"));

						HTTPProcessor httpProc = new HTTPProcessor(this);
						httpProc.httpSend(notifyRespMm);
						/*
						 * 트랜젝션 아이디 제대로 꺼내 와서 제대로 만드나 확인해 봐야함
						 */
					}
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				db.delete("NotificationMessage", "_id = '" + key + "' ", null);

				/*
				 * 거절에 대한 MM 만들어서 보내야 할듯
				 */

				// mHelper = new WordDBHelper(this);

				db = openOrCreateDatabase("db.sqlite", Context.MODE_PRIVATE,
						null);
				mCursor = db
						.rawQuery("SELECT * FROM NotificationMessage", null);
				message.clear();
				// mCursor.moveToFirst();
				int i = 0;
				mCursor.moveToLast();

				if (mCursor.getCount() != 0) {
					do {
						messageInfo[i] = new Info();
						messageInfo[i]._id = mCursor.getString(mCursor
								.getColumnIndex("_id"));
						messageInfo[i].MessageID = mCursor.getString(mCursor
								.getColumnIndex("MessageID"));
						messageInfo[i].From = mCursor.getString(mCursor
								.getColumnIndex("_From"));
						// messageInfo[i].Subject = mCursor.getString(mCursor
						// .getColumnIndex("Subject"));
						messageInfo[i].DeliveryStateReq = mCursor
								.getString(mCursor
										.getColumnIndex("DeliveryStateReq"));
						messageInfo[i].Expiry = mCursor.getString(mCursor
								.getColumnIndex("Expiry"));
						messageInfo[i].Size = mCursor.getString(mCursor
								.getColumnIndex("Size"));
						message.add(messageInfo[i]);
					} while (mCursor.moveToPrevious());
				}
				MyAdapter = new MyListAdapter(this,
						R.layout.listview_notification, message);
				mCursor.close();
				ListView MyList;
				MyList = (ListView) findViewById(R.id.list);
				MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				MyList.setAdapter(MyAdapter);

			}
			break;

		default:
			break;
		}
	}

	class MyListAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<Info> arSrc;
		int layout;

		String checkedList[];

		int checkCount = 0;

		public MyListAdapter(Context context, int alayout,
				ArrayList<Info> message) {
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = message;
			layout = alayout;
		}

		public int getCount() {
			return arSrc.size();
		}

		public String getItem(int position) {
			return arSrc.get(position).From;
		}

		public long getItemId(int position) {
			return position;
		}

		// 각 항목의 뷰 생성
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}

			TextView txt2 = (TextView) convertView.findViewById(R.id.text);
			txt2.setText(arSrc.get(pos).From);

			Date expiryDate = null;
			try {
				expiryDate = formatter.parse(arSrc.get(pos).Expiry);
			} catch (Exception e) {
				e.getStackTrace();
			}
			
			Date nowDate = new Date();
			if (expiryDate.before(nowDate)) {
				ImageView endMessage = (ImageView) convertView
						.findViewById(R.id.endmessage);
				endMessage.setImageResource(R.drawable.messageend);
				endMessage.setVisibility(View.VISIBLE);
				convertView.setEnabled(false);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					MM notificationIndMM = new MM();
					notificationIndMM.x_mms_content_location = arSrc.get(pos).MessageID;
					notificationIndMM.from = arSrc.get(pos).From;
					// notificationIndMM.subject = arSrc.get(pos).Subject;
					if (arSrc.get(pos).DeliveryStateReq.equalsIgnoreCase("Y")) {
						notificationIndMM.x_mms_delivery_report = true;
					} else {
						notificationIndMM.x_mms_delivery_report = false;
					}
					try {
						notificationIndMM.x_mms_expiry = formatter.parse(arSrc
								.get(pos).Expiry);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
					Log.i("HERMESSAGE", arSrc.get(pos).Size);
					notificationIndMM.x_mms_message_size = Integer
							.parseInt(arSrc.get(pos).Size);

					Intent intent2 = new Intent(MMBoxNotification.this,
							NotificationBranchComponent.class);
					intent2.putExtra("mNotificationInd", notificationIndMM);
					intent2.putExtra("isImmediate", false);
					startActivityForResult(intent2, 1);
				}
			});

			final CheckBox cb = (CheckBox) convertView
					.findViewById(R.id.checkList);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						cb.setChecked(true);
						checkedList2.put(arSrc.get(pos)._id,
								arSrc.get(pos).From);
						checkedPosition[pos] = 1;
						Log.i(arSrc.get(pos)._id, arSrc.get(pos).From);
					} else {
						checkedList2.remove(arSrc.get(pos)._id);
						cb.setChecked(false);
						checkedPosition[pos] = 0;
					}
				}

			});
			if (checkedPosition[pos] == 1) {
				cb.setChecked(true);
			} else {

				cb.setChecked(false);
			}
			return convertView;
		}
	}

	class Info {
		String _id;
		String MessageID;
		String From;
		// String Subject;
		String DeliveryStateReq;
		String Expiry;
		String Size;
	}

}
