package com.insurance.claim.controller;

import com.insurance.claim.dto.ClaimRequest;
import com.insurance.claim.dto.ClaimResponse;
import com.insurance.claim.entity.Claim;
import com.insurance.claim.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @PostMapping
    public ResponseEntity<ClaimResponse> submitClaim(@RequestBody ClaimRequest request) {
        ClaimResponse response = claimService.submitClaim(request);
        if ("APPROVED".equalsIgnoreCase(response.getClaim().getStatus())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long claimId) {
        Claim claim = claimService.getClaimById(claimId);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<Claim>> getClaimsByPolicyId(@PathVariable Long policyId) {
        List<Claim> claims = claimService.getClaimsByPolicyId(policyId);
        return ResponseEntity.ok(claims);
    }
}
