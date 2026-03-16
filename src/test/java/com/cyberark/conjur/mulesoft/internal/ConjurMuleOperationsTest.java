package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConjurMuleOperationsTest {

    @Mock
    private ConjurMuleConnection mockConnection;

    private ConjurMuleOperations conjurMuleOperations;

    @Before
    public void setUp() throws Exception {
        conjurMuleOperations = new ConjurMuleOperations();
    }

    @Test
    public void retrieveSecretReturnsValue() {
        when(mockConnection.getValue()).thenReturn("mySecret");

        var result = conjurMuleOperations.retrieveSecret(null, mockConnection);

        assertEquals("mySecret", result);
    }

    @Test
    public void retrieveSecretReturnsErrorMsgOnException() throws Exception {
        when(mockConnection.getValue()).thenThrow(new RuntimeException("fail"));
        when(mockConnection.getErrorMsg()).thenReturn("errorOccurred");

        var result = conjurMuleOperations.retrieveSecret(null, mockConnection);

        assertEquals("errorOccurred", result);
    }
}