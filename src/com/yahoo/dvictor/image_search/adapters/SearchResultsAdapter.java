package com.yahoo.dvictor.image_search.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yahoo.dvictor.image_search.R;
import com.yahoo.dvictor.image_search.models.SearchResult;

public class SearchResultsAdapter extends ArrayAdapter<SearchResult> {

	public SearchResultsAdapter(Context context, List<SearchResult> images) {
		super(context, R.layout.search_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//return super.getView(position, convertView, parent);
		SearchResult imageInfo = getItem(position);
		if(convertView==null){ // Create one from XML if not being recycled.
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result, parent, false);
		}
		// Get at the UI pieces from the view.
		ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
		TextView  tvTitle = (TextView ) convertView.findViewById(R.id.tvTitle);
		// Clear out image from last time (assuming it was recycled, else is benign)
		ivImage.setImageResource(0);
		// Populate the title
		tvTitle.setText(Html.fromHtml(imageInfo.title));
		// Set the image by remote URL.  Remotely load the image in the background using picaso
		Picasso.with(getContext()).load(imageInfo.urlThumb).into(ivImage);
		// Return the completed view to be displayed.
		return convertView;
	}
	
}
