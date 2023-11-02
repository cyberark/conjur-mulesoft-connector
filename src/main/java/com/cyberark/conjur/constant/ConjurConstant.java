package com.cyberark.conjur.constant;

/**
 * All error and constants for the Conjur Constant defined
 * 
 * @author Jaleela.FaizurRahman
 *
 */
public class ConjurConstant {

	/**
	 * Generic extension for properties define at conjur.properties.
	 */
	public static final String CONJUR_MAPPING = "conjur.mapping.";

	/**
	 * Conjur_Kind.
	 */
	public static final String CONJUR_KIND = "variable";

	/**
	 * Custom property file name.
	 */
	public static final String CONJUR_PROPERTIES = "/conjur.properties";

	/**
	 * Environment variable.
	 */
	public static final String CONJUR_ACCOUNT = System.getenv("CONJUR_ACCOUNT");

	/**
	 * Error Message.
	 */
	public static final String CONJUR_APIKEY_ERROR = "Please provide Conjur Authn Token file or else api Key in environment Variable";

	/**
	 * Not Found message.
	 */
	public static final String NOT_FOUND = "NotFound";
	/**
	 * Conjur connection is successful
	 */
	public static final String CODE_200_CONNECTION = "Connection with Conjur is successful";

	/**
	 * Secret value successfully retrieved
	 */
	public static final String CODE_200_DATA_FOUND = "The secret values was retrieved successfully.";
	/**
	 * Invalid authentication credentials
	 */
	public static final String CODE_401 = "The request lacks valid authentication credentials.";
	/**
	 * Invalid privilege
	 */
	public static final String CODE_403 = "The authenticated user lacks the necessary privilege.";
	/**
	 * Variable or data not found for single retrieval
	 */
	public static final String CODE_404_SINGLE_RETRIEVAL = "The variable does not exist, or it does not have any secret values.";
	/**
	 * Variable does not exist or variable does not have data for Batch retrieval
	 */
	public static final String CODE_404_BATCH_RETRIEVAL = "At least one variable does not exist, or at least one variable does not have any secret values.";
	/**
	 * Missing or invalid parameter
	 */
	public static final String CODE_422 = "A request parameter was missing or invalid.";
	
	/**InValid Account*/
	public static final String INVALID_ACCOUNT="Missing or invalid ConjurAccount";
	
	/**Invalid BaseUrl*/
	public static final String INVALID_BASEURL="Missing or invalid Conjur BaseUrl/ApplianceUrl";
	
	/**Invalid AuthenticationLogin */
	public static final String INVALID_USERNAME="Missing or invalid Conjur AuthnLogin";
	
	/**Invalid BaseUrl*/
	public static final String INVALID_SSLCERT="Missing or invalid Conjur SSLCertificate";
	
	/**Invalid APIKey*/
	public static final String INVALID_APIKEY="Missing or invalid API KEY";
	

}
