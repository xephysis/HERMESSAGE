package ac.ajou.hermessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ac.ajou.hermessage.MMBoxSend.Info;
import ac.ajou.hermessage.MMBoxSend.MyListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MMBoxDraft extends Activity implements OnClickListener {
	private Cursor mCursor;
	ArrayList<Info> message = new ArrayList<Info>();
	Info messageInfo[] = new Info[20];

	Button messageDelete;
	MyListAdapter MyAdapter;
	HashMap<String, String> checkedList2 = new HashMap<String, String>();
	int[] checkedPosition = new int[100];
	WordDBHelper mHelper;

	MM mm = new MM();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmbox);

		mHelper = new WordDBHelper(this);

		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM DraftMessage", null);
		// mCursor.moveToFirst();
		int i = 0;
		mCursor.moveToLast();

		if (mCursor.getCount() != 0) {
			do {
				messageInfo[i] = new Info();
				messageInfo[i]._id = mCursor.getString(mCursor
						.getColumnIndex("_id"));
				messageInfo[i].Receiver = mCursor.getString(mCursor
						.getColumnIndex("Receiver"));
				/*
				 * messageInfo[i].Subject = mCursor.getString(mCursor
				 * .getColumnIndex("Subject"));
				 */
				messageInfo[i].Content = mCursor.getString(mCursor
						.getColumnIndex("Content"));
				messageInfo[i].DeliveryState = mCursor.getString(mCursor
						.getColumnIndex("DeliveryState"));
				messageInfo[i].ReadState = mCursor.getString(mCursor
						.getColumnIndex("ReadState"));
				messageInfo[i].LastEditDate = mCursor.getString(mCursor
						.getColumnIndex("LastEditDate"));
				messageInfo[i].ContentType = mCursor.getString(mCursor
						.getColumnIndex("ContentType"));
				messageInfo[i].ContentLocation = mCursor.getString(mCursor
						.getColumnIndex("ContentLocation"));

				message.add(messageInfo[i]);
			} while (mCursor.moveToPrevious());
		}
		Typeface face = Typeface.createFromAsset(getAssets(), "LOVE.TTF");

		messageDelete = (Button) findViewById(R.id.messageDelete);
		messageDelete.setTypeface(face);
		messageDelete.setOnClickListener(this);

		MyAdapter = new MyListAdapter(this, R.layout.listview_draft, message);
		db.close();
		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				messageInfo[i].Receiver = mCursor.getString(mCursor
						.getColumnIndex("Receiver"));
				messageInfo[i].Content = mCursor.getString(mCursor
						.getColumnIndex("Content"));
				messageInfo[i].DeliveryState = mCursor.getString(mCursor
						.getColumnIndex("DeliveryState"));
				messageInfo[i].ReadState = mCursor.getString(mCursor
						.getColumnIndex("ReadState"));
				messageInfo[i].LastEditDate = mCursor.getString(mCursor
						.getColumnIndex("LastEditDate"));
				messageInfo[i].ContentType = mCursor.getString(mCursor
						.getColumnIndex("ContentType"));
				messageInfo[i].ContentLocation = mCursor.getString(mCursor
						.getColumnIndex("ContentLocation"));

				message.add(messageInfo[i]);
			} while (mCursor.moveToPrevious());
		}

		MyAdapter = new MyListAdapter(this, R.layout.listview_notification,
				message);
		db.close();
		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.messageDelete:
			Iterator itr = checkedList2.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				String value = checkedList2.get(key);

				SQLiteDatabase db;
				db = mHelper.getWritableDatabase();
				db.delete("DraftMessage", "_id = '" + key + "' ", null);
			}

			message.clear();
			mHelper = new WordDBHelper(this);

			SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db.rawQuery("SELECT * FROM DraftMessage", null);
			// mCursor.moveToFirst();
			int i = 0;
			mCursor.moveToLast();

			if (mCursor.getCount() != 0) {
				do {
					messageInfo[i] = new Info();
					messageInfo[i]._id = mCursor.getString(mCursor
							.getColumnIndex("_id"));
					messageInfo[i].Receiver = mCursor.getString(mCursor
							.getColumnIndex("Receiver"));
					/*
					 * messageInfo[i].Subject = mCursor.getString(mCursor
					 * .getColumnIndex("Subject"));
					 */
					messageInfo[i].Content = mCursor.getString(mCursor
							.getColumnIndex("Content"));
					messageInfo[i].DeliveryState = mCursor.getString(mCursor
							.getColumnIndex("DeliveryState"));
					messageInfo[i].ReadState = mCursor.getString(mCursor
							.getColumnIndex("ReadState"));
					messageInfo[i].LastEditDate = mCursor.getString(mCursor
							.getColumnIndex("LastEditDate"));
					messageInfo[i].ContentType = mCursor.getString(mCursor
							.getColumnIndex("ContentType"));
					messageInfo[i].ContentLocation = mCursor.getString(mCursor
							.getColumnIndex("ContentLocation"));

					message.add(messageInfo[i]);
				} while (mCursor.moveToPrevious());
			}

			for (int j = 0; j < 100; j++) {
				checkedPosition[j] = 0;
			}
			MyAdapter = new MyListAdapter(this, R.layout.listview_draft,
					message);
			db.close();
			ListView MyList;
			MyList = (ListView) findViewById(R.id.list);
			MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			MyList.setAdapter(MyAdapter);
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
			return arSrc.get(position).Receiver;
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
			Typeface face = Typeface.createFromAsset(getAssets(), "LOVE.TTF");

			TextView txt = (TextView) convertView.findViewById(R.id.text);
			String temp = arSrc.get(pos).Content;
			if (arSrc.get(pos).Content.length() > 10) {
				temp = arSrc.get(pos).Content.substring(0, 10);
				temp = temp + "....";
			}
			txt.setText(temp);
			txt.setTypeface(face);

			TextView txt2 = (TextView) convertView.findViewById(R.id.textView3);
			txt2.setText(arSrc.get(pos).Receiver);
			txt2.setTypeface(face);

			TextView txt3 = (TextView) convertView.findViewById(R.id.textView4);
			txt3.setText(arSrc.get(pos).LastEditDate);
			txt3.setTypeface(face);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(MMBoxDraft.this, MMMake.class);

					mm.to = arSrc.get(pos).Receiver;
					mm.messageContent = arSrc.get(pos).Content;
					intent.putExtra("mm", mm);

					SQLiteDatabase db;
					db = mHelper.getWritableDatabase();

					Intent intent3 = getIntent();
					db.delete("DraftMessage", "_id = '" + arSrc.get(pos)._id
							+ "'", null);

					// Intent intent2 = getIntent();
					// Log.i("_id", intent2.getStringExtra("_id"));

					mHelper.close();
					db.close();

					// mm.attachFilePath = ////// 희준 - 파일 경로 들어가는 부분
					startActivityForResult(intent, 0);
				}
			});

			final CheckBox cb = (CheckBox) convertView
					.findViewById(R.id.checkList);
			cb.setChecked(false);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						cb.setChecked(true);
						checkedList2.put(arSrc.get(pos)._id,
								arSrc.get(pos).Receiver);
						checkedPosition[pos] = 1;
						Log.i(arSrc.get(pos)._id, arSrc.get(pos).Receiver);
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
		String Receiver;
		// String Subject;
		String Content;
		String DeliveryState;
		String ReadState;
		String LastEditDate;
		String ContentType;
		String ContentLocation;
	}
}
//