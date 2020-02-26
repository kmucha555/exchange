package pl.mkjb.exchange.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class TransactionBuyModel {
    private UUID currencyRateId;
    private String currencyCode;
    private int currencyUnit;
    private BigDecimal sellPrice;
    private BigDecimal userWalletAmount;
    private BigDecimal buyAmount;
    private BigDecimal maxAmountOfCurrencyForBuyByUser;
}
