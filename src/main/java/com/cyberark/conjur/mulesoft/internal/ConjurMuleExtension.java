package com.cyberark.conjur.mulesoft.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;

import com.cyberark.conjur.error.ConjurErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;



/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "conjurvaultconnector")
@Extension(name = "ConjurVaultConnector")
@Configurations(ConjurMuleConfiguration.class)
@ErrorTypes(ConjurErrorTypes.class)
@JavaVersionSupport({JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17})
public class ConjurMuleExtension {

}
