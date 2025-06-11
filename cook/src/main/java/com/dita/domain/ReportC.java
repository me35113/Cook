package com.dita.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "report_c")
public class ReportC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "comment_id")
    private Integer commentId;

    private String type;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String detail;

    // --- getters / setters ---
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }

    public Integer getRecipeId() { return recipeId; }
    public void setRecipeId(Integer recipeId) { this.recipeId = recipeId; }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
