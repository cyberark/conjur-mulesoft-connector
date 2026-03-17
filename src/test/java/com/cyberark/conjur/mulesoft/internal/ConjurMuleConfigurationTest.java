package com.cyberark.conjur.mulesoft.internal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConjurMuleConfigurationTest {

    private ConjurMuleConfiguration conjurMuleConfiguration;

    @BeforeEach
    public void setUp() throws Exception {
        conjurMuleConfiguration = new ConjurMuleConfiguration();
    }

	@Test
    @DisplayName("setConfigId should persist the config ID so getConfigId returns the same value")
	public void setConfigIdShouldStoreValue() {
        var expectedConfigId = "1";

        conjurMuleConfiguration.setConfigId(expectedConfigId);
        var actualConfigId = conjurMuleConfiguration.getConfigId();

		assertEquals(expectedConfigId,actualConfigId);
	}
}
