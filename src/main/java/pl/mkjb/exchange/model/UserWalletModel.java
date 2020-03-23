package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class UserWalletModel {
    private UUID currencyRateId;
    private String code;
    private BigDecimal unit;
    private BigDecimal amount;
    private BigDecimal purchasePrice;

    public UserWalletModel(String code, BigDecimal amount) {
        this.code = code;
        this.amount = amount;
    }
}
