package com.example.mathhub;
public class Report {
    private String postId;
    private String reporterId;
    private String reportReason;

    public Report() {
    }

    public Report(String postId, String reporterId, String reportReason) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reportReason = reportReason;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }
}
