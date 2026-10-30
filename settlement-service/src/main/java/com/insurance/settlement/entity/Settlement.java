package com.insurance.settlement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long settlementId;

    @Column(name = "claim_id", nullable = false, unique = true)
    private Long claimId;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "approved_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus; // SUCCESS, FAILED, PROCESSING

    @Column(name = "processed_at", updatable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.processedAt = LocalDateTime.now();
    }

    public Settlement() {}

    public Settlement(Long claimId, Long policyId, BigDecimal approvedAmount, String paymentStatus) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.approvedAmount = approvedAmount;
        this.paymentStatus = paymentStatus;
    }

    public Long getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
