package com.insurance.settlement.controller;

import com.insurance.settlement.dto.SettlementRequest;
import com.insurance.settlement.entity.Settlement;
import com.insurance.settlement.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @PostMapping
    public ResponseEntity<Settlement> processSettlement(@RequestBody SettlementRequest request) {
        Settlement settlement = settlementService.processSettlement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(settlement);
    }

    @GetMapping("/{settlementId}")
    public ResponseEntity<Settlement> getSettlementById(@PathVariable Long settlementId) {
        Settlement settlement = settlementService.getSettlementById(settlementId);
        return ResponseEntity.ok(settlement);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<Settlement> getSettlementByClaimId(@PathVariable Long claimId) {
        Settlement settlement = settlementService.getSettlementByClaimId(claimId);
        return ResponseEntity.ok(settlement);
    }
}
