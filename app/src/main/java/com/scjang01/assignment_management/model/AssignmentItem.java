package com.scjang01.assignment_management.model;

import com.google.gson.annotations.SerializedName;

public class AssignmentItem {
    @SerializedName("subject")
    private String subject;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("deadline")
    private String deadline;
    
    @SerializedName("isSubmitted")
    private boolean isSubmitted;
    
    @SerializedName("link")
    private String link;

    public AssignmentItem(String subject, String title, String deadline, boolean isSubmitted, String link) {
        this.subject = subject;
        this.title = title;
        this.deadline = deadline;
        this.isSubmitted = isSubmitted;
        this.link = link;
    }

    // Getters
    public String getSubject() { return subject; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public boolean isSubmitted() { return isSubmitted; }
    public String getLink() { return link; }

    // Setters
    public void setSubject(String subject) { this.subject = subject; }
    public void setTitle(String title) { this.title = title; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setSubmitted(boolean submitted) { isSubmitted = submitted; }
    public void setLink(String link) { this.link = link; }
}