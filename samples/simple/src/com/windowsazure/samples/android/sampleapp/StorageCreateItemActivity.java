package com.windowsazure.samples.android.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StorageCreateItemActivity extends Activity {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.title";
	static final String LABEL_TEXT_NAMESPACE = "com.windowsazure.samples.android.sampleapp.create_item.label_text";	
	
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
        
        Bundle optionSet = getIntent().getExtras();
        
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
		AlertDialog dialog = new AlertDialog.Builder(this).create();    
		
		switch(id) {
			case MISSING_NAME_FIELD:	
				dialog.setTitle(getString(R.string.create_item_failed_title));
				dialog.setMessage(getString(R.string.error_missing_title));
		    	break;
			case MISSING_IMAGE_FIELD:
				dialog.setTitle(getString(R.string.create_item_failed_title));
				dialog.setMessage(getString(R.string.error_missing_image));
				break;
		}
	
		return dialog;
	}

    private void onBackButton(View v) {
    	finish();
	}
    
    private void onPickImageButton(View v) {
		startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
    }
    
    private void onCreateButton(View v) {
    	EditText name = (EditText)findViewById(R.id.storage_create_item_value);
    	
    	// TODO: Validate names according to the API.
    	
		if (name.getText().toString().trim().length() == 0) {
        	showDialog(MISSING_NAME_FIELD);
        	return;	
		}
			
    	switch(createItemType){
    		case CREATE_ITEM_TYPE_TABLE:
    			// TODO: Create a new table 
    			break;
    		case CREATE_ITEM_TYPE_BLOB:
    			// TODO: Store image
    			if (imageLocation == null) {
    	        	showDialog(MISSING_IMAGE_FIELD);
    	        	return;	
    			}
    				
    			break;
    		case CREATE_ITEM_TYPE_CONTAINER:
    			// TODO: Create a new container
    			break;
    		case CREATE_ITEM_TYPE_QUEUE:
    			// TODO: Create a new queue
    			break;
    		case CREATE_ITEM_TYPE_QUEUE_MESSAGE:
    			// TODO: Create a new message in a queue
    			break;
    	}
    }

}