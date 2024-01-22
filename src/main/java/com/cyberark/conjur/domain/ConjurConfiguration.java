package com.cyberark.conjur.domain;


import org.mule.runtime.extension.api.annotation.param.Parameter;

/** 
 * POJO class for Conjur Configuration/Authentication parameter
 */
public class ConjurConfiguration {

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

