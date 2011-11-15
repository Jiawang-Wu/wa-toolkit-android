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

		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		if (policy != null) {
			String signedPermissions = SharedAccessPolicy
					.permissionsToString(policy.permissions);
			if (Utility.isNullOrEmpty(signedPermissions))
				signedPermissions = null;
			String signedStart = Utility
					.getUTCTimeOrEmpty(policy.sharedAccessStartTime);
			if (!Utility.isNullOrEmpty(signedStart))
				uriQueryBuilder.add("st", signedStart);
			String signedExpiry = Utility
					.getUTCTimeOrEmpty(policy.sharedAccessExpiryTime);
			if (!Utility.isNullOrEmpty(signedExpiry))
				uriQueryBuilder.add("se", signedExpiry);
			if (!Utility.isNullOrEmpty(signedPermissions))
				uriQueryBuilder.add("sp", signedPermissions);
		}
		
		uriQueryBuilder.add("sr", signedResource);
		if (!Utility.isNullOrEmpty(signedIdentifier))
			uriQueryBuilder.add("si", signedIdentifier);
		if (!Utility.isNullOrEmpty(signature))
			uriQueryBuilder.add("sig", signature);
		return uriQueryBuilder;
	}

	protected static String generateSharedAccessSignatureHash(
			SharedAccessPolicy policy, String signedIdentifier, String saCanonicalName,
			CloudBlobClient serviceClient) throws InvalidKeyException,
			StorageException, NotImplementedException {
		Utility.assertNotNullOrEmpty("resourceName", saCanonicalName);
		Utility.assertNotNull("client", serviceClient);

		String sharedAccessSignatureString = null;
		if (policy == null) {
			Utility.assertNotNullOrEmpty("groupPolicyIdentifier", signedIdentifier);
			sharedAccessSignatureString = String.format("%s\n%s\n%s\n%s\n%s", new Object[] { "", "", "",
					saCanonicalName, signedIdentifier });
		} else {
			if (policy.sharedAccessExpiryTime == null)
				throw new IllegalArgumentException(
						"Policy Expiry time is mandatory and cannot be null");
			if (policy.permissions == null)
				throw new IllegalArgumentException(
						"Policy permissions are mandatory and cannot be null");
			sharedAccessSignatureString = String
					.format("%s\n%s\n%s\n%s\n%s",
							new Object[] {
									SharedAccessPolicy
											.permissionsToString(policy.permissions),
									Utility.getUTCTimeOrEmpty(policy.sharedAccessStartTime),
									Utility.getUTCTimeOrEmpty(policy.sharedAccessExpiryTime),
									saCanonicalName, signedIdentifier != null ? signedIdentifier : "" });
		}
		sharedAccessSignatureString = Utility.safeDecode(sharedAccessSignatureString);
		String hmac256String = serviceClient.getCredentials().computeHmac256(sharedAccessSignatureString);
		return hmac256String;
	}

	protected static StorageCredentialsSharedAccessSignature parseQuery(
			HashMap<String, String[]> sasQueryArguments) throws IllegalArgumentException,
			StorageException {
		
		String signature = null;
		String signedStart = null;
		String signedExpiry = null;
		String signedResource = null;
		String signedPermissions = null;
		String signedIdentifier = null;
		String sv = null;
		boolean hasSasParameters = false;
		StorageCredentialsSharedAccessSignature credentialsSAS = null;
		for (Entry<String, String[]> entry : sasQueryArguments.entrySet()) {
			String keyAsLowerCase = entry.getKey().toLowerCase(Locale.US);
			String entryValue = entry.getValue()[0];
			if (keyAsLowerCase.equals("st")) {
				signedStart = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("se")) {
				signedExpiry = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("sp")) {
				signedPermissions = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("sr")) {
				signedResource = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("si")) {
				signedIdentifier = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("sig")) {
				signature = entryValue;
				hasSasParameters = true;
			} else if (keyAsLowerCase.equals("sv")) {
				sv = entryValue;
				hasSasParameters = true;
			}
		}
		
		if (hasSasParameters) {
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
			if (!Utility.isNullOrEmpty(sv))
				uriquerybuilder.add("sv", sv);
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
