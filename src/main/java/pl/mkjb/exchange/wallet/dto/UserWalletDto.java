package pl.mkjb.exchange.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class UserWalletDto {
    private UUID currencyRateId;
    private String code;
    private BigDecimal unit;
    private BigDecimal amount;
    private BigDecimal purchasePrice;

    public UserWalletDto(String code, BigDecimal amount) {
        this.code = code;
        this.amount = amount;
    }
}
