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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.dvictor.image_search.R;
import com.yahoo.dvictor.image_search.adapters.SearchResultsAdapter;
import com.yahoo.dvictor.image_search.models.SearchResult;

public class SearchActivity extends Activity {
	// Remembered Views (instead of searching for them every time)
	private EditText etSearch;
	private GridView gvSearch;
	// Member Variables & Models
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
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
		String urlSearch = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+query+"&rsz=8";
		client.get(urlSearch, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("DEBUG", response.toString());
				JSONArray searchResultsJSON = null; // Set only if parsing succeeds.
				try {     searchResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				searchResults.clear(); // Clear only when new search.
				// Add directly to adapter so that it wil auto-notify. (otherwise we could add to arrayList and notify the adapter).
				searchResultsAdapter.addAll(SearchResult.fromJSONArray(searchResultsJSON));
				Log.i("INFO", searchResults.toString());
			}
		});
		
	}
}
