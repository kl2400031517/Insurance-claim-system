package com.insurance.claim.client;

import com.insurance.claim.dto.PolicyValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    @GetMapping("/api/v1/policies/{policyId}/validate")
    PolicyValidationResponse validatePolicyForClaim(
            @PathVariable("policyId") Long policyId,
            @RequestParam("claimAmount") BigDecimal claimAmount);
}
