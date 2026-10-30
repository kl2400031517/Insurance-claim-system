package com.insurance.claim.dto;

import java.math.BigDecimal;

public class ClaimRequest {
    private Long policyId;
    private BigDecimal claimAmount;
    private Boolean simulatePaymentFailure; // optional testing parameter

    public ClaimRequest() {}

    public ClaimRequest(Long policyId, BigDecimal claimAmount) {
        this.policyId = policyId;
        this.claimAmount = claimAmount;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public Boolean getSimulatePaymentFailure() {
        return simulatePaymentFailure != null && simulatePaymentFailure;
    }

    public void setSimulatePaymentFailure(Boolean simulatePaymentFailure) {
        this.simulatePaymentFailure = simulatePaymentFailure;
    }
}
