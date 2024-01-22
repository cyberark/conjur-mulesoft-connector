package com.cyberark.conjur.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;

/**
 * Implementing class to get AccesToken from ApiClient
 * 
 * @author Jaleela.FaizurRahman
 *
 */
public class AccessTokenProviderImpl implements AccessTokenProvider {

	 final Logger LOGGER = LoggerFactory.getLogger(AccessTokenProviderImpl.class);

	/*
	 * Method to get the AccessToken for APIKey Authentication
	 * 
	 * @param ApiClient client
	 * 
	 * @return AccessToken
	 * 
	 * @throws ApiException
	 */
	@Override
	public AccessToken getNewAccessToken(ApiClient client) throws ApiException {
		LOGGER.debug("Start: creating new Access Token");
		try {
			return client.getNewAccessToken();
		} catch (Exception ex) {
			throw new ApiException(ex);
		}
	}

	/*
	 * Method to get the AccessToken for JWT Authentication
	 * 
	 * @param ApiClient client
	 * 
	 * @return AccessToken
	 * 
	 * @throws ApiException
	 */
	@Override
	public AccessToken getJwtAccessToken(ApiClient conjurClient, String jwtTokenPath, String authenticatorId) {

		LOGGER.warn("JWT Access Token retrieval is not implemented yet.");
		return null;
	}

}
