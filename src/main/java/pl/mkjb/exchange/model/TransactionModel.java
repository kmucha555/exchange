package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mkjb.exchange.util.TransactionTypeConstant;
import pl.mkjb.exchange.validator.TransactionAmount;
import pl.mkjb.exchange.validator.TransactionType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TransactionAmount(transactionAmountFieldName = "transactionAmount")
public class TransactionModel {
    private UUID currencyRateId;
    private String currencyCode;
    private BigDecimal currencyUnit;
    private BigDecimal transactionPrice;
    private BigDecimal userWalletAmount;

    @NotNull
    private BigDecimal transactionAmount;

    private BigDecimal maxAllowedTransactionAmount;

    @TransactionType
    private TransactionTypeConstant transactionTypeConstant;
}
