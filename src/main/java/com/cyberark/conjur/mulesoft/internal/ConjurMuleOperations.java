package com.cyberark.conjur.mulesoft.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.error.ConjurErrorProvider;

/**
 * This class is a container for operations, every public method in this class
 * will be taken as an extension operation.
 */
@MediaType(value = ANY, strict = false)
@Throws({ ConjurErrorProvider.class })
public class ConjurMuleOperations {
	private final Logger LOGGER = LoggerFactory.getLogger(ConjurMuleOperations.class);

	@MediaType(value = ANY, strict = false)
	public String retrieveSecret(@Config ConjurMuleConfiguration configuration,
			@Connection ConjurMuleConnection connection) {
		LOGGER.debug("Inside Retrieve Secrets call");
		Object secretVal = null;
		try {
			secretVal = connection.getValue();

		} catch (Exception ex) {
			return connection.getErrorMsg();

		}
		return secretVal.toString();
	}

}
