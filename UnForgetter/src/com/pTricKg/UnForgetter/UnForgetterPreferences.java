package com.pTricKg.UnForgetter;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import com.pTricKg.UnForgetter.R;

public class UnForgetterPreferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);

		// Set the time default to a numeric number only
		EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key)); 	
		timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance()); 
	}
}
