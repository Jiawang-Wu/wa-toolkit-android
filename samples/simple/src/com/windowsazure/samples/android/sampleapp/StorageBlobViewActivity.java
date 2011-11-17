package com.windowsazure.samples.android.sampleapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class StorageBlobViewActivity extends Activity {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_blob_view.type";
	static final String LOCATION_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_blob_view.location";
	
	static final int CONTENT_TYPE_IMAGE = 1;
	static final int CONTENT_TYPE_TEXT = 2;
	
	String location;
	int contentType;
	
	ScrollView scrollView;
	TextView textView;
	ImageView imageView;
	ProgressBar progressBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);    	
    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_blob_view);
        
        Button backButton = (Button)findViewById(R.id.header_back_button);	    
        backButton.setVisibility(View.VISIBLE);
        
        TextView title = (TextView)findViewById(R.id.header_title);
        title.setText(getString(R.string.blob_view_title));

	    scrollView = (ScrollView) findViewById(R.id.storage_blob_view_scroll_view);
		textView = (TextView) findViewById(R.id.storage_blob_view_text_view);
	    imageView = (ImageView) findViewById(R.id.storage_blob_view_image_view);
	    progressBar = (ProgressBar) findViewById(R.id.storage_blob_view_progress);
	    
	    scrollView.setVisibility(View.GONE);
	    textView.setVisibility(View.GONE);
	    imageView.setVisibility(View.GONE);
	    progressBar.setVisibility(View.GONE);
 
        Bundle optionSet = getIntent().getExtras();            
        contentType = optionSet.getInt(StorageBlobViewActivity.TYPE_NAMESPACE);
        location = optionSet.getString(StorageBlobViewActivity.LOCATION_NAMESPACE);
        
        backButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onBackButton(view); }
        });
    }
    
    public void onStart() {
		super.onStart();
    	switch (contentType){
			case CONTENT_TYPE_IMAGE:
				new DownloadImageTask().execute(location);
				break;
			case CONTENT_TYPE_TEXT:
				new DownloadTextTask().execute(location);
				break;		
    	}
    }
    
    private void onBackButton(View v) {
    	finish();
	}
	
	public class DownloadTextTask extends AsyncTask<String, Void, StringBuilder> {
		 
		@Override
		protected StringBuilder doInBackground(String... params) {		
			try {
				URL url = new URL(location);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream stream = connection.getInputStream();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				StringBuilder content = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				
				return content;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			return null;
		}
		
		protected void onPreExecute() {
			scrollView.setVisibility(View.GONE);
			textView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}
		
		protected void onPostExecute(StringBuilder result) {    	 	
			textView.setText(result);
			scrollView.setVisibility(View.VISIBLE);
			textView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
	
	public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		 
		@Override
		protected Bitmap doInBackground(String... params) {		
			try {
				URL url = new URL(params[0]);                     
				HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
				InputStream stream = connection.getInputStream();
				return BitmapFactory.decodeStream(stream);  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			return null;
		}
		
		protected void onPreExecute() {
			imageView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}
		
		protected void onPostExecute(Bitmap result) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			imageView.setImageBitmap(result);
			imageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
	
}