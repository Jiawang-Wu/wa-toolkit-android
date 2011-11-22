package com.windowsazure.samples.android.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class MainWindowActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	Intent storageTypeSelector = new Intent(this, StorageTypeSelectorActivity.class);
    	startActivity (storageTypeSelector);
    }
}