package com.cyberark.conjur.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConjurErrorProviderTest {
    private ConjurErrorProvider errorProvider;

    @BeforeEach
    public void setUp() throws Exception {
        errorProvider = new ConjurErrorProvider();
    }

    @Test
    @DisplayName("getErrorTypes should include INVALID_DATA")
    public void getErrorTypesReturnsExpectedErrors() {
        var expectedError = ConjurErrorTypes.INVALID_DATA;

        var actualErrors = errorProvider.getErrorTypes();

        assertNotNull(actualErrors);
        assertTrue(actualErrors.contains(expectedError));
    }
}
