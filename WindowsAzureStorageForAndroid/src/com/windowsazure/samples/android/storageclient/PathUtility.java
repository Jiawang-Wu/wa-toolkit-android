package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PathUtility {
	public static URI addToQuery(URI uri, HashMap<String, String[]> hashmap)
			throws URISyntaxException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		for (Iterator<Entry<String, String[]>> iterator = hashmap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, String[]> entry = iterator.next();
			String as[] = (String[]) entry.getValue();
			int i = as.length;
			int j = 0;
			while (j < i) {
				String s = as[j];
				uriquerybuilder.add((String) entry.getKey(), s);
				j++;
			}
		}

		return uriquerybuilder.addToURI(uri);
	}

	public static URI addToQuery(URI uri, String s) throws URISyntaxException,
			StorageException {
		return addToQuery(uri, parseQueryString(s));
	}

	public static URI appendPathToUri(URI uri, String path)
			throws URISyntaxException {
		return appendPathToUri(uri, path, "/");
	}

	public static URI appendPathToUri(URI baseUri, String path,
			String pathSeparator) throws URISyntaxException {
		byte authorityStartIndex = -1;
		if (path.length() > 8) {
			String prefixOf8 = path.substring(0, 8).toLowerCase();
			if ("https://".equals(prefixOf8)) {
				authorityStartIndex = 8;
			} else if ("http://".equals(prefixOf8.substring(0, 7))) {
				authorityStartIndex = 7;
			}
		}
		if (authorityStartIndex > 0) {
			int pathSeparatorIndex = path.substring(authorityStartIndex)
					.indexOf(pathSeparator);
			String authority = path.substring(authorityStartIndex,
					authorityStartIndex + pathSeparatorIndex);
			URI pathAsUri = new URI(path);
			if (baseUri.getAuthority().equals(authority)) {
				return pathAsUri;
			} else {
				return new URI(baseUri.getScheme(), baseUri.getAuthority(),
						pathAsUri.getPath(), pathAsUri.getRawQuery(),
						pathAsUri.getRawFragment());
			}
		}
		if (baseUri.getPath().length() == 0 && path.startsWith(pathSeparator)) {
			return new URI(baseUri.getScheme(), baseUri.getAuthority(), path,
					baseUri.getRawQuery(), baseUri.getRawFragment());
		}
		StringBuilder stringbuilder = new StringBuilder(baseUri.getPath());
		if (baseUri.getPath().endsWith(pathSeparator)) {
			stringbuilder.append(path);
		} else {
			stringbuilder.append(pathSeparator);
			stringbuilder.append(path);
		}
		return new URI(baseUri.getScheme(), baseUri.getAuthority(),
				stringbuilder.toString(), baseUri.getQuery(),
				baseUri.getFragment());
	}

	protected static String getBlobNameFromURI(URI uri)
			throws URISyntaxException {
		return Utility.safeRelativize(new URI(getContainerURI(uri).toString()
				.concat("/")), uri);
	}

	public static String getContainerNameFromUri(URI uri)
			throws IllegalArgumentException {
		Utility.assertNotNull("resourceAddress", uri);
		String pathParts[] = uri.getRawPath().split("/");
		byte properContainerPathPartsCount = 2;
		if (pathParts.length < properContainerPathPartsCount) {
			String s = String.format(
					"Invalid blob address '%s', missing container information",
					new Object[] { uri });
			throw new IllegalArgumentException(s);
		} else {
			String containerFolder = pathParts[1];
			return Utility.trimEnd(containerFolder, '/');
		}
	}

	public static URI getContainerURI(URI uri) throws URISyntaxException {
		String s = getContainerNameFromUri(uri);
		URI uri1 = appendPathToUri(new URI(getServiceClientBaseAddress(uri)), s);
		return uri1;
	}

	public static String getServiceClientBaseAddress(URI uri)
			throws URISyntaxException {
		return (new URI(uri.getScheme(), uri.getAuthority(), null, null,
				null)).toString();
	}

	public static HashMap<String, String[]> parseQueryString(String s)
			throws StorageException {
		HashMap<String, String[]> hashmap = new HashMap<String, String[]>();
		if (Utility.isNullOrEmpty(s))
			return hashmap;
		int i = s.indexOf("?");
		if (i >= 0 && s.length() > 0)
			s = s.substring(i + 1);
		String as[] = s.contains("&") ? s.split("&") : s.split(";");
		for (int j = 0; j < as.length; j++) {
			int k = as[j].indexOf("=");
			if (k < 0)
				continue;
			String s1 = as[j].substring(0, k);
			String s2 = as[j].substring(k + 1);
			s1 = Utility.safeDecode(s1);
			s2 = Utility.safeDecode(s2);
			String as1[] = hashmap.get(s1);
			if (as1 == null) {
				as1 = (new String[] { s2 });
				hashmap.put(s1, as1);
				continue;
			}
			String as2[] = new String[as1.length + 1];
			for (int l = 0; l < as1.length; l++)
				as2[l] = as1[l];

			as2[as2.length] = s2;
		}

		return hashmap;
	}

	public static URI stripURIQueryAndFragment(URI uri) throws StorageException {
		try {
			return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
					null, null);
		} catch (URISyntaxException urisyntaxexception) {
			throw Utility
					.generateNewUnexpectedStorageException(urisyntaxexception);
		}
	}
}
