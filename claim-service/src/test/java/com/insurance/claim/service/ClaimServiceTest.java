package com.insurance.claim.service;

import com.insurance.claim.client.PolicyClient;
import com.insurance.claim.client.SettlementClient;
import com.insurance.claim.dto.*;
import com.insurance.claim.entity.Claim;
import com.insurance.claim.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyClient policyClient;

    @Mock
    private SettlementClient settlementClient;

    @InjectMocks
    private ClaimService claimService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitClaim_ApprovedAndSettled() {
        ClaimRequest request = new ClaimRequest(1L, new BigDecimal("200000.00"));

        PolicyValidationResponse validResponse = new PolicyValidationResponse();
        validResponse.setValid(true);
        validResponse.setPolicyId(1L);
        validResponse.setRemainingCoverage(new BigDecimal("500000.00"));

        when(policyClient.validatePolicyForClaim(eq(1L), any(BigDecimal.class))).thenReturn(validResponse);

        Claim mockClaim = new Claim(1L, new BigDecimal("200000.00"), "APPROVED", "Claim validated and approved");
        mockClaim.setClaimId(10L);
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setSettlementId(100L);
        settlementResponse.setClaimId(10L);
        settlementResponse.setPolicyId(1L);
        settlementResponse.setApprovedAmount(new BigDecimal("200000.00"));
        settlementResponse.setPaymentStatus("SUCCESS");
        settlementResponse.setProcessedAt(LocalDateTime.now());

        when(settlementClient.processSettlement(any(SettlementRequest.class))).thenReturn(settlementResponse);

        ClaimResponse response = claimService.submitClaim(request);

        assertNotNull(response);
        assertEquals("APPROVED", response.getClaim().getStatus());
        assertNotNull(response.getSettlement());
        assertEquals("SUCCESS", response.getSettlement().getPaymentStatus());
    }

    @Test
    void testSubmitClaim_RejectedDueToExceedingCoverage() {
        ClaimRequest request = new ClaimRequest(1L, new BigDecimal("600000.00"));

        PolicyValidationResponse invalidResponse = new PolicyValidationResponse();
        invalidResponse.setValid(false);
        invalidResponse.setPolicyId(1L);
        invalidResponse.setRemainingCoverage(new BigDecimal("500000.00"));
        invalidResponse.setReason("Claim amount exceeds remaining coverage");

        when(policyClient.validatePolicyForClaim(eq(1L), any(BigDecimal.class))).thenReturn(invalidResponse);

        Claim mockClaim = new Claim(1L, new BigDecimal("600000.00"), "REJECTED", "Claim amount exceeds remaining coverage");
        mockClaim.setClaimId(11L);
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        ClaimResponse response = claimService.submitClaim(request);

        assertNotNull(response);
        assertEquals("REJECTED", response.getClaim().getStatus());
        assertNull(response.getSettlement());
        verify(settlementClient, never()).processSettlement(any());
    }
}
