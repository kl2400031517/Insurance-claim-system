package com.insurance.policy.dto;

import java.math.BigDecimal;

public class PolicyCreateRequest {
    private String userId;
    private BigDecimal coverageAmount;

    public PolicyCreateRequest() {}

    public PolicyCreateRequest(String userId, BigDecimal coverageAmount) {
        this.userId = userId;
        this.coverageAmount = coverageAmount;
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
}
