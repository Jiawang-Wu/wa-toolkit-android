package com.windowsazure.samples.android.sampleapp;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.CloudQueueMessage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class StorageEntityListActivity extends SecuredActivity implements OnChildClickListener {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity_list.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity_list.title";
		
	static final int ENTITY_LIST_TYPE_TABLE = 1;
	static final int ENTITY_LIST_TYPE_QUEUE = 2;
	
	ExpandableListView listView;
	
	int entityListType = 0;
		
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        		
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_entity_list);
        
        Button addButton = (Button)findViewById(R.id.header_add_button);
        Button backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
        
        addButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        
        title.setText(this.entityName());
              
        entityListType = optionSet().getInt(TYPE_NAMESPACE);        
        listView = (ExpandableListView)findViewById(R.id.entity_list_expandable_list_view);
        listView.setOnChildClickListener(this);
        
        backButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onBackButton(view); }
        });
        
        addButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onAddButton(view); }
        });
    }

	private String entityName() {
		return this.optionSet().getString(StorageEntityListActivity.TITLE_NAMESPACE);
	}
        
	public void onStart() {
		super.onStart();
			 class ListEntityItemsTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
			 SimpleExpandableListAdapter listAdapter = null;
			     protected AlertDialog.Builder doInBackground(Void... params) {
			        try {
				    	switch(entityListType) {
				    		case ENTITY_LIST_TYPE_TABLE:
				    			// TODO: Plug with real table services
				    			listAdapter = getTableMockData();
				    			break;
				    		case ENTITY_LIST_TYPE_QUEUE:
				    			listAdapter = getQueueMessages();
				    			break;
				    	}
					}
					catch (Exception exception)
					{
						return dialogToShow("Couldn't list the items of the entity", exception);
					}
			        return null;
			     }
			     protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
			    	 if (dialogBuilder == null)
			    	 {
							listView.setAdapter(listAdapter);
			    	 }
			    	 else
			    	 {
			    		 dialogBuilder.show();
			    	 }
				 }
	    	};
	    	new ListEntityItemsTask().execute();
    }
    
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	switch(entityListType) {
			case ENTITY_LIST_TYPE_TABLE:
				// TODO: Plug with real table services  
				
				@SuppressWarnings("unchecked")
				Map<String, String> identifier = (Map<String, String>) parent.getItemAtPosition(groupPosition);
				
				Intent intent = new Intent(this, StorageEntityActivity.class);
		    	intent.putExtra(StorageEntityActivity.TITLE_NAMESPACE, getString(R.string.edit_entity_title));
		    	intent.putExtra(StorageEntityActivity.TYPE_NAMESPACE, StorageEntityActivity.OPERATION_TYPE_EDIT);
		    	intent.putExtra(StorageEntityActivity.PARTITION_KEY_NAMESPACE, identifier.get("PartitionKey"));
		    	intent.putExtra(StorageEntityActivity.ROW_KEY_NAMESPACE, identifier.get("RowKey"));
		    	startActivity (intent);
    	}
    	
		return true;
	}
	
    private void onBackButton(View v) {
    	finish();
	}
    
    private void onAddButton(View v) {
		Intent intent;
		
    	switch(entityListType) {    		
			case ENTITY_LIST_TYPE_TABLE:
				intent = new Intent(this, StorageEntityActivity.class);
		    	intent.putExtra(StorageEntityActivity.TITLE_NAMESPACE, getString(R.string.add_entity_title));
		    	intent.putExtra(StorageEntityActivity.TYPE_NAMESPACE, StorageEntityActivity.OPERATION_TYPE_ADD);
		    	startActivity (intent);	    	
				break;
			case ENTITY_LIST_TYPE_QUEUE:
		    	intent = new Intent(this, StorageCreateItemActivity.class);
	   	    	intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_QUEUE_MESSAGE);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_queue_message));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString(R.string.create_queue_message_label));
    	    	startActivity(intent);
				break;
    	}
	}
    
    private SimpleExpandableListAdapter getTableMockData() {
    	List<List<Map<String, String>>> items = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < 5; i++) {
			Map<String, String> groupFields = new HashMap<String, String>();
			List<Map<String, String>> entry = new ArrayList<Map<String, String>>();
				
			// Mock Groups
       		groupFields.put("PartitionKey", "pk-" + i);
       		groupFields.put("RowKey", "rk-" + i);
	    	groupData.add(groupFields);	  
	    	
			Map<String, String> partitionKey = new HashMap<String, String>();
			partitionKey.put("Name", "PartitionKey");
			partitionKey.put("Value", "pk-" + i);
   			entry.add(partitionKey);
			
   			Map<String, String> rowKey = new HashMap<String, String>();
   			rowKey.put("Name", "RowKey");
   			rowKey.put("Value", "rk-" + i);
   			entry.add(rowKey);
   			
   			for (int j = 0; j < 2; j++) {
    			Map<String, String> field = new HashMap<String, String>();
    			field.put("Name", "field-" + j);
    			field.put("Value", "value-" + j);
       			entry.add(field);
   			}
   			
	    	items.add(entry);     			
		}
		
		return new SimpleExpandableListAdapter(this, 
												groupData, 
												android.R.layout.simple_expandable_list_item_1, 
												new String[] {"PartitionKey", "RowKey"}, 
												new int[] { android.R.id.text1, android.R.id.text2 }, 
												items, 
												android.R.layout.simple_expandable_list_item_2, 
												new String[] {"Name", "Value"}, 
												new int[] { android.R.id.text1, android.R.id.text2 });
    }
    
    private SimpleExpandableListAdapter getQueueMessages() throws URISyntaxException, Exception {
    	List<List<Map<String, String>>> items = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		
		CloudQueue queue = this.getSampleApplication().getCloudQueueClient().getQueueReference(this.entityName());
		for (CloudQueueMessage message :  queue.getMessages(32, 1)) {
			Map<String, String> groupFields = new HashMap<String, String>();
			List<Map<String, String>> entry = new ArrayList<Map<String, String>>();
				
			// Mock Groups
       		groupFields.put("Message", message.getId());
	    	groupData.add(groupFields);	  
	    	
	    	// Mock Properties
			Map<String, String> property = new HashMap<String, String>();
			
   			property = new HashMap<String, String>();
   			property.put("Name", "Message ID");
   			property.put("Value", message.getId());
   			entry.add(property);
   			
   			property = new HashMap<String, String>();
   			property.put("Name", "Insetion Time");
   			property.put("Value", message.getInsertionTime().toString());
   			entry.add(property);
   			
   			property = new HashMap<String, String>();
   			property.put("Name", "Expiration Time");
   			property.put("Value", message.getExpirationTime().toString());
   			entry.add(property);
   			
   			// The pop receipt will always be null unless we use a get to list the messages
	   			property = new HashMap<String, String>();
	   			property.put("Name", "Pop Receipt");
	   			if (message.getPopReceipt() != null)
	   			{
	   				property.put("Value", message.getPopReceipt().toString());
	   			}
	   			else
	   			{
	   				property.put("Value", "<none>");
	   			}
	   			entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Time Next Visible");
   			if (message.getNextVisibleTime() != null)
   			{
   	   			property.put("Value", message.getNextVisibleTime().toString());
   			}
   			else
   			{
   				property.put("Value", "<none>");
   			}
   			entry.add(property);
   			
   			property = new HashMap<String, String>();
   			property.put("Name", "Dequeue Count");
   			property.put("Value", "" + message.getDequeueCount());
   			entry.add(property);
   			
	    	items.add(entry);     			
		}
		
		return new SimpleExpandableListAdapter(this, 
												groupData, 
												android.R.layout.simple_expandable_list_item_1, 
												new String[] {"Message"}, 
												new int[] { android.R.id.text1, android.R.id.text2 }, 
												items, 
												android.R.layout.simple_expandable_list_item_2, 
												new String[] {"Name", "Value"}, 
												new int[] { android.R.id.text1, android.R.id.text2 });
    }    
}
