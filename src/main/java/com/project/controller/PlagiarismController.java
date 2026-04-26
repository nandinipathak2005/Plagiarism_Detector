
package com.project.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.model.PlagiarismResponse;
import com.project.service.FileService;
import com.project.service.JPlagService;

@RestController
@RequestMapping("/api/plagiarism")
@CrossOrigin("*")
public class PlagiarismController {

    private final FileService fileService;
    private final JPlagService jPlagService;

    public PlagiarismController(FileService fileService, JPlagService jPlagService) {
        this.fileService = fileService;
        this.jPlagService = jPlagService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PlagiarismResponse> upload(@RequestParam("file") MultipartFile file) {

        String jobId = "job_" + System.currentTimeMillis();

        try {
            File folder = fileService.extractZip(file, jobId);

            Map<String, Object> result = jPlagService.runJPlag(folder, jobId, false);

            return ResponseEntity.ok(new PlagiarismResponse(
                    jobId,
                    (Map<String, Double>) result.get("peerMatches"),
                    (List<String>) result.get("detailedLogs"),
                    "Success"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PlagiarismResponse(jobId, null, List.of(e.getMessage()), "Error")
            );
        }
    }

    @GetMapping("/view-report")
    public ResponseEntity<String> viewReport(@RequestParam String jobId) {
        try {
            jPlagService.openReportViewer(jobId);
            return ResponseEntity.ok("Viewer started");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}