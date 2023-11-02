package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConjurMuleConfigurationTest {
	
	ConjurMuleConfiguration config = new ConjurMuleConfiguration();
	String configId="1";
	

	@Test
	public void testConfigId() {
		config.setConfigId(configId);
		assertEquals(config.getConfigId(),configId);
	}

}
