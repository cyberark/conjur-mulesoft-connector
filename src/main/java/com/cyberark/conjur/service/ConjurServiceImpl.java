package com.cyberark.conjur.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
/**
 * 
 * Returns the secrets as single/Batch retrieval
 *
 */
public class ConjurServiceImpl implements ConjurService {

	private final Logger LOGGER = LoggerFactory.getLogger(ConjurServiceImpl.class);

	SecretsApi secretsApi = new SecretsApi();
	Object secrets = null;

	/**
	 * Method to fetch secret based on the key provided
	 * @param String conjurAccount
	 * @param String variable_const "variable"
	 * @param String variableId/key
	 * @return String secret for the key provided
	 * @throws ApiException if key not found or data not found
	 */
	@Override
	public Object getSecret(String account, String variable_const, String variableId) throws ApiException {

		LOGGER.info("Getting secret for account: {}", account);
		secrets = secretsApi.getSecret(account, "variable", variableId);
		LOGGER.info("Secret retrieved successfully.");
		return secrets;
	}
	/**
	 * Method to fetch secret as batch based on the key(s) provided
	 * @param String  comma separated encoded variableId's/key's
	 * @param String conjurAccount 
	 * @return Object secret for the keys provided as JSON
	 * @throws ApiException if key not found or data not found
	 */
	@Override
	public Object getBatchSecrets(String variableIds, String account) throws ApiException {

		LOGGER.info("Getting secrets for account: {}", account);
		String[] keys = variableIds.split(",");
		StringBuilder encodeKey = new StringBuilder();

		for (String k : keys) {
			if (encodeKey.length() == 0) {

				encodeKey.append(account + ":");
				encodeKey.append("variable:");
				encodeKey.append(k);

			} else {
				encodeKey.append(",");
				encodeKey.append(account + ":");
				encodeKey.append("variable:");
				encodeKey.append(k);
			}
		}

		secrets = secretsApi.getSecrets(encodeKey.toString());
		LOGGER.info("Secrets retrieved successfully.");
		return secrets;
	}

}
