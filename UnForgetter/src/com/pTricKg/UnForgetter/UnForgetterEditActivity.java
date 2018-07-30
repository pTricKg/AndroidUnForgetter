package com.pTricKg.UnForgetter;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class UnForgetterEditActivity extends Activity {
	
	
	//dialog stuff for date/time picker
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;

	//date and time stuff
	private static final String DATE_FORMAT = "MM-dd-yyyy"; 
	private static final String TIME_FORMAT = "hh:mm";
	public static final String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm:ss";

//	initialized variables
	private EditText mTitleText;
    private EditText mBodyText;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mConfirmButton;
    private Long mRowId;
    private UnForgetterDbAdapter mDbHelper;
    private Calendar mCalendar;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new UnForgetterDbAdapter(this);
        
        setContentView(R.layout.unforgetter_edit);
        
        //initializing variables
        mCalendar = Calendar.getInstance(); 
        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDateButton = (Button) findViewById(R.id.date_button);
        mTimeButton = (Button) findViewById(R.id.unforgetter_timer);
      
        mConfirmButton = (Button) findViewById(R.id.unforgetter_save);
       
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(UnForgetterDbAdapter.KEY_ROWID) 
                							: null;
      
        registerButtonListenersAndSetDefaultText();
    }

  //links creation/edit tasks from ListActivity
	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();            
			mRowId = extras != null ? extras.getLong(UnForgetterDbAdapter.KEY_ROWID) 
									: null;

		}
	}
    
    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close(); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open(); 
    	setRowIdFromIntent();
		populateFields();
    }
    
    //called by showDialog method in registerButtonListener
    //int id passed into showDialog
    @SuppressWarnings("deprecation")
	@Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case DATE_PICKER_DIALOG: 			//determines Id passed-if same as showDialog, returns user value
    			return showDatePicker();
    		case TIME_PICKER_DIALOG: 
    			return showTimePicker(); 
    	}
    	return super.onCreateDialog(id);
    }
    
    //for user selection of Date
 	private DatePickerDialog showDatePicker() {


		DatePickerDialog datePicker = new DatePickerDialog(UnForgetterEditActivity.this, new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDateButtonText(); 
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)); 
		return datePicker; 
	}

 	//for user selection of Time
    private TimePickerDialog showTimePicker() {

    	TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

    		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute); 
				updateTimeButtonText(); 
			}
		}, mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE), true); 

    	return timePicker; 
	}
 	
    //set-up date/time picker listener
	private void registerButtonListenersAndSetDefaultText() {

		mDateButton.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);  
			}
		}); 


		mTimeButton.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG); 
			}
		}); 

		mConfirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState(); 
        		setResult(RESULT_OK);
        	    Toast.makeText(UnForgetterEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
        	    finish(); 
        	}
          
        });

		  //called to update button text
		  updateDateButtonText(); 
	      updateTimeButtonText();
	}
   
    @SuppressWarnings("deprecation")
	private void populateFields()  {
    	    	
    	// Only populate the text boxes and change the calendar date
    	// if the row is not null from the DB 
        if (mRowId != null) {
            Cursor reminder = mDbHelper.fetchReminder(mRowId);
            startManagingCursor(reminder);
            mTitleText.setText(reminder.getString(
    	            reminder.getColumnIndexOrThrow(UnForgetterDbAdapter.KEY_TITLE)));
            mBodyText.setText(reminder.getString(
                    reminder.getColumnIndexOrThrow(UnForgetterDbAdapter.KEY_BODY)));
            
            // Get the date from the DB and format it for our use 
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
            Date date = null;

				
				try {
					String dateString = reminder.getString(reminder.getColumnIndexOrThrow(UnForgetterDbAdapter.KEY_DATE_TIME)); 
					date = (Date) dateTimeFormat.parse(dateString);
					mCalendar.setTime(date);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("TaskerEditActivity", e.getMessage(), e); 
				}
	             

        } else {
        	// This is a new task - add defaults from preferences if set 
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        	String defaultTitleKey = getString(R.string.pref_task_title_key); 
        	String defaultTimeKey = getString(R.string.pref_default_time_from_now_key); 
        	
        	String defaultTitle = prefs.getString(defaultTitleKey, null);
        	String defaultTime = prefs.getString(defaultTimeKey, null); 
        	
        	if(defaultTitle != null)
        		mTitleText.setText(defaultTitle); 
        	
        	if(defaultTime != null)
        		mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
        	
        }
        
        updateDateButtonText(); 
        updateTimeButtonText(); 
        	
    }

    //actually implements user selection
	private void updateTimeButtonText() {
		// Set the time button text based upon the value from the DB
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.US); 
        String timeForButton = timeFormat.format(mCalendar.getTime()); 
        mTimeButton.setText(timeForButton);
	}

	private void updateDateButtonText() {
		// Set the date button text based upon the value from the DB 
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US); 
        String dateForButton = dateFormat.format(mCalendar.getTime()); 
        mDateButton.setText(dateForButton);
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(UnForgetterDbAdapter.KEY_ROWID, mRowId);
    }
    

    //determines whether to save new or an updated task
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US); 
    	String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

        if (mRowId == null) {
        	
        	long id = mDbHelper.createReminder(title, body, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
        }
       
        //creates notification for set UnForgetter tasks
        new UnForgetterManager(this).setReminder(mRowId, mCalendar); 
    }
}