package com.windowsazure.samples.android.sampleapp;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.CloudQueueMessage;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudTableObject;
import com.windowsazure.samples.android.storageclient.StorageCredentials;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class StorageEntityListActivity extends SecuritableActivity implements OnChildClickListener {

	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity_list.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity_list.title";

	static final int ENTITY_LIST_TYPE_TABLE = 1;
	static final int ENTITY_LIST_TYPE_QUEUE = 2;

	private ExpandableListView listView;
    private SimpleExpandableListAdapter listAdapter;

	private int entityListType = 0;
	private ProgressBar progressBar;
	private Button addButton;
	private AsyncTask<Void, Void, AlertDialog.Builder> currentTask;

	@Override
    public void onCreateCompleted(Bundle savedInstanceState) {
        super.onCreateCompleted(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_entity_list);

        addButton = (Button)findViewById(R.id.header_add_button);
        Button backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
	    progressBar = (ProgressBar) findViewById(R.id.storage_entity_list_progress);

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

	protected void onPause() {
		super.onPause();
		AsyncTask<Void, Void, Builder> task = currentTask;
		if (task != null) {
			task.cancel(true);
			currentTask = null;
		}
	}

	private String entityName() {
		return this.optionSet().getString(StorageEntityListActivity.TITLE_NAMESPACE);
	}

	public void onStart() {
		super.onStart();
			 class ListEntityItemsTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
				protected AlertDialog.Builder doInBackground(Void... params) {
			        try {
				    	switch(entityListType) {
				    		case ENTITY_LIST_TYPE_TABLE:
				    			listAdapter = getTableRows();
				    			break;
				    		case ENTITY_LIST_TYPE_QUEUE:
				    			listAdapter = getQueueMessages();
				    			break;
				    	}
					}
					catch (Exception exception) {
						return dialogToShow("Couldn't list the items of the entity", exception);
					}
			        return null;
			     }
				protected void onPreExecute() {
					listView.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
				}
			     protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
			    	 if (dialogBuilder == null) {
							listView.setAdapter(listAdapter);
			    	 }
			    	 else {
			    		 dialogBuilder.show();
			    	 }
					progressBar.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				 }
	    	};
	    	currentTask = new ListEntityItemsTask();
	    	currentTask.execute();
    }

	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	switch(entityListType) {
			case ENTITY_LIST_TYPE_TABLE:
				@SuppressWarnings("unchecked")
				Map<String, String> identifier = (Map<String, String>) listAdapter.getGroup(groupPosition);

				Intent intent = new Intent(this, StorageEntityActivity.class);
		    	intent.putExtra(StorageEntityActivity.TITLE_NAMESPACE, getString(R.string.edit_entity_title));
		    	intent.putExtra(StorageEntityActivity.TYPE_NAMESPACE, StorageEntityActivity.OPERATION_TYPE_EDIT);
		    	intent.putExtra(StorageEntityActivity.PARTITION_KEY_NAMESPACE, identifier.get("PartitionKey"));
		    	intent.putExtra(StorageEntityActivity.ROW_KEY_NAMESPACE, identifier.get("RowKey"));
		    	intent.putExtra(StorageEntityActivity.TABLE_NAME_NAMESPACE, this.entityName());
		    	startActivity (intent);
    	}

		return true;
	}

    private void onBackButton(View v) {
    	finish();
	}

    protected void onResume() {
    	super.onResume();
    	if (addButton != null) addButton.setEnabled(true);
    }

    private void onAddButton(View v) {
    	addButton.setEnabled(false);
		Intent intent;

    	switch(entityListType) {
			case ENTITY_LIST_TYPE_TABLE:
				intent = new Intent(this, StorageEntityActivity.class);
		    	intent.putExtra(StorageEntityActivity.TITLE_NAMESPACE, getString(R.string.add_entity_title));
		    	intent.putExtra(StorageEntityActivity.TYPE_NAMESPACE, StorageEntityActivity.OPERATION_TYPE_ADD);
		    	intent.putExtra(StorageEntityActivity.TABLE_NAME_NAMESPACE, this.entityName());
		    	startActivity (intent);
				break;
			case ENTITY_LIST_TYPE_QUEUE:
		    	intent = new Intent(this, StorageCreateItemActivity.class);
	   	    	intent.putExtra(StorageCreateItemActivity.TYPE_NAMESPACE, StorageCreateItemActivity.CREATE_ITEM_TYPE_QUEUE_MESSAGE);
    	    	intent.putExtra(StorageCreateItemActivity.TITLE_NAMESPACE, getString(R.string.create_queue_message));
    	    	intent.putExtra(StorageCreateItemActivity.LABEL_TEXT_NAMESPACE, getString(R.string.create_queue_message_label));
    	    	intent.putExtra(StorageCreateItemActivity.CONTAINER_OR_QUEUE_NAME_NAMESPACE, this.entityName());
    	    	startActivity(intent);
				break;
    	}
	}

    private SimpleExpandableListAdapter getTableRows() throws Exception {
    	List<List<Map<String, String>>> items = new ArrayList<List<Map<String, String>>>();
		List<Map<String, Object>> groupData = new ArrayList<Map<String, Object>>();

		CloudTableClient tableClient = getSampleApplication().getCloudClientAccount().createCloudTableClient();
		StorageCredentials tableCredentials = tableClient.getCredentials();
		for (Map<String, Object> entity : CloudTableObject.query(tableClient.getEndpoint(), tableCredentials, this.entityName())) {
			List<Map<String, String>> entry = new ArrayList<Map<String, String>>();

			groupData.add(entity);

   			for (Entry<String, Object> property : entity.entrySet()) {
    			Map<String, String> field = new HashMap<String, String>();
    			field.put("Name", property.getKey());
    			field.put("Value", property.getValue().toString());
       			entry.add(field);
   			}

	    	items.add(entry);
		}

		return new SimpleExpandableListAdapter(this,
												groupData,
												android.R.layout.simple_expandable_list_item_1,
												new String[] {"RowKey"},
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

			// Groups
       		groupFields.put("Message", message.getId());
	    	groupData.add(groupFields);

			Map<String, String> property = new HashMap<String, String>();

   			property = new HashMap<String, String>();
   			property.put("Name", "Message ID");
   			property.put("Value", message.getId());
   			entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Message Text");
   			property.put("Value", message.getAsString());
   			entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Insertion Time");
   			property.put("Value", message.getInsertionTime().toString());
   			entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Expiration Time");
   			property.put("Value", message.getExpirationTime().toString());
   			entry.add(property);

   			// The pop receipt will be null if we use peek to list the messages
	   		property = new HashMap<String, String>();
	   		property.put("Name", "Pop Receipt");
	   		if (message.getPopReceipt() != null) {
	   			property.put("Value", message.getPopReceipt().toString());
	   		}
	   		else {
	   			property.put("Value", "<none>");
	   		}
	   		entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Time Next Visible");
   			if (message.getNextVisibleTime() != null) {
   	   			property.put("Value", message.getNextVisibleTime().toString());
   			}
   			else {
   				property.put("Value", "<none>");
   			}
   			entry.add(property);

   			property = new HashMap<String, String>();
   			property.put("Name", "Dequeue Count");
   			property.put("Value", "" + message.getDequeueCount());
   			entry.add(property);

	    	items.add(entry);
		}

		return new SimpleExpandableListAdapter(this, //context
												groupData, //groupData
												//android.R.layout.simple_expandable_list_item_1, //groupLayout
												R.layout.custom_expandable_group_item, //groupLayout
												new String[] {"Message"}, //groupFrom
												new int[] { R.id.groupText }, //groupTo
												items, //childData
												R.layout.custom_expandable_list_item, //custom childLayout
												new String[] {"Name", "Value"}, //childFrom
												new int[] { R.id.text1, R.id.text2 }); //childTo
    }
}
