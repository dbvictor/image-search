package com.yahoo.dvictor.image_search.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult implements Serializable{
	private static final long serialVersionUID = 1L;
	public String urlFull;
	public String urlThumb;
	public String title;
	
	public SearchResult(JSONObject json){
		try{
			this.urlFull  = json.getString("url"  );
			this.urlThumb = json.getString("tbUrl");
			this.title    = json.getString("title");
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public static ArrayList<SearchResult> fromJSONArray(JSONArray array){
		ArrayList<SearchResult> results = new ArrayList<SearchResult>(array.length());
		for(int i=0; i<array.length(); i++){
			try{
				results.add(new SearchResult(array.getJSONObject(i)));
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		return results;
	}
}
