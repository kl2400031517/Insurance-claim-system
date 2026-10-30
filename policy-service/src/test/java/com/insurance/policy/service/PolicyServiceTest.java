package com.insurance.policy.service;

import com.insurance.policy.dto.DeductCoverageRequest;
import com.insurance.policy.dto.PolicyCreateRequest;
import com.insurance.policy.dto.PolicyValidationResponse;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePolicy_Success() {
        PolicyCreateRequest req = new PolicyCreateRequest("USER100", new BigDecimal("500000.00"));
        Policy mockSaved = new Policy("USER100", new BigDecimal("500000.00"));
        mockSaved.setPolicyId(1L);

        when(policyRepository.save(any(Policy.class))).thenReturn(mockSaved);

        Policy created = policyService.createPolicy(req);

        assertNotNull(created);
        assertEquals(1L, created.getPolicyId());
        assertEquals(new BigDecimal("500000.00"), created.getCoverageAmount());
        assertEquals(new BigDecimal("500000.00"), created.getRemainingCoverage());
    }

    @Test
    void testValidatePolicyForClaim_Valid() {
        Policy policy = new Policy("USER100", new BigDecimal("500000.00"));
        policy.setPolicyId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        PolicyValidationResponse response = policyService.validatePolicyForClaim(1L, new BigDecimal("200000.00"));

        assertTrue(response.isValid());
        assertEquals("Policy is valid for claim", response.getReason());
    }

    @Test
    void testValidatePolicyForClaim_ExceedsCoverage() {
        Policy policy = new Policy("USER100", new BigDecimal("100000.00"));
        policy.setPolicyId(1L);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        PolicyValidationResponse response = policyService.validatePolicyForClaim(1L, new BigDecimal("200000.00"));

        assertFalse(response.isValid());
        assertEquals("Claim amount exceeds remaining coverage", response.getReason());
    }

    @Test
    void testDeductRemainingCoverage_Success() {
        Policy policy = new Policy("USER100", new BigDecimal("500000.00"));
        policy.setPolicyId(1L);

        when(policyRepository.findByIdWithLock(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArguments()[0]);

        Policy updated = policyService.deductRemainingCoverage(1L, new DeductCoverageRequest(new BigDecimal("200000.00")));

        assertEquals(new BigDecimal("300000.00"), updated.getRemainingCoverage());
    }

    @Test
    void testDeductRemainingCoverage_InsufficientCoverage_ThrowsException() {
        Policy policy = new Policy("USER100", new BigDecimal("100000.00"));
        policy.setPolicyId(1L);

        when(policyRepository.findByIdWithLock(1L)).thenReturn(Optional.of(policy));

        assertThrows(IllegalStateException.class, () -> {
            policyService.deductRemainingCoverage(1L, new DeductCoverageRequest(new BigDecimal("200000.00")));
        });
    }
}
