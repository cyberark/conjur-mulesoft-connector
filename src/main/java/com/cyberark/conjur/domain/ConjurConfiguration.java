package com.cyberark.conjur.domain;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * POJO class for Conjur Configuration/Authentication parameter
 */
public class ConjurConfiguration {
	
		private static final Logger LOGGER = LoggerFactory.getLogger(ConjurConfiguration.class);

	
		// telemetry headers
		private String integrationName = System.getenv().getOrDefault("INTEGRATION_NAME", "Mulesoft Connector");
		private String integrationType = System.getenv().getOrDefault("INTEGRATION_TYPE", "cybr-secretsmanager-mulesoft");
	    private String integrationVersion = getSDKVersion();  // Fetch from VERSION file
	    private String vendorName = System.getenv().getOrDefault("VENDOR_NAME", "MuleSoft");

		/**
		 * Returns the integration name used for telemetry.
		 * 
		 * @return the integration name.
		 */
		public String getIntegrationName() {
			return integrationName;
		}

		/**
		 * Returns the integration type used for telemetry.
		 * 
		 * @return the integration type.
		 */
		public String getIntegrationType() {
			return integrationType;
		}
		
		/**
		 * Returns the integration version used for telemetry.
		 * 
		 * @return the integration version.
		 */
		public String getIntegrationVersion() {
			return integrationVersion;
		}

		/**
		 * Returns the vendor name used for telemetry.
		 * 
		 * @return the vendor name.
		 */
		public String getVendorName() {
			return vendorName;
		}

	 // Method to get the SDK version from the VERSION file
	    public static String getSDKVersion() {
	        try {
	            Path rootDir = Paths.get("");
	            Path versionFile = rootDir.resolve("VERSION");

	            if (Files.exists(versionFile)) {
	                String version = new String(Files.readAllBytes(versionFile)).trim();
	                if (version.isEmpty()) {
	                    LOGGER.info("VERSION file is empty");
	                    return "unknown";
	                }

	                // Remove the build number and snapshot
	                String[] parts = version.split("-");
	                String versionWithoutSnapshot = parts[0];
	                LOGGER.info("SDK Version: " + versionWithoutSnapshot);

	                return versionWithoutSnapshot;
	            }
	            return "unknown";

	        } catch (IOException e) {
	            LOGGER.error("Error reading VERSION file: " + e.getMessage(), e);
	            return "unknown";  // Default version in case of error
	        }
	    }

	@Parameter
	private String configId;

	public String getConfigId() {
		return configId;
	}
	
	/** 
	 * Conjur Account
	 */
	@Parameter
	private String conjurAccount;
	/**
	 * Conjur ApplianceURL/basepath
	 */
	@Parameter
	private String conjurApplianceUrl;
	/**
	 * Conjur Authn Login /UserName
	 */
	@Parameter
	private String conjurAuthnLogin;
	/**
	 * Conjur API Key
	 */
	@Parameter
	private String conjurApiKey;
	/**
	 * Conjur SSL Certificate
	 */
	@Parameter
	private String conjurSslCertificate;
	/**
	 * Conjur CertFile
	 */
	@Parameter
	private String conjurCertFile;

	/**
	 * Getter method to return Conjur Account
	 * @return conjurAccount
	 */
	public String getConjurAccount() {
		return conjurAccount;
	}

	/**
	 * Getter method to return Conjur Appliance Url
	 * @return conjurApplianceUrl
	 */
	public String getConjurApplianceUrl() {
		return conjurApplianceUrl;
	}

	/**
	 * Getter method to return Conjur Authentication User
	 * @return conjurAuthnLogin
	 */
	public String getConjurAuthnLogin() {
		return conjurAuthnLogin;
	}

	/**
	 * Getter method to return Conjur Api Key
	 * @return conjurApiKey
	 */
	public String getConjurApiKey() {
		return conjurApiKey;
	}
	/**
	 * Getter method to return Conjur SSL Certificate
	 * @return conjurSslCertificate
	 */

	public String getConjurSslCertificate() {
		return conjurSslCertificate;
	}

	/**
	 * Getter method to return Conjur Cert File
	 * @return conjurCertFile
	 */
	public String getConjurCertFile() {
		return conjurCertFile;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	/**
	 * Setter method to return Conjur Account
	 * @param conjurAccount
	 */
	public void setConjurAccount(String conjurAccount) {
		this.conjurAccount = conjurAccount;
	}
	/**
	 * Setter method to return Conjur Appliance URL
	 * @param conjurApplianceUrl
	 */
	public void setConjurApplianceUrl(String conjurApplianceUrl) {
		this.conjurApplianceUrl = conjurApplianceUrl;
	}
	/**
	 * Setter method to return Conjur Authentication User
	 * @param conjurAuthnLogin
	 */
	public void setConjurAuthnLogin(String conjurAuthnLogin) {
		this.conjurAuthnLogin = conjurAuthnLogin;
	}
	/**
	 * Setter method to return Conjur API Key
	 * @param conjurApiKey
	 */
	public void setConjurApiKey(String conjurApiKey) {
		this.conjurApiKey = conjurApiKey;
	}

	/**
	 * Setter method to return Conjur SSL Certificate
	 * @param conjurSslCertificate
	 */
	public void setConjurSslCertificate(String conjurSslCertificate) {
		this.conjurSslCertificate = conjurSslCertificate;
	}
	
	/**
	 * Setter method to return Conjur Cert File
	 * @param conjurCertFile
	 */
	public void setConjurCertFile(String conjurCertFile) {
		this.conjurCertFile = conjurCertFile;
	}
}

