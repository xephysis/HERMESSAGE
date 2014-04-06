package ac.ajou.hermessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ac.ajou.hermessage.R;
import ac.ajou.hermessage.AddressConfig.MyListAdapter;
import ac.ajou.hermessage.R.id;
import ac.ajou.hermessage.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddressReference extends Activity implements OnClickListener {
	private Cursor mCursor;
	Cursor c;
	String reference;
	Intent intent1;
	int referencechoice = 0;
	ArrayList<String> temp = new ArrayList<String>();

	ArrayList<Info> people = new ArrayList<Info>();
	ArrayList<Info> searchpeople = new ArrayList<Info>();
	Info peopleInfo[] = new Info[20];

	Context mContext;

	TextView referenceTitle, number, email;;

	EditText searchEdit;
	Button refebutton;
	MyListAdapter MyAdapter;
	HashMap<String, String> checkedList2 = new HashMap<String, String>();
	int[] checkedPosition1 = new int[100];
	int[] checkedPosition2 = new int[100];

	ArrayList<String> address = new ArrayList<String>();
	// String[] address = new String[20];
	WordDBHelper mHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressreference);
		mHelper = new WordDBHelper(this);
		mContext = this;

		SQLiteDatabase db = openOrCreateDatabase("db.sqlite",
				Context.MODE_PRIVATE, null);
		mCursor = db.rawQuery("SELECT * FROM AddressBook", null);
		// mCursor.moveToFirst();
		Typeface face = Typeface.createFromAsset(getAssets(), "PILGI1.TTF");
		referenceTitle = (TextView) findViewById(R.id.referencetitle);
		referenceTitle.setText("주소록 참조");
		referenceTitle.setTypeface(face);
		referenceTitle.setTextSize(50);
		refebutton = (Button) findViewById(R.id.refebutton);
		refebutton.setTypeface(face);
		refebutton.setOnClickListener(this);

		number = (TextView) findViewById(R.id.number);
		number.setTypeface(face);
		number.setText("번호");
		email = (TextView) findViewById(R.id.email);
		email.setTypeface(face);
		email.setText("이메일");

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

		MyAdapter = new MyListAdapter(this, R.layout.listview_reference, people);

		ListView MyList;
		MyList = (ListView) findViewById(R.id.list);
		MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		MyList.setAdapter(MyAdapter);

		searchEdit = (EditText) findViewById(R.id.addresssearch);
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("after", "after");
				searchpeople.clear();
				for (int i = 0; i < people.size(); i++) {
					String temp = people.get(i).name.toLowerCase();
					String temp2 = people.get(i).emailAddress.toLowerCase();
					String phone = people.get(i).phoneNumber;
					String temp3 = searchEdit.getText().toString()
							.toLowerCase();

					if (temp.indexOf(temp3) != -1 || temp2.indexOf(temp3) != -1
							|| phone.indexOf(temp3) != -1) {
						searchpeople.add(people.get(i));
					}
				}
				MyAdapter = new MyListAdapter(mContext,
						R.layout.listview_reference, searchpeople);

				ListView MyList;
				MyList = (ListView) findViewById(R.id.list);
				MyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				MyList.setAdapter(MyAdapter);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		db.close();
		searchEdit.addTextChangedListener(watcher);
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
			maincon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = people;
			layout = alayout;
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

			TextView txt = (TextView) convertView
					.findViewById(R.id.refeaddress1);
			txt.setText(arSrc.get(pos).phoneNumber);
			txt.setTypeface(face);
			TextView txt2 = (TextView) convertView
					.findViewById(R.id.refeaddress2);
			txt2.setText(arSrc.get(pos).emailAddress);
			txt2.setTypeface(face);
			TextView txt3 = (TextView) convertView
					.findViewById(R.id.refeaddress5);
			txt3.setText(arSrc.get(pos).name);
			txt3.setTypeface(face);

			final CheckBox cb = (CheckBox) convertView
					.findViewById(R.id.refeaddress3);

			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {// ///////////////////////체크 되는 순간
											// 다이얼로그 뜨면서 선택하게 한다.
					if (isChecked) {
						cb.setChecked(true);
						checkedList2.put(arSrc.get(pos).phoneNumber,
								arSrc.get(pos)._id);
						checkedPosition1[pos] = 1;
						int i = checkedList2.size();
						Log.i("test", i + "");

					} else {
						checkedList2.remove(arSrc.get(pos).phoneNumber);
						cb.setChecked(false);
						checkedPosition1[pos] = 0;
					}
				}
			});

			final CheckBox cb2 = (CheckBox) convertView
					.findViewById(R.id.refeaddress4);
			cb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {// ///////////////////////체크 되는 순간
											// 다이얼로그 뜨면서 선택하게 한다.
					if (isChecked) {
						cb2.setChecked(true);
						checkedList2.put(arSrc.get(pos).emailAddress,
								arSrc.get(pos)._id);
						checkedPosition2[pos] = 1;

						int i = checkedList2.size();
						// Log.i(arSrc.get(pos)._id, arSrc.get(pos).name);
					} else {
						checkedList2.remove(arSrc.get(pos).emailAddress);
						cb2.setChecked(false);
						checkedPosition2[pos] = 0;

					}
				}
			});

			if (arSrc.get(pos).phoneNumber.equals(""))// 전화번호가 없을 경우
			{
				cb.setEnabled(false);
			}
			if (arSrc.get(pos).emailAddress.equals(""))// 이메일이 없을 경우
			{
				cb2.setEnabled(false);
			}
			if (checkedPosition1[pos] == 1) {
				cb.setChecked(true);
			} else {

				cb.setChecked(false);
			}

			if (checkedPosition2[pos] == 1) {
				cb2.setChecked(true);
			} else {

				cb2.setChecked(false);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (checkedList2.size() != 0) {
			Iterator itr = checkedList2.keySet().iterator();

			Intent intent3 = getIntent();
			int temp = intent3.getIntExtra("addressCount", 0);

			if (temp + checkedList2.size() > 10)// 수신자가 20개를 넘어갈 경우
			{
				Toast.makeText(this, "수신자 수 제한을 초과하였습니다", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Intent intent2 = new Intent();
			while (itr.hasNext()) {

				String key = (String) itr.next();// 키 값은 전화번호나 이메일
				String value = checkedList2.get(key);// 벨류는 id

				Log.i("key", key);

				address.add(key);

			}

			intent2.putStringArrayListExtra("address", address);
			this.setResult(3, intent2);
		}

		finish();

	}
}