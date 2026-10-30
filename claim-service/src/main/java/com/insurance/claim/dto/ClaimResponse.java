package com.insurance.claim.dto;

import com.insurance.claim.entity.Claim;

public class ClaimResponse {
    private Claim claim;
    private SettlementResponse settlement;

    public ClaimResponse() {}

    public ClaimResponse(Claim claim, SettlementResponse settlement) {
        this.claim = claim;
        this.settlement = settlement;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public SettlementResponse getSettlement() {
        return settlement;
    }

    public void setSettlement(SettlementResponse settlement) {
        this.settlement = settlement;
    }
}
