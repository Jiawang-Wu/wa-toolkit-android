package com.windowsazure.samples.sample;

import com.windowsazure.samples.sample.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StorageTypeSelector extends Activity
{

	static int STORAGE_TYPE_TABLE = 1;
	static int STORAGE_TYPE_BLOB = 2;
	static int STORAGE_TYPE_QUEUE = 3;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storagetypeselector);
        Button tableButton = (Button)findViewById(R.id.TableStorageButton);
        Button blobButton = (Button)findViewById(R.id.BlobStorageButton);
        Button queueButton = (Button)findViewById(R.id.QueueStorageButton);
        tableButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {listTables();}
        	});
        blobButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {listBlobs();}
        	});
        queueButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {listQueues();}
        	});
    }

	/* DIALOG TO SHOW FOR FUNCTIONALITY UNDER DEVELOPMENT
	final StorageTypeSelector activity = this;
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Blob's Functionality");  
	builder.setMessage("The Blob's functionality is still under development, and it's not stable yet.\nAre you sure you want to continue?");
	builder.setCancelable(true);
	builder.setPositiveButton("Continue Anyway", new DialogInterface.OnClickListener() {  
	    @Override  
	    public void onClick(DialogInterface dialog, int which) {  
	        dialog.dismiss();            
	    }  
	});  
	builder.setNegativeButton("Don't Continue", new DialogInterface.OnClickListener() {  
	    @Override  
	    public void onClick(DialogInterface dialog, int which) {  
	        dialog.dismiss();                      
	    }  
	});  
	AlertDialog ad = builder.create();  
	ad.show();*/

    private void listTables()
	{
    	Intent launchTableDisplay = new Intent(this, ListDisplay.class);
    	launchTableDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.type", STORAGE_TYPE_TABLE);
    	launchTableDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.title", "Table Storage");
    	startActivity (launchTableDisplay);
	}

    private void listBlobs()
	{
    	Intent launchBlobDisplay = new Intent(this, ListDisplay.class);
    	launchBlobDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.type", STORAGE_TYPE_BLOB);
    	launchBlobDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.title", "Blob Storage");
    	startActivity (launchBlobDisplay);
	}

    private void listQueues()
	{
    	Intent launchQueueDisplay = new Intent(this, ListDisplay.class);
    	launchQueueDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.type", STORAGE_TYPE_QUEUE);
    	launchQueueDisplay.putExtra("com.windowsazure.samples.sample.listdisplay.title", "Queue Storage");
    	startActivity (launchQueueDisplay);
	}
}
