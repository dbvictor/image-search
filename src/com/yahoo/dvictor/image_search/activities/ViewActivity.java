package com.yahoo.dvictor.image_search.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;
import com.yahoo.dvictor.image_search.R;
import com.yahoo.dvictor.image_search.models.SearchResult;

public class ViewActivity extends Activity {
	private ShareActionProvider miShareAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		// Remove action bar on image display activity.
		//NO: getActionBar().hide(); -- don't hide if we are going offer a share button on it.
		// Pull out the URL from the intent.
		// - if simple: String url = getIntent().getStringExtra("url");
		SearchResult imageInfo = (SearchResult) getIntent().getSerializableExtra("result");
		// Find the image view
		SmartImageView iv = (SmartImageView) findViewById(R.id.ivImageFull);
		// Load the image URL into the image view using Picasso.
		// - normally: Picasso.with(this).load(imageInfo.urlFull).into(iv);
		// - but instead load so that we get a call on complete to setup sharing options only when image is loaded.
		// Load image async from remote URL, setup share when completed
	    iv.setImageUrl(imageInfo.urlFull, new OnCompleteListener() {
	        @Override
	        public void onComplete() {
	            // Setup share intent now that image has loaded
	            setupShareIntent();
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		// Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.action_share);
	    // Fetch reference to the share action provider
	    miShareAction = (ShareActionProvider) item.getActionProvider();
	    // Return true to display menu
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
	
	// Gets the image URI and setup the associated share intent to hook into the provider
	public void setupShareIntent() {
	    // Fetch Bitmap Uri locally
	    ImageView ivImage = (ImageView) findViewById(R.id.ivImageFull);
	    Uri bmpUri = getLocalBitmapUri(ivImage); // see previous remote images section
	    // Create share intent as described above
	    Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
	    shareIntent.setType("image/*");
	    // Attach share event to the menu item provider
	    if(miShareAction==null) Toast.makeText(this, "Share Provider Not Found", Toast.LENGTH_SHORT).show();
	    else miShareAction.setShareIntent(shareIntent);
	}	
	
	/** Share the current image being viewed in the full screen view (when the share option is clicked). */
	/* WE ARE NOT USING onClick:
	public void share(MenuItem menuItem){
		Toast.makeText(this, "Share!", Toast.LENGTH_SHORT).show();
		//Intent i = new Intent(this,SettingsActivity.class);
		//i.putExtra("settings", searchFilters);
		//startActivityForResult(i, 1);
	} */
	
	/** Returns the URI path to the Bitmap displayed in specified ImageView */
	public Uri getLocalBitmapUri(ImageView imageView) {
	    // Extract Bitmap from ImageView drawable
	    Drawable drawable = imageView.getDrawable();
	    Bitmap bmp = null;
	    if (drawable instanceof BitmapDrawable){
	       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
	    } else {
	       return null;
	    }
	    // Store image to default external storage directory
	    Uri bmpUri = null;
	    try {
	        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
	        file.getParentFile().mkdirs();
	        FileOutputStream out = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	        out.close();
	        bmpUri = Uri.fromFile(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return bmpUri;
	}	
}
