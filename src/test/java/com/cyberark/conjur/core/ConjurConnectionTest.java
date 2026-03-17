package com.cyberark.conjur.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;

import com.cyberark.conjur.sdk.ApiClient;
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
        // In this scenario, the SecretsApi does not have an ApiClient.
        // The getAccount() method should then fallback to the CONJUR_ACCOUNT environment variable.
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
}