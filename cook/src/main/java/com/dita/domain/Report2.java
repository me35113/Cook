package com.dita.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "report")
public class Report2 {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "report_id")       // ← 여기서 실제 컬럼명 지정
	    private Long reportId;

	    @Column(name = "recipe_id")       // ← 마찬가지로 recipe_id 매핑
	    private Long recipeId;

	    private String type;
	    private String title;

	    @Column(columnDefinition = "TEXT")
	    private String detail;

    // getters / setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
