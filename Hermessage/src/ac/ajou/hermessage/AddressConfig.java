package ac.ajou.hermessage;

import java.lang.reflect.Array;
import java.util.*;

import ac.ajou.hermessage.R;
import ac.ajou.hermessage.AddressReference.MyListAdapter;
import ac.ajou.hermessage.R.id;
import ac.ajou.hermessage.R.layout;
import ac.ajou.hermessage.R.string;
import android.R.integer;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddressConfig extends Activity implements OnClickListener {
	private Cursor mCursor;
	ArrayList<Info> people = new ArrayList<Info>();
	ArrayList<Info> searchpeople = new ArrayList<Info>();
	Info peopleInfo[] = new Info[20];
	EditText searchedit;
	Button addressadd, addressdel, addresssearch;
	MyListAdapter MyAdapter;
	HashMap<String, String> checkedList2 = new HashMap<String, String>();
	MM mm = new MM();

	TextView title;

	// HashMap<Integer, String> checkedPosition = new HashMap<Integer,
	// String>();
	int[] checkedPosition = new int[100];
	WordDBHelper mHelper;
	Context mContext;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressconfig);
		mContext = this;
		mHelper = new WordDBHelper(this);

		mContext = this;
		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM AddressBook", null);
		// mCursor.moveToFirst();
		int i = 0;
		while (mCursor.moveToNext()) {
			peopleInfo[i] = new Info();
			peopleInfo[i]._id = mCursor
					.getString(mCursor.getColumnIndex("_id"));
			peopleInfo[i].name = mCursor.getString(mCursor
					.getColumnIndex("Name"));
			peopleInfo[i].phoneNumber = mCursor.getString(mCursor
					.getColumnIndex("PhoneNumber"));
			peopleInfo[i].emailAddress = mCursor.getString(mCursor
					.getColumnIndex("EmailAddress"));
			peopleInfo[i].memo = mCursor.getString(mCursor
					.getColumnIndex("Memo"));

			people.add(peopleInfo[i]);
		}

		Typeface face = Typeface.createFromAsset(getAssets(), "PILGI1.TTF");
		title = (TextView) findViewById(R.id.textView1);
		title.setTypeface(face);
		title.setTextSize(50);
		title.setTextColor(Color.BLUE);
		searchedit = (EditText) findViewById(R.id.searchedit);

		addressadd = (Button) findViewById(R.id.addressadd);
		addressdel = (Button) findViewById(R.id.addressdel);
		addressdel.setTypeface(face);
		addressadd.setTypeface(face);
		addresssearch = (Button) findViewById(R.id.addresssearch);
		addressadd.setOnClickListener(this);
		addressdel.setOnClickListener(this);

		MyAdapter = new MyListAdapter(this, R.layout.listview_config, people);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.i("after", "after");
				searchpeople.clear();
				for (int i = 0; i < people.size(); i++) {
					String temp = people.get(i).name.toLowerCase();
					String temp2 = people.get(i).emailAddress.toLowerCase();
					String phone = people.get(i).phoneNumber;
					String temp3 = searchedit.getText().toString()
							.toLowerCase();

					if (temp.indexOf(temp3) != -1 || temp2.indexOf(temp3) != -1
							|| phone.indexOf(temp3) != -1) {
						searchpeople.add(people.get(i));
					}
				}
				MyAdapter = new MyListAdapter(mContext,
						R.layout.listview_config, searchpeople);

				ListView MyList;
				MyList = (ListView) findViewById(R.id.list);
				MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				MyList.setAdapter(MyAdapter);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		};
		searchedit.addTextChangedListener(watcher);
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "환경 설정");
		menu.add(0, 2, 0, "보관함");
		menu.add(0, 3, 0, "메시지 작성");
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public void onResume() {
		super.onResume();

		if (searchpeople.size() != 0) {
			MyAdapter = new MyListAdapter(mContext, R.layout.listview_config,
					searchpeople);
			ListView MyList;
			MyList = (ListView) findViewById(R.id.list);
			MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			MyList.setAdapter(MyAdapter);
		}
		// Log.i(TAG, "onResume()");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent goPreference = new Intent(AddressConfig.this,
					Preference.class);
			startActivity(goPreference);
			break;
		case 2:
			Intent goBox = new Intent(AddressConfig.this, MMBox.class);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goBox);
			break;
		case 3:
			Intent goMMmake = new Intent(AddressConfig.this, MMMake.class);
			goMMmake.putExtra("mm", mm);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goMMmake);
			break;
		default:
			break;
		}

		return (itemCallback(item) || super.onOptionsItemSelected(item));
	}

	private boolean itemCallback(MenuItem item) {

		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 0:

			break;
		default:
			Log.i("test resume", "ok??");

			SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db.rawQuery("SELECT * FROM AddressBook", null);
			// mCursor.moveToFirst();
			int i = 0;
			while (mCursor.moveToNext()) {
				peopleInfo[i] = new Info();
				peopleInfo[i]._id = mCursor.getString(mCursor
						.getColumnIndex("_id"));
				peopleInfo[i].name = mCursor.getString(mCursor
						.getColumnIndex("Name"));
				peopleInfo[i].phoneNumber = mCursor.getString(mCursor
						.getColumnIndex("PhoneNumber"));
				peopleInfo[i].emailAddress = mCursor.getString(mCursor
						.getColumnIndex("EmailAddress"));
				peopleInfo[i].memo = mCursor.getString(mCursor
						.getColumnIndex("Memo"));

				people.add(peopleInfo[i]);
			}

			addressadd = (Button) findViewById(R.id.addressadd);
			addressdel = (Button) findViewById(R.id.addressdel);

			addressadd.setOnClickListener(this);
			addressdel.setOnClickListener(this);

			MyAdapter = new MyListAdapter(this, R.layout.listview_config,
					people);
			db.close();
			ListView MyList;
			MyList = (ListView) findViewById(R.id.list);
			MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			MyList.setAdapter(MyAdapter);
			break;
		}
	}

	@Override
	public void onRestart() {
		super.onRestart();
		people.clear();
		mHelper = new WordDBHelper(this);

		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM AddressBook", null);
		// mCursor.moveToFirst();
		int i = 0;
		while (mCursor.moveToNext()) {
			peopleInfo[i] = new Info();
			peopleInfo[i]._id = mCursor
					.getString(mCursor.getColumnIndex("_id"));
			peopleInfo[i].name = mCursor.getString(mCursor
					.getColumnIndex("Name"));
			peopleInfo[i].phoneNumber = mCursor.getString(mCursor
					.getColumnIndex("PhoneNumber"));
			peopleInfo[i].emailAddress = mCursor.getString(mCursor
					.getColumnIndex("EmailAddress"));
			peopleInfo[i].memo = mCursor.getString(mCursor
					.getColumnIndex("Memo"));

			people.add(peopleInfo[i]);
		}
		MyAdapter = new MyListAdapter(this, R.layout.listview_config, people);
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
		case R.id.addressadd:
			Intent intent = new Intent(AddressConfig.this, AddressAdd.class);
			startActivityForResult(intent, 0);

			break;
		case R.id.addressdel:
			Iterator itr = checkedList2.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				String value = checkedList2.get(key);

				SQLiteDatabase db;
				db = mHelper.getWritableDatabase();
				db.delete("AddressBook", "_id = '" + key + "'", null);

				people.clear();

				mCursor = db.rawQuery("SELECT * FROM AddressBook", null);
				// mCursor.moveToFirst();
				int i = 0;
				while (mCursor.moveToNext()) {
					peopleInfo[i] = new Info();
					peopleInfo[i]._id = mCursor.getString(mCursor
							.getColumnIndex("_id"));
					peopleInfo[i].name = mCursor.getString(mCursor
							.getColumnIndex("Name"));
					peopleInfo[i].phoneNumber = mCursor.getString(mCursor
							.getColumnIndex("PhoneNumber"));
					peopleInfo[i].emailAddress = mCursor.getString(mCursor
							.getColumnIndex("EmailAddress"));
					peopleInfo[i].memo = mCursor.getString(mCursor
							.getColumnIndex("Memo"));

					people.add(peopleInfo[i]);
				}

				for (int j = 0; j < 100; j++) {
					checkedPosition[j] = 0;
				}
				MyAdapter = new MyListAdapter(this, R.layout.listview_config,
						people);

				ListView MyList;
				MyList = (ListView) findViewById(R.id.list);
				MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				MyList.setAdapter(MyAdapter);
				// finish();
			}

			break;
		case R.id.addresssearch:

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
				ArrayList<Info> people) {
			this.maincon = context;
			this.Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.arSrc = people;
			this.layout = alayout;
		}

		public int getCount() {
			return arSrc.size();
		}

		public String getItem(int position) {
			return arSrc.get(position).name;
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
			Typeface face = Typeface.createFromAsset(getAssets(), "PILGI1.TTF");

			// convertView = Inflater.inflate(layout, parent, false);
			TextView txt3 = (TextView) convertView
					.findViewById(R.id.textofname);
			txt3.setText(arSrc.get(pos).name);
			txt3.setTypeface(face);
			TextView txt = (TextView) convertView.findViewById(R.id.text);
			txt.setText(arSrc.get(pos).phoneNumber);
			txt.setTypeface(face);
			TextView txt2 = (TextView) convertView.findViewById(R.id.textView3);
			txt2.setText(arSrc.get(pos).emailAddress);
			txt2.setTypeface(face);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*
					 * String str = arSrc.get(pos).name + "를 주문합니다.";
					 * Toast.makeText(maincon, str, Toast.LENGTH_SHORT).show();
					 */

					Intent intent = new Intent(AddressConfig.this,
							AddressInfo.class);// //수정 필요
					intent.putExtra("_id", arSrc.get(pos)._id);
					intent.putExtra("name", arSrc.get(pos).name);
					intent.putExtra("phoneNumber", arSrc.get(pos).phoneNumber);
					intent.putExtra("emailAddress", arSrc.get(pos).emailAddress);
					intent.putExtra("memo", arSrc.get(pos).memo);

					startActivityForResult(intent, 0);
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
								arSrc.get(pos).name);
						checkedPosition[pos] = 1;
						Log.i(arSrc.get(pos)._id, arSrc.get(pos).name);
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
		String name;
		String phoneNumber;
		String emailAddress;
		String memo;
	}
}