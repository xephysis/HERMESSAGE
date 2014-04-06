package ac.ajou.hermessage;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class MMBox extends TabActivity {

	TabHost TH;
	MM mm = new MM();

	Context mContext;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TH = getTabHost();
		mContext = this;
		// LayoutInflater inflater = LayoutInflater.from(this);
		// inflater.inflate(R.layout.mmbox, TH.getTabContentView(), true);

		TH.addTab(TH.newTabSpec("list1").setIndicator("수신함")
				.setContent(new Intent(this, MMBoxReceive.class)));
		TH.addTab(TH.newTabSpec("list2").setIndicator("발신함")
				.setContent(new Intent(this, MMBoxSend.class)));
		TH.addTab(TH.newTabSpec("list3").setIndicator("통지함")
				.setContent(new Intent(this, MMBoxNotification.class)));
		TH.addTab(TH.newTabSpec("list3").setIndicator("임시보관함")
				.setContent(new Intent(this, MMBoxDraft.class)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "환경 설정");
		menu.add(0, 2, 0, "메시지 작성");
		menu.add(0, 3, 0, "주소록 관리");
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent goPreference = new Intent(MMBox.this, Preference.class);
			startActivity(goPreference);
			break;
		case 2:
			Intent goMMmake = new Intent(MMBox.this, MMMake.class);
			goMMmake.putExtra("mm", mm);
			((Hermessage) Hermessage.mContext).temp((Activity) mContext);
			((Hermessage) Hermessage.mContext).clearActivity();
			startActivity(goMMmake);
			break;
		case 3:
			Intent goAddress = new Intent(MMBox.this, AddressConfig.class);
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
}
