package com.cyberark.conjur.constant;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class ConjurConstantTest {
	
	ConjurConstant constant = new ConjurConstant();

	@Test
	public void testConstant() {
		Object expectedObj = new ConjurConstant();
		assertNotEquals(expectedObj, constant);
	}

}
