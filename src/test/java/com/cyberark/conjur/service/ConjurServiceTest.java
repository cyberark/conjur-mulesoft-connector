package com.cyberark.conjur.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ConjurServiceTest {

    @Mock
    public SecretsApi mockSecretsApi;

    public ConjurServiceImpl conjurService;

    private final String ACCOUNT = "testAccount";
    private final String VARIABLE_CONST = "variable";

    @BeforeEach
    public void setUp() {
        conjurService = new ConjurServiceImpl(mockSecretsApi);
    }

    @Test
    @DisplayName("getSecret should return secret from SecretsApi")
    public void getSecretReturnsSecret() throws ApiException {
        var variableId = "mySecret";
        var expectedSecret = "Secret";
        when(mockSecretsApi.getSecret(ACCOUNT, VARIABLE_CONST, variableId))
                .thenReturn(expectedSecret);
        var result = conjurService.getSecret(ACCOUNT, VARIABLE_CONST, variableId);

        assertEquals(expectedSecret, result);
    }

    @Test
    @DisplayName("getBatchSecrets should return mapped secrets for variable IDs")
    public void getBatchSecretsReturnsSecrets() throws ApiException {
        var variableIds = "secret1,secret2";

        var expectedEncoded = String.format(
                "%s:variable:secret1,%s:variable:secret2",
                ACCOUNT, ACCOUNT
        );

        var expectedSecrets = Map.of(
                "secret1", "value1",
                "secret2", "value2"
        );

        when(mockSecretsApi.getSecrets(expectedEncoded)).thenReturn(expectedSecrets);
        var actualSecrets = conjurService.getBatchSecrets(variableIds, ACCOUNT);

        assertEquals(expectedSecrets, actualSecrets);
    }
}
