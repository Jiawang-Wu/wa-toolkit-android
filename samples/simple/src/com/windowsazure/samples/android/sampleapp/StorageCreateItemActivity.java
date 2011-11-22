package com.windowsazure.samples.android.sampleapp;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StorageCreateItemActivity extends SecuredActivity {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.title";
	static final String LABEL_TEXT_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.label_text";	
	static final String CONTAINER_NAME_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.container_name";	
	
	static final int CREATE_ITEM_TYPE_TABLE = 1;
	static final int CREATE_ITEM_TYPE_CONTAINER = 2;
	static final int CREATE_ITEM_TYPE_BLOB = 3;
	static final int CREATE_ITEM_TYPE_QUEUE = 4;
	static final int CREATE_ITEM_TYPE_QUEUE_MESSAGE = 5;

	static final int MISSING_NAME_FIELD = 0;
	static final int MISSING_IMAGE_FIELD = 1;
	
	static final int PICK_IMAGE_REQUEST = 0;
	
	int createItemType = 0;
	Uri imageLocation;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_create_item);
        
        Bundle optionSet = optionSet();  
        
        createItemType = optionSet.getInt(TYPE_NAMESPACE);
        
        Button backButton = (Button)findViewById(R.id.header_back_button);
        Button createButton = (Button)findViewById(R.id.storage_create_item_button);
        
        TextView title = (TextView)findViewById(R.id.header_title);
        
        backButton.setVisibility(View.VISIBLE);        
        title.setText(optionSet.getString(TITLE_NAMESPACE));
        
        TextView label = (TextView)findViewById(R.id.storage_create_item_title);
        label.setText(optionSet.getString(LABEL_TEXT_NAMESPACE));
        
        if (createItemType == CREATE_ITEM_TYPE_BLOB)
        {
            Button pickImageButton = (Button)findViewById(R.id.pick_image_button);
            pickImageButton.setVisibility(View.VISIBLE);
            
            pickImageButton.setOnClickListener(new View.OnClickListener( ) {
            	public void onClick(View view) { onPickImageButton(view); }
            });
        }
        
        backButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onBackButton(view); }
        });
        
        createButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onCreateButton(view); }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_IMAGE_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				imageLocation = data.getData();

		        TextView imageSelectedLabel = (TextView)findViewById(R.id.storage_create_item_image_selected);
		        imageSelectedLabel.setText(imageLocation.toString());
		        imageSelectedLabel.setVisibility(View.VISIBLE);
			} 
		}
	}
    
	protected Dialog onCreateDialog(int id)	{	
		AlertDialog.Builder dialogBuilder = createDialogBuilder(id);
		return dialogBuilder.create();
	}

	private AlertDialog.Builder createDialogBuilder(int id) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);    
		
		switch(id) {
			case MISSING_NAME_FIELD:	
				dialogBuilder.setTitle(getString(R.string.create_item_failed_title));
				dialogBuilder.setMessage(getString(R.string.error_missing_title));
		    	break;
			case MISSING_IMAGE_FIELD:
				dialogBuilder.setTitle(getString(R.string.create_item_failed_title));
				dialogBuilder.setMessage(getString(R.string.error_missing_image));
				break;
		}
		return dialogBuilder;
	}

    private void onBackButton(View v) {
    	finish();
	}
    
    private void onPickImageButton(View v) {
		startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
    }
    
    private void onCreateButton(View v) {
    	EditText nameView = (EditText)findViewById(R.id.storage_create_item_value);
    	
    	// TODO: Validate names according to the API.
    	
		final String name = nameView.getText().toString().trim();
		final String containerName = createItemType == CREATE_ITEM_TYPE_BLOB 
				? this.optionSet().getString(CONTAINER_NAME_NAMESPACE)
				: null;

		if (name.length() == 0) {
        	showDialog(MISSING_NAME_FIELD);
        	return;	
		}

		final StorageCreateItemActivity thisActivity = this;
		class CreateItemTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
			public String getFilePath(Uri uri) {
			    String[] projection = { MediaStore.Images.Media.DATA };
			    Cursor cursor = managedQuery(uri, projection, null, null, null);
			    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			    cursor.moveToFirst();
			    return cursor.getString(column_index);
			}

			@Override
			protected AlertDialog.Builder doInBackground(Void... params) {
				try
				{
			    	switch(createItemType){
			    		case CREATE_ITEM_TYPE_TABLE:
			    			// TODO: Create a new table 
			    			break;
			    		case CREATE_ITEM_TYPE_BLOB:
			    			if (imageLocation == null) {
			    				return createDialogBuilder(MISSING_IMAGE_FIELD);
			    			}
			    			CloudBlockBlob blob = getContainerReference(containerName).getBlockBlobReference(name);
			    			String filePath = getFilePath(imageLocation);
			    			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			    			OutputStream stream = blob.openOutputStream();
			    			bitmap.compress(CompressFormat.JPEG, 75, stream);
			    			stream.close();
			    			blob.getProperties().contentType = "image/jpeg";
			    			blob.uploadProperties();
			    			break;
			    		case CREATE_ITEM_TYPE_CONTAINER:
			    			getContainerReference(name).create();
			    			break;
			    		case CREATE_ITEM_TYPE_QUEUE:
			    			// TODO: Create a new queue
			    			break;
			    		case CREATE_ITEM_TYPE_QUEUE_MESSAGE:
			    			// TODO: Create a new message in a queue
			    			break;
			    	}
				}
				catch (Exception exception)
				{
					return dialogToShow("Couldn't create the item", exception);
				}
		    	return null;
			}

			protected void onPostExecute(AlertDialog.Builder dialogToShow) {
				if (dialogToShow == null)
				{
					thisActivity.finish();
				}
				else
				{
					AlertDialog dialog = dialogToShow.create();
					dialog.setCanceledOnTouchOutside(true);
					dialog.show();

				}
			}
		};
		
		new CreateItemTask().execute();
    }

	private CloudBlobContainer getContainerReference(final String containerName)
			throws NotImplementedException, URISyntaxException,
			StorageException, Exception {
		return this.getSampleApplication().getCloudBlobClient().getContainerReference(containerName);
	}

}