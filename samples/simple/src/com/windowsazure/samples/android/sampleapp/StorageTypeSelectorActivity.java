package com.windowsazure.samples.android.sampleapp;

import com.windowsazure.samples.android.sampleapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class StorageTypeSelectorActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      

    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_type_selector);
        
        Button tableButton = (Button)findViewById(R.id.table_storage_button);
        Button blobButton = (Button)findViewById(R.id.blob_storage_button);
        Button queueButton = (Button)findViewById(R.id.queue_storage_button);
        
        tableButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { listTables(); }
        });
        
        blobButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { listContainers(); }
        });
        
        queueButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { listQueues(); }
        });
    }
    
    private void listTables() {
    	Intent launchTableDisplay = new Intent(this, StorageListActivity.class);
    	launchTableDisplay.putExtra(StorageListActivity.TYPE_NAMESPACE, StorageListActivity.LIST_TYPE_TABLE);
    	launchTableDisplay.putExtra(StorageListActivity.TITLE_NAMESPACE, getString(R.string.table_storage_title));
    	startActivity (launchTableDisplay);
	}

    private void listContainers() {
    	Intent launchBlobDisplay = new Intent(this, StorageListActivity.class);
    	launchBlobDisplay.putExtra(StorageListActivity.TYPE_NAMESPACE, StorageListActivity.LIST_TYPE_CONTAINER);
    	launchBlobDisplay.putExtra(StorageListActivity.TITLE_NAMESPACE, getString(R.string.blob_storage_title));
    	startActivity (launchBlobDisplay);
	}

    private void listQueues() {
    	Intent launchQueueDisplay = new Intent(this, StorageListActivity.class);
    	launchQueueDisplay.putExtra(StorageListActivity.TYPE_NAMESPACE, StorageListActivity.LIST_TYPE_QUEUE);
    	launchQueueDisplay.putExtra(StorageListActivity.TITLE_NAMESPACE, getString(R.string.queue_storage_title));
    	startActivity (launchQueueDisplay);
	}
}
