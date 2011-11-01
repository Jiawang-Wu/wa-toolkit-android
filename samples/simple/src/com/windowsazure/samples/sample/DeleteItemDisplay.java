package com.windowsazure.samples.sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowsazure.samples.sample.R;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.blob.AzureBlob;
import com.windowsazure.samples.blob.AzureBlobCollection;
import com.windowsazure.samples.blob.AzureBlobManager;
import com.windowsazure.samples.blob.AzureContainer;
import com.windowsazure.samples.blob.AzureContainerCollection;
import com.windowsazure.samples.queue.AzureQueue;
import com.windowsazure.samples.queue.AzureQueueCollection;
import com.windowsazure.samples.table.AzureTable;
import com.windowsazure.samples.table.AzureTableCollection;
import com.windowsazure.samples.queue.AzureQueueManager;
import com.windowsazure.samples.table.AzureTableManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DeleteItemDisplay extends Activity implements OnItemClickListener
{

	List<String> items = new ArrayList<String>();
	int listType;
	int listSubtype;
	int selectedRow;
	String blobName;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteitemdisplay);
        Button deleteButton = (Button)findViewById(R.id.DeleteButton);
        Bundle optionSet = getIntent().getExtras();
        listType = optionSet.getInt("com.windowsazure.samples.sample.deleteitemdisplay.type");
        if (listType == StorageTypeSelector.STORAGE_TYPE_TABLE)
        {
        	this.setTitle("Delete Table");
        }
        else if (listType == StorageTypeSelector.STORAGE_TYPE_BLOB)
        {
            listSubtype = optionSet.getInt("com.windowsazure.samples.sample.deleteitemdisplay.subtype");
        	if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_CONTAINER)
        	{
        		this.setTitle("Delete Blob Container");
        	}
        	else if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_BLOB)
        	{
        		blobName = optionSet.getString("com.windowsazure.samples.sample.deleteitemdisplay.blobname");
                this.setTitle("Delete Blob");
        	}
        }
        else if (listType == StorageTypeSelector.STORAGE_TYPE_QUEUE)
        {
        	this.setTitle("Delete Queue");
        }
        ListView listView = (ListView)findViewById(R.id.DeleteItemListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        deleteButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {deleteItem();}
        	});
    }

	public void onStart()
	{
		super.onStart();
		items.clear();
        try
        {
	        if (listType == StorageTypeSelector.STORAGE_TYPE_TABLE)
	        {
        		AzureTableCollection tables = new AzureTableManager(ProxySelector.credential).queryTables();
        		Iterator<AzureTable> iterator = tables.iterator();
        		while (iterator.hasNext())
        		{
        			AzureTable table = iterator.next();
        			items.add(table.getTableName());
        		}
	        }
	        else if (listType == StorageTypeSelector.STORAGE_TYPE_BLOB)
	        {
	        	if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_CONTAINER)
	        	{
	        		for (CloudBlobContainer container : ProxySelector.blobClient.listContainers())
	        		{
	        			items.add(container.getName());
	        		}
	        	}
	        	else if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_BLOB)
	        	{
	        		// blobName is actually the container name
	        		CloudBlobContainer container = ProxySelector.blobClient.getContainerReference(blobName);
	    	    	for (CloudBlob blob : container.listBlobs())
	    	    	{
	    	    		items.add(blob.getName());
	    	    	}
	        	}
	        }
	        else if (listType == StorageTypeSelector.STORAGE_TYPE_QUEUE)
	        {
        		AzureQueueCollection queues = new AzureQueueManager(ProxySelector.credential).listAllQueues();
        		Iterator<AzureQueue> iterator = queues.iterator();
          		while (iterator.hasNext())
        		{
        			AzureQueue queue = iterator.next();
        			items.add(queue.getQueueName());
        		}
	        }
	        ListView table = (ListView)findViewById(R.id.DeleteItemListView);
	        table.setAdapter(new ArrayAdapter<String>(this, android.R. layout.simple_list_item_single_choice, items));
        }
        catch (Exception e)
        {
        	System.out.println(e.toString());
        }
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		selectedRow = arg2;
	}

	private void deleteItem()
	{
        try
        {
	        if (listType == StorageTypeSelector.STORAGE_TYPE_TABLE)
	        {
	        	AzureTableManager tableWriter = new AzureTableManager(ProxySelector.credential);
	        	tableWriter.deleteTable(items.get(selectedRow));
	        }
	        else if (listType == StorageTypeSelector.STORAGE_TYPE_BLOB)
	        {
	        	if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_CONTAINER)
	        	{
		        	ProxySelector.blobClient.getContainerReference(items.get(selectedRow)).delete();
	        	}
	        	else if (listSubtype == ModifyItemDisplay.MODIFY_ITEM_SUBTYPE_BLOB)
	        	{
		        	ProxySelector.blobClient.getContainerReference(blobName)
		        		.getBlockBlobReference(items.get(selectedRow)).delete();
	        	}
	        }
	        else if (listType == StorageTypeSelector.STORAGE_TYPE_QUEUE)
	        {
	        	AzureQueueManager queueWriter = new AzureQueueManager(ProxySelector.credential);
	        	queueWriter.deleteQueue(items.get(selectedRow));
	        }
        }
        catch (Exception e)
        {
        	System.out.println(e.toString());
        }
        finish();
	}
}
