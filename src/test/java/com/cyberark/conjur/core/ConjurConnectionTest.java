package com.cyberark.conjur.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.junit.jupiter.api.Test;

import com.cyberark.conjur.authentication.AccessTokenProviderImpl;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConjurConnectionTest {

    @Mock
    private SecretsApi secretsApi;

    @Mock
    private ApiClient apiClient;

    @Test
    @DisplayName("getAccount should return account from api client")
    public void getAccountReturnsAccountWhenApiClientExists() {
        var expected = "testAccount";
        when(secretsApi.getApiClient()).thenReturn(apiClient);
        when(apiClient.getAccount()).thenReturn(expected);

        var actual = ConjurConnection.getAccount(secretsApi);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("getAccount should return env account when api client is missing")
    public void getAccountReturnsAccountWhenApiClientDoesNotExist() {
        var expected = System.getenv("CONJUR_ACCOUNT");
        when(secretsApi.getApiClient()).thenReturn(null);

        var actual = ConjurConnection.getAccount(secretsApi);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("obfuscateString should obfuscate middle for long strings")
    public void obfuscateStringLongStringObfuscatesMiddle() {
        var input = "hell";
        var expected = "h*******l";

        String actual = ConjurConnection.obfuscateString(input);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("obfuscateString should return same value for short strings")
    public void obfuscateStringShortStringReturnsSame() {
        var expected = "tt";

        var actual = ConjurConnection.obfuscateString(expected);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("obfuscateString should return empty for empty string")
    public void obfuscateStringEmptyStringReturnsEmpty() {
        var expected = "";

        var actual = ConjurConnection.obfuscateString(expected);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("getConnection should use getCertAccessToken when cert auth params are present")
    public void getConnectionCallsCertAccessTokenWhenCertParamsPresent() throws ApiException {
        ConjurConfiguration config = new ConjurConfiguration();
        config.setConjurAccount("conjur");
        config.setConjurApplianceUrl("https://conjur.example.com");
        config.setConjurAuthnCertServiceId("test-service");
        config.setConjurClientCertFile("/path/to/cert.crt");
        config.setConjurClientKeyFile("/path/to/key.key");

        try (MockedConstruction<AccessTokenProviderImpl> mocked =
                mockConstruction(AccessTokenProviderImpl.class, (mock, ctx) -> {
                    AccessToken mockToken = mock(AccessToken.class);
                    when(mockToken.getHeaderValue()).thenReturn("Token token=rawBase64Token");
                    when(mock.getCertAccessToken(any(ConjurConfiguration.class)))
                            .thenReturn(mockToken);
                })) {

            ApiClient result = ConjurConnection.getConnection(config);

            assertNotNull(result);
            AccessTokenProviderImpl mockProvider = mocked.constructed().get(0);
            verify(mockProvider).getCertAccessToken(config);
            verifyNoMoreInteractions(mockProvider);
        }
    }

    @Test
    @DisplayName("getConnection should throw ApiException when getCertAccessToken returns null")
    public void getConnectionThrowsWhenCertTokenIsNull() {
        ConjurConfiguration config = new ConjurConfiguration();
        config.setConjurAccount("conjur");
        config.setConjurApplianceUrl("https://conjur.example.com");
        config.setConjurAuthnCertServiceId("test-service");
        config.setConjurClientCert("-----BEGIN CERTIFICATE-----\nfake\n-----END CERTIFICATE-----");
        config.setConjurClientKey("-----BEGIN PRIVATE KEY-----\nfake\n-----END PRIVATE KEY-----");

        try (MockedConstruction<AccessTokenProviderImpl> mocked =
                mockConstruction(AccessTokenProviderImpl.class, (mock, ctx) ->
                        when(mock.getCertAccessToken(any(ConjurConfiguration.class)))
                                .thenReturn(null))) {

            assertThrows(ApiException.class, () -> ConjurConnection.getConnection(config));
        }
    }
}