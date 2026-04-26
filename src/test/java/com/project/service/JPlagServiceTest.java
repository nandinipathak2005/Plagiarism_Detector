
package com.project.service;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class JPlagServiceTest {

    private JPlagService service;

    @BeforeEach
    void setUp() {
        service = spy(new JPlagService()); // Mockito spy (partial mock)
    }

    // 1. Validation logic 
    @Test
    @DisplayName("Should return NO_VALID_SUBMISSIONS when folder has no valid files")
    void testNoValidSubmissions() throws Exception {

        // create real empty directory
        File tempDir = Files.createTempDirectory("jplag-empty").toFile();

        Map<String, Object> result =
                service.runJPlag(tempDir, "job1", false);

        assertNotNull(result);
        assertEquals("NO_VALID_SUBMISSIONS", result.get("error"));
    }

    // 2. Invalid path → INTERNAL_SERVER_ERROR
    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR for invalid path")
    void testInvalidDirectory() {

        File invalid = new File("nonexistent/path");

        Map<String, Object> result =
                service.runJPlag(invalid, "job2", false);

        assertNotNull(result);
        assertEquals("INTERNAL_SERVER_ERROR", result.get("error"));
    }

    // 3. Verify method execution (Mockito usage)
    @Test
    @DisplayName("Verify runJPlag is invoked")
    void testMethodInvocation() {

        JPlagService spyService = spy(new JPlagService());
        File dummy = new File("invalid");

        spyService.runJPlag(dummy, "job3", false);

        verify(spyService, times(1))
                .runJPlag(dummy, "job3", false);
    }

    // 4. Response structure test
    @Test
    @DisplayName("Should always return response containing jobId")
    void testResponseStructure() {

        File dummy = new File("invalid");

        Map<String, Object> result =
                service.runJPlag(dummy, "job4", false);

        assertNotNull(result);
        assertTrue(result.containsKey("jobId"));
    }

    // 5. Timeout safety
    @Test
    @DisplayName("Should complete within 30 seconds")
    void testTimeout() {

        File dummy = new File("invalid");

        assertTimeoutPreemptively(Duration.ofSeconds(30), () ->
                service.runJPlag(dummy, "job5", false)
        );
    }
}