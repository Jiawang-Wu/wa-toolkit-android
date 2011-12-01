package com.windowsazure.samples.android.sampleapp;

import java.util.ArrayList;
import java.util.List;

import com.windowsazure.samples.android.sampleapp.R;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudQueue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StorageListActivity extends SecuritableActivity implements OnItemClickListener {

	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_list.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_list.title";

	static final int LIST_TYPE_TABLE = 1;
	static final int LIST_TYPE_CONTAINER = 2;
	static final int LIST_TYPE_BLOB = 3;
	static final int LIST_TYPE_QUEUE = 4;

	private List<String> items;
	private int listType = 0;
	private ProgressBar progressBar;
	private ListView listView;
	private Button addButton;
	private Button backButton;
	private AsyncTask<Integer, Integer, AlertDialog.Builder> currentTask;

    @Override
    public void onCreateCompleted(Bundle savedInstanceState) {
    	super.onCreateCompleted(savedInstanceState);

    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_list);

        addButton = (Button)findViewById(R.id.header_add_button);
        backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
	    progressBar = (ProgressBar) findViewById(R.id.storage_list_progress);

        addButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        Bundle optionSet = optionSet();
        String text = optionSet.getString(StorageListActivity.TITLE_NAMESPACE);
        title.setText(text);

        listType = optionSet.getInt(TYPE_NAMESPACE);
        listView = (ListView)findViewById(R.id.storage_list_list_view);
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
        final StorageListActivity thisActivity = this;

		 class ListItemsTask extends AsyncTask<Integer, Integer, AlertDialog.Builder> {
		     private ArrayList<String> listedItems;

			protected AlertDialog.Builder doInBackground(Integer... listTypes) {
		    	listedItems = new ArrayList<String>();
		        try {
			        listedItems = new ArrayList<String>();
		        	switch(listType) {
			    		case LIST_TYPE_TABLE:
			    	        for (String tableName : thisActivity.getSampleApplication().getCloudTableClient().listTables()) {
			    	        	listedItems.add(tableName);
			    	        }
			    			break;

			    		case LIST_TYPE_CONTAINER:
			    	        for (CloudBlobContainer container : thisActivity.getSampleApplication().getCloudBlobClient().listContainers()) {
			    	        	listedItems.add(container.getName());
			    	        }
			    			break;

			    		case LIST_TYPE_BLOB:
			    	        String containerName = thisActivity.getContainerListedName();
			    	        CloudBlobContainer container = thisActivity.getSampleApplication().getCloudBlobClient().getContainerReference(containerName);
			    	        for (CloudBlob blob : container.listBlobs("", true)) {
			    	        	listedItems.add(blob.getName());
			    	        }
			    			break;
			    		case LIST_TYPE_QUEUE:
			    	        for (CloudQueue queue : thisActivity.getSampleApplication().getCloudQueueClient().listQueues()) {
			    	        	listedItems.add(queue.getName());
			    	        }
			    			break;
		        	}
		        }
		        catch (Exception exception) {
		        	return dialogToShow("Couldn't execute the list operation", exception);
		        }
				return null;
		     }

		     protected void onPreExecute() {
		    	 listView.setVisibility(View.GONE);
		    	 progressBar.setVisibility(View.VISIBLE);
		     }

		     protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
		    	 if (dialogBuilder == null)
		    	 {
			    	items = listedItems;
				    listView.setAdapter(new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, items));
			    	listView.setVisibility(View.VISIBLE);
		    	 }
		    	 else
		    	 {
		    		 Dialog dialog = dialogBuilder.create();
		    		 dialog.setCanceledOnTouchOutside(true);
		    		 dialog.show();
		    	 }
			    progressBar.setVisibility(View.GONE);
			    currentTask = null;
		     }
		 };
		 currentTask = new ListItemsTask();
		 currentTask.execute(listType);
	}

	protected void onPause()
	{
		super.onPause();
		AsyncTask<Integer, Integer, AlertDialog.Builder> task = currentTask; 
		if (task != null)
		{
			task.cancel(true);
			currentTask = null;
		}
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent storageEntityListIntent = new Intent(this, StorageEntityListActivity.class);
		Intent storageListIntent = new Intent(this, StorageListActivity.class);
		Intent blobViewIntent = new Intent(this, StorageBlobViewActivity.class);

		try {
	    	switch (listType) {
				case LIST_TYPE_TABLE:
					storageEntityListIntent.putExtra(StorageEntityListActivity.TYPE_NAMESPACE, StorageEntityListActivity.ENTITY_LIST_TYPE_TABLE);
					storageEntityListIntent.putExtra(StorageEntityListActivity.TITLE_NAMESPACE, items.get(arg2));
			    	startActivity (storageEntityListIntent);
					break;
				case LIST_TYPE_BLOB:
		    	    String containerName = this.optionSet().getString(StorageListActivity.TITLE_NAMESPACE);
					blobViewIntent.putExtra(StorageBlobViewActivity.CONTAINER_NAME_NAMESPACE, containerName);
					blobViewIntent.putExtra(StorageBlobViewActivity.BLOB_NAME_NAMESPACE, items.get(arg2));
			    	startActivity (blobViewIntent);
					break;
				case LIST_TYPE_CONTAINER:
					storageListIntent.putExtra(StorageListActivity.TYPE_NAMESPACE, StorageListActivity.LIST_TYPE_BLOB);
					storageListIntent.putExtra(StorageListActivity.TITLE_NAMESPACE, items.get(arg2));
			    	startActivity (storageListIntent);
			    	break;
				case LIST_TYPE_QUEUE:
					storageEntityListIntent.putExtra(StorageEntityListActivity.TYPE_NAMESPACE, StorageEntityListActivity.ENTITY_LIST_TYPE_QUEUE);
					storageEntityListIntent.putExtra(StorageEntityListActivity.TITLE_NAMESPACE, items.get(arg2));
			    	startActivity (storageEntityListIntent);
					break;
			}
		}
        catch (Exception exception) {
        	this.showErrorMessage("Couldn't view the details of the item", exception);
        }
	}

    private void onBackButton(View v) {
    	addButton.setEnabled(false);
    	finish();
	}

    protected void onResume()
    {
    	super.onResume();
    	if (addButton != null) addButton.setEnabled(true);
    }

    private void onAddButton(View v) {
    	addButton.setEnabled(false);
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
    	    	intent.putExtra(StorageCreateItemActivity.CONTAINER_OR_QUEUE_NAME_NAMESPACE, this.getContainerListedName());
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

	private String getContainerListedName() {
		return this.optionSet().getString(StorageListActivity.TITLE_NAMESPACE);
	}

}
