package com.cyberark.conjur.authentication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenProviderTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private AccessTokenProviderImpl accessTokenProvider;

    @Test
    public void getNewAccessTokenReturnsToken() throws ApiException {
        when(apiClient.getNewAccessToken()).thenReturn(accessToken);

        var result = accessTokenProvider.getNewAccessToken(apiClient);

        assertEquals(accessToken, result);
        verify(apiClient).getNewAccessToken();
    }

    @Test
    public void getNewAccessTokenReturnsException() {
        when(apiClient.getNewAccessToken()).thenThrow(new RuntimeException("API error"));

        assertThrows(ApiException.class, () ->
                accessTokenProvider.getNewAccessToken(apiClient)
        );

        verify(apiClient).getNewAccessToken();
    }

    @Test
//    @DisplayName("getJwtAccessToken should return null because it is not implemented yet")
    public void getJWTAccessTokenReturnsNull() {
        var jwtTokenPath = "test-jwt-token-path";
        var authenticatorId = "test-authenticator-id";

        var result = accessTokenProvider.getJwtAccessToken(apiClient, jwtTokenPath, authenticatorId);

        assertNull(result);
        verifyNoInteractions(apiClient);
    }
}
