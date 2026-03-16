package com.cyberark.conjur.error;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConjurErrorProviderTest {
    private ConjurErrorProvider errorProvider;

    @Before
    public void setUp() throws Exception {
        errorProvider = new ConjurErrorProvider();
    }

    @Test
    public void getErrorTypesReturnsExpectedErrors() {
        var expectedError = ConjurErrorTypes.INVALID_DATA;

        var actualErrors = errorProvider.getErrorTypes();

        assertNotNull(actualErrors);
        assertTrue(actualErrors.contains(expectedError));
    }
}
