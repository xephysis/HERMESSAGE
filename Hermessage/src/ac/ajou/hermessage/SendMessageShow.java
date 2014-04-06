package ac.ajou.hermessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SendMessageShow extends Activity implements OnClickListener {

	EditText toText, dateText, contentText;
	String from, subject, contentType, contentLocation, date, content;
	Button backToSendMessage, deleteSendMessage;
	MM mm;
	ArrayList<String> attachFilesArray = new ArrayList<String>();

	WordDBHelper mHelper;

	ImageView[] imagePreview = new ImageView[100];
	TextView[] imageText = new TextView[100];
	LinearLayout attachImageShow;
	LinearLayout attachImageTitle;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmessageshow);

		mHelper = new WordDBHelper(this);

		toText = (EditText) findViewById(R.id.toInSend);

		contentText = (EditText) findViewById(R.id.contentInSend);
		dateText = (EditText) findViewById(R.id.dateInSend);

		backToSendMessage = (Button) findViewById(R.id.backToSendMessage);
		deleteSendMessage = (Button) findViewById(R.id.deleteSendMessage);

		backToSendMessage.setOnClickListener(this);
		deleteSendMessage.setOnClickListener(this);

		Intent intent = getIntent();

		toText.setText(intent.getStringExtra("Receiver"));
		dateText.setText(intent.getStringExtra("Date"));
		contentText.setText(intent.getStringExtra("Content"));
		attachImageShow = (LinearLayout) findViewById(R.id.receive_imageshow);
		attachImageTitle = (LinearLayout) findViewById(R.id.receive_imagetitle);

		String attachFilesString = intent.getStringExtra("ContentLocation");

		StringTokenizer tokenize = new StringTokenizer(attachFilesString, ";");

		while (tokenize.hasMoreTokens()) {
			attachFilesArray.add(tokenize.nextToken(";"));
		}

		Iterator<String> itr = attachFilesArray.iterator();
		while (itr.hasNext()) {
			// System.out.println(itr.next());
			File file = new File(itr.next());
			Uri uri = Uri.fromFile(file);
			Bitmap selPhoto = null;
			try {
				int i = 0;
				selPhoto = Images.Media.getBitmap(getContentResolver(), uri);
				selPhoto = Bitmap.createScaledBitmap(selPhoto, 150, 150, true);
				imagePreview[i] = new ImageView(this);
				imagePreview[i].setImageBitmap(selPhoto);
				attachImageShow.addView(imagePreview[i]);

				imageText[i] = new TextView(this);
				imageText[i].setText(attachFilesArray.get(i).substring(
						attachFilesArray.get(i).lastIndexOf("/") + 1));
				imageText[i].setPadding(0, 50, 0, 50);
				attachImageTitle.addView(imageText[i]);

				i++;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.backToSendMessage:

			Intent intent4 = new Intent();
			intent4.putExtra("TextOut", "asdf");// //////
			setResult(RESULT_OK, intent4);
			finish();
			break;
		case R.id.deleteSendMessage:
			SQLiteDatabase db;
			db = mHelper.getWritableDatabase();

			Intent intent3 = getIntent();
			db.delete("SendMessage", "_id = '" + intent3.getStringExtra("_id")
					+ "'", null);

			mHelper.close();
			db.close();

			intent.putExtra("TextOut", "asdf");
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}

	}
}
