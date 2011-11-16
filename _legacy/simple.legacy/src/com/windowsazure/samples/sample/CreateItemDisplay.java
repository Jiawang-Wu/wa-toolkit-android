package com.windowsazure.samples.sample;

import java.io.InputStream;
import java.util.Date;

import com.windowsazure.samples.sample.R;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.queue.AzureQueueManager;
import com.windowsazure.samples.table.AzureTableManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateItemDisplay extends Activity
{

	int listType;
	int listSubtype;
	String container;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createitemdisplay);
        TextView nameLabel = (TextView)findViewById(R.id.CreateItemLabel);
        EditText itemName = (EditText)findViewById(R.id.CreateItemName);
        itemName.setText("");
        Button defaultUploadButton = (Button)findViewById(R.id.UploadDefaultImageButton);
        Button createButton = (Button)findViewById(R.id.CreateItemButton);
        createButton.setVisibility(View.VISIBLE);
        defaultUploadButton.setVisibility(View.INVISIBLE);
        Bundle optionSet = getIntent().getExtras();
        listType = optionSet.getInt("com.windowsazure.samples.sample.createitemdisplay.type");
        if (listType == StorageTypeSelector.STORAGE_TYPE_TABLE)
        {
        	this.setTitle("Create Table");
        	nameLabel.setText("Table Name:");
        }
        else if (listType == StorageTypeSelector.STORAGE_TYPE_BLOB)
        {
            listSubtype = optionSet.getInt("com.windowsazure.samples.sample.createitemdisplay.subtype");
            container = optionSet.getString("com.windowsazure.samples.sample.createitemdisplay.containerName");
        	if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_CONTAINER)
        	{
        		this.setTitle("Create Container");
        		nameLabel.setText("Container Name:");
        	}
        	else if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_BLOB)
        	{
        		this.setTitle("Create Blob");
        		nameLabel.setText("Blob Name:");
                createButton.setVisibility(View.INVISIBLE);
                defaultUploadButton.setVisibility(View.VISIBLE);
        	}
        }
        else if (listType == StorageTypeSelector.STORAGE_TYPE_QUEUE)
        {
        	this.setTitle("Create Queue");
        	nameLabel.setText("Queue Name:");
        }
        createButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {createItem();}
        	});
        defaultUploadButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {uploadDefaultImage();}
        	});
    }

    private void createItem()
	{
        EditText itemName = (EditText)findViewById(R.id.CreateItemName);
        try
        {
	        if (listType == StorageTypeSelector.STORAGE_TYPE_TABLE)
	        {
	        	AzureTableManager tableWriter = new AzureTableManager(ProxySelector.credential);
	        	tableWriter.createTable(null, new Date(), "Sample App", itemName.getText().toString());
	        }
	        if (listType == StorageTypeSelector.STORAGE_TYPE_BLOB)
	        {
	        	ProxySelector.blobClient.getContainerReference(itemName.getText().toString()).create();
	        }
	        if (listType == StorageTypeSelector.STORAGE_TYPE_QUEUE)
	        {
	        	AzureQueueManager queueManager = new AzureQueueManager(ProxySelector.credential);
	        	queueManager.createQueue(itemName.getText().toString());
	        }
        }
        catch (Exception e)
        {
        	System.out.println(e.toString());
        }
        finish();
	}

    private void uploadDefaultImage()
	{
        EditText itemName = (EditText)findViewById(R.id.CreateItemName);
    	if (itemName.getText().toString().length() == 0)
    	{
    		this.showDialog(1);
    	}
    	else
    	{
	        try
	        {
				InputStream blobDataStream = getResources().openRawResource(getResources().getIdentifier("windows_azure", "drawable", "com.windowsazure.samples.sample"));
				CloudBlockBlob blob = ProxySelector.blobClient.getContainerReference(container).getBlockBlobReference(itemName.getText().toString());
				blob.getProperties().contentType = "image/jpeg";
				blob.upload(blobDataStream, blobDataStream.available());
	        }
	        catch (Exception e)
	        {
	        	System.out.println(e.toString());
	        }
	    	finish();
    	}
	}

    protected Dialog onCreateDialog(int id)
    {
    	AlertDialog errorOverlay = new AlertDialog.Builder(this).create();
    	errorOverlay.setTitle("Name Error");
    	errorOverlay.setMessage("You must provide a name for the image to be saved as.");
    	return errorOverlay;
    }

}
