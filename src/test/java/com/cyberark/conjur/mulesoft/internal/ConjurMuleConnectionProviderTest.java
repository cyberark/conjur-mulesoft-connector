package com.cyberark.conjur.mulesoft.internal;

import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.core.ConjurConnection;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import com.cyberark.conjur.service.ConjurServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.lang.reflect.Field;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConjurMuleConnectionProviderTest {

    private ConjurMuleConnectionProvider provider;
    private ConjurConfiguration config;

    @Mock
    private ConjurServiceImpl mockService;

    @Mock
    private SecretsApi mockSecretsApi;

    @Mock
    private ApiClient mockApiClient;

    private static final String ACCOUNT = "myAccount";
    private static final String URL = "http://test.com";
    private static final String LOGIN = "admin";
    private static final String API_KEY = "myApiKey";
    private static final String SSL_CERT = "sslCert";
    private static final String CERT_FILE = "cert";

    @BeforeEach
    public void setUp() throws Exception {
        config = new ConjurConfiguration();
        config.setConjurAccount(ACCOUNT);
        config.setConjurApplianceUrl(URL);
        config.setConjurAuthnLogin(LOGIN);
        config.setConjurApiKey(API_KEY);
        config.setConjurSslCertificate(SSL_CERT);
        config.setConjurCertFile(CERT_FILE);

        provider = new ConjurMuleConnectionProvider();

        setPrivateField(provider, "config", config);
        setPrivateField(provider, "conjurService", mockService);
        setPrivateField(provider, "secretsApi", mockSecretsApi);
        setPrivateField(provider, "conjurAccount", ACCOUNT);
        setPrivateField(provider, "conjurApplianceUrl", URL);
        setPrivateField(provider, "conjurAuthnLogin", LOGIN);
        setPrivateField(provider, "conjurApiKey", API_KEY);
        setPrivateField(provider, "conjurSslCertificate", SSL_CERT);
        setPrivateField(provider, "conjurCertFile", CERT_FILE);
    }

    @Test
    @DisplayName("connect should return single secret when key has one value")
    public void testConnectSingleSecret() throws Exception {
        setPrivateField(provider, "key", "db/password");

        try (MockedStatic<ConjurConnection> conjurStatic = mockStatic(ConjurConnection.class)) {
            conjurStatic.when(() -> ConjurConnection.getConnection(config)).thenReturn(mockApiClient);
            conjurStatic.when(() -> ConjurConnection.getAccount(mockSecretsApi)).thenReturn(ACCOUNT);

            when(mockService.getSecret(ACCOUNT, ConjurConstant.CONJUR_KIND, "db/password"))
                    .thenReturn("mySecret");

            ConjurMuleConnection connection = provider.connect();

            assertNotNull(connection);
            assertEquals("mySecret", connection.getValue());
        }
    }

    @Test
    @DisplayName("connect should return batch secrets when key has multiple values")
    public void testConnectBatchSecret() throws Exception {
        setPrivateField(provider, "key", "db/password,db/username");

        try (MockedStatic<ConjurConnection> conjurStatic = mockStatic(ConjurConnection.class)) {
            conjurStatic.when(() -> ConjurConnection.getConnection(config)).thenReturn(mockApiClient);
            conjurStatic.when(() -> ConjurConnection.getAccount(mockSecretsApi)).thenReturn(ACCOUNT);

            when(mockService.getBatchSecrets("db/password,db/username", ACCOUNT))
                    .thenReturn("secret1,secret2");

            ConjurMuleConnection connection = provider.connect();

            assertNotNull(connection);
            assertEquals("secret1,secret2", connection.getValue());
        }
    }

    @Test
    @DisplayName("connect should throw ConnectionException when connection setup fails")
    public void testConnectThrowsModuleException() throws Exception {
        setPrivateField(provider, "key", "db/password");

        try (MockedStatic<ConjurConnection> conjurStatic = mockStatic(ConjurConnection.class)) {
            conjurStatic.when(() -> ConjurConnection.getConnection(config))
                    .thenThrow(new RuntimeException("fail"));

            assertThrows(ConnectionException.class, () -> {
                provider.connect();
            });
        }
    }

    @Test
    @DisplayName("disconnect should invalidate connection")
    public void testDisconnectCallsInvalidate() {
        ConjurMuleConnection mockConnection = mock(ConjurMuleConnection.class);

        provider.disconnect(mockConnection);

        verify(mockConnection).invalidate();
    }

    @Test
    @DisplayName("validate should return valid result")
    public void testValidateReturnsSuccess() {
        var mockConnection = mock(ConjurMuleConnection.class);
        var result = provider.validate(mockConnection);
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("isValidParameters should throw ModuleException for each missing required field")
    public void testIsValidParametersThrowsException() {
        // Empty account
        var ex1 = assertThrows(ModuleException.class, () ->
                provider.isValidParameters("", URL, LOGIN, API_KEY, SSL_CERT));
        assertTrue(ex1.getMessage().contains(ConjurConstant.INVALID_ACCOUNT));

        // Empty URL
        var ex2 = assertThrows(ModuleException.class, () ->
                provider.isValidParameters(ACCOUNT, "", LOGIN, API_KEY, SSL_CERT));
        assertTrue(ex2.getMessage().contains(ConjurConstant.INVALID_BASEURL));

        // Empty login
        var ex3 = assertThrows(ModuleException.class, () ->
                provider.isValidParameters(ACCOUNT, URL, "", API_KEY, SSL_CERT));
        assertTrue(ex3.getMessage().contains(ConjurConstant.INVALID_USERNAME));

        // Empty API key
        var ex4 = assertThrows(ModuleException.class, () ->
                provider.isValidParameters(ACCOUNT, URL, LOGIN, "", SSL_CERT));
        assertTrue(ex4.getMessage().contains(ConjurConstant.INVALID_APIKEY));

        // Empty SSL certificate
        var ex5 = assertThrows(ModuleException.class, () ->
                provider.isValidParameters(ACCOUNT, URL, LOGIN, API_KEY, ""));
        assertTrue(ex5.getMessage().contains(ConjurConstant.INVALID_SSLCERT));
    }

    @Test
    @DisplayName("isValidParameters should return false for error flag when all parameters are valid")
    public void testIsValidParametersAllValidReturnsFalse() throws Exception {
        boolean result = provider.isValidParameters(ACCOUNT, URL, LOGIN, API_KEY, SSL_CERT);
        assertFalse(result);
    }

    @Test
    @DisplayName("connect should succeed with cert auth when serviceId and cert params are set")
    public void testConnectWithCertAuth() throws Exception {
        setPrivateField(provider, "key", "db/password");
        setPrivateField(provider, "conjurAuthnCertServiceId", "my-service");
        setPrivateField(provider, "conjurClientCertFile", "/path/to/cert.crt");
        setPrivateField(provider, "conjurClientKeyFile", "/path/to/key.key");

        try (MockedStatic<ConjurConnection> conjurStatic = mockStatic(ConjurConnection.class)) {
            conjurStatic.when(() -> ConjurConnection.getConnection(any(ConjurConfiguration.class)))
                    .thenReturn(mockApiClient);
            conjurStatic.when(() -> ConjurConnection.getAccount(mockSecretsApi)).thenReturn(ACCOUNT);

            when(mockService.getSecret(ACCOUNT, ConjurConstant.CONJUR_KIND, "db/password"))
                    .thenReturn("certSecret");

            ConjurMuleConnection connection = provider.connect();

            assertNotNull(connection);
            assertEquals("certSecret", connection.getValue());
        }
    }

    @Test
    @DisplayName("connect should use SPIFFE mode (no workload ID in URL) when authnLogin is empty for cert auth")
    public void testConnectCertAuthSpiffeMode() throws Exception {
        setPrivateField(provider, "key", "db/password");
        setPrivateField(provider, "conjurAuthnCertServiceId", "my-service");
        setPrivateField(provider, "conjurClientCert", "-----BEGIN CERTIFICATE-----\nfake\n-----END CERTIFICATE-----");
        setPrivateField(provider, "conjurClientKey", "-----BEGIN PRIVATE KEY-----\nfake\n-----END PRIVATE KEY-----");
        setPrivateField(provider, "conjurAuthnLogin", "");

        try (MockedStatic<ConjurConnection> conjurStatic = mockStatic(ConjurConnection.class)) {
            conjurStatic.when(() -> ConjurConnection.getConnection(any(ConjurConfiguration.class)))
                    .thenReturn(mockApiClient);
            conjurStatic.when(() -> ConjurConnection.getAccount(mockSecretsApi)).thenReturn(ACCOUNT);

            when(mockService.getSecret(ACCOUNT, ConjurConstant.CONJUR_KIND, "db/password"))
                    .thenReturn("spiffeSecret");

            ConjurMuleConnection connection = provider.connect();

            assertNotNull(connection);
            assertEquals("spiffeSecret", connection.getValue());
        }
    }

    @Test
    @DisplayName("connect should throw ConnectionException when cert auth service ID set but cert file missing")
    public void testConnectCertAuthMissingCertThrowsException() throws Exception {
        setPrivateField(provider, "key", "db/password");
        setPrivateField(provider, "conjurAuthnCertServiceId", "my-service");
        // No conjurClientCertFile or conjurClientCert

        assertThrows(ConnectionException.class, () -> provider.connect());
    }

    @Test
    @DisplayName("connect should throw ConnectionException when cert auth service ID set but key file missing")
    public void testConnectCertAuthMissingKeyThrowsException() throws Exception {
        setPrivateField(provider, "key", "db/password");
        setPrivateField(provider, "conjurAuthnCertServiceId", "my-service");
        setPrivateField(provider, "conjurClientCertFile", "/path/to/cert.crt");
        // No conjurClientKeyFile or conjurClientKey

        assertThrows(ConnectionException.class, () -> provider.connect());
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}