package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProviderMetadata;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccessControlLoginAdapter extends BaseAdapter {

    private Context mContext;
    private IdentityProviderMetadata[] mIdentityProvidersMetadata;
	
	public AccessControlLoginAdapter(Context context, IdentityProviderMetadata[] identityProvidersMetadata) {
		mContext = context;
		mIdentityProvidersMetadata = identityProvidersMetadata;
	}
	
	@Override
	public int getCount() {
		return mIdentityProvidersMetadata.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IdentityProviderView view = null;
		
        if (convertView == null) {
        	view = new IdentityProviderView(mContext, mIdentityProvidersMetadata[position].getName());
        } else {
        	view = (IdentityProviderView) convertView;
        	view.setName(mIdentityProvidersMetadata[position].getName());
        }

        return view;
	}

    private static class IdentityProviderView extends TextView {
    	
    	private static final Integer TextSize = 17;
    	private static final Integer Padding = 11;
    	
        public IdentityProviderView(Context context, String name) {
            super(context);

            this.setText(name);
            this.setTextSize(TextSize);
            this.setPadding(Padding, Padding, Padding, Padding);
        }
        
		public void setName(String name) {
        	this.setText(name);
        }
		
    }
    
}
