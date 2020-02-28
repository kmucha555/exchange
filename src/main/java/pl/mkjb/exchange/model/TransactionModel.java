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
public class TransactionModel {
    private UUID currencyRateId;
    private String currencyCode;
    private BigDecimal currencyUnit;
    private BigDecimal transactionPrice;
    private BigDecimal userWalletAmount;

    @NotNull
    private BigDecimal transactionAmount;

    private BigDecimal maxAllowedTransactionAmount;
}
