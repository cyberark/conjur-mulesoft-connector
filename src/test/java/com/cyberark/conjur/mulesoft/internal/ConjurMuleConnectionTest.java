package com.cyberark.conjur.mulesoft.internal;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConjurMuleConnectionTest {

    private ConjurMuleConnection connection;

    private static final String TEST_ID = "id1";
    private static final Object TEST_VALUE = "value1";
    private static final int TEST_ERROR_CODE = 123;
    private static final String TEST_ERROR_MSG = "msg";

    @BeforeEach
    public void setUp() {
        connection = new ConjurMuleConnection(TEST_ID, TEST_VALUE, TEST_ERROR_CODE, TEST_ERROR_MSG);
    }

    @Test
    @DisplayName("constructor should set values and getters should return them")
    public void constructorAndGettersShouldReturnCorrectValues() {
        assertEquals(TEST_ID, connection.getId());
        assertEquals(TEST_VALUE, connection.getValue());
        assertEquals(TEST_ERROR_CODE, connection.getErrorCode());
        assertEquals(TEST_ERROR_MSG, connection.getErrorMsg());
    }
}
