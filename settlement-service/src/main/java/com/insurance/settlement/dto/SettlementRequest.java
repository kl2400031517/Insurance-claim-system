package com.insurance.settlement.dto;

import java.math.BigDecimal;

public class SettlementRequest {
    private Long claimId;
    private Long policyId;
    private BigDecimal approvedAmount;
    private Boolean simulateFailure; // optional flag to simulate payment failure

    public SettlementRequest() {}

    public SettlementRequest(Long claimId, Long policyId, BigDecimal approvedAmount) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.approvedAmount = approvedAmount;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Boolean getSimulateFailure() {
        return simulateFailure != null && simulateFailure;
    }

    public void setSimulateFailure(Boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
