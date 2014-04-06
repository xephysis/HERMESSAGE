package ac.ajou.hermessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ac.ajou.hermessage.AddressConfig.Info;
import ac.ajou.hermessage.AddressConfig.MyListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MMBoxReceive extends Activity implements OnClickListener {
	private Cursor mCursor;
	ArrayList<Info> message = new ArrayList<Info>();
	Info messageInfo[] = new Info[20];

	Button messageDelete;

	HashMap<String, String> checkedList2 = new HashMap<String, String>();
	int[] checkedPosition = new int[100];

	WordDBHelper mHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmbox);
		MyListAdapter MyAdapter;
		mHelper = new WordDBHelper(this);

		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM ReceiveMessage", null);
		// mCursor.moveToFirst();
		int i = 0;
		mCursor.moveToLast();

		if (mCursor.getCount() != 0) {
			do {
				messageInfo[i] = new Info();
				messageInfo[i]._id = mCursor.getString(mCursor
						.getColumnIndex("_id"));
				messageInfo[i].From = mCursor.getString(mCursor
						.getColumnIndex("_From"));
				messageInfo[i].ContentType = mCursor.getString(mCursor
						.getColumnIndex("ContentType"));
				messageInfo[i].ContentLocation = mCursor.getString(mCursor
						.getColumnIndex("ContentLocation"));
				messageInfo[i].Date = mCursor.getString(mCursor
						.getColumnIndex("Date"));
				messageInfo[i].Content = mCursor.getString(mCursor
						.getColumnIndex("Content"));
				message.add(messageInfo[i]);
			} while (mCursor.moveToPrevious());
		}
		Typeface face = Typeface.createFromAsset(getAssets(), "LOVE.TTF");

		messageDelete = (Button) findViewById(R.id.messageDelete);
		messageDelete.setTypeface(face);
		messageDelete.setOnClickListener(this);

		MyAdapter = new MyListAdapter(this, R.layout.listview_receive, message);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mHelper = new WordDBHelper(this);
		MyListAdapter MyAdapter;
		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM ReceiveMessage", null);
		message.clear();
		// mCursor.moveToFirst();
		int i = 0;
		mCursor.moveToLast();

		do {
			messageInfo[i] = new Info();
			messageInfo[i]._id = mCursor.getString(mCursor
					.getColumnIndex("_id"));
			messageInfo[i].From = mCursor.getString(mCursor
					.getColumnIndex("_From"));
			// messageInfo[i].Subject = mCursor.getString(mCursor
			// .getColumnIndex("Subject"));
			messageInfo[i].ContentType = mCursor.getString(mCursor
					.getColumnIndex("ContentType"));
			messageInfo[i].ContentLocation = mCursor.getString(mCursor
					.getColumnIndex("ContentLocation"));
			messageInfo[i].Date = mCursor.getString(mCursor
					.getColumnIndex("Date"));
			messageInfo[i].Content = mCursor.getString(mCursor
					.getColumnIndex("Content"));
			message.add(messageInfo[i]);
		} while (mCursor.moveToPrevious());
		MyAdapter = new MyListAdapter(this, R.layout.listview_receive, message);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		MyListAdapter MyAdapter;
		switch (v.getId()) {

		case R.id.messageDelete:
			Iterator itr = checkedList2.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				String value = checkedList2.get(key);

				SQLiteDatabase db;
				db = mHelper.getWritableDatabase();
				db.delete("ReceiveMessage", "_id = '" + key + "' ", null);
			}
			mHelper = new WordDBHelper(this);

			SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db.rawQuery("SELECT * FROM ReceiveMessage", null);
			message.clear();
			// mCursor.moveToFirst();
			int i = 0;
			mCursor.moveToLast();

			if (mCursor.getCount() != 0) {
				do {
					messageInfo[i] = new Info();
					messageInfo[i]._id = mCursor.getString(mCursor
							.getColumnIndex("_id"));
					messageInfo[i].From = mCursor.getString(mCursor
							.getColumnIndex("_From"));
					// messageInfo[i].Subject = mCursor.getString(mCursor
					// .getColumnIndex("Subject"));
					messageInfo[i].ContentType = mCursor.getString(mCursor
							.getColumnIndex("ContentType"));
					messageInfo[i].ContentLocation = mCursor.getString(mCursor
							.getColumnIndex("ContentLocation"));
					messageInfo[i].Date = mCursor.getString(mCursor
							.getColumnIndex("Date"));
					messageInfo[i].Content = mCursor.getString(mCursor
							.getColumnIndex("Content"));
					message.add(messageInfo[i]);
				} while (mCursor.moveToPrevious());
			}

			for (int j = 0; j < 100; j++) {
				checkedPosition[j] = 0;
			}
			MyAdapter = new MyListAdapter(this, R.layout.listview_receive,
					message);

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
			txt2.setText(arSrc.get(pos).From);
			txt2.setTypeface(face);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(MMBoxReceive.this,
							ReceiveMessageShow.class);// //수정 필요
					intent.putExtra("_id", arSrc.get(pos)._id);
					intent.putExtra("From", arSrc.get(pos).From);
					// intent.putExtra("Subject", arSrc.get(pos).Subject);
					intent.putExtra("ContentType", arSrc.get(pos).ContentType);
					intent.putExtra("ContentLocation",
							arSrc.get(pos).ContentLocation);
					intent.putExtra("Date", arSrc.get(pos).Date);
					intent.putExtra("Content", arSrc.get(pos).Content);

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
		String From;
		// String Subject;
		String ContentType;
		String ContentLocation;
		String Date;
		String Content;
	}
}