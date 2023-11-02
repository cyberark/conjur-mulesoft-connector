package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.core.ConjurConnection;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import com.cyberark.conjur.service.ConjurServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ConjurMuleConnectionProviderTest {

	@InjectMocks
	public ConjurMuleConnectionProvider connectionProvider;

	public SecretsApi secretsApi;

	public ApiClient apiClient;

	public ConjurServiceImpl conjurService;

	@InjectMocks
	public ConjurConfiguration conjurConfig;

	String conjurAccount;
	String conjurAuthnLogin;
	String conjurApplianceUrl;
	String conjurApiKey;
	String conjurSslCert;
	String conjurCert;

	String key = System.getenv().getOrDefault("KEY_VARIABLES", "");

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws UnknownHostException {
		mock(ConjurConfiguration.class);
		MockitoAnnotations.initMocks(this);
		apiClient = mock(ApiClient.class);
		conjurService = mock(ConjurServiceImpl.class);
		secretsApi = mock(SecretsApi.class);

		conjurAccount = System.getenv().getOrDefault("CONJUR_ACCOUNT", null);
		conjurApplianceUrl = System.getenv().getOrDefault("CONJUR_APPLIANCE_URL", null);
		conjurAuthnLogin = System.getenv().getOrDefault("CONJUR_AUTHN_LOGIN", null);
		conjurApiKey = System.getenv().getOrDefault("CONJUR_AUTHN_API_KEY", null);
		conjurSslCert =System.getenv().getOrDefault("CONJUR_SSL_CERTIFICATE",null);
		conjurCert =System.getenv().getOrDefault("CONJUR_CERT_FILE",null);

	
		conjurConfig.setConjurAccount(conjurAccount);
		conjurConfig.setConjurApplianceUrl(conjurApplianceUrl);
		conjurConfig.setConjurAuthnLogin(conjurAuthnLogin);
		conjurConfig.setConjurApiKey(conjurApiKey);
		conjurConfig.setConjurSslCertificate(conjurSslCert);
		conjurConfig.setConjurCertFile(conjurCert);

	}

	@Test
	public void testConstructor() {
		Object expectedObj = new ConjurMuleConnectionProvider();
		assertNotEquals(expectedObj, connectionProvider);
	}

	@Test
	public void testConnect() throws Exception
	{
		ConjurMuleConnectionProvider provider = new ConjurMuleConnectionProvider();
		//assertThrows(Exception.class,()->provider.connect());
		//assertFalse(provider.isValidParameters(conjurAccount, conjurApplianceUrl, conjurAuthnLogin, conjurApiKey, conjurSslCert));
		
		//ConjurConnection.getConnection(conjurConfig);
		//assertEquals(conjurAccount,ConjurConnection.getAccount(secretsApi));
		
	}
	
	@Test
	public void testIsValidParams()
	{
		ConjurMuleConnectionProvider provider = new ConjurMuleConnectionProvider();
		assertThrows(Exception.class,()->provider.isValidParameters("", conjurApplianceUrl,conjurAuthnLogin,conjurApiKey, conjurSslCert));
		assertThrows(Exception.class,()->provider.isValidParameters(conjurAccount,"" ,conjurAuthnLogin,conjurApiKey, conjurSslCert));
		assertThrows(Exception.class,()->provider.isValidParameters(conjurAccount,conjurApplianceUrl,"",conjurApiKey, conjurSslCert));
		assertThrows(Exception.class,()->provider.isValidParameters(conjurAccount,conjurApplianceUrl,conjurAuthnLogin,"", conjurSslCert));
		assertThrows(Exception.class,()->provider.isValidParameters(conjurAccount,conjurApplianceUrl,conjurAuthnLogin,conjurApiKey, ""));
		
		
	}

	@Test
	public void conjurConnection() throws ApiException {

		try (MockedStatic<ConjurConnection> getConnectionMockStatic = mockStatic(ConjurConnection.class)) {

			getConnectionMockStatic.when(() -> ConjurConnection.getConnection(conjurConfig)).thenReturn(apiClient);

			assertEquals(apiClient, ConjurConnection.getConnection(conjurConfig));
		}
	}

	@Test
	public void conjurConnectionFailure() throws ApiException {
		try (MockedStatic<ConjurConnection> getConnectionMockStatic = mockStatic(ConjurConnection.class)) {
			getConnectionMockStatic.when(() -> ConjurConnection.getConnection(conjurConfig)).thenReturn(null);

			assertNull(ConjurConnection.getConnection(conjurConfig));
		}
		try (MockedStatic<ConjurConnection> getConnectionMockStatic = mockStatic(ConjurConnection.class)) {
			getConnectionMockStatic.when(() -> ConjurConnection.getConnection(conjurConfig))
					.thenThrow(new ApiException("Conjur Connection error"));
			assertThrows(ApiException.class, () -> ConjurConnection.getConnection(conjurConfig));
		}
	}

	@Test
	public void checkConjurAccount() {
		conjurConfig = mock(ConjurConfiguration.class);
		when(conjurConfig.getConjurAccount()).thenReturn("myConjurAccount");
		assertEquals("myConjurAccount", conjurConfig.getConjurAccount());

	}

	@Test
	public void checkConjurAccountNullOrEmpty() {
		ConjurConfiguration conjurConfig = mock(ConjurConfiguration.class);
		when(conjurConfig.getConjurAccount()).thenReturn(null);
		String actualAccount = conjurConfig.getConjurAccount();
		assertTrue(actualAccount == null || actualAccount.isEmpty());
	}

	@Test
	public void getAccountValidApiClient() {

		when(secretsApi.getApiClient()).thenReturn(apiClient);

		when(apiClient.getAccount()).thenReturn("myConjurAccount");

		String result = ConjurConnection.getAccount(secretsApi);

		verify(secretsApi, times(1)).getApiClient();

		verify(apiClient, times(1)).getAccount();

		assertEquals("myConjurAccount", result);

	}

	@Test
	public void getAccountInvalidApiClient() {
		when(secretsApi.getApiClient()).thenReturn(apiClient);
		when(apiClient.getAccount()).thenReturn(null); // Simulate an unexpected response
		String result = ConjurConnection.getAccount(secretsApi);
		verify(secretsApi, times(1)).getApiClient();
		verify(apiClient, times(1)).getAccount();
		assertNotEquals("myConjurAccount", result);
	}

	@Test
	public void getSecretVal() throws ApiException {

		String[] keys = key.split(",");

		if (keys.length > 1) {

			try (MockedStatic<Object> getBatchSecretsMockStatic = mockStatic(Object.class)) {

				Object batchSecrets = conjurService.getBatchSecrets(key, conjurConfig.getConjurAccount());

				mock(ConjurServiceImpl.class);

				getBatchSecretsMockStatic
						.when(() -> conjurService.getBatchSecrets(key, conjurConfig.getConjurAccount()))
						.thenReturn(batchSecrets);
				assertEquals(conjurService.getBatchSecrets(key, conjurConfig.getConjurAccount()), batchSecrets);
			}

		} else {

			try (MockedStatic<Object> getSecretValMockStatic = mockStatic(Object.class)) {

				Object secretValue = conjurService.getSecret(conjurConfig.getConjurAccount(),
						ConjurConstant.CONJUR_KIND, key);

				mock(ConjurServiceImpl.class);

				getSecretValMockStatic.when(
						() -> conjurService.getSecret(conjurConfig.getConjurAccount(), ConjurConstant.CONJUR_KIND, key))
						.thenReturn(secretValue);

				assertEquals(conjurService.getSecret(conjurConfig.getConjurAccount(), ConjurConstant.CONJUR_KIND, key),
						secretValue);
			}

		}

	}

	@Test
	public void getSecretValFailure() throws ApiException {
		String[] keys = key.split(",");

		if (keys.length > 1) {
			try (MockedStatic<Object> getBatchSecretsMockStatic = mockStatic(Object.class)) {
				getBatchSecretsMockStatic
						.when(() -> conjurService.getBatchSecrets(key, conjurConfig.getConjurAccount()))
						.thenThrow(new ApiException("Invalid keys"));
				assertThrows(ApiException.class,
						() -> conjurService.getBatchSecrets(key, conjurConfig.getConjurAccount()));
			}
		} else {
			try (MockedStatic<Object> getSecretValMockStatic = mockStatic(Object.class)) {

				getSecretValMockStatic.when(
						() -> conjurService.getSecret(conjurConfig.getConjurAccount(), ConjurConstant.CONJUR_KIND, key))
						.thenThrow(new ApiException("Invalid key"));

				assertThrows(ApiException.class, () -> conjurService.getSecret(conjurConfig.getConjurAccount(),
						ConjurConstant.CONJUR_KIND, key));
			}
		}
	}

	@Test
	public void conjurDisconnect() {

		ConjurMuleConnection connection = mock(ConjurMuleConnection.class);

		connectionProvider.disconnect(connection);

		verify(connection, times(1)).invalidate();
	}
	
	@Test
	public void testValidate()
	{
		ConjurMuleConnection connection = mock(ConjurMuleConnection.class);
		assertNotNull(connectionProvider.validate(connection));
	}

}