package com.cyberark.conjur.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class ConjurConfigurationTest {
	
	ConjurConfiguration conjurConfig = new ConjurConfiguration();
	String configId="1";

	@Test
	public void test() {
		Object expectedObj = new ConjurConfiguration();
		assertNotEquals(expectedObj, conjurConfig);
	}
	
	@Test
	public void testConfigId()
	{
		conjurConfig.setConfigId(configId);
		assertEquals(conjurConfig.getConfigId(),configId);
	}

}
