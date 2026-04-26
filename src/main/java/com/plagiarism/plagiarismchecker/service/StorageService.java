package com.plagiarism.plagiarismchecker.service;
import com.plagiarism.plagiarismchecker.entity.Job;
import com.plagiarism.plagiarismchecker.entity.Submission;
import com.plagiarism.plagiarismchecker.repository.JobRepository;
import com.plagiarism.plagiarismchecker.repository.SubmissionRepository;
import com.plagiarism.plagiarismchecker.utils.ZipUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class StorageService {

    private final Path root = Paths.get("jobs");

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private JobRepository jobRepository;

    // ✅ Create required folders
    public void createJobFolders(Long jobId) throws IOException {
        Path jobPath = root.resolve(String.valueOf(jobId));

        Files.createDirectories(jobPath.resolve("submissions"));
        Files.createDirectories(jobPath.resolve("results"));
    }

    // ✅ Main method (upload + extract + save DB)
    public void storeZip(Long jobId, String studentName, MultipartFile file) throws IOException {

        // 🔴 Validate ZIP
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".zip")) {
            throw new IllegalArgumentException("Only ZIP files allowed");
        }

        // 🔴 Fetch Job (IMPORTANT)
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        createJobFolders(jobId);

        Path jobPath = root.resolve(String.valueOf(jobId));
        Path studentPath = jobPath.resolve("submissions").resolve(studentName);

        Files.createDirectories(studentPath);

        // Save ZIP
        Path zipPath = studentPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), zipPath, StandardCopyOption.REPLACE_EXISTING);

        // Extract ZIP
        ZipUtils.unzip(zipPath, studentPath);

        // Delete ZIP
        Files.deleteIfExists(zipPath);

        // ✅ Save submission in DB (FIXED)
        Submission submission = new Submission();
        submission.setJob(job);
        submission.setStudentName(studentName);
        submission.setFilePath(studentPath.toString());
        submission.setFileName(file.getOriginalFilename());
        submission.setSubmittedAt(LocalDateTime.now());

        submissionRepository.save(submission);
    }

    public Path getSubmissionPath(Long jobId) {
        return root.resolve(String.valueOf(jobId)).resolve("submissions");
    }

    public Path getResultPath(Long jobId) {
        return root.resolve(String.valueOf(jobId)).resolve("results");
    }
}