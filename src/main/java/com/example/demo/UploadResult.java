package com.example.demo;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class UploadResult {
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Long id;

    private String fileName;
    private UploadStatus status;
    private String percentComplete;
    private int total;
    private int success;
    private int failed;
}

enum UploadStatus {
    IN_PROGRESS("In Progress"), COMPLETE("Complete"), FAILED("Failed");

    private String status;

    UploadStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}