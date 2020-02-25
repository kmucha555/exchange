package pl.mkjb.exchange.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletModel {
    private long currencyRateId;
    private String code;
    private BigDecimal amount;
    private BigDecimal purchasePrice;

    public WalletModel(String code, BigDecimal amount) {
        this.code = code;
        this.amount = amount;
    }
}
