package com.windowsazure.samples.android.sampleapp;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StorageEntityActivity extends SecuredActivity {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.title";
	static final String PARTITION_KEY_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.partition_key";
	static final String ROW_KEY_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.row_key";
	
	static final int OPERATION_TYPE_ADD = 1;
	static final int OPERATION_TYPE_EDIT = 2;
	
	int operationType;
	String partitionKey;
	String rowKey;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);    	
    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_entity);
        
        Button saveButton = (Button)findViewById(R.id.header_save_button);
        Button backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
        
        saveButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        Bundle optionSet = getIntent().getExtras();  
        String text = optionSet.getString(StorageEntityActivity.TITLE_NAMESPACE);
        title.setText(text);
        
        operationType = optionSet.getInt(TYPE_NAMESPACE);  
        
        if (operationType == StorageEntityActivity.OPERATION_TYPE_EDIT) {
	        partitionKey = optionSet.getString(StorageEntityActivity.PARTITION_KEY_NAMESPACE);
	        rowKey = optionSet.getString(StorageEntityActivity.ROW_KEY_NAMESPACE);
        }
        
        backButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onBackButton(view); }
        });
        
        saveButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onSaveButton(view); }
        });
    }
	
    public void onStart() {
		super.onStart();
		
		final LinearLayout layout = (LinearLayout)findViewById(R.id.storage_entity);
		
    	switch (operationType){
			case OPERATION_TYPE_EDIT:
				
				// TODO: retrieve the real row using partitionKey and rowKey and display real fields								
	   			for (int i = 0; i < 2; i++) {
	   				addTextView("field_" + i + "_text_view", "field-" + i, layout);
	   				addEditView("field_" + i + "_edit_view", "field-" + i, layout);
	   			}
	   			
				break;
			case OPERATION_TYPE_ADD:
				
				// TODO: retrieve tables fields and show real data		
   				addTextView("partition_key_text_view", "PartitionKey", layout);
   				addEditView("partition_key_edit_view", "", layout);
   				
   				addTextView("row_key_text_view", "RownKey", layout);
   				addEditView("row_key_edit_view", "", layout);
   				
	   			for (int i = 0; i < 2; i++) {
	   				addTextView("field_" + i + "_text_view", "field-" + i, layout);
	   				addEditView("field_" + i + "_edit_view", "", layout);
	   			}
	   			
				break;
		}
	}
	
    private void onBackButton(View v) {
    	finish();
	}

    private void onSaveButton(View v) {
    	switch (operationType){
    		case OPERATION_TYPE_EDIT:
    			// TODO: update existing table row.
    			break;
    		case OPERATION_TYPE_ADD:
    			// TODO: create a new table row.
    			break;
    	}
    	
		finish();
	}
    
    private void addTextView(String id, String value, LinearLayout layout) {
    	TextView label = new TextView(this);
		label.setTag(id);
		label.setText(value);	
		label.setTextSize(20);
		label.setPadding(0, 5, 0, 0);
		layout.addView(label);
    }
    
    private void addEditView(String id, String value, LinearLayout layout) {
		EditText field = new EditText(this);
		field.setTag(id);
		field.setText(value);
		layout.addView(field);
    }
}
