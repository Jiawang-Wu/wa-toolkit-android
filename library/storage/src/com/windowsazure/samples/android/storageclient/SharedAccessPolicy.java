package com.windowsazure.samples.android.storageclient;

import java.util.Date;
import java.util.EnumSet;

public class SharedAccessPolicy {

	public static EnumSet<SharedAccessPermissions> permissionsFromString(String policyString) {
		EnumSet<SharedAccessPermissions> permissions = EnumSet.noneOf(SharedAccessPermissions.class);

		char policyAsCharacters[] = policyString.toCharArray();
		for (char singlePermissionCharacter : policyAsCharacters) {
			switch (singlePermissionCharacter) {
			case 'r':
				permissions.add(SharedAccessPermissions.READ);
				break;

			case 'w':
				permissions.add(SharedAccessPermissions.WRITE);
				break;

			case 'd':
				permissions.add(SharedAccessPermissions.DELETE);
				break;

			case 'l':
				permissions.add(SharedAccessPermissions.LIST);
				break;

			default:
				throw new IllegalArgumentException("value");
			}
		}

		return permissions;
	}

	public static String permissionsToString(EnumSet<SharedAccessPermissions> permissions) {
		if (permissions == null) {
			return "";
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (permissions.contains(SharedAccessPermissions.READ))
			stringBuilder.append("r");
		if (permissions.contains(SharedAccessPermissions.WRITE))
			stringBuilder.append("w");
		if (permissions.contains(SharedAccessPermissions.DELETE))
			stringBuilder.append("d");
		if (permissions.contains(SharedAccessPermissions.LIST))
			stringBuilder.append("l");
		return stringBuilder.toString();
	}

	public EnumSet<SharedAccessPermissions> permissions;

	public Date sharedAccessExpiryTime;
	public Date sharedAccessStartTime;
	public SharedAccessPolicy() {
	}
}
