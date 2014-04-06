package ac.ajou.hermessage;

import java.util.ArrayList;

import ac.ajou.hermessage.R;
import ac.ajou.hermessage.AddressConfig.Info;
import ac.ajou.hermessage.R.id;
import ac.ajou.hermessage.R.layout;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddressAdd extends Activity implements OnClickListener {

	EditText name, phoneNum, emailAddress, memo;
	Button saveaddress, canceladdress;
	WordDBHelper mHelper;
	private Cursor mCursor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressadd);

		mHelper = new WordDBHelper(this);

		name = (EditText) findViewById(R.id.inputname);
		phoneNum = (EditText) findViewById(R.id.inputnum);
		emailAddress = (EditText) findViewById(R.id.inputaddress);
		memo = (EditText) findViewById(R.id.inputmemo);

		saveaddress = (Button) findViewById(R.id.saveaddress);
		canceladdress = (Button) findViewById(R.id.canceladdress);

		saveaddress.setOnClickListener(this);
		canceladdress.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SQLiteDatabase db;
		ContentValues row;

		switch (v.getId()) {
		case R.id.saveaddress:// //////

			char[] checkPhoneNum = phoneNum.getText().toString().toCharArray();

			for (int i = 0; i < checkPhoneNum.length; i++) {
				if (checkPhoneNum[i] < 48 || checkPhoneNum[i] > 57) {
					Toast.makeText(this, "잘못된 입력입니다", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			// ///////////////////단말기 입력 오류 체크

			SQLiteDatabase db2 = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db2.rawQuery("SELECT * FROM AddressBook", null);
			// mCursor.moveToFirst();
			int i = 0;
			while (mCursor.moveToNext()) {

				if (mCursor.getString(mCursor.getColumnIndex("PhoneNumber"))
						.equals(phoneNum.getText().toString())
						&& !phoneNum.getText().toString().equals("")) {
					Toast.makeText(this, "이미 입력되어 있는 전화번호 입니다",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (mCursor.getString(mCursor.getColumnIndex("EmailAddress"))
						.equals(emailAddress.getText().toString())
						&& !emailAddress.getText().toString().equals("")) {
					Toast.makeText(this, "이미 입력되어 있는 이메일입니다",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (name.getText().toString().equals("")) {
				Toast.makeText(this, "등록할 사람의 이름을 입력해 주세요", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (phoneNum.getText().toString().equals("")
					&& emailAddress.getText().toString().equals("")) {
				Toast.makeText(this, "등록할 사람의 단말기 번호 혹은 이메일을 적어도 하나 입력해 주세요",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// ///////////////////////단말기 번호 이메일 중복 체크
			Log.i("test", "gogo");
			db = mHelper.getWritableDatabase();
			// insert �޼���� ����
			row = new ContentValues();
			row.put("Name", name.getText().toString());
			row.put("PhoneNumber", phoneNum.getText().toString());
			row.put("EmailAddress", emailAddress.getText().toString());
			row.put("Memo", memo.getText().toString());
			db.insert("AddressBook", null, row);
			mHelper.close();

			finish();
			// mText.setText("Insert Success");
			break;
		case R.id.canceladdress:// ////

			break;

		default:
			break;
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
}
