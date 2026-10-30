package com.insurance.settlement.service;

import com.insurance.settlement.client.PolicyClient;
import com.insurance.settlement.dto.DeductCoverageRequest;
import com.insurance.settlement.dto.SettlementRequest;
import com.insurance.settlement.entity.Settlement;
import com.insurance.settlement.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SettlementService {

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private PolicyClient policyClient;

    @Transactional
    public Settlement processSettlement(SettlementRequest request) {
        Optional<Settlement> existing = settlementRepository.findByClaimId(request.getClaimId());
        if (existing.isPresent()) {
            return existing.get();
        }

        BigDecimal amount = request.getApprovedAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Approved settlement amount must be positive");
        }

        // Simulate payment gateway payout
        boolean isPaymentSuccessful = !request.getSimulateFailure();
        String paymentStatus = isPaymentSuccessful ? "SUCCESS" : "FAILED";

        Settlement settlement = new Settlement(
                request.getClaimId(),
                request.getPolicyId(),
                amount,
                paymentStatus
        );

        Settlement savedSettlement = settlementRepository.save(settlement);

        // Deduct coverage ONLY upon successful payment payout
        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            try {
                policyClient.deductCoverage(request.getPolicyId(), new DeductCoverageRequest(amount));
            } catch (Exception e) {
                // If notification to Policy Service fails, update payment status to reflect issue
                savedSettlement.setPaymentStatus("FAILED");
                settlementRepository.save(savedSettlement);
                throw new IllegalStateException("Payment succeeded but coverage deduction failed: " + e.getMessage());
            }
        }

        return savedSettlement;
    }

    public Settlement getSettlementById(Long settlementId) {
        return settlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found with ID: " + settlementId));
    }

    public Settlement getSettlementByClaimId(Long claimId) {
        return settlementRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found for claim ID: " + claimId));
    }
}
