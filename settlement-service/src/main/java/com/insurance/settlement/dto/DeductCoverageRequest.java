package com.insurance.settlement.dto;

import java.math.BigDecimal;

public class DeductCoverageRequest {
    private BigDecimal amount;

    public DeductCoverageRequest() {}

    public DeductCoverageRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
