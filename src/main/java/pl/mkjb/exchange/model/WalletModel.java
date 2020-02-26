package pl.mkjb.exchange.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletModel {
    private UUID currencyRateId;
    private String code;
    private BigDecimal amount;
    private BigDecimal purchasePrice;

    public WalletModel(String code, BigDecimal amount) {
        this.code = code;
        this.amount = amount;
    }
}
