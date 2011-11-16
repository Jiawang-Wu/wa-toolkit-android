package com.windowsazure.samples.android.storageclient;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum QueueListingDetails
{
    None(0), Metadata(1), All(Metadata);

    private static final Map<Integer,QueueListingDetails> lookup 
    = new HashMap<Integer,QueueListingDetails>();

	static {
	    for(QueueListingDetails s : EnumSet.allOf(QueueListingDetails.class))
	         lookup.put(s.getCode(), s);
	}
	
	private int code;
	
	private QueueListingDetails(int code) {
	    this.code = code;
	}
	
	private QueueListingDetails(QueueListingDetails queueListingDetails) {
	    this.code = queueListingDetails.getCode();
	}

	public int getCode() { return code; }
	
	public boolean equals(QueueListingDetails queueListingDetails)
	{
		return this.getCode() == queueListingDetails.getCode();
	}
	
	public static QueueListingDetails get(int code) { 
	    return lookup.get(code); 
	}
}