package com.windowsazure.samples.android.sampleapp;

import com.windowsazure.samples.android.sampleapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StorageListActivity extends SecuredActivity implements OnItemClickListener {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_list.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_list.title";
	
	static final int LIST_TYPE_TABLE = 1;
	static final int LIST_TYPE_CONTAINER = 2;
	static final int LIST_TYPE_BLOB = 3;
	static final int LIST_TYPE_QUEUE = 4;
	
	String[] items;
	int listType = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);    	
    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_list);
        
        Button addButton = (Button)findViewById(R.id.header_add_button);
        Button backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
        
        addButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        Bundle optionSet = getIntent().getExtras();  
        String text = optionSet.getString(StorageListActivity.TITLE_NAMESPACE);
        title.setText(text);
              
        listType = optionSet.getInt(TYPE_NAMESPACE);        
        ListView listView = (ListView)findViewById(R.id.storage_list_list_view);
        listView.setOnItemClickListener((OnItemClickListener) this);
        
        backButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onBackButton(view); }
        });
        
        addButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onAddButton(view); }
        });
    }
    
	public void onStart() {
		super.onStart();
		
        try {
        	switch(listType) {
	    		case LIST_TYPE_TABLE:
	    			// TODO: Plug with real table services
	    			items = new String[] { "table-1",  "table-2", "table-3" };	 
	    			break;
	    		case LIST_TYPE_CONTAINER:
	    			// TODO: Plug with real blob services
	    			items = new String[] { "container-1",  "container-2", "container-3" };
	    			break;
	    		case LIST_TYPE_BLOB:
	    			// TODO: Plug with real blob services
	    			items = new String[] { "blob-1",  "blob-2", "blob-3" };	 
	    			break;
	    		case LIST_TYPE_QUEUE:
	    			// TODO: Plug with real queue services
	    			items = new String[] { "queue-1",  "queue-2", "queue-3" };
	    			break;
        	}
	        
	    	ListView listView = (ListView)findViewById(R.id.storage_list_list_view);
	    	listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        }
        catch (Exception e) {
        	System.out.println(e.toString());
        }
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent storageEntityListIntent = new Intent(this, StorageEntityListActivity.class);
		Intent storageListIntent = new Intent(this, StorageListActivity.class);
		Intent blobViewIntent = new Intent(this, StorageBlobViewActivity.class);
		
    	switch (listType) {			
			case LIST_TYPE_TABLE:
				// TODO: Show table rows
				storageEntityListIntent.putExtra(StorageEntityListActivity.TYPE_NAMESPACE, StorageEntityListActivity.ENTITY_LIST_TYPE_TABLE);
				storageEntityListIntent.putExtra(StorageEntityListActivity.TITLE_NAMESPACE, items[arg2]);
		    	startActivity (storageEntityListIntent);
				break;
			case LIST_TYPE_BLOB:
				// TODO: Set real url and content-type and remove the even/odd hack to show both contentTypes			
				if (arg2 % 2 == 0) {
					String mockImageLocation = "http://blog.toggle.com/wp-content/uploads/2011/05/android-apps-iphone-apps.jpg";
					blobViewIntent.putExtra(StorageBlobViewActivity.TYPE_NAMESPACE, StorageBlobViewActivity.CONTENT_TYPE_IMAGE);
					blobViewIntent.putExtra(StorageBlobViewActivity.LOCATION_NAMESPACE, mockImageLocation);
				} else {
					String mockTextLocation = "http://generator.lorem-ipsum.info/lorem-ipsum-copy";
					blobViewIntent.putExtra(StorageBlobViewActivity.TYPE_NAMESPACE, StorageBlobViewActivity.CONTENT_TYPE_TEXT);
					blobViewIntent.putExtra(StorageBlobViewActivity.LOCATION_NAMESPACE, mockTextLocation);
				}				
				
		    	startActivity (blobViewIntent);		    	
				break;    			
			case LIST_TYPE_CONTAINER:				
				storageListIntent.putExtra(StorageListActivity.TYPE_NAMESPACE, StorageListActivity.LIST_TYPE_BLOB);
				storageListIntent.putExtra(StorageListActivity.TITLE_NAMESPACE, items[arg2]);
		    	startActivity (storageListIntent);
		    	break;
			case LIST_TYPE_QUEUE:
				// TODO: Show queue messages
				storageEntityListIntent.putExtra(StorageEntityListActivity.TYPE_NAMESPACE, StorageEntityListActivity.ENTITY_LIST_TYPE_QUEUE);
				storageEntityListIntent.putExtra(StorageEntityListActivity.TITLE_NAMESPACE, items[arg2]);
		    	startActivity (storageEntityListIntent);
				break;
		}
	}    
	
    private void onBackButton(View v) {
    	finish();
	}

    private void onAddButton(View v) {
    	Intent intent = new Intent(this, StorageCreateItemActivity.class);
    	
    	switch (listType){
    		case LIST_TYPE_TABLE:
    			intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_TABLE);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_table));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString((R.string.create_table_label)));    	
    			break;
    		case LIST_TYPE_BLOB:
    	    	intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_BLOB);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_blob));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString(R.string.create_blob_label));
    			break;    			
    		case LIST_TYPE_CONTAINER:
    	    	intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_CONTAINER);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_container));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString(R.string.create_container_label));
    			break;
    		case LIST_TYPE_QUEUE:
    	    	intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_QUEUE);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_queue));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString(R.string.create_queue_label));
    			break;
    	}
    	
    	startActivity(intent);
	}

}
