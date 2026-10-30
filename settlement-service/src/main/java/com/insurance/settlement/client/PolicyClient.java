package com.insurance.settlement.client;

import com.insurance.settlement.dto.DeductCoverageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    @PostMapping("/api/v1/policies/{policyId}/deduct")
    Object deductCoverage(@PathVariable("policyId") Long policyId, @RequestBody DeductCoverageRequest request);
}
