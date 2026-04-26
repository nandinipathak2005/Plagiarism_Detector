package com.plagiarism.plagiarismchecker;

import com.plagiarism.plagiarismchecker.entity.Job;
import com.plagiarism.plagiarismchecker.entity.User;
import com.plagiarism.plagiarismchecker.repository.JobRepository;
import com.plagiarism.plagiarismchecker.repository.UserRepository;
import com.plagiarism.plagiarismchecker.service.StorageService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FullIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private StorageService storageService;

    @Test
    void testFullFlow_User_Job_Storage() throws Exception {

        // ✅ 1. Create User
        User user = new User();
        user.setName("Sneha");
        user.setEmail("sneha" + System.currentTimeMillis() + "@test.com");
        user.setPassword("1234");
        user.setRole("STUDENT");

        userRepository.save(user);

        // ✅ 2. Create Job
        Job job = new Job();
        job.setJobName("Assignment 1");
        job.setDescription("Plagiarism Check");
        job.setUser(user);  // ✅ correct
        Job savedJob = jobRepository.save(job);

        // ✅ 3. Mock ZIP file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.zip",
                "application/zip",
                "dummy data".getBytes()
        );

        // ✅ 4. Call STORAGE SERVICE (IMPORTANT)
        storageService.storeZip(savedJob.getId(), "Sneha", file);

        // ✅ If no exception → success
        assertTrue(true);
    }
}