package ac.ajou.hermessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preference extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.data_prefactivity);

		SharedPreferences mainPreference;
		// 1. \res\xml\preferences.xml로 부터 Preference 계층구조를 읽어와
		// 2. 이 PreferenceActivity의 계층구조로 지정/표현 하고
		// 3. \data\data\패키지이름\shared_prefs\패키지이름_preferences.xml 생성
		// 4. 이 후 Preference에 변경 사항이 생기면 파일에 자동 저장

		mainPreference = PreferenceManager.getDefaultSharedPreferences(this);
	}
}
