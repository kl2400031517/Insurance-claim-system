package com.insurance.claim.client;

import com.insurance.claim.dto.SettlementRequest;
import com.insurance.claim.dto.SettlementResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "SETTLEMENT-SERVICE")
public interface SettlementClient {

    @PostMapping("/api/v1/settlements")
    SettlementResponse processSettlement(@RequestBody SettlementRequest request);
}
