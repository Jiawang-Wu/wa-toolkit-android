package com.windowsazure.samples.android.sampleapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class StorageBlobViewActivity extends SecuritableActivity {

	static final String CONTAINER_NAME_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_blob_view.container_name";
	static final String BLOB_NAME_NAMESPACE = "com.windowsazure.samples.android.sampleapp.storage_blob_view.blob_name";

	static final int CONTENT_TYPE_IMAGE = 1;
	static final int CONTENT_TYPE_TEXT = 2;

	private String containerName;
	private String blobName;

	private ScrollView scrollView;
	private TextView textView;
	private ImageView imageView;
	private ProgressBar progressBar;

    @Override
    public void onCreateCompleted(Bundle savedInstanceState) {
    	super.onCreateCompleted(savedInstanceState);

    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storage_blob_view);

        Button backButton = (Button)findViewById(R.id.header_back_button);
        backButton.setVisibility(View.VISIBLE);

        TextView title = (TextView)findViewById(R.id.header_title);
        title.setText(getString(R.string.blob_view_title));

	    scrollView = (ScrollView) findViewById(R.id.storage_blob_view_scroll_view);
		textView = (TextView) findViewById(R.id.storage_blob_view_text_view);
	    imageView = (ImageView) findViewById(R.id.storage_blob_view_image_view);
	    progressBar = (ProgressBar) findViewById(R.id.storage_blob_view_progress);

	    scrollView.setVisibility(View.GONE);
	    textView.setVisibility(View.GONE);
	    imageView.setVisibility(View.GONE);
	    progressBar.setVisibility(View.GONE);

        Bundle optionSet = getIntent().getExtras();
        containerName = optionSet.getString(StorageBlobViewActivity.CONTAINER_NAME_NAMESPACE);
        blobName = optionSet.getString(StorageBlobViewActivity.BLOB_NAME_NAMESPACE);

        backButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) { onBackButton(view); }
        });
    }

    public void onStart() {
    	super.onStart();
    	new DownloadContentTask().execute(this);
    }

    private void onBackButton(View v) {
    	finish();
	}

	public class DownloadContentTask extends AsyncTask<StorageBlobViewActivity, Void, AlertDialog.Builder> {
		ByteArrayOutputStream outputStream;
		Bitmap bitmap;
		boolean imageIsTooBig = false;
		
		@Override
		protected AlertDialog.Builder doInBackground(StorageBlobViewActivity... params) {
			try {
		    	CloudBlobContainer container = params[0].getSampleApplication().getCloudBlobClient().getContainerReference(containerName);
		    	CloudBlockBlob blob = container.getBlockBlobReference(blobName);

		    	outputStream = new ByteArrayOutputStream();
		    	blob.download(outputStream);

		    	byte[] contentBytes = outputStream.toByteArray();
		    	ByteArrayInputStream intputStream = new ByteArrayInputStream(contentBytes);

		    	// First we check the size, as images too big crashes the application
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeStream(intputStream, null, options);
				intputStream.close();
				
				if (options.outHeight * options.outWidth > 512 * 512)
				{
					imageIsTooBig = true;
				}
				else
				{
					intputStream = new ByteArrayInputStream(contentBytes);
					bitmap = BitmapFactory.decodeStream(intputStream);
					intputStream.close();
				}
				
			} catch (Exception e) {
				return params[0].dialogToShow("Couldn't view the Blob's contents", e);
			} catch (OutOfMemoryError e) {
				if (bitmap != null)
				{
					bitmap.recycle();
					bitmap = null;
				}
				return params[0].dialogToShow("Couldn't view the Blob's contents", e);
			}
			return null;
		}

		protected void onPreExecute() {
			scrollView.setVisibility(View.GONE);
			textView.setVisibility(View.GONE);
			imageView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
			if (imageIsTooBig)
			{
				textView.setText("The image is too big and can't be shown. Only images smaller than 512px x 512px are supported.");
				textView.setVisibility(View.VISIBLE);
				scrollView.setVisibility(View.VISIBLE);
			}
			else if (dialogBuilder != null)
			{
				AlertDialog dialog = dialogBuilder.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
			else if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				imageView.setVisibility(View.VISIBLE);
			}
			else {
				textView.setText(new String(outputStream.toByteArray()));
				textView.setVisibility(View.VISIBLE);
				scrollView.setVisibility(View.VISIBLE);
			}
			progressBar.setVisibility(View.GONE);
		}
	}
}