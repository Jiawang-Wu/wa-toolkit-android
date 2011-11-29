package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProviderMetadata;

public class AccessControlLoginView extends LinearLayout {
	
	private static final int TextSize = 17;

	private AccessControlLoginContext mAccessControlLoginContext;
	private IdentityProviderMetadata[] mIdentityProvidersMetadata;
	
    private Context mContext;
    private LinearLayout mLoadingLayout;
    private ListView mIdentityProviders;
    private Button mCancel;
	
    public AccessControlLoginView(Context context, AccessControlLoginContext loginContext) {
        super(context);
        
        mContext = context;
        mAccessControlLoginContext = loginContext;
                
        createViewControls();       
        refreshIndentityProvidersList();
        
        this.setOrientation(VERTICAL);
    }
           
    private void createViewControls() {
        mLoadingLayout = new LinearLayout(mContext);
        mLoadingLayout.setOrientation(HORIZONTAL);
        View layoutView = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);    
        mLoadingLayout.addView(layoutView);
        layoutView = new TextView(mContext);
        ((TextView)layoutView).setText("  Loading Identity Providers...  ");
        ((TextView)layoutView).setTextSize(TextSize);
        mLoadingLayout.addView(layoutView);
        
        mIdentityProviders = new ListView(mContext);
        addView(mIdentityProviders, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
       
        mCancel = new Button(mContext);
        mCancel.setText("Cancel");
        mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				((Activity)mContext).finish();
			}
		});
        addView(mCancel, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));                       	
    }
    
    private void refreshIndentityProvidersList() {
	 	mCancel.setVisibility(INVISIBLE);
		mIdentityProviders.setVisibility(INVISIBLE);
        addView(mLoadingLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));        

		Runnable r = new Runnable()
        {
            public void run() 
            {
            	final IdentityProviderMetadata[] identityProviders = loadIdentityProviders();
    			((Activity)mContext).runOnUiThread(new Runnable() {
    			     public void run() {
    			    	 	removeView(mLoadingLayout);
    	        			setIdentityProviders(identityProviders);
    	        			mIdentityProviders.setVisibility(VISIBLE);
    	        			mCancel.setVisibility(VISIBLE);
    			    }
    			});        			
            }
        };
        
        new Thread(r).start();        
    }
    
    private IdentityProviderMetadata[] loadIdentityProviders() {
    	mIdentityProvidersMetadata = mAccessControlLoginContext.IdentityProviderRepository.getIndentityProvidersMetadata();    	
    	return mIdentityProvidersMetadata;
    }  
    
    private void setIdentityProviders(IdentityProviderMetadata[] metadata) {
    	if ((metadata != null) && (metadata.length > 0)) {
	    	mIdentityProviders.setAdapter(new AccessControlLoginAdapter(mContext, metadata));
	        mIdentityProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
	                onListItemClick(v, pos, id);
	            }
	        });       
    	}
    	else {
    		int displayId = (metadata == null) ? AccessControlLoginActivity.CANNOTLOADIPLIST : AccessControlLoginActivity.IPLISTEMPTY;
    		((Activity)mContext).showDialog(displayId);
    	}
    }

    private void onListItemClick(View v, int pos, long id) {   	
    	String loginUrl = mIdentityProvidersMetadata[pos].getLoginUrl();   	
		Intent intent = new Intent(mContext, AccessControlWebAuthActivity.class);
		Activity parentActivity = (Activity)mContext;
		intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, mAccessControlLoginContext);
		intent.putExtra(AccessControlWebAuthActivity.LoginUrlKey, loginUrl);
		parentActivity.startActivity(intent);
    }

}