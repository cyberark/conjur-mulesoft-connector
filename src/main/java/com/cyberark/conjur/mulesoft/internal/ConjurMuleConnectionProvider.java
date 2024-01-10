package com.cyberark.conjur.mulesoft.internal;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.stereotype.Validator;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.core.ConjurConnection;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.error.ConjurErrorProvider;
import com.cyberark.conjur.error.ConjurErrorTypes;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import com.cyberark.conjur.service.ConjurService;
import com.cyberark.conjur.service.ConjurServiceImpl;

/**
 * This class (as it's name implies) provides connection instances and the
 * funcionality to disconnect and validate those connections.
 * <p>
 * All connection related parameters (values required in order to create a
 * connection) must be declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares
 * that connections resolved by this provider will be pooled and reused. There
 * are other implementations like {@link CachedConnectionProvider} which lazily
 * creates and caches connections or simply {@link ConnectionProvider} if you
 * want a new connection each time something requires one.
 */

public class ConjurMuleConnectionProvider implements ConnectionProvider<ConjurMuleConnection> {

	private final Logger LOGGER = LoggerFactory.getLogger(ConjurMuleConnectionProvider.class);

	/**
	 * A parameter that is always required to be configured.
	 */
	@Parameter
	private String conjurAccount;
	@Parameter
	private String conjurApplianceUrl;
	@Parameter
	private String conjurAuthnLogin;
	@Parameter
	@Optional
	private String conjurApiKey;
	@Parameter
	@Optional
	private String conjurSslCertificate;
	@Parameter
	@Optional
	private String conjurCertFile;
	@Parameter
	private String key;

	private SecretsApi secretsApi = new SecretsApi();
	ConjurService conjurService = new ConjurServiceImpl();

	Object secretValue;
	int errorCode;
	String errorMsg;

	String uniqueID = UUID.randomUUID().toString();
	ConjurConfiguration config = new ConjurConfiguration();


	@Override
	public ConjurMuleConnection connect() throws ConnectionException {

		LOGGER.info("Calling Demo Connection Provider connect()");

		
		try {
			boolean errorFlag = isValidParameters(conjurAccount, conjurApplianceUrl, conjurAuthnLogin, conjurApiKey,
					conjurSslCertificate);

			if (!errorFlag) {
				config.setConjurAccount(conjurAccount);
				config.setConjurApplianceUrl(conjurApplianceUrl);
				config.setConjurAuthnLogin(conjurAuthnLogin);
				config.setConjurApiKey(conjurApiKey);
				config.setConjurSslCertificate(conjurSslCertificate);
				config.setConjurCertFile(conjurCertFile);
			}

			ConjurConnection.getConnection(config);

			String account = ConjurConnection.getAccount(secretsApi);
			String[] keys = key.split(",");
			if (keys.length > 1) {
				
				secretValue = conjurService.getBatchSecrets(key, account);

			} else {
				secretValue = conjurService.getSecret(account, ConjurConstant.CONJUR_KIND, key);
			}
			
		} catch (Exception e) {
			throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new ConnectionException(e.getCause()));
		} 

		return new ConjurMuleConnection(uniqueID, secretValue,errorCode,errorMsg);

	}

	@Override
	public void disconnect(ConjurMuleConnection connection) {
		connection.invalidate();
	}

	@Override
	public ConnectionValidationResult validate(ConjurMuleConnection connection) {
		return ConnectionValidationResult.success();
	}

	@Validator
	@Throws(ConjurErrorProvider.class)
	public boolean isValidParameters(String conjurAccount, String conjurApplianceUrl, String conjurAuthnLogin,
			String conjurApiKey, String conjurSslCertificate) throws Exception {
		boolean errorFlag = false;
		if (StringUtils.isEmpty(conjurAccount)) {
			errorFlag = true;
			throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new Exception(ConjurConstant.INVALID_ACCOUNT));
		} else if (StringUtils.isEmpty(conjurApplianceUrl)) {
			errorFlag = true;
			throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new Exception(ConjurConstant.INVALID_BASEURL));
		} else if (StringUtils.isEmpty(conjurAuthnLogin)) {
			errorFlag = true;
			throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new Exception(ConjurConstant.INVALID_USERNAME));
		} else if (StringUtils.isEmpty(conjurApiKey)) {
			errorFlag = true;
			throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new Exception(ConjurConstant.INVALID_APIKEY));
		} else {
			if (StringUtils.isEmpty(conjurSslCertificate)) {
				errorFlag = true;
				throw new ModuleException(ConjurErrorTypes.INVALID_DATA, new Exception(ConjurConstant.INVALID_SSLCERT));
			}
		}
		return errorFlag;
	}
}
