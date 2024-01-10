package com.cyberark.conjur.service;

import com.cyberark.conjur.sdk.ApiException;

/**
 * 
 * Retrieves secrets either as Single or Batch request
 *
 */
public interface ConjurService {

	Object getSecret(String account, String variable_const, String variableId) throws ApiException;

	Object getBatchSecrets(String variableIds, String account) throws ApiException;

}
