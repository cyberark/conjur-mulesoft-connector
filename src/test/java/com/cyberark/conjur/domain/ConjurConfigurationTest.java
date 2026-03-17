package com.cyberark.conjur.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConjurConfigurationTest {

    private ConjurConfiguration config;
    private Path changelogPath;

    @BeforeEach
    public void setUp() throws Exception {
        config = new ConjurConfiguration();

        File classpathRoot = new File(getClass().getClassLoader().getResource("").toURI());
        changelogPath = new File(classpathRoot, "CHANGELOG.md").toPath();
    }

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(changelogPath);
    }

    @Test
    @DisplayName("getIntegrationVersion should return version from changelog")
    public void getIntegrationVersionReturnsVersion() throws Exception {
        var expectedVersion = "2.3";
        var content = "## [" + expectedVersion + "]";

        Files.writeString(changelogPath, content);
        var actualVersion = config.getIntegrationVersion();

        assertNotNull(actualVersion);
        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    @DisplayName("getSDKVersion should return parsed version from changelog")
    public void getSDKVersionReturnsParsedVersion() throws Exception {
        var expectedVersion = "2.3";
        var content = "## [" + expectedVersion + "]";

        Files.writeString(changelogPath, content);

        var actualVersion = ConjurConfiguration.getSDKVersion();

        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    @DisplayName("getSDKVersion should return unset when changelog has no version")
    public void getSDKVersionReturnsUnset() throws Exception {
        var expected = "unset";
        var content = "NotMatched";

        Files.writeString(changelogPath, content);

        var actualVersion = ConjurConfiguration.getSDKVersion();

        assertEquals(expected, actualVersion);
    }

    @Test
    @DisplayName("telemetry defaults should return expected headers")
    public void telemetryDefaultsShouldReturnCorrectTelemetryHeaders() {
        assertEquals("Mulesoft Connector", config.getIntegrationName());
        assertEquals("cybr-secretsmanager-mulesoft", config.getIntegrationType());
        assertEquals("MuleSoft", config.getVendorName());
    }
}
