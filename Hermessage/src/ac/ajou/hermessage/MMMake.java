package ac.ajou.hermessage;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MMMake extends Activity {

	EditText title, context;
	Button attachmemt, receiverSetting, attachImage, attachAudio;
	PopupWindow popup;
	View popupview;
	LinearLayout linear;
	ImageView[] imagePreview = new ImageView[100];
	Context mContext;
	Bitmap selPhoto;
	TextView[] imageTitle = new TextView[100];
	TextView[] imageSize = new TextView[100];

	final static int ACT_EDIT = 0;
	String subject;
	String content;
	String path;
	MM mm;

	private static final int CAMERA_CAPTURE = 1;
	private static final int GALLERY_CAPTURE = 2;
	HashMap<String, String> pathList = new HashMap<String, String>();

	LinearLayout linearImage;
	LinearLayout linearText;
	int imageCount = 1;
	int imageNumber = 0;

	/*
	 * protected void onDestroy() { m_cPhoneCursor.close(); super.onDestroy(); }
	 */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmmake);
		mContext = this;

		context = (EditText) findViewById(R.id.Context);
		attachmemt = (Button) findViewById(R.id.Attachment);
		receiverSetting = (Button) findViewById(R.id.Receiver);

		linear = (LinearLayout) findViewById(R.id.linear);

		Intent intent = getIntent();
		mm = (MM) intent.getSerializableExtra("mm");

		if (mm.messageContent != null) {
			context.setText(mm.messageContent);
		}

		/*
		 * if (mm.attachFilePath != null) {
		 * 
		 * for (int i = 0; i < mm.attachFilePath.size(); i++) {
		 * 
		 * } }// //첨부파일 있을 경우 다시 띄워준다
		 */TextView text1 = (TextView) findViewById(R.id.textView2);
		TextView text2 = (TextView) findViewById(R.id.textView3);

		TextView title = (TextView) findViewById(R.id.mmmake_title);
		Typeface face = Typeface.createFromAsset(getAssets(), "HMKMYEOP.TTF");
		title.setTypeface(face);
		title.setText("메시지 작성");
		context.setTypeface(face);
		text1.setTypeface(face);
		text2.setTypeface(face);
		title.setTextSize(50);

		attachmemt.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(intent, GALLERY_CAPTURE);

			}
		});

		receiverSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MMMake.this, RecipientSubmit.class);

				try {
					// mm = new MM();
					mm.x_mms_message_type = "m-send-req";
					// mm.subject = title.getText().toString();
					// 제목 자체는 옵션이므로 오류 처리 해 줄 필요 없지만 뒤에서 읽다가 죽을수도 있음
					mm.messageContent = context.getText().toString();
					if (path != null) {
						Log.w("HERMESSAGE", path);
						// mm.attachFilePath.add(path.substring(path.indexOf(":")
						// + 3));
					}
					Iterator itr = pathList.keySet().iterator();
					while (itr.hasNext()) {
						String key = (String) itr.next();
						String value = pathList.get(key);
						mm.attachFilePath.add(value.substring(path.indexOf(":") + 3));
					}

					// 파일하고 내용 둘다 없으면 오류처리임

					intent.putExtra("mm", mm);
				} catch (Exception e) {
					e.getStackTrace();
					Log.e("HERMESSAGE", e.getMessage());
				}

				((Hermessage) Hermessage.mContext).temp((Activity) mContext);

				startActivityForResult(intent, ACT_EDIT);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "환경 설정");
		menu.add(0, 2, 0, "보관함");
		menu.add(0, 3, 0, "주소록 관리");
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent goPreference = new Intent(MMMake.this, Preference.class);
			startActivity(goPreference);
			break;
		case 2:
			Intent goBox = new Intent(MMMake.this, MMBox.class);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goBox);
			break;
		case 3:
			Intent goAddress = new Intent(MMMake.this, AddressConfig.class);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goAddress);
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

		case GALLERY_CAPTURE:
			Log.i("gallerytest", "gallerytest");
			try {
				if (data != null) {

					Log.i("gallerytest", "gallerytest");

					Uri selPhotoUri = data.getData();
					selPhoto = Images.Media.getBitmap(getContentResolver(),
							selPhotoUri);
					selPhoto = Bitmap.createScaledBitmap(selPhoto, 150, 150,
							true);

					// 이것도 일단 멈춰놨음

					path = selPhotoUri.getPath();
					Log.i("camera", path);
					Cursor c = getContentResolver().query(selPhotoUri, null,
							null, null, null);
					c.moveToNext();
					String path2 = c.getString(c
							.getColumnIndex(MediaStore.MediaColumns.DATA));
					Uri uri = Uri.fromFile(new File(path2));
					Log.e("URI", uri.toString());

					path = uri.toString();
					Bitmap bm = BitmapFactory.decodeFile(data
							.getStringExtra("Path"));
					Log.i("camera", path);
					c.close();

					/*
					 * final String realPath = path .substring(path.indexOf(":")
					 * + 3);
					 * 
					 * File attachFile = new File(realPath); long L = 0; if
					 * (attachFile.exists()) { L = attachFile.length();
					 * Log.i("HERMESSAGE", "첨부된 파일 용량 : " + L + " bytes : " +
					 * attachFile.getAbsoluteFile()); TextView file = new
					 * TextView(this); file.setText(realPath.substring(realPath
					 * .lastIndexOf("/") + 1) + " : " + L + " bytes");
					 * file.setOnClickListener(new OnClickListener() {
					 * 
					 * @Override public void onClick(View v) { // TODO
					 * Auto-generated method stub Log.i("HERMESSAGE", realPath);
					 * 
					 * 정상적으로만 뜨면 화면 보여줘도 됨
					 * 
					 * } }); } Log.i("file size", L + "");// ///파일 사이즈 알아내기 if
					 * (L > 1048576) { Toast.makeText(this,
					 * "첨부파일의 제한용량을 초과하셨습니다 - 1MB", Toast.LENGTH_SHORT).show();
					 * return;
					 * 
					 * }
					 */
					linearImage = (LinearLayout) findViewById(R.id.imagescroll);
					linearText = (LinearLayout) findViewById(R.id.contentscroll);

					imagePreview[imageCount] = new ImageView(this);
					imagePreview[imageCount].setImageBitmap(selPhoto);
					imagePreview[imageCount].setTag("" + imageCount);
					linearImage.addView(imagePreview[imageCount]);

					imageTitle[imageCount] = new TextView(this);
					imageSize[imageCount] = new TextView(this);
					Typeface face = Typeface.createFromAsset(getAssets(),
							"HMKMYEOP.TTF");
					imageTitle[imageCount].setText(path.substring(path
							.lastIndexOf("/") + 1));
					imageTitle[imageCount].setPadding(0, 70, 0, 50);
					imageTitle[imageCount].setTypeface(face);
					linearText.addView(imageTitle[imageCount]);
					pathList.put("" + imageCount, path);
					imagePreview[imageCount]
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(final View v) {
									// TODO Auto-generated method stub
									AlertDialog.Builder bld = new AlertDialog.Builder(
											MMMake.this);
									bld.setTitle("첨부파일 선택")
											.setIcon(R.drawable.icon)
											.setItems(
													R.array.imagedelete,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															Log.i("imagecount",
																	String.valueOf(imageNumber));
															Log.i("imagetag", v
																	.getTag()
																	.toString());
															String[] selects = getResources()
																	.getStringArray(
																			R.array.imagedelete);

															if (selects[which]
																	.equals("첨부삭제")) {
																linearImage
																		.removeView(imagePreview[Integer
																				.parseInt((String) v
																						.getTag())]);
																linearText
																		.removeView(imageTitle[Integer
																				.parseInt((String) v
																						.getTag())]);
																Log.i("imagedelete",
																		"delete");
																pathList.remove(v
																		.getTag()
																		.toString());
																imageNumber--;

															} else if (selects[which]
																	.equals("닫기")) {

															}
														}
													}).show();
								}
							});
					imageCount++;
					imageNumber++;
				}
				break;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
