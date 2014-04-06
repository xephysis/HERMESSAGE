package ac.ajou.hermessage;

import ac.ajou.hermessage.R;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AddressInfo extends Activity implements OnClickListener {

	String name, phoneNumber, emailAddress, memo, _id;
	TextView titleText, nameText, phoneNumberText, emailAddressText, memoText;
	Button changeAddress, backToAddressConfig, deleteAddressConfig;
	WordDBHelper mHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressinfo);

		Typeface face = Typeface.createFromAsset(getAssets(), "PILGI1.TTF");

		titleText = (TextView) findViewById(R.id.addressinfotitle);
		nameText = (TextView) findViewById(R.id.nameInInfo);
		phoneNumberText = (TextView) findViewById(R.id.phoneInInfo);
		emailAddressText = (TextView) findViewById(R.id.emailInInfo);
		memoText = (TextView) findViewById(R.id.memoInInfo);

		changeAddress = (Button) findViewById(R.id.changeToAddress);
		deleteAddressConfig = (Button) findViewById(R.id.deleteAddressInfo);
		backToAddressConfig = (Button) findViewById(R.id.backToAddress);
		Intent intent = getIntent();

		_id = intent.getStringExtra("_id");
		phoneNumber = intent.getStringExtra("phoneNumber");
		emailAddress = intent.getStringExtra("emailAddress");
		name = intent.getStringExtra("name");
		memo = intent.getStringExtra("memo");

		titleText.setText("주소록 정보");
		nameText.setText(intent.getStringExtra("name"));
		phoneNumberText.setText(intent.getStringExtra("phoneNumber"));
		emailAddressText.setText(intent.getStringExtra("emailAddress"));
		memoText.setText(intent.getStringExtra("memo"));

		nameText.setTypeface(face);
		phoneNumberText.setTypeface(face);
		emailAddressText.setTypeface(face);
		memoText.setTypeface(face);
		titleText.setTypeface(face);
		titleText.setTextSize(50);
		titleText.setTextColor(Color.BLUE);

		changeAddress.setOnClickListener(this);
		deleteAddressConfig.setOnClickListener(this);
		backToAddressConfig.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.changeToAddress:
			Intent intent = new Intent(AddressInfo.this, AddressChange.class);
			intent.putExtra("_id", _id);
			intent.putExtra("name", name);
			intent.putExtra("phoneNumber", phoneNumber);
			intent.putExtra("emailAddress", emailAddress);
			intent.putExtra("memo", memo);

			startActivityForResult(intent, 0);
			break;
		case R.id.deleteAddressInfo:
			mHelper = new WordDBHelper(this);
			SQLiteDatabase db;
			db = mHelper.getWritableDatabase();
			db.delete("AddressBook", "_id = '" + _id + "'", null);
			Intent intent2 = new Intent();
			setResult(RESULT_OK, intent2);
			finish();
			break;
		case R.id.backToAddress:
			Intent intent3 = new Intent();
			setResult(RESULT_OK, intent3);
			finish();
			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 수행을 제대로 한 경우
		if (resultCode == RESULT_OK && data != null) {
			nameText.setText(data.getStringExtra("name"));
			phoneNumberText.setText(data.getStringExtra("phoneNumber"));
			emailAddressText.setText(data.getStringExtra("emailAddress"));
			memoText.setText(data.getStringExtra("memo"));
		}
		// 수행을 제대로 하지 못한 경우
		else if (resultCode == RESULT_CANCELED) {

		}

	}

}
