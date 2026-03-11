package com.cyberark.conjur.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;

@RunWith(MockitoJUnitRunner.class)
public class ConjurConnectionTest {

    @Mock
    private SecretsApi secretsApi;

    @Mock
    private ApiClient apiClient;

    @Test
    public void getAccountReturnsAccountWhenApiClientExists() {
        var expected = "testAccount";
        when(secretsApi.getApiClient()).thenReturn(apiClient);
        when(apiClient.getAccount()).thenReturn(expected);

        var actual = ConjurConnection.getAccount(secretsApi);

        assertEquals(expected, actual);
    }

    @Test
    public void getAccountReturnsAccountWhenApiClientDoesNotExist() {
        // In this scenario, the SecretsApi does not have an ApiClient.
        // The getAccount() method should then fallback to the CONJUR_ACCOUNT environment variable.
        var expected = System.getenv("CONJUR_ACCOUNT");
        when(secretsApi.getApiClient()).thenReturn(null);

        var actual = ConjurConnection.getAccount(secretsApi);

        assertEquals(expected, actual);
    }

    @Test
    public void obfuscateStringLongStringObfuscatesMiddle() {
        var input = "hell";
        var expected = "h*******l";

        String actual = ConjurConnection.obfuscateString(input);

        assertEquals(expected, actual);
    }

    @Test
    public void obfuscateStringShortStringReturnsSame() {
        var expected = "tt";

        var actual = ConjurConnection.obfuscateString(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void obfuscateStringEmptyStringReturnsEmpty() {
        var expected = "";

        var actual = ConjurConnection.obfuscateString(expected);

        assertEquals(expected, actual);
    }
}