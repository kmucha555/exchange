package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionBuyModel {
    private UUID currencyRateId;
    private String currencyCode;
    private int currencyUnit;
    private BigDecimal sellPrice;
    private BigDecimal userWalletAmount;

    @NotNull
    private BigDecimal buyAmount;

    private BigDecimal maxAmountOfCurrencyForBuyByUser;
}
