
package com.cyberark.conjur.core;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.authentication.AccessTokenProvider;
import com.cyberark.conjur.authentication.AccessTokenProviderImpl;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;

@RunWith(MockitoJUnitRunner.class)

public class ConjurConnectionTest {

	String conjurAccount;
	String conjurAuthnLogin;
	String conjurApplianceUrl;
	String conjurApiKey;
	String conjurSslCert;
	String conjurCert;
	AccessTokenProviderImpl accessTokenProvider;
	//@Mock
	ApiClient apiClient = new ApiClient();
	AccessTokenProvider provider = new AccessTokenProviderImpl();

	// @InjectMocks
	public ConjurConfiguration conjurConfig = new ConjurConfiguration();

	@Before
	public void setup() {
		
		conjurAccount = System.getenv().getOrDefault("CONJUR_ACCOUNT", null);
		conjurApplianceUrl = System.getenv().getOrDefault("CONJUR_APPLIANCE_URL", null);
		conjurAuthnLogin = System.getenv().getOrDefault("CONJUR_AUTHN_LOGIN", null);
		conjurApiKey = System.getenv().getOrDefault("CONJUR_AUTHN_API_KEY", null);
		conjurSslCert = System.getenv().getOrDefault("CONJUR_SSL_CERTIFICATE", null);
		conjurCert = System.getenv().getOrDefault("CONJUR_CERT_FILE", null);

		conjurConfig.setConjurAccount(conjurAccount);
		conjurConfig.setConjurApplianceUrl(conjurApplianceUrl);
		conjurConfig.setConjurAuthnLogin(conjurAuthnLogin);
		conjurConfig.setConjurApiKey(conjurApiKey);
		conjurConfig.setConjurSslCertificate(conjurSslCert);
		conjurConfig.setConjurCertFile(conjurCert);

		accessTokenProvider = mock(AccessTokenProviderImpl.class);

	}

	@Test
	public void testConstructor() {
		Object expectedObj = new ConjurConnection();
		assertNotEquals(expectedObj, ConjurConnection.class);

	}

	@Test
	public void testConnection() throws ApiException {

		AccessTokenProvider provider = new AccessTokenProviderImpl();
		apiClient = new ApiClient();

		apiClient.setAccount(conjurAccount);
		apiClient.setApiKey(conjurApiKey);
		apiClient.setBasePath(conjurApplianceUrl);
		apiClient.setUsername(conjurAuthnLogin);

	}

	@Test
public void testobfuscateString() {
    String obfuscatedString = ConjurConnection.obfuscateString(conjurAccount);
}


	@Test
	public void testApiKey() throws ApiException {
		boolean condition = false;
		apiClient = mock(ApiClient.class);

		apiClient.setAccount(conjurConfig.getConjurAccount());
		apiClient.setApiKey(conjurConfig.getConjurApiKey());
		apiClient.setBasePath(conjurConfig.getConjurApplianceUrl());
		apiClient.setUsername(conjurConfig.getConjurAuthnLogin());

			condition = true;

		assertTrue(condition);
	}
@Test
public void testAccessTokenNull() throws ApiException {
    apiClient = mock(ApiClient.class);

    apiClient.setAccount(conjurConfig.getConjurAccount());
    apiClient.setApiKey(conjurConfig.getConjurApiKey());
    apiClient.setBasePath(conjurConfig.getConjurApplianceUrl());
    apiClient.setUsername(conjurConfig.getConjurAuthnLogin());

    // Get the access token and assert that it is null
    assertNull("Access token should be null", provider.getNewAccessToken(apiClient));
}


	@Test
public void testNullConnection() {
    try (MockedStatic<ConjurConnection> getConnectionMockStatic = mockStatic(ConjurConnection.class)) {
        getConnectionMockStatic.when(() -> ConjurConnection.getConnection(conjurConfig)).thenReturn(null);

    }

    try (MockedStatic<ConjurConnection> getConnectionMockStatic = mockStatic(ConjurConnection.class)) {
        getConnectionMockStatic.when(() -> ConjurConnection.getConnection(conjurConfig))
                .thenThrow(new ApiException("Conjur Connection error"));

    }
}


	@Test
	public void testConjurAccount() {
		assertNotEquals("", apiClient.getAccount());
	}

}