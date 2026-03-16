package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConjurMuleConfigurationTest {

    private ConjurMuleConfiguration conjurMuleConfiguration;

    @Before
    public void setUp() throws Exception {
        conjurMuleConfiguration = new ConjurMuleConfiguration();
    }

	@Test
	public void setConfigIdShouldStoreValue() {
        var expectedConfigId = "1";

        conjurMuleConfiguration.setConfigId(expectedConfigId);
        var actualConfigId = conjurMuleConfiguration.getConfigId();

		assertEquals(expectedConfigId,actualConfigId);
	}
}
