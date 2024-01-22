package com.cyberark.conjur.mulesoft.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class will hold the conjur authentication parameters as global configuration.
 *
 */
@Operations(ConjurMuleOperations.class)
@ConnectionProviders(ConjurMuleConnectionProvider.class)
public class ConjurMuleConfiguration {
	private final Logger LOGGER = LoggerFactory.getLogger(ConjurMuleConfiguration.class);
	private String configId;
	
	/**
	 * Set the unique ConfigId/TrasactionId per request
	 * @param configId
	 */
	public void setConfigId(String configId) {
		this.configId = configId;
	}


	/**
	 * Get the unique ConfigId/TrasactionId per request
	 * @return String configId
	 */
	public String getConfigId() {
		return configId;
	}
}
