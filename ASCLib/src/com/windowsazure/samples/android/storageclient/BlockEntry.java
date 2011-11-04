package com.windowsazure.samples.android.storageclient;

public class BlockEntry {

	public String id;

	public long size;
	public BlockSearchMode searchMode;
	public BlockEntry(String s, BlockSearchMode blocksearchmode) {
		searchMode = BlockSearchMode.LATEST;
		id = s;
		searchMode = blocksearchmode;
	}
}
