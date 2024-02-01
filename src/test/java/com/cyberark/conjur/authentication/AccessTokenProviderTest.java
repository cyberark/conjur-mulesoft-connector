package com.cyberark.conjur.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenProviderTest {

	//@Mock
	ApiClient apiClient = new ApiClient();

	String conjurAccount;
	String conjurAuthnLogin;
	String conjurApplianceUrl;
	String conjurApiKey;
	String conjurSslCert;
	String conjurCert;
	AccessTokenProviderImpl accessTokenProvider = null;
	AccessTokenProvider provider = new AccessTokenProviderImpl();

	@Before
	public void setup() {

		conjurAccount = System.getenv().getOrDefault("CONJUR_ACCOUNT", null);
		conjurApplianceUrl = System.getenv().getOrDefault("CONJUR_APPLIANCE_URL", null);
		conjurAuthnLogin = System.getenv().getOrDefault("CONJUR_AUTHN_LOGIN", null);
		conjurApiKey = System.getenv().getOrDefault("CONJUR_AUTHN_API_KEY", null);
		conjurSslCert = System.getenv().getOrDefault("CONJUR_SSL_CERTIFICATE", null);
		conjurCert = System.getenv().getOrDefault("CONJUR_CERT_FILE", null);

		accessTokenProvider = mock(AccessTokenProviderImpl.class);
		apiClient = mock(ApiClient.class);

		apiClient.setAccount(conjurAccount);
		apiClient.setApiKey(conjurApiKey);
		apiClient.setBasePath(conjurApplianceUrl);
		apiClient.setUsername(conjurAuthnLogin);

	}

	@Test
	public void testConstructor() {

		Object expectedObj = new AccessTokenProviderImpl();
		assertNotEquals(expectedObj, provider);

	}

	@Test
	public void getNewAccessToken() throws ApiException {


		try (MockedStatic<AccessToken> getAccessTokenMockStatic = mockStatic(AccessToken.class)) {

			AccessToken token1 = accessTokenProvider.getNewAccessToken(apiClient);// apiClient.getNewAccessToken();
			getAccessTokenMockStatic.when(() -> apiClient.getNewAccessToken()).thenReturn(token1);
			assertEquals(apiClient.getNewAccessToken(), token1);
		}

	}

	@Test
	public void getJWTAccessToken() {

		try (MockedStatic<AccessToken> getAccessTokenMockStatic = mockStatic(AccessToken.class)) {
			AccessToken token = accessTokenProvider.getJwtAccessToken(apiClient, null, null);
			assertEquals(token, null);
		}

	}

	@Test
	public void whenAccessTokenException() {
		try (MockedStatic<AccessToken> getAccessTokenMockStatic = mockStatic(AccessToken.class)) {
			getAccessTokenMockStatic.when(() -> accessTokenProvider.getNewAccessToken(apiClient))
					.thenThrow(new ApiException("Access Token invalid"));
			assertThrows(ApiException.class, () -> accessTokenProvider.getNewAccessToken(apiClient));
		}

	}

}
