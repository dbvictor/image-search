package com.yahoo.dvictor.image_search.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.dvictor.image_search.R;
import com.yahoo.dvictor.image_search.SettingsActivity;
import com.yahoo.dvictor.image_search.adapters.SearchResultsAdapter;
import com.yahoo.dvictor.image_search.models.SearchFilters;
import com.yahoo.dvictor.image_search.models.SearchResult;

public class SearchActivity extends Activity {
	// Remembered Views (instead of searching for them every time)
	private EditText etSearch;
	private GridView gvSearch;
	// Member Variables & Models
	private SearchFilters           searchFilters;
	private ArrayList<SearchResult> searchResults;
	private SearchResultsAdapter    searchResultsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initRememberedViews();
		// Creates data source
		searchResults = new ArrayList<SearchResult>();
		// Attaches data source to an adapter, which can be displayed.
		searchResultsAdapter = new SearchResultsAdapter(this, searchResults);
		// Link the adapter to a view to be displayed
		gvSearch.setAdapter(searchResultsAdapter);
		gvSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Launch the full image display activity.
				// Create intent (way we switch screens in Android)
				Intent i = new Intent(SearchActivity.this, ViewActivity.class);
				// Get the image result to display
				SearchResult result = searchResults.get(position);
				// Pass image result into intent
				// - if simple: i.putExtra("url", result.urlFull);
				i.putExtra("result", result); // must be serializable or parcelable.
				// Launch the new activity
				startActivity(i);
			}
		});
		// Create settings
		searchFilters = new SearchFilters();
		//NO: - If we just came back from the settings activity, load those results.
		//NO: SearchFilters searchFilter = (SearchFilters) getIntent().getSerializableExtra("settings");
		//NO: - otherwise create new settings if not coming from that activity.
		//NO: if(searchFilters==null) searchFilters = new SearchFilters();
	}
	
	private void initRememberedViews(){
		etSearch = (EditText) findViewById(R.id.etSearch);
		gvSearch = (GridView) findViewById(R.id.gvSearch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
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
	
	/** Fired whenever Search button clicked, because of the onClick property we set. */
	public void search(View v){
		String query = etSearch.getText().toString();
		//Toast.makeText(this, "Searching: "+query, Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		// Build Search URL with Filter Parameters
		String url = buildSearchUrl(query);
		Log.d("URL",url);
		// Do Search
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("DEBUG", response.toString());
				// Parse Results
				JSONArray searchResultsJSON = null; // Set only if parsing succeeds.
				try {     searchResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// Display Results
				searchResults.clear(); // Clear only when new search.
				// Add directly to adapter so that it wil auto-notify. (otherwise we could add to arrayList and notify the adapter).
				searchResultsAdapter.addAll(SearchResult.fromJSONArray(searchResultsJSON));
				Log.i("INFO", searchResults.toString());
			}
		});
	}
	
	/** Build the search URL with current filter settings. */
	private String buildSearchUrl(String query){
		// Build Search URL with Filter Parameters
		// - ex: https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
		// - if no params: String urlSearch = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+query+"&rsz=8";
		StringBuilder url = new StringBuilder();
		// - Primary URL
		url.append("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=").append(query);
		// - Max size
		url.append("&rsz=8");
		// - Filters (note we rely on empty string to mean no filter, instead of entirely omitting)
		// -- size
		url.append("&imgsz=").append(searchFilters.size);
		// -- color
		url.append("&imgcolor=").append(searchFilters.color);
		// -- type
		url.append("&imgtype=").append(searchFilters.type);
		// -- site
		url.append("&as_sitesearch=").append(searchFilters.site);
		return url.toString();
	}
	
	/** Method the settings icon calls onClick.  We will launch the settings activity for the user to make changes. */
	public void changeSettings(MenuItem menuItem){
		//Toast.makeText(this, "Settings!", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,SettingsActivity.class);
		i.putExtra("settings", searchFilters);
		startActivityForResult(i, 1);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==1){ // SettingsActivity Result
    		if(resultCode == RESULT_OK){
    			searchFilters = (SearchFilters) data.getSerializableExtra("settings");
    			Toast.makeText(this, "Settings Changed", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
	
}
