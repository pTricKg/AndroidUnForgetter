package com.pTricKg.Tasker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TaskerDbAdapter {
	
	//DataBase
	private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "reminders";
    private static final int DATABASE_VERSION = 3;
    
	public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_DATE_TIME = "reminder_date_time"; 
    public static final String KEY_ROWID = "_id";
    
    
    private static final String TAG = "TaskerDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    //DB creation SQL statement
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
            		+ KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_TITLE + " text not null, " 
                    + KEY_BODY + " text not null, " 
                    + KEY_DATE_TIME + " text not null);"; 

    

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    //constructor - takes context to allow DB to be opened/created
    
    //@param ctx Context within which to work
    
    public TaskerDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    // Open DB, if cannot, try create new
 	// If cannot, throw exception to signal failure
 	
 	// @return this (self reference, allowing this to be chained in initialization call)
 	// @throws SQLException if DB cannot be opened or created

    public TaskerDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    // Create new task using the title, body and date/time provided 
 	// If successfully created return the new rowId
 	// otherwise return -1 to indicate failure
   
    public long createReminder(String title, String body, String reminderDateTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE_TIME, reminderDateTime); 

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete task with the given rowId
    
    // @param rowId id of task to delete
    // @return true if deleted, false otherwise
    
    public boolean deleteReminder(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // Return Cursor over the list of tasks in DB
    
    //  Cursor over all tasks
    
    public Cursor fetchAllReminders() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_DATE_TIME}, null, null, null, null, null);
    }

    // Return Cursor positioned at task that matches given rowId
    
    // @param rowId id of task to retrieve
    // @return Cursor positioned to matching task, if found
    // @throws SQLException if task could not be found/retrieved
    
    public Cursor fetchReminder(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_BODY, KEY_DATE_TIME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    // Update tasks using the details provided. Task to be updated is
    // specified using the rowId, and altered to use the title, body, date/time values
        
    // @param rowId id to update
    // @param title value to set title
    // @param body value to set body
    // @param reminderDateTime value to set time
    // @return true if successfully updated, otherwise false
    
    public boolean updateReminder(long rowId, String title, String body, String reminderDateTime) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE_TIME, reminderDateTime);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
