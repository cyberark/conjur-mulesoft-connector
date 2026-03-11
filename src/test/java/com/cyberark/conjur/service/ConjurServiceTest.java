package com.cyberark.conjur.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConjurServiceImpl.class})
public class ConjurServiceTest {

    @Mock
    public SecretsApi mockSecretsApi;

    public ConjurServiceImpl conjurService;

    private final String ACCOUNT = "testAccount";
    private final String VARIABLE_CONST = "variable";

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(SecretsApi.class).withNoArguments().thenReturn(mockSecretsApi);
        conjurService = new ConjurServiceImpl();
    }

    @Test
    public void getSecretReturnsSecret() throws ApiException {
        var variableId = "mySecret";
        var expectedSecret = "Secret";
        when(mockSecretsApi.getSecret(ACCOUNT, VARIABLE_CONST, variableId)).thenReturn(expectedSecret);

        var result = conjurService.getSecret(ACCOUNT, VARIABLE_CONST, variableId);

        assertEquals(expectedSecret, result);
    }

    @Test
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
