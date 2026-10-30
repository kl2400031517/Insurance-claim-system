package com.insurance.settlement.service;

import com.insurance.settlement.client.PolicyClient;
import com.insurance.settlement.dto.DeductCoverageRequest;
import com.insurance.settlement.dto.SettlementRequest;
import com.insurance.settlement.entity.Settlement;
import com.insurance.settlement.repository.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SettlementServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private PolicyClient policyClient;

    @InjectMocks
    private SettlementService settlementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessSettlement_SuccessfulPayment_DeductsCoverage() {
        SettlementRequest req = new SettlementRequest(10L, 1L, new BigDecimal("200000.00"));
        req.setSimulateFailure(false);

        Settlement mockSaved = new Settlement(10L, 1L, new BigDecimal("200000.00"), "SUCCESS");
        mockSaved.setSettlementId(100L);

        when(settlementRepository.save(any(Settlement.class))).thenReturn(mockSaved);

        Settlement result = settlementService.processSettlement(req);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getPaymentStatus());
        verify(policyClient, times(1)).deductCoverage(eq(1L), any(DeductCoverageRequest.class));
    }

    @Test
    void testProcessSettlement_PaymentFailure_DoesNotDeductCoverage() {
        SettlementRequest req = new SettlementRequest(10L, 1L, new BigDecimal("200000.00"));
        req.setSimulateFailure(true);

        Settlement mockSaved = new Settlement(10L, 1L, new BigDecimal("200000.00"), "FAILED");
        mockSaved.setSettlementId(101L);

        when(settlementRepository.save(any(Settlement.class))).thenReturn(mockSaved);

        Settlement result = settlementService.processSettlement(req);

        assertNotNull(result);
        assertEquals("FAILED", result.getPaymentStatus());
        verify(policyClient, never()).deductCoverage(any(), any());
    }
}
