package com.insurance.policy.dto;

import java.math.BigDecimal;

public class PolicyValidationResponse {
    private boolean valid;
    private Long policyId;
    private String status;
    private BigDecimal remainingCoverage;
    private String reason;

    public PolicyValidationResponse() {}

    public PolicyValidationResponse(boolean valid, Long policyId, String status, BigDecimal remainingCoverage, String reason) {
        this.valid = valid;
        this.policyId = policyId;
        this.status = status;
        this.remainingCoverage = remainingCoverage;
        this.reason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRemainingCoverage() {
        return remainingCoverage;
    }

    public void setRemainingCoverage(BigDecimal remainingCoverage) {
        this.remainingCoverage = remainingCoverage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
