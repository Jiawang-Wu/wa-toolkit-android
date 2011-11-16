package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

final class UriQueryBuilder {

	private HashMap<String, ArrayList<String>> m_Parameters;

	UriQueryBuilder() {
		m_Parameters = new HashMap<String, ArrayList<String>>();
	}

	public void add(String parameterKey, String parameterValue)
			throws IllegalArgumentException, StorageException {
		if (Utility.isNullOrEmpty(parameterKey)) {
			throw new IllegalArgumentException(
					"Cannot encode a query parameter with a null or empty key");
		} else {
			insertKeyValue(parameterKey, parameterValue);
		}
	}

	public URI addToURI(URI originalUri) throws URISyntaxException,
			StorageException {
		String originalRawQuery = originalUri.getRawQuery();
		String originalRawFragment = originalUri.getRawFragment();
		String originalUriAsString = originalUri.resolve(originalUri)
				.toASCIIString();
		HashMap<String, String[]> queryArguments = PathUtility.parseQueryString(originalRawQuery);
		for (Iterator<Entry<String, String[]>> queryArgumentIterator = queryArguments.entrySet()
				.iterator(); queryArgumentIterator.hasNext();) {
			java.util.Map.Entry<String, String[]> entry = queryArgumentIterator
					.next();
			String argumentValues[] = (String[]) entry.getValue();
			int argumentValuesLength = argumentValues.length;
			int argumentValueIndex = 0;
			while (argumentValueIndex < argumentValuesLength) {
				String argumentValue = argumentValues[argumentValueIndex];
				insertKeyValue((String) entry.getKey(), argumentValue);
				argumentValueIndex++;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (Utility.isNullOrEmpty(originalRawQuery)
				&& !Utility.isNullOrEmpty(originalRawFragment)) {
			int anchorIndex = originalUriAsString.indexOf('#');
			stringBuilder.append(originalUriAsString.substring(0, anchorIndex));
		} else if (!Utility.isNullOrEmpty(originalRawQuery)) {
			int querySeparatorIndex = originalUriAsString.indexOf('?');
			stringBuilder.append(originalUriAsString.substring(0, querySeparatorIndex));
		} else {
			stringBuilder.append(originalUriAsString);
			if (originalUri.getRawPath().length() <= 0)
				stringBuilder.append("/");
		}
		String uriAsString = toString();
		if (uriAsString.length() > 0) {
			stringBuilder.append("?");
			stringBuilder.append(uriAsString);
		}
		if (!Utility.isNullOrEmpty(originalRawFragment)) {
			stringBuilder.append("#");
			stringBuilder.append(originalRawFragment);
		}
		return new URI(stringBuilder.toString());
	}

	private void insertKeyValue(String name, String value) throws StorageException {
		if (value != null)
			value = Utility.safeEncode(value);
		name = Utility.safeEncode(name);
		ArrayList<String> arraylist = m_Parameters.get(name);
		if (arraylist == null) {
			arraylist = new ArrayList<String>();
			arraylist.add(value);
			m_Parameters.put(name, arraylist);
		} else if (!arraylist.contains(value))
			arraylist.add(value);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		boolean firstArgument = true;
		Iterator<String> iterator = m_Parameters.keySet().iterator();
		do {
			if (!iterator.hasNext())
				break;
			String s = (String) iterator.next();
			if (m_Parameters.get(s) != null) {
				Iterator<String> iterator1 = m_Parameters.get(s)
						.iterator();
				while (iterator1.hasNext()) {
					String s1 = (String) iterator1.next();
					if (firstArgument)
						firstArgument = false;
					else
						stringBuilder.append("&");
					stringBuilder.append(String.format("%s=%s", new Object[] {
							s, s1 }));
				}
			}
		} while (true);
		return stringBuilder.toString();
	}
}
