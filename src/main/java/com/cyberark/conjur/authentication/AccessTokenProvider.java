package com.cyberark.conjur.authentication;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
/**
 * Interface to retrieve AccessToken for APIKey/JWT based authentication
 * the methods calls the ApiClient getNewAccessToken() method to return
 * the AccesToken,by validating the Conjur Authentication Parameter
 */
public interface AccessTokenProvider {
	/*
	 * Method to get the AccessToken for APIKey  Authentication
	 * @param ApiClient client
	 * @return AccessToken
	 * @throws ApiException
	 */
	public AccessToken getNewAccessToken(ApiClient client) throws ApiException;
	/*
	 * Method to get the AccessToken for  JWT Authentication
	 * @param ApiClient client
	 * @return AccessToken
	 * @throws ApiException
	 */
	public AccessToken getJwtAccessToken(ApiClient conjurClient, String jwtTokenPath, String authenticatorId);


}
