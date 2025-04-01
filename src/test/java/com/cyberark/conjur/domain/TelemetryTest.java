package com.cyberark.conjur.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import com.cyberark.conjur.domain.ConjurConfiguration;

public class TelemetryTest {

    @Test
    public void testTelemetryHeaders() {
        ConjurConfiguration config = new ConjurConfiguration();

        String integrationName = config.getIntegrationName();
        String integrationType = config.getIntegrationType();
        String integrationVersion = config.getIntegrationVersion();
        String vendorName = config.getVendorName();

        assertEquals("Mulesoft Connector", integrationName);
        assertEquals("cybr-secretsmanager-mulesoft", integrationType);
        assertNotNull(integrationVersion);
        assertEquals("MuleSoft", vendorName);
    }
}
