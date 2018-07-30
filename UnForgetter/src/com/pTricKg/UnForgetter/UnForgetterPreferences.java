package com.pTricKg.UnForgetter;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;

import com.pTricKg.UnForgetter.R;

public class UnForgetterPreferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
		      addPreferencesFromResource(R.xml.task_preferences);
		      
		    }
		 

		// Set the time default to a numeric number only
		EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key)); 	
		timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance()); 
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	  public void onBuildHeaders(List<Header> target) {
	    loadHeadersFromResource(R.xml.task_preferences, target);
	  }
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	  public static class First extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);

	      addPreferencesFromResource(R.xml.task_preferences);
	    }
	  }
}
