package com.insurance.claim.service;

import com.insurance.claim.client.PolicyClient;
import com.insurance.claim.client.SettlementClient;
import com.insurance.claim.dto.*;
import com.insurance.claim.entity.Claim;
import com.insurance.claim.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyClient policyClient;

    @Autowired
    private SettlementClient settlementClient;

    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request) {
        if (request.getPolicyId() == null) {
            throw new IllegalArgumentException("Policy ID is required");
        }
        if (request.getClaimAmount() == null || request.getClaimAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Claim amount must be positive");
        }

        // 1. Call Policy Service to validate policy and remaining coverage
        PolicyValidationResponse validation;
        try {
            validation = policyClient.validatePolicyForClaim(request.getPolicyId(), request.getClaimAmount());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to communicate with Policy Service: " + e.getMessage());
        }

        // 2. Decisioning based on Policy Service validation
        if (validation == null || !validation.isValid()) {
            String reason = (validation != null && validation.getReason() != null)
                    ? validation.getReason()
                    : "Policy validation failed";

            Claim rejectedClaim = new Claim(request.getPolicyId(), request.getClaimAmount(), "REJECTED", reason);
            Claim savedRejected = claimRepository.save(rejectedClaim);
            return new ClaimResponse(savedRejected, null);
        }

        // 3. Save Approved Claim
        Claim approvedClaim = new Claim(request.getPolicyId(), request.getClaimAmount(), "APPROVED", "Claim validated and approved");
        Claim savedApproved = claimRepository.save(approvedClaim);

        // 4. Trigger Settlement Service
        SettlementResponse settlementResponse = null;
        try {
            SettlementRequest settlementReq = new SettlementRequest(
                    savedApproved.getClaimId(),
                    savedApproved.getPolicyId(),
                    savedApproved.getClaimAmount(),
                    request.getSimulatePaymentFailure()
            );
            settlementResponse = settlementClient.processSettlement(settlementReq);
        } catch (Exception e) {
            savedApproved.setRemarks("Claim APPROVED, but settlement execution encountered an issue: " + e.getMessage());
            claimRepository.save(savedApproved);
        }

        return new ClaimResponse(savedApproved, settlementResponse);
    }

    public Claim getClaimById(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + claimId));
    }

    public List<Claim> getClaimsByPolicyId(Long policyId) {
        return claimRepository.findByPolicyId(policyId);
    }
}
