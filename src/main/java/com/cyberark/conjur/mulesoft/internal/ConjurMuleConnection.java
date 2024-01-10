package com.cyberark.conjur.mulesoft.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extension connection to the Conjur Server to
 * authenticate and retrieve secrets.
 */
public final class ConjurMuleConnection {

	private final Logger LOGGER = LoggerFactory.getLogger(ConjurMuleConnection.class);

	private final String id;

	private final Object value;

	private int errorCode;
	private String errorMsg;

	/**
	 * Constructor for Conjur Mule Connection, invoked from
	 * ConjurMuleConnectionProvider
	 * 
	 * @param id        unique Config ID
	 * @param value     value to be returned to the calling flow module
	 * @param errorCode errorCode to be thrown
	 * @param errorMsg  error Msg to be thrown
	 */
	public ConjurMuleConnection(String id, Object value, int errorCode, String errorMsg) {
		this.id = id;
		this.value = value;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * Get the unique config ID to pass to the calling flow
	 * 
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Value/Secret to be returned to the calling flow
	 * 
	 * @return String secret
	 */

	public Object getValue() {
		return value;
	}

	/**
	 * ErrorCode to be returned to the calling flow
	 * 
	 * @return int errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Error message to be returned to the calling flow
	 * 
	 * @return String errorCode
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * Invalidate connection
	 */
	public void invalidate() {

	}

}
