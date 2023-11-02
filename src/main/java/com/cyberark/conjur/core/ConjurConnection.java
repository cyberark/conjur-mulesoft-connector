package com.cyberark.conjur.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.authentication.AccessTokenProvider;
import com.cyberark.conjur.authentication.AccessTokenProviderImpl;
import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.Configuration;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;

/**
 * Class to retrieve the AccessToken by authenticating the request with
 * APIKey/JWT token The conjur authentication parameter are passed either a
 * configuration parameter in the flow or as configured in conjur.properties
 * file and maintained by the CustomConfiguration parameter.
 * <B>ConjurAccount</B> <b>ConjurApplianceURL(BasePath)</b>
 * <b>ConjurAuthnLogin(UserName)</b> <b>ConjurAPIKey/JWTToken</b>
 * <b>ConjurSSLCertiifcate</b> <b>ConjurCertFile</b> on successful retrieval of
 * AccessToken, access Token will set in header for each request call to
 * retrieve the secrets else throws ApiException, in case of invalid conjur
 * authentication parameters or unauthorised access.
 *
 */
public class ConjurConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConjurConnection.class);

	/**
	 * Method to get the accesstoken and set in the header
	 * 
	 * @param config ConjurConfiguration object
	 * @return ApiClient
	 * @throws ApiException
	 */
	public static ApiClient getConnection(ConjurConfiguration config) throws ApiException {

		LOGGER.debug("Start: Calling getConnection()");
		AccessToken accessToken = null;

		ApiClient client = Configuration.getDefaultApiClient();
		AccessTokenProvider accessTokenProvider = new AccessTokenProviderImpl();

		String conjurAccount = config.getConjurAccount();
		String conjurBasePath = config.getConjurApplianceUrl();
		String conjurUserName = config.getConjurAuthnLogin();
		String conjurApiKey = config.getConjurApiKey();
		String sslCertificate = config.getConjurSslCertificate();
		String certFile = config.getConjurCertFile();

		client.setAccount(conjurAccount);
		client.setBasePath(conjurBasePath);
		client.setUsername(conjurUserName);
		client.setApiKey(conjurApiKey);

		InputStream sslInputStream = null;

		try {
			if (StringUtils.isNotEmpty(sslCertificate)) {
				sslInputStream = new FileInputStream(sslCertificate);
			} else {
				if (StringUtils.isNotEmpty(certFile))
					sslInputStream = new FileInputStream(certFile);
			}

			if (sslInputStream != null) {
				client.setSslCaCert(sslInputStream);
				sslInputStream.close();
			}
		} catch (IOException ex) {
			LOGGER.error("Error processing CERT_FILE: {}", ex.getMessage());

		}

		if (conjurApiKey != null && !conjurApiKey.isEmpty()) {
			try {
				accessToken = accessTokenProvider.getNewAccessToken(client);

				String token = accessToken.getHeaderValue();
				client.setAccessToken(token);
				Configuration.setDefaultApiClient(client);
				LOGGER.debug("StatusCode:200_OK" + ConjurConstant.CODE_200_CONNECTION);
			}

			catch (Exception ex) {

				throw new ApiException(ex);
			}
		}

		return client;
	}

	/**
	 * Method to get the account after successful authentication to retrieve the
	 * 
	 * @param secretApi
	 * @return String account for the request
	 */
	public static String getAccount(SecretsApi secretApi) {
		ApiClient apiClient = secretApi.getApiClient();
		return (apiClient != null) ? apiClient.getAccount() : ConjurConstant.CONJUR_ACCOUNT;
	}

	/**
	 * Obfuscate string string.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String obfuscateString(String str) {
		if (StringUtils.isNoneEmpty(str) && str.length() > 2) {
			int len = str.length();
			char first = str.charAt(0);
			char last = str.charAt(len - 1);
			String middle = "*******";
			return first + middle + last;

		}
		return str;
	}
}
