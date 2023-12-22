package com.goldenKids.dotami;

import com.google.gson.annotations.SerializedName;

public class ReportResponse {
    @SerializedName("ReportID")
    private String reportId;

    @SerializedName("ReporterID")
    private int reporterId;

    @SerializedName("DateReported")
    private String dateReported;

    @SerializedName("Location")
    private String location;

    @SerializedName("ImageUrl")
    private String imageUrl;

    @SerializedName("Status")
    private String status;

    // 게터 및 세터 메서드

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString 메서드 (선택적)

    @Override
    public String toString() {
        return "ReportResponse{" +
                "reportId='" + reportId + '\'' +
                ", reporterId=" + reporterId +
                ", dateReported='" + dateReported + '\'' +
                ", location='" + location + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
