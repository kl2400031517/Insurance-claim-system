package com.insurance.policy.controller;

import com.insurance.policy.dto.DeductCoverageRequest;
import com.insurance.policy.dto.PolicyCreateRequest;
import com.insurance.policy.dto.PolicyValidationResponse;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@RequestBody PolicyCreateRequest request) {
        Policy created = policyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<Policy> getPolicy(@PathVariable Long policyId) {
        Policy policy = policyService.getPolicy(policyId);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Policy>> getPoliciesByUserId(@PathVariable String userId) {
        List<Policy> policies = policyService.getPoliciesByUserId(userId);
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{policyId}/validate")
    public ResponseEntity<PolicyValidationResponse> validatePolicyForClaim(
            @PathVariable Long policyId,
            @RequestParam BigDecimal claimAmount) {
        PolicyValidationResponse response = policyService.validatePolicyForClaim(policyId, claimAmount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{policyId}/deduct")
    public ResponseEntity<Policy> deductCoverage(
            @PathVariable Long policyId,
            @RequestBody DeductCoverageRequest request) {
        Policy updatedPolicy = policyService.deductRemainingCoverage(policyId, request);
        return ResponseEntity.ok(updatedPolicy);
    }
}
