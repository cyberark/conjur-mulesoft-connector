package com.cyberark.conjur.mulesoft.internal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConjurMuleOperationsTest {

    @Mock
    private ConjurMuleConnection mockConnection;

    private ConjurMuleOperations conjurMuleOperations;

    @BeforeEach
    public void setUp() throws Exception {
        conjurMuleOperations = new ConjurMuleOperations();
    }

    @Test
    @DisplayName("retrieveSecret should return secret value from connection")
    public void retrieveSecretReturnsValue() {
        when(mockConnection.getValue()).thenReturn("mySecret");

        var result = conjurMuleOperations.retrieveSecret(null, mockConnection);

        assertEquals("mySecret", result);
    }

    @Test
    @DisplayName("retrieveSecret should return error message when value retrieval fails")
    public void retrieveSecretReturnsErrorMsgOnException() throws Exception {
        when(mockConnection.getValue()).thenThrow(new RuntimeException("fail"));
        when(mockConnection.getErrorMsg()).thenReturn("errorOccurred");

        var result = conjurMuleOperations.retrieveSecret(null, mockConnection);

        assertEquals("errorOccurred", result);
    }
}