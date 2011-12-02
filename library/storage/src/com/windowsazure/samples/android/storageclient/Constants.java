package com.windowsazure.samples.android.storageclient;

final class Constants {
	
	public static class BlobConstants {

		public static final int DEFAULT_CONCURRENT_REQUEST_COUNT = 1;
		public static final String DEFAULT_DELIMITER = "/";
		public static final int DEFAULT_MINIMUM_PAGE_STREAM_WRITE_IN_BYTES = 0x400000;
		public static final int DEFAULT_MINIMUM_READ_SIZE_IN_BYTES = 0x400000;
		public static final int DEFAULT_SINGLE_BLOB_PUT_THRESHOLD_IN_BYTES = 0x2000000;
		public static final int DEFAULT_WRITE_BLOCK_SIZE_IN_BYTES = 0x400000;
		public static final int MAX_SINGLE_UPLOAD_BLOB_SIZE_IN_BYTES = 0x4000000;
		public static final int PAGE_SIZE = 512;

		public BlobConstants() {
		}
	}

	public static class HeaderConstants {
		public static final String PREFIX_FOR_STORAGE_HEADER = "x-ms-";
		public static final String APPROXIMATE_MESSAGES_COUNT = "x-ms-approximate-messages-count";
		public static final String AUTHORIZATION = "Authorization";
		public static final String BLOB_CONTENT_MD5_HEADER = "x-ms-blob-content-md5";
		public static final String BLOB_PUBLIC_ACCESS_HEADER = "x-ms-blob-public-access";
		public static final String BLOB_TYPE_HEADER = "x-ms-blob-type";
		public static final String BLOCK_BLOB = "BlockBlob";
		public static final String CACHE_CONTROL = "Cache-Control";
		public static final String CACHE_CONTROL_HEADER = "x-ms-blob-cache-control";
		public static final String COMP = "comp";
		public static final String CONTENT_ENCODING = "Content-Encoding";
		public static final String CONTENT_ENCODING_HEADER = "x-ms-blob-content-encoding";
		public static final String CONTENT_LANGUAGE = "Content-Language";
		public static final String CONTENT_LANGUAGE_HEADER = "x-ms-blob-content-language";
		public static final String CONTENT_LENGTH = "Content-Length";
		public static final String CONTENT_LENGTH_HEADER = "x-ms-blob-content-length";
		public static final String CONTENT_MD5 = "Content-MD5";
		public static final String CONTENT_RANGE = "Cache-Range";
		public static final String CONTENT_TYPE = "Content-Type";
		public static final String CONTENT_TYPE_HEADER = "x-ms-blob-content-type";
		public static final String COPY_SOURCE_HEADER = "x-ms-copy-source";
		public static final String DATE = "x-ms-date";
		public static final String DELETE_SNAPSHOT_HEADER = "x-ms-delete-snapshots";
		public static final String ETAG = "ETag";
		public static final String IF_MATCH = "If-Match";
		public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
		public static final String IF_NONE_MATCH = "If-None-Match";
		public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
		public static final String INCLUDE_SNAPSHOTS_VALUE = "include";
		public static final String LEASE_ID_HEADER = "x-ms-lease-id";
		public static final String LEASE_STATUS = "x-ms-lease-status";
		public static final String PAGE_BLOB = "PageBlob";
		public static final String PAGE_WRITE = "x-ms-page-write";
		public static final String PREFIX_FOR_STORAGE_METADATA = "x-ms-meta-";
		public static final String PREFIX_FOR_STORAGE_PROPERTIES = "x-ms-prop-";
		public static final String RANGE = "Range";
		public static final String RANGE_GET_CONTENT_MD5 = "x-ms-range-get-content-md5";
		public static final String RANGE_HEADER_FORMAT = "bytes=%d-%d";
		public static final String REQUEST_ID_HEADER = "x-ms-request-id";
		public static final String SEQUENCE_NUMBER = "x-ms-blob-sequence-number";
		public static final String SIZE = "x-ms-blob-content-length";
		public static final String SNAPSHOT = "snapshot";
		public static final String SNAPSHOT_HEADER = "x-ms-snapshot";
		public static final String SNAPSHOTS_ONLY_VALUE = "only";
		public static final String SOURCE_IF_MATCH_HEADER = "x-ms-source-if-match";
		public static final String SOURCE_IF_MODIFIED_SINCE_HEADER = "x-ms-source-if-modified-since";
		public static final String SOURCE_IF_NONE_MATCH_HEADER = "x-ms-source-if-none-match";
		public static final String SOURCE_IF_UNMODIFIED_SINCE_HEADER = "x-ms-source-if-unmodified-since";
		public static final String STORAGE_RANGE_HEADER = "x-ms-range";
		public static final String STORAGE_VERSION_HEADER = "x-ms-version";
		public static final String TARGET_STORAGE_VERSION = "2009-09-19";
		public static final String USER_AGENT = "User-Agent";
		public static final String USER_AGENT_PREFIX = "WA-Storage";

		public HeaderConstants() {
		}
	}

	public static class QueryConstants {
		public static final String SIGNATURE = "sig";
		public static final String SIGNED_EXPIRY = "se";
		public static final String SIGNED_IDENTIFIER = "si";
		public static final String SIGNED_PERMISSIONS = "sp";
		public static final String SIGNED_RESOURCE = "sr";
		public static final String SIGNED_START = "st";
		public static final String SIGNED_VERSION = "sv";
		public static final String SNAPSHOT = "snapshot";

		public QueryConstants() {
		}
	}

	public static final int KB = 1024;

	public static final int MB = 0x100000;
	public static final int GB = 0x40000000;
	protected static final int BUFFER_COPY_LENGTH = 8192;
	public static final String ACCESS_POLICY = "AccessPolicy";
	public static final String AUTHENTICATION_ERROR_DETAIL = "AuthenticationErrorDetail";
	public static final String BLOB_ELEMENT = "Blob";
	public static final String BLOB_PREFIX_ELEMENT = "BlobPrefix";
	public static final String BLOBS_ELEMENT = "Blobs";
	public static final String BLOB_TYPE_ELEMENT = "BlobType";
	public static final String BLOCK_BLOB_VALUE = "BlockBlob";
	public static final String BLOCK_ELEMENT = "Block";
	public static final String BLOCK_LIST_ELEMENT = "BlockList";
	public static final String COMMITTED_BLOCKS_ELEMENT = "CommittedBlocks";
	public static final String COMMITTED_ELEMENT = "Committed";
	public static final String CONTAINER_ELEMENT = "Container";
	public static final String CONTAINERS_ELEMENT = "Containers";
	public static final int DEFAULT_TIMEOUT_IN_MS = 0x15f90;
	public static final String DELIMITER_ELEMENT = "Delimiter";
	public static final String EMPTY_STRING = "";
	public static final String END_ELEMENT = "End";
	public static final String ERROR_CODE = "Code";
	public static final String ERROR_EXCEPTION = "ExceptionDetails";
	public static final String ERROR_EXCEPTION_MESSAGE = "ExceptionMessage";
	public static final String ERROR_EXCEPTION_STACK_TRACE = "StackTrace";
	public static final String ERROR_MESSAGE = "Message";
	public static final String ERROR_ROOT_ELEMENT = "Error";
	public static final String ETAG_ELEMENT = "Etag";
	public static final String EXPIRY = "Expiry";
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String ID = "Id";
	public static final String INVALID_METADATA_NAME = "x-ms-invalid-name";
	public static final String LAST_MODIFIED_ELEMENT = "Last-Modified";
	public static final String LATEST_ELEMENT = "Latest";
	public static final String LEASE_STATUS_ELEMENT = "LeaseStatus";
	public static final String LOCKED_VALUE = "Locked";
	public static final String MARKER_ELEMENT = "Marker";
	public static final int MAXIMUM_SEGMENTED_RESULTS = 5000;
	public static final String MAX_RESULTS_ELEMENT = "MaxResults";
	public static final int MAX_SHARED_ACCESS_POLICY_IDENTIFIERS = 5;
	public static final String MESSAGES = "messages";
	public static final String METADATA_ELEMENT = "Metadata";
	public static final String NAME_ELEMENT = "Name";
	public static final String NEXT_MARKER_ELEMENT = "NextMarker";
	public static final String PAGE_BLOB_VALUE = "PageBlob";
	public static final String PAGE_LIST_ELEMENT = "PageList";
	public static final String PAGE_RANGE_ELEMENT = "PageRange";
	public static final String PERMISSION = "Permission";
	public static final String PREFIX_ELEMENT = "Prefix";
	public static final String PROPERTIES = "Properties";
	public static final String SIGNED_IDENTIFIER_ELEMENT = "SignedIdentifier";
	public static final String SIGNED_IDENTIFIERS_ELEMENT = "SignedIdentifiers";
	public static final String SIZE_ELEMENT = "Size";
	public static final String SNAPSHOT_ELEMENT = "Snapshot";
	public static final String START = "Start";
	public static final String START_ELEMENT = "Start";
	public static final String UNCOMMITTED_BLOCKS_ELEMENT = "UncommittedBlocks";
	public static final String UNCOMMITTED_ELEMENT = "Uncommitted";
	public static final String UNLOCKED_VALUE = "Unlocked";
	public static final String URL_ELEMENT = "Url";
	
	public Constants() throws NotImplementedException {	}
	
}
