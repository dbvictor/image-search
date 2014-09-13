package com.yahoo.dvictor.image_search.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yahoo.dvictor.image_search.R;
import com.yahoo.dvictor.image_search.models.SearchResult;

public class ViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		// Remove action bar on image display activity.
		getActionBar().hide();
		// Pull out the URL from the intent.
		// - if simple: String url = getIntent().getStringExtra("url");
		SearchResult imageInfo = (SearchResult) getIntent().getSerializableExtra("result");
		// Find the image view
		ImageView iv = (ImageView) findViewById(R.id.ivImageFull);
		// Load the image URL into the image view using Picasso.
		Picasso.with(this).load(imageInfo.urlFull).into(iv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
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
}
