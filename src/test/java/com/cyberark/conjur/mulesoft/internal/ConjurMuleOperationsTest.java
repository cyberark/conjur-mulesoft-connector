package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.core.ConjurConnection;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.ApiException;

@RunWith(MockitoJUnitRunner.class)
public class ConjurMuleOperationsTest {

	public ConjurMuleConfiguration configuration;

	public ConjurMuleConnection connection;
	ConjurMuleOperations operations = new ConjurMuleOperations();
	ConjurConfiguration conjurConfig = new ConjurConfiguration();

	// String conjurAccount = System.getenv().getOrDefault("CONJUR_ACCOUNT", null);

	String conjurAccount;
	String conjurAuthnLogin;
	String conjurApplianceUrl;
	String conjurApiKey;
	String conjurSslCert;
	String conjurCert;

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

	}

	@Test
	public void testConstructor() {

		Object expectedObj = new ConjurMuleOperations();

	}

	@Test
	public void testSecret() throws ApiException {
		connection = new ConjurMuleConnection("1", "testData", 200, "OK");
		assertNotNull(connection.getValue());

	}

	@Test
	public void testRetrieveSecret() throws ApiException {

		connection = new ConjurMuleConnection("1", "testData", 200, "OK");
		Object secretVal = operations.retrieveSecret(configuration, connection);
		assertEquals(operations.retrieveSecret(configuration, connection), secretVal);

		try (MockedStatic<ConjurMuleOperations> callRetrieveMockedStatic = mockStatic(ConjurMuleOperations.class)) {
			mock(ConjurMuleConnection.class);
			mock(ConjurMuleConfiguration.class);
			ConjurMuleOperations conjurMuleOperations = mock(ConjurMuleOperations.class);
			callRetrieveMockedStatic.when(() -> conjurMuleOperations.retrieveSecret(configuration, connection))
					.thenReturn("Using Account[" + conjurAccount + "] with Connection id[" + "1" + "]");

			assertEquals("Using Account[" + conjurAccount + "] with Connection id[" + "1" + "]",
					conjurMuleOperations.retrieveSecret(configuration, connection));
		}

	}

	@Test
	public void testRetrieveException() throws ApiException {

		connection = new ConjurMuleConnection("", "", 404, "UnAuthorized");

		String errorCode = "404";
		String errorMsg = "UnAuthorized";
		try {
			String val = (String) connection.getValue();

		} catch (Exception e) {

			assertEquals(String.valueOf(connection.getErrorCode()), errorCode);
			assertEquals(connection.getErrorMsg(), errorMsg);
		}

	}

}