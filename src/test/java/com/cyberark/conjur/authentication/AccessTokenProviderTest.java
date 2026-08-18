package com.cyberark.conjur.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccessTokenProviderTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private AccessTokenProviderImpl accessTokenProvider;

    @Test
    @DisplayName("getNewAccessToken should return token on success")
    public void getNewAccessTokenReturnsToken() throws ApiException {
        when(apiClient.getNewAccessToken()).thenReturn(accessToken);

        var result = accessTokenProvider.getNewAccessToken(apiClient);

        assertEquals(accessToken, result);
        verify(apiClient).getNewAccessToken();
    }

    @Test
    @DisplayName("getNewAccessToken should throw ApiException on client error")
    public void getNewAccessTokenReturnsException() {
        when(apiClient.getNewAccessToken()).thenThrow(new RuntimeException("API error"));

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getNewAccessToken(apiClient)
        );

        verify(apiClient).getNewAccessToken();
    }

    @Test
    @DisplayName("getJwtAccessToken should return null (not implemented)")
    public void getJWTAccessTokenReturnsNull() {
        var jwtTokenPath = "test-jwt-token-path";
        var authenticatorId = "test-authenticator-id";

        var result = accessTokenProvider.getJwtAccessToken(apiClient, jwtTokenPath, authenticatorId);

        assertNull(result);
        verifyNoInteractions(apiClient);
    }

    @Test
    @DisplayName("getCertAccessToken should throw ApiException when client cert is missing")
    public void getCertAccessTokenThrowsWhenClientCertMissing() {
        ConjurConfiguration config = minimalCertAuthConfig();
        // No client cert set

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getCertAccessToken(config)
        );
    }

    @Test
    @DisplayName("getCertAccessToken should throw ApiException when client key is missing")
    public void getCertAccessTokenThrowsWhenClientKeyMissing() {
        ConjurConfiguration config = minimalCertAuthConfig();
        config.setConjurClientCert("-----BEGIN CERTIFICATE-----\nMIIFake\n-----END CERTIFICATE-----");
        // No client key set

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getCertAccessToken(config)
        );
    }

    @Test
    @DisplayName("getCertAccessToken should throw ApiException when inline PEM cert is malformed")
    public void getCertAccessTokenThrowsOnMalformedInlineCert() {
        ConjurConfiguration config = minimalCertAuthConfig();
        config.setConjurClientCert("not-valid-pem");
        config.setConjurClientKey("not-valid-pem");

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getCertAccessToken(config)
        );
    }

    @Test
    @DisplayName("getCertAccessToken should throw ApiException when cert file path does not exist")
    public void getCertAccessTokenThrowsWhenCertFileNotFound() {
        ConjurConfiguration config = minimalCertAuthConfig();
        config.setConjurClientCertFile("/nonexistent/path/client.crt");
        config.setConjurClientKeyFile("/nonexistent/path/client.key");

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getCertAccessToken(config)
        );
    }

    private ConjurConfiguration minimalCertAuthConfig() {
        ConjurConfiguration config = new ConjurConfiguration();
        config.setConjurAccount("conjur");
        config.setConjurApplianceUrl("https://conjur.example.com");
        config.setConjurAuthnCertServiceId("test-service");
        return config;
    }
}
