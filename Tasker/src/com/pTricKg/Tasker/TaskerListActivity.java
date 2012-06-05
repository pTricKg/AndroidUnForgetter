package com.pTricKg.Tasker;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TaskerListActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasker_list);
        registerForContextMenu(getListView());
        
    }
    
    //set up event-handling
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	super.onListItemClick(l, v, position, id);
    	
    }
        //Set up database
        String[] items = new String[]{"Doo", "Dee", "Duh", "Doh"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.tasker_row,
        		R.id.textRow, items);
//        setListAdapter(adapter);
        
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	super.onCreateContextMenu(menu, v, menuInfo);
		}
}