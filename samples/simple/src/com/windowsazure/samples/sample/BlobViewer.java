package com.windowsazure.samples.sample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.blob.AzureBlob;
import com.windowsazure.samples.blob.AzureBlobManager;
import com.windowsazure.samples.blob.data.BitmapBlobData;
import com.windowsazure.samples.sample.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class BlobViewer extends Activity
{

	String containerName = null;
	String blobName = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blobviewer);
        Bundle optionSet = getIntent().getExtras();
        containerName = optionSet.getString("com.windowsazure.samples.sample.blobviewer.container");
        blobName = optionSet.getString("com.windowsazure.samples.sample.blobviewer.blob");
        this.setTitle(optionSet.getString("com.windowsazure.samples.sample.blobviewer.blob"));
    }

	public void onStart()
	{
		super.onStart();
		ImageView imageView = (ImageView)findViewById(R.id.BlobImageView);
		try
		{
			CloudBlockBlob blob = ProxySelector.blobClient.getContainerReference(containerName).getBlockBlobReference(blobName);
			ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
			blob.download(contentStream);
			byte[] content = contentStream.toByteArray();
			Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
	        imageView.setImageBitmap(bitmap);
		}
        catch (Exception e)
        {
        	System.out.println(e.toString());
        }
	}
}
