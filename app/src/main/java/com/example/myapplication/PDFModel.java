package com.example.myapplication;

public class PDFModel {
    private String id;
    private String title;
    private String subject;
    private String semester;
    private String fileUrl;
    private String fileName;
    private long uploadedAt;

    // Empty constructor (required for Supabase and JSON parsing)
    public PDFModel() {
        // Empty constructor
    }

    // Parameterized constructor
    public PDFModel(String id, String title, String subject, String semester,
                    String fileUrl, String fileName, long uploadedAt) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.semester = semester;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;

    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(long uploadedAt) { this.uploadedAt = uploadedAt; }

}