package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map.Entry;

public final class PathUtility {
	public static URI addToQuery(URI uri, HashMap<String, String[]> queryArguments)
			throws URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		for (Entry<String, String[]> queryArgument : queryArguments.entrySet()) {
			for (String argumentValue : queryArgument.getValue()) {
				uriQueryBuilder.add(queryArgument.getKey(), argumentValue);
			}
		}

		return uriQueryBuilder.addToURI(uri);
	}

	public static URI addToQuery(URI uri, String queryString) throws URISyntaxException,
			StorageException {
		return addToQuery(uri, parseQueryString(queryString));
	}

	public static URI appendPathToUri(URI baseUri, String path)
			throws URISyntaxException {
		return appendPathToUri(baseUri, path, "/");
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
		String containerName = getContainerNameFromUri(uri);
		URI containerUri = appendPathToUri(new URI(getServiceClientBaseAddress(uri)), containerName);
		return containerUri;
	}

	public static String getServiceClientBaseAddress(URI uri)
			throws URISyntaxException {
		return (new URI(uri.getScheme(), uri.getAuthority(), null, null,
				null)).toString();
	}

	public static HashMap<String, String[]> parseQueryString(String queryString)
			throws StorageException {
		HashMap<String, String[]> queryArguments = new HashMap<String, String[]>();
		if (Utility.isNullOrEmpty(queryString)) {
			return queryArguments;
		}

		int querySeparatorIndex = queryString.indexOf("?");
		if (querySeparatorIndex >= 0 && queryString.length() > 0) {
			queryString = queryString.substring(querySeparatorIndex + 1);
		}

		String queryArgumentsAsStringsList[] = queryString.contains("&") ? queryString.split("&") : queryString.split(";");

		for (String queryArgument : queryArgumentsAsStringsList) {
			int queryArgumentSeparatorIndex = queryArgument.indexOf("=");
			if (queryArgumentSeparatorIndex < 0) {
				continue;
			}

			String argumentName = queryArgument.substring(0, queryArgumentSeparatorIndex);
			String argumentValue = queryArgument.substring(queryArgumentSeparatorIndex + 1);
			argumentName = Utility.safeDecode(argumentName);
			argumentValue = Utility.safeDecode(argumentValue);

			String queryArgumentValues[] = queryArguments.get(argumentName);

			if (queryArgumentValues == null) {
				queryArgumentValues = new String[] { argumentValue };
			}
			else {
				String enlargedArray[] = new String[queryArgumentValues.length + 1];
				for (int index = 0; index < queryArgumentValues.length; index++) {
					enlargedArray[index] = queryArgumentValues[index];
				}

				enlargedArray[enlargedArray.length] = argumentValue;
				queryArgumentValues = enlargedArray;
			}
			queryArguments.put(argumentName, queryArgumentValues);
		}

		return queryArguments;
	}

	public static URI stripURIQueryAndFragment(URI uri) throws StorageException {
		try {
			return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
					null, null);
		} catch (URISyntaxException urisyntaxexception) {
			throw StorageException
					.generateNewUnexpectedStorageException(urisyntaxexception);
		}
	}
}
