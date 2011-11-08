package com.windowsazure.samples.android.storageclient;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

final class SharedAccessSignatureHelper {

	protected static UriQueryBuilder generateSharedAccessSignature(
			SharedAccessPolicy policy, String signedIdentifier,
			String signedResource, String signature)
			throws IllegalArgumentException, StorageException {
		Utility.assertNotNullOrEmpty("resourceType", signedResource);
		Utility.assertNotNull("signature", signature);
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		if (policy != null) {
			String signedpermissions = SharedAccessPolicy
					.permissionsToString(policy.permissions);
			if (Utility.isNullOrEmpty(signedpermissions))
				signedpermissions = null;
			String signedstart = Utility
					.getUTCTimeOrEmpty(policy.sharedAccessStartTime);
			if (!Utility.isNullOrEmpty(signedstart))
				uriquerybuilder.add("st", signedstart);
			String signedexpiry = Utility
					.getUTCTimeOrEmpty(policy.sharedAccessExpiryTime);
			if (!Utility.isNullOrEmpty(signedexpiry))
				uriquerybuilder.add("se", signedexpiry);
			if (!Utility.isNullOrEmpty(signedpermissions))
				uriquerybuilder.add("sp", signedpermissions);
		}
		uriquerybuilder.add("sr", signedResource);
		if (!Utility.isNullOrEmpty(signedIdentifier))
			uriquerybuilder.add("si", signedIdentifier);
		if (!Utility.isNullOrEmpty(signature))
			uriquerybuilder.add("sig", signature);
		return uriquerybuilder;
	}

	protected static String generateSharedAccessSignatureHash(
			SharedAccessPolicy policy, String signedIdentifier, String saCanonicalName,
			CloudBlobClient serviceClient) throws InvalidKeyException,
			StorageException, NotImplementedException {
		Utility.assertNotNullOrEmpty("resourceName", saCanonicalName);
		Utility.assertNotNull("client", serviceClient);
		String s2 = null;
		if (policy == null) {
			Utility.assertNotNullOrEmpty("groupPolicyIdentifier", signedIdentifier);
			s2 = String.format("%s\n%s\n%s\n%s\n%s", new Object[] { "", "", "",
					saCanonicalName, signedIdentifier });
		} else {
			if (policy.sharedAccessExpiryTime == null)
				throw new IllegalArgumentException(
						"Policy Expiry time is mandatory and cannot be null");
			if (policy.permissions == null)
				throw new IllegalArgumentException(
						"Policy permissions are mandatory and cannot be null");
			s2 = String
					.format("%s\n%s\n%s\n%s\n%s",
							new Object[] {
									SharedAccessPolicy
											.permissionsToString(policy.permissions),
									Utility.getUTCTimeOrEmpty(policy.sharedAccessStartTime),
									Utility.getUTCTimeOrEmpty(policy.sharedAccessExpiryTime),
									saCanonicalName, signedIdentifier != null ? signedIdentifier : "" });
		}
		s2 = Utility.safeDecode(s2);
		String s3 = serviceClient.getCredentials().computeHmac256(s2);
		return s3;
	}

	protected static StorageCredentialsSharedAccessSignature parseQuery(
			HashMap<String, String[]> hashmap) throws IllegalArgumentException,
			StorageException {
		String signature = null;
		String signedStart = null;
		String signedExpiry = null;
		String signedResource = null;
		String signedPermissions = null;
		String signedIdentifier = null;
		String s6 = null;
		boolean flag = false;
		StorageCredentialsSharedAccessSignature credentialsSAS = null;
		for (Entry<String, String[]> entry : hashmap.entrySet()) {
			String keyAsLowerCase = entry.getKey().toLowerCase(Locale.US);
			String entryValue = entry.getValue()[0];
			if (keyAsLowerCase.equals("st")) {
				signedStart = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("se")) {
				signedExpiry = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("sp")) {
				signedPermissions = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("sr")) {
				signedResource = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("si")) {
				signedIdentifier = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("sig")) {
				signature = entryValue;
				flag = true;
			} else if (keyAsLowerCase.equals("sv")) {
				s6 = entryValue;
				flag = true;
			}
		}
		if (flag) {
			if (signature == null || signedResource == null) {
				throw new IllegalArgumentException(
						"Missing mandatory parameters for valid Shared Access Signature");
			}
			UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
			if (!Utility.isNullOrEmpty(signedStart))
				uriquerybuilder.add("st", signedStart);
			if (!Utility.isNullOrEmpty(signedExpiry))
				uriquerybuilder.add("se", signedExpiry);
			if (!Utility.isNullOrEmpty(signedPermissions))
				uriquerybuilder.add("sp", signedPermissions);
			uriquerybuilder.add("sr", signedResource);
			if (!Utility.isNullOrEmpty(signedIdentifier))
				uriquerybuilder.add("si", signedIdentifier);
			if (!Utility.isNullOrEmpty(s6))
				uriquerybuilder.add("sv", s6);
			if (!Utility.isNullOrEmpty(signature))
				uriquerybuilder.add("sig", signature);
			credentialsSAS = new StorageCredentialsSharedAccessSignature(
					uriquerybuilder.toString());
		}
		return credentialsSAS;
	}

	SharedAccessSignatureHelper() {
	}
}
