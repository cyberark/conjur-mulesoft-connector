package com.cyberark.conjur.mulesoft.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConjurMuleConnectionTest {

	String id ="1";
	Object value= "testData";
	int errorCode=200;
	String errorMsg="OK";
	ConjurMuleConnection connection = new ConjurMuleConnection(id,value,errorCode,errorMsg);

	@Test
	public void testId() {
		
		assertEquals(connection.getId(),id);
		
	}
	@Test
	public void testValue() {
		
		assertEquals(connection.getValue(),value);
		
	}
	@Test
	public void testErrorCode() {
		
		assertEquals(connection.getErrorCode(),errorCode);
		
	}
	@Test
	public void testErrorMsg() {
		
		assertEquals(connection.getErrorMsg(),errorMsg);
		
	}
	@Test
	public void testInVaidate() {
		
		
		
	}

}
