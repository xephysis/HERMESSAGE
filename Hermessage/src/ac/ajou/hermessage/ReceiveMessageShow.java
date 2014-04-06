package ac.ajou.hermessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
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

public class ReceiveMessageShow extends Activity implements OnClickListener {

	EditText fromText, dateText, contentText;
	String from, subject, contentType, contentLocation, date, content;
	Button backToReceiveMessage, deleteReceiveMessage;
	MM mm;
	ArrayList<String> attachFilesArray = new ArrayList<String>();

	WordDBHelper mHelper;

	ImageView[] imagePreview = new ImageView[100];
	TextView[] imageText = new TextView[100];
	LinearLayout attachImageShow;
	LinearLayout attachImageTitle;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receivemessageshow);

		mHelper = new WordDBHelper(this);

		fromText = (EditText) findViewById(R.id.fromInReceive);
		dateText = (EditText) findViewById(R.id.dateInReceive);
		contentText = (EditText) findViewById(R.id.contentInReceive);

		backToReceiveMessage = (Button) findViewById(R.id.backToReceiveMessage);
		deleteReceiveMessage = (Button) findViewById(R.id.deleteReceiveMessage);

		backToReceiveMessage.setOnClickListener(this);
		deleteReceiveMessage.setOnClickListener(this);

		Intent intent = getIntent();

		fromText.setText(intent.getStringExtra("From"));
		fromText.setClickable(false);

		dateText.setText(intent.getStringExtra("Date"));

		dateText.setClickable(false);
		contentText.setText(intent.getStringExtra("Content"));
		contentText.setClickable(false);

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
		// Log.i("path1", "//mnt/sdcard/mmfile/161709072.png");
		// Log.i("path2", attachFilesString);

		/*
		 * Scanner tokenize = new Scanner(attachFilesString); while
		 * (tokenize.hasNext(";")) { attachFilesArray.add(tokenize.next()); }
		 * 
		 * Iterator<String> itr = attachFilesArray.iterator(); while
		 * (itr.hasNext()) { itr.next(); }
		 */

		// 여기서 텍스트가 있다면 기본적으로 텍스트를 보여주고 첨부파일은 목록으로 보여주던가
		// 스크롤뷰 띄워서 이미지들 아래에 쭉 나열하거나 그 방식으로 해야할듯 싶음
		// 현재 방식 한개만 보여줌

		// contentLocationText.setText(mm.attachFilePath);

		// 파일이 한개 이상 들어올 수 있으므로 파일 경로 파싱해서 경우에 따라 직접 띄워야 함(jpg,png,mp3등)

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.backToReceiveMessage:

			intent.putExtra("TextOut", "asdf");// //////
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.deleteReceiveMessage:
			SQLiteDatabase db;
			db = mHelper.getWritableDatabase();

			Intent intent3 = getIntent();
			db.delete("ReceiveMessage",
					"_id = '" + intent3.getStringExtra("_id") + "'", null);
			Intent intent2 = getIntent();
			Log.i("_id", intent2.getStringExtra("_id"));

			mHelper.close();
			db.close();

			intent.putExtra("TextOut", "asdf");// /////
			setResult(RESULT_OK, intent);
			finish();

			break;

		default:
			break;
		}

	}
}
