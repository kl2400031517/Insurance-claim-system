package com.insurance.policy.service;

import com.insurance.policy.dto.DeductCoverageRequest;
import com.insurance.policy.dto.PolicyCreateRequest;
import com.insurance.policy.dto.PolicyValidationResponse;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Transactional
    public Policy createPolicy(PolicyCreateRequest request) {
        if (request.getCoverageAmount() == null || request.getCoverageAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Coverage amount must be greater than zero");
        }
        Policy policy = new Policy(request.getUserId(), request.getCoverageAmount());
        return policyRepository.save(policy);
    }

    public Policy getPolicy(Long policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found with ID: " + policyId));
    }

    public List<Policy> getPoliciesByUserId(String userId) {
        return policyRepository.findByUserId(userId);
    }

    public PolicyValidationResponse validatePolicyForClaim(Long policyId, BigDecimal claimAmount) {
        Policy policy = policyRepository.findById(policyId).orElse(null);
        if (policy == null) {
            return new PolicyValidationResponse(false, policyId, "NOT_FOUND", BigDecimal.ZERO, "Policy does not exist");
        }

        if (!"ACTIVE".equalsIgnoreCase(policy.getPolicyStatus())) {
            return new PolicyValidationResponse(false, policyId, policy.getPolicyStatus(), policy.getRemainingCoverage(), "Policy is not ACTIVE");
        }

        if (claimAmount == null || claimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PolicyValidationResponse(false, policyId, policy.getPolicyStatus(), policy.getRemainingCoverage(), "Claim amount must be positive");
        }

        if (claimAmount.compareTo(policy.getRemainingCoverage()) > 0) {
            return new PolicyValidationResponse(false, policyId, policy.getPolicyStatus(), policy.getRemainingCoverage(), "Claim amount exceeds remaining coverage");
        }

        return new PolicyValidationResponse(true, policyId, policy.getPolicyStatus(), policy.getRemainingCoverage(), "Policy is valid for claim");
    }

    @Transactional
    public Policy deductRemainingCoverage(Long policyId, DeductCoverageRequest request) {
        Policy policy = policyRepository.findByIdWithLock(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found with ID: " + policyId));

        BigDecimal deductAmount = request.getAmount();
        if (deductAmount == null || deductAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }

        if (deductAmount.compareTo(policy.getRemainingCoverage()) > 0) {
            throw new IllegalStateException("Insufficient remaining coverage. Current: " + policy.getRemainingCoverage() + ", Requested: " + deductAmount);
        }

        BigDecimal updatedRemaining = policy.getRemainingCoverage().subtract(deductAmount);
        policy.setRemainingCoverage(updatedRemaining);
        return policyRepository.save(policy);
    }
}
