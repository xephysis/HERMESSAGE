package ac.ajou.hermessage;

import ac.ajou.hermessage.R;
import ac.ajou.hermessage.AddressAdd.WordDBHelper;
import ac.ajou.hermessage.R.id;
import ac.ajou.hermessage.R.layout;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddressChange extends Activity implements OnClickListener {

	EditText nameEdit, phoneNumberEdit, emailAddressEdit, memoEdit;
	String id, name, phoneNumber, emailAddress, memo;
	Button changeButton, backButton;
	TextView addressChangeTitle;
	WordDBHelper mHelper;
	private Cursor mCursor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addresschange);

		mHelper = new WordDBHelper(this);
		Typeface face = Typeface.createFromAsset(getAssets(), "PILGI1.TTF");
		addressChangeTitle = (TextView) findViewById(R.id.addresschangetitle);
		nameEdit = (EditText) findViewById(R.id.nameEdit);
		phoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEdit);
		emailAddressEdit = (EditText) findViewById(R.id.emailAddressEdit);
		memoEdit = (EditText) findViewById(R.id.memoEdit);

		Intent intent = getIntent();

		id = intent.getStringExtra("_id");
		phoneNumber = intent.getStringExtra("phoneNumber");
		emailAddress = intent.getStringExtra("emailAddress");
		name = intent.getStringExtra("name");
		memo = intent.getStringExtra("memo");
		addressChangeTitle.setText("주소록 변경");
		nameEdit.setText(name);
		phoneNumberEdit.setText(phoneNumber);
		emailAddressEdit.setText(emailAddress);
		memoEdit.setText(memo);

		nameEdit.setTypeface(face);
		phoneNumberEdit.setTypeface(face);
		emailAddressEdit.setTypeface(face);
		memoEdit.setTypeface(face);
		addressChangeTitle.setTypeface(face);
		addressChangeTitle.setTextSize(50);
		addressChangeTitle.setTextColor(Color.BLUE);

		changeButton = (Button) findViewById(R.id.changeButton);
		backButton = (Button) findViewById(R.id.backButton);

		changeButton.setOnClickListener(this);
		backButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.changeButton:

			SQLiteDatabase db2 = openOrCreateDatabase("db.sqlite",
					Context.MODE_PRIVATE, null);
			mCursor = db2.rawQuery("SELECT * FROM AddressBook", null);
			// mCursor.moveToFirst();
			int i = 0;
			while (mCursor.moveToNext()) {

				if (!id.equals(mCursor.getString(mCursor.getColumnIndex("_id")))) {
					if (mCursor
							.getString(mCursor.getColumnIndex("PhoneNumber"))
							.equals(phoneNumberEdit.getText().toString())
							&& !phoneNumberEdit.getText().toString().equals("")) {
						Toast.makeText(this, "이미 입력되어 있는 전화번호 입니다",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (mCursor.getString(
							mCursor.getColumnIndex("EmailAddress")).equals(
							emailAddressEdit.getText().toString())
							&& !emailAddressEdit.getText().toString()
									.equals("")) {
						Toast.makeText(this, "이미 입력되어 있는 이메일입니다",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			SQLiteDatabase db;
			ContentValues row;
			db = mHelper.getWritableDatabase();
			row = new ContentValues();

			row.put("name", nameEdit.getText().toString());
			row.put("phoneNumber", phoneNumberEdit.getText().toString());
			row.put("emailAddress", emailAddressEdit.getText().toString());
			row.put("memo", memoEdit.getText().toString());

			db.update("AddressBook", row, "_id = " + id, null);

			Intent intent = new Intent();
			intent.putExtra("name", nameEdit.getText().toString());
			intent.putExtra("phoneNumber", phoneNumberEdit.getText().toString());
			intent.putExtra("emailAddress", emailAddressEdit.getText()
					.toString());
			intent.putExtra("memo", memoEdit.getText().toString());
			this.setResult(RESULT_OK, intent);
			mHelper.close();

			finish();
			break;
		case R.id.backButton:
			Intent intent2 = new Intent();
			intent2.putExtra("name", nameEdit.getText().toString());
			intent2.putExtra("phoneNumber", phoneNumberEdit.getText()
					.toString());
			intent2.putExtra("emailAddress", emailAddressEdit.getText()
					.toString());
			intent2.putExtra("memo", memoEdit.getText().toString());
			setResult(RESULT_OK, intent2);
			finish();
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
