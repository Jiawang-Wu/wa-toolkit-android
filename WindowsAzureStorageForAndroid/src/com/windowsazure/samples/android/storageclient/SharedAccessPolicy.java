package com.windowsazure.samples.android.storageclient;

import java.util.Date;
import java.util.EnumSet;

public class SharedAccessPolicy {

	public static EnumSet permissionsFromString(String s) {
		char ac[] = s.toCharArray();
		EnumSet enumset = EnumSet.noneOf(SharedAccessPermissions.class);
		char ac1[] = ac;
		int i = ac1.length;
		for (int j = 0; j < i; j++) {
			char c = ac1[j];
			switch (c) {
			case 114: // 'r'
				enumset.add(SharedAccessPermissions.READ);
				break;

			case 119: // 'w'
				enumset.add(SharedAccessPermissions.WRITE);
				break;

			case 100: // 'd'
				enumset.add(SharedAccessPermissions.DELETE);
				break;

			case 108: // 'l'
				enumset.add(SharedAccessPermissions.LIST);
				break;

			default:
				throw new IllegalArgumentException("value");
			}
		}

		return enumset;
	}

	public static String permissionsToString(EnumSet enumset) {
		if (enumset == null)
			return "";
		StringBuilder stringbuilder = new StringBuilder();
		if (enumset.contains(SharedAccessPermissions.READ))
			stringbuilder.append("r");
		if (enumset.contains(SharedAccessPermissions.WRITE))
			stringbuilder.append("w");
		if (enumset.contains(SharedAccessPermissions.DELETE))
			stringbuilder.append("d");
		if (enumset.contains(SharedAccessPermissions.LIST))
			stringbuilder.append("l");
		return stringbuilder.toString();
	}

	public EnumSet permissions;

	public Date sharedAccessExpiryTime;
	public Date sharedAccessStartTime;
	public SharedAccessPolicy() {
	}
}
