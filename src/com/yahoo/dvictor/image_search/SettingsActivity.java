package com.yahoo.dvictor.image_search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.yahoo.dvictor.image_search.models.SearchFilters;

public class SettingsActivity extends Activity {
	// Member Variables & Models
	private SearchFilters searchFilters;
	// Remembered Views for Easy Access (without having to find each time)
	Spinner  spColor;
	Spinner  spSize;
	Spinner  spType;
	EditText etSite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		searchFilters = (SearchFilters) getIntent().getSerializableExtra("settings");
		// Remember views for easy access later
		spColor = (Spinner ) findViewById(R.id.spFilterColor);
		spSize  = (Spinner ) findViewById(R.id.spFilterSize );
		spType  = (Spinner ) findViewById(R.id.spFilterType );
		etSite  = (EditText) findViewById(R.id.etFilterSite );
		// Initialize views to existing settings.
		setViewsToCurrentSettings();
	}
	
	private void setViewsToCurrentSettings(){
		if(!searchFilters.color.isEmpty()) setSpinnerToValue(spColor, searchFilters.color);
		if(!searchFilters.size .isEmpty()) setSpinnerToValue(spSize , searchFilters.size );
		if(!searchFilters.type .isEmpty()) setSpinnerToValue(spType , searchFilters.type );
		etSite.setText(searchFilters.site);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** User clicks save. */
	public void save(View v){
		// 1. Read Current Settings from Input Fields into Model
		searchFilters.color = spColor.getSelectedItem().toString();
		searchFilters.size  = spSize .getSelectedItem().toString();
		searchFilters.type  = spType .getSelectedItem().toString();
		searchFilters.site  = etSite .getText        ().toString();
		// - If selected all, change to empty string so we don't have to handle special case later.
		if(searchFilters.color.equalsIgnoreCase("ALL")) searchFilters.color="";
		if(searchFilters.size .equalsIgnoreCase("ALL")) searchFilters.size ="";
		if(searchFilters.type .equalsIgnoreCase("ALL")) searchFilters.type ="";
		// 2. Return Result
		Intent i = new Intent();
		i.putExtra("settings", searchFilters);
		setResult(RESULT_OK, i);
		finish();
	}

	/** Utility to set spinners back to a value based on their value instead of by position. */
	public void setSpinnerToValue(Spinner spinner, String value) {
	    int index = 0;
	    SpinnerAdapter adapter = spinner.getAdapter();
	    for (int i = 0; i < adapter.getCount(); i++) {
	        if (adapter.getItem(i).equals(value)) {
	            index = i;
	            break; // terminate loop
	        }
	    }
	    spinner.setSelection(index);
	}	
}
