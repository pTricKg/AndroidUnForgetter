package com.pTricKg.UnForgetter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class UnForgetterListActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String[] PROJECTION = new String[] { "_id",
			"text_column" };

	// The loader's unique id. Loader ids are specific to the Activity or
	// Fragment in which they reside.
	private static final int LOADER_ID = 1;

	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	// The adapter that binds our data to the ListView
	private SimpleCursorAdapter mAdapter;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private UnForgetterDbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] dataColumns = { "text_column" };
		int[] viewIDs = { R.id.title };
		setContentView(R.layout.unforgetter_list);

		// Initialize the adapter. Note that we pass a 'null' Cursor as the
		// third argument. We will pass the adapter a Cursor only when the
		// data has finished loading for the first time (i.e. when the
		// LoaderManager delivers the data to onLoadFinished). Also note
		// that we have passed the '0' flag as the last argument. This
		// prevents the adapter from registering a ContentObserver for the
		// Cursor (the CursorLoader will do this for us!).
		mAdapter = new SimpleCursorAdapter(this, R.layout.unforgetter_list,
				null, dataColumns, viewIDs, 0);

		// Associate the (now empty) adapter with the ListView.
		setListAdapter(mAdapter);

		// The Activity (which implements the LoaderCallbacks<Cursor>
		// interface) is the callbacks object through which we will interact
		// with the LoaderManager. The LoaderManager uses this object to
		// instantiate the Loader and to notify the client when data is made
		// available/unavailable.
		mCallbacks = this;
		
		// Initialize the Loader with id '1' and callbacks 'mCallbacks'.
	    // If the loader doesn't already exist, one is created. Otherwise,
	    // the already created Loader is reused. In either case, the
	    // LoaderManager will manage the Loader across the Activity/Fragment
	    // lifecycle, will receive any new loads once they have completed,
	    // and will report this new data back to the 'mCallbacks' object.
	    LoaderManager lm = getLoaderManager();
	    lm.initLoader(LOADER_ID, null, mCallbacks);

		mDbHelper = new UnForgetterDbAdapter(this);
		mDbHelper.open();
		fillData();

		registerForContextMenu(getListView());

	}

	// loads SQL data into ListView
	private void fillData() {
		Cursor taskerCursor = mDbHelper.fetchAllReminders();
		startManagingCursor(taskerCursor);

		// create array. returns task title
		String[] from = new String[] { UnForgetterDbAdapter.KEY_TITLE };

		// specify which fields of array to return
		int[] to = new int[] { R.id.textRow };

		// set cursor to pull form XML layout
		SimpleCursorAdapter reminders = new SimpleCursorAdapter(this,
				R.layout.unforgetter_row, taskerCursor, from, to);
		setListAdapter(reminders);
	}

	// creates menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu, menu);
		return true;

	}

	// handles user interaction in menu
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_insert:
			createReminder();
			return true;

		case R.id.menu_settings:
			Intent i = new Intent(this, UnForgetterPreferences.class);
			startActivity(i);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// creates ContextMenu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu_item_longpress, menu);

	}

	// Start UnForgetterEditActivity. for creating UnForgetter tasks
	private void createReminder() {

		Intent i = new Intent(this, UnForgetterEditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// set up event-handling
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Set up Intent for checking whether to edit present tasks or create
		// new
		Intent i = new Intent(this, UnForgetterEditActivity.class);
		i.putExtra(UnForgetterDbAdapter.KEY_ROWID, id); // pull data
		startActivityForResult(i, ACTIVITY_EDIT);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData(); // returns edited data from DB

	}

	// handles user interaction for ContextMenu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			// delete task
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteReminder(info.id);
			fillData(); // returns deleted data from DB
			return true;
		}
		return super.onContextItemSelected(item);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Create a new CursorLoader with the following query parameters.
	    return new CursorLoader(UnForgetterListActivity.this, CONTENT_URI,
	        PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// A switch-case is useful when dealing with multiple Loaders/IDs
	    switch (loader.getId()) {
	      case LOADER_ID:
	        // The asynchronous load is complete and the data
	        // is now available for use. Only now can we associate
	        // the queried Cursor with the SimpleCursorAdapter.
	        mAdapter.swapCursor(cursor);
	        break;

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// For whatever reason, the Loader's data is now unavailable.
	    // Remove any references to the old data by replacing it with
	    // a null Cursor.
	    mAdapter.swapCursor(null);

	}

}