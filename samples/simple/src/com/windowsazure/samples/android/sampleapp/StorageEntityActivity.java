package com.windowsazure.samples.android.sampleapp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudTableObject;
import com.windowsazure.samples.android.storageclient.StorageCredentials;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StorageEntityActivity extends SecuritableActivity {
	
	static final String TYPE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.type";
	static final String TITLE_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.title";
	static final String PARTITION_KEY_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.partition_key";
	static final String ROW_KEY_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.row_key";
	static final String TABLE_NAME_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_entity.table_name";
	
	static final int OPERATION_TYPE_ADD = 1;
	static final int OPERATION_TYPE_EDIT = 2;
	
	private static final String PARTITION_KEY_FIELD_NAME = "PartitionKey";
	private static final String ROW_KEY_FIELD_NAME = "RowKey";
	private static final String TIMESTAMP_FIELD_NAME = "Timestamp";

	private int operationType;
	private String partitionKey;
	private String rowKey;
	private String tableName;
	private ProgressBar progressBar;
	private ArrayList<View> entityViews = new ArrayList<View>();
	private Button saveButton;
	
    @Override
    public void onCreateCompleted(Bundle savedInstanceState) {
    	super.onCreateCompleted(savedInstanceState);    	
    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_entity);
        
        saveButton = (Button)findViewById(R.id.header_save_button);
        Button backButton = (Button)findViewById(R.id.header_back_button);
        TextView title = (TextView)findViewById(R.id.header_title);
	    progressBar = (ProgressBar) findViewById(R.id.storage_entity_progress);
        
        saveButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        Bundle optionSet = getIntent().getExtras();  
        String text = optionSet.getString(StorageEntityActivity.TITLE_NAMESPACE);
        title.setText(text);
        
        operationType = optionSet.getInt(TYPE_NAMESPACE);  

        tableName = optionSet().getString(StorageEntityActivity.TABLE_NAME_NAMESPACE);
        
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
		
		final LinearLayout layout = getLayout();
		final StorageEntityActivity thisActivity = this;

		class LoadEntityDataTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
		     private Iterable<Map<String, Object>> entities;

			protected AlertDialog.Builder doInBackground(Void... dummy) {
		 		try
				{
					CloudTableClient tableClient = getSampleApplication().getCloudClientAccount().createCloudTableClient();
					StorageCredentials tableCredentials = tableClient.getCredentials();

					switch (operationType){
						case OPERATION_TYPE_EDIT:
						{
							String filter = String.format("PartitionKey eq '%s' and RowKey eq '%s'", partitionKey, rowKey);
							entities = CloudTableObject.query(tableClient.getEndpoint(), tableCredentials, tableName, filter);
							break;
						}
						case OPERATION_TYPE_ADD:
						{
							entities = CloudTableObject.query(tableClient.getEndpoint(), tableCredentials, tableName, null, 1);
							break;
						}
					}
				}
				catch (Exception exception)
				{
					return dialogToShow("Couldn't complete the operation", exception);
				}
		 		return null;
		     }

		     protected void onPreExecute() {
		    	progressBar.setVisibility(View.VISIBLE);
		     }
		     
		     protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
		    	 if (dialogBuilder == null)
		    	 {
						switch (operationType){
						case OPERATION_TYPE_EDIT:
						{
							if (entities.iterator().hasNext())
							{
								int i = 0;
								for (Entry<String, Object> prototypeProperty : entities.iterator().next().entrySet())
								{
									addTextView("field_" + i + "_text_view", prototypeProperty.getKey(), layout);
							   		addEditView(prototypeProperty.getKey(), prototypeProperty.getValue().toString(),
							   				layout, isEditableField(prototypeProperty.getKey()));
						   			++i;
								}
							}
							else
							{
								AlertDialog.Builder unexpectedDialogBuilder = new AlertDialog.Builder(thisActivity);
								unexpectedDialogBuilder.setTitle("Couldn't obtain the entity to edit");
								unexpectedDialogBuilder.setMessage("The entity doesn't appear to exist any more");
						    	Dialog dialog = unexpectedDialogBuilder.create();
						    	dialog.setCanceledOnTouchOutside(true);
						    	dialog.show();
							}
							break;
						}
						case OPERATION_TYPE_ADD:
						{
							if (entities.iterator().hasNext())
							{
								int i = 0;
								for (Entry<String, Object> prototypeProperty : entities.iterator().next().entrySet())
								{
									if (!prototypeProperty.getKey().equals(TIMESTAMP_FIELD_NAME))
									{
										addTextView("field_" + i + "_text_view", prototypeProperty.getKey(), layout);
							   			addEditView(prototypeProperty.getKey(), "", layout, true);
									}
						   			++i;
								}
							}
							else
							{
				   				addTextView("partition_key_text_view", PARTITION_KEY_FIELD_NAME, layout);
				   				addEditView(PARTITION_KEY_FIELD_NAME, "", layout, true);
				   				
				   				addTextView("row_key_text_view", ROW_KEY_FIELD_NAME, layout);
				   				addEditView(ROW_KEY_FIELD_NAME, "", layout, true);
							}
							break;
						}
					}
				}
		    	else
		    	{
			    	Dialog dialog = dialogBuilder.create();
			    	dialog.setCanceledOnTouchOutside(true);
			    	dialog.show();
		    	 }
		    	progressBar.setVisibility(View.GONE);
		     }
		}
		new LoadEntityDataTask().execute();
	}

	private boolean isEditableField(String fieldName) {
		return !fieldName.equals(TIMESTAMP_FIELD_NAME)
				&& !fieldName.equals(PARTITION_KEY_FIELD_NAME)
				&& !fieldName.equals(ROW_KEY_FIELD_NAME);
	}

	private LinearLayout getLayout() {
		return (LinearLayout)findViewById(R.id.storage_entity);
	}
	
    private void onBackButton(View v) {
    	finish();
	}

    private void onSaveButton(View v) {
    	saveButton.setEnabled(false);
		class SaveEntityDataTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
		     protected void onPreExecute() {
			 	for (View view : entityViews)
				{
			 		view.setVisibility(View.GONE);
				}
		    	progressBar.setVisibility(View.VISIBLE);
		     }

		     protected AlertDialog.Builder doInBackground(Void... dummy) {
		    	try
		    	{
					CloudTableClient tableClient = getSampleApplication().getCloudClientAccount().createCloudTableClient();
					StorageCredentials tableCredentials = tableClient.getCredentials();
					Map<String, Object> entity = new Hashtable<String, Object>();
					for (EditText editText : getEditTexts())
					{
						entity.put((String) editText.getTag(), editText.getText().toString());
					}
			    	switch (operationType){
			    		case OPERATION_TYPE_EDIT:
							CloudTableObject.update(tableClient.getEndpoint(), tableCredentials, tableName, entity);
			    			break;
			    		case OPERATION_TYPE_ADD:
							CloudTableObject.insert(tableClient.getEndpoint(), tableCredentials, tableName, entity);
			    			break;
			    	}
			    	return null;
		    	}
		    	catch (Exception exception)
		    	{
		    		return dialogToShow("Couldn't save entity", exception);
		    	}
			}
		     protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
		    	 if (dialogBuilder == null) {
						finish();
		    	 }
		    	 else
		    	 {
					 for (View view : entityViews)
					 {
						 view.setVisibility(View.VISIBLE);
					 }
			    	 progressBar.setVisibility(View.GONE);
					 saveButton.setEnabled(true);
		    		 Dialog dialog = dialogBuilder.create();
				     dialog.setCanceledOnTouchOutside(true);
				     dialog.show();
		    	 }
		     }
		}
		new SaveEntityDataTask().execute();	
	}
    
    private void addTextView(String id, String value, LinearLayout layout) {
    	TextView label = new TextView(this);
		label.setTag(id);
		label.setText(value);	
		label.setTextSize(20);
		label.setPadding(0, 5, 0, 0);
		layout.addView(label);
		entityViews.add(label);
    }
    
    private void addEditView(String id, String value, LinearLayout layout, boolean enabled) {
		EditText field = new EditText(this);
		field.setEnabled(enabled);
		field.setFocusable(enabled);
		field.setTag(id);
		field.setText(value);
		layout.addView(field);
		entityViews.add(field);
    }

    private ArrayList<EditText> getEditTexts() {
		LinearLayout layout = getLayout();
    	ArrayList<EditText> editViews = new ArrayList<EditText>();
		for (int i = 0; i < layout.getChildCount(); ++i)
		{
			View child = layout.getChildAt(i);
			if (child instanceof EditText)
			{
				editViews.add((EditText) child);
			}
		}
		return editViews;
    }
}
