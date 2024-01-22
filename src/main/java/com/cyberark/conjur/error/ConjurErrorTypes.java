package com.cyberark.conjur.error;

import java.util.Optional;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

/**
 * Defines the error type for the Conjur authentication and secret retrieval
 * 
 *
 */
public enum ConjurErrorTypes implements ErrorTypeDefinition<ConjurErrorTypes> {
	
	INVALID_DATA(MuleErrors.VALIDATION);
	private ErrorTypeDefinition<? extends Enum<?>> parent;
	ConjurErrorTypes(ErrorTypeDefinition<? extends Enum<?>> parent) {
	    this.parent = parent;
	  }
	  @Override
	  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
	    return Optional.ofNullable(parent);
	  }
}
