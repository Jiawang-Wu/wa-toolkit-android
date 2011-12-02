package com.windowsazure.samples.android.storageclient;

public final class BlockEntry {

	public String id;

	public long size;
	public BlockSearchMode searchMode;
	
	public BlockEntry(String encodedBlockId, BlockSearchMode blockSearchMode) {
		searchMode = BlockSearchMode.LATEST;
		id = encodedBlockId;
		searchMode = blockSearchMode;
	}
	
}
