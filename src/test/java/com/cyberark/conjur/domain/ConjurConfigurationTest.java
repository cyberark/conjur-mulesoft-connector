package com.cyberark.conjur.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ConjurConfigurationTest {

    private ConjurConfiguration config;
    private Path changelogPath;

    @Before
    public void setUp() throws Exception {
        config = new ConjurConfiguration();

        File classpathRoot = new File(getClass().getClassLoader().getResource("").toURI());
        changelogPath = new File(classpathRoot, "CHANGELOG.md").toPath();
    }

    @After
    public void cleanup() throws IOException {
        Files.deleteIfExists(changelogPath);
    }

    @Test
    public void getIntegrationVersionReturnsVersion() throws Exception {
        var expectedVersion = "2.3";
        var content = "## [" + expectedVersion + "]";

        Files.writeString(changelogPath, content);
        var actualVersion = config.getIntegrationVersion();

        assertNotNull(actualVersion);
        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    public void getSDKVersionReturnsParsedVersion() throws Exception {
        var expectedVersion = "2.3";
        var content = "## [" + expectedVersion + "]";

        Files.writeString(changelogPath, content);

        var actualVersion = ConjurConfiguration.getSDKVersion();

        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    public void getSDKVersionReturnsUnset() throws Exception {
        var expected = "unset";
        var content = "NotMatched";

        Files.writeString(changelogPath, content);

        var actualVersion = ConjurConfiguration.getSDKVersion();

        assertEquals(expected, actualVersion);
    }

    @Test
    public void telemetryDefaultsShouldReturnCorrectTelemetryHeaders() {
        assertEquals("Mulesoft Connector", config.getIntegrationName());
        assertEquals("cybr-secretsmanager-mulesoft", config.getIntegrationType());
        assertEquals("MuleSoft", config.getVendorName());
    }
}
