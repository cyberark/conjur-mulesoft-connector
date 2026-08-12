package com.cyberark.conjur.authentication;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.domain.ConjurConfiguration;
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
	/*
	 * Method to authenticate via X.509 client certificate (authn-cert / mTLS).
	 * Performs the TLS handshake against the authn-cert endpoint and returns
	 * an AccessToken ready to be used via getHeaderValue().
	 *
	 * @param config ConjurConfiguration carrying all cert-auth parameters
	 * @return AccessToken wrapping the base64-encoded Conjur token
	 * @throws ApiException on authentication failure
	 */
	public AccessToken getCertAccessToken(ConjurConfiguration config) throws ApiException;
}
