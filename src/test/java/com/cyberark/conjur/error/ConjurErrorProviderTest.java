package com.cyberark.conjur.error;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ConjurErrorProviderTest {
	
	ConjurErrorProvider provider = new ConjurErrorProvider();

	@Test
	public void test() {
		Object expectedObj = new ConjurErrorProvider();
		assertNotEquals(expectedObj, provider);
	}
	
	@Test
	public void testErrorTypes()
	{
		Set<ConjurErrorTypes> errors = new HashSet<ConjurErrorTypes>();
		errors.add(ConjurErrorTypes.INVALID_DATA);
		assertFalse(errors.contains(provider.getErrorTypes()));
		
	}

}
