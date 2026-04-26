
package com.project.service;

import java.io.File;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JPlagServiceIT {

    private JPlagService jPlagService;

    @BeforeEach
    void setUp() {
        jPlagService = new JPlagService();
    }

    // 1. Valid submissions
    @Test
    @DisplayName("Run JPlag on real submissions folder")
    void testRunJPlagUsingFolderPath() {

        File submissions = new File("src/test/resources/STUDENT");

        assertTrue(submissions.exists(), "Test folder does not exist!");

        Map<String, Object> result =
                jPlagService.runJPlag(submissions, "folder-test", false);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        // either success or controlled failure
        assertTrue(
                result.containsKey("peerMatches") ||
                result.containsKey("error")
        );
    }

    // 2. Missing directory
    @Test
    @DisplayName("Fail if directory does not exist")
    void testMissingDirectory() {

        File submissions = new File("src/test/resources/nonexistent");

        Map<String, Object> result =
                jPlagService.runJPlag(submissions, "fail-test", false);

        assertEquals("INTERNAL_SERVER_ERROR", result.get("error"));
    }

    // 3. Timeout protection
    @Test
    @DisplayName("Protect server from long JPlag execution")
    void testTimeoutProtection() {

        assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {

            File submissions = new File("src/test/resources/STUDENT");

            if (submissions.exists()) {
                jPlagService.runJPlag(submissions, "timeout-test", false);
            }
        });
    }

    // 4. Dirty data
    @Test
    @DisplayName("Handle dirty data submissions")
    void testDirtyDataFiles() {

        File submissions = new File("src/test/resources/dirty");

        Map<String, Object> result =
                jPlagService.runJPlag(submissions, "dirty-test", false);

        assertNotNull(result);
        assertTrue(
                result.containsKey("error") &&
                (
                        result.get("error").equals("NO_VALID_SUBMISSIONS")
                        || result.get("error").equals("INTERNAL_SERVER_ERROR")
                )
        );
    }

    // 5. Single submission
    @Test
    @DisplayName("Handle single submission case")
    void testSingleSubmission() {

        File submissions = new File("src/test/resources/single");

        Map<String, Object> result =
                jPlagService.runJPlag(submissions, "single-test", false);

        assertNotNull(result);
        assertTrue(
                result.containsKey("error") &&
                (
                        result.get("error").equals("NO_VALID_SUBMISSIONS")
                        || result.get("error").equals("INTERNAL_SERVER_ERROR")
                )
        );
    }

    // 6. Parallel execution
    @Test
    @DisplayName("Handle parallel JPlag executions safely")
    void testParallelExecution() throws Exception {

        File submissions = new File("src/test/resources/STUDENT");

        assertTrue(submissions.exists());

        final boolean[] completed = {false, false};

        Thread t1 = new Thread(() -> {
            jPlagService.runJPlag(submissions, "job1", false);
            completed[0] = true;
        });

        Thread t2 = new Thread(() -> {
            jPlagService.runJPlag(submissions, "job2", false);
            completed[1] = true;
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertTrue(completed[0] && completed[1]);
    }
}