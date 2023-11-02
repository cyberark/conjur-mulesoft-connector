package com.cyberark.conjur.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.core.ConjurConnection;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;

@RunWith(MockitoJUnitRunner.class)
public class ConjurServiceTest {

	// @InjectMocks
	public ConjurConfiguration conjurConfig = new ConjurConfiguration();

	String conjurAccount;
	String conjurAuthnLogin;
	String conjurApplianceUrl;
	String conjurApiKey;
	String conjurSslCert;
	String conjurCert;

	ConjurServiceImpl service;

	SecretsApi secretsApi;
	// @Mock
	ConjurService provider = new ConjurServiceImpl();

	@Before
	public void setUp() {
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

		service = mock(ConjurServiceImpl.class);
		secretsApi = mock(SecretsApi.class);

	}

	@Test
	public void testGetSecret() throws ApiException {
		String key = "jenkins-app/dbUserName";
	
	}

}
