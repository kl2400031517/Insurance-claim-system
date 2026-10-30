package com.insurance.policy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "coverage_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal coverageAmount;

    @Column(name = "remaining_coverage", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingCoverage;

    @Column(name = "policy_status", nullable = false, length = 20)
    private String policyStatus; // e.g. ACTIVE, EXPIRED, CANCELLED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.policyStatus == null) {
            this.policyStatus = "ACTIVE";
        }
    }

    public Policy() {}

    public Policy(String userId, BigDecimal coverageAmount) {
        this.userId = userId;
        this.coverageAmount = coverageAmount;
        this.remainingCoverage = coverageAmount;
        this.policyStatus = "ACTIVE";
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public BigDecimal getRemainingCoverage() {
        return remainingCoverage;
    }

    public void setRemainingCoverage(BigDecimal remainingCoverage) {
        this.remainingCoverage = remainingCoverage;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
