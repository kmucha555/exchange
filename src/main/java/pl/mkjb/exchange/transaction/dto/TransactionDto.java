package pl.mkjb.exchange.transaction.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import pl.mkjb.exchange.infrastructure.mvc.validator.TransactionAmount;
import pl.mkjb.exchange.infrastructure.mvc.validator.TransactionType;
import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@TransactionAmount(transactionAmountFieldName = "transactionAmount")
public class TransactionDto {
    private final UUID currencyRateId;
    private final String currencyCode;
    private final BigDecimal currencyUnit;
    private final BigDecimal transactionPrice;
    private final BigDecimal userWalletAmount;

    @NotNull
    private final BigDecimal transactionAmount;

    private final BigDecimal maxAllowedTransactionAmount;

    @TransactionType
    private final TransactionTypeConstant transactionTypeConstant;

    @JsonCreator
    public TransactionDto(@JsonProperty("currencyRateId") UUID currencyRateId,
                          @JsonProperty("currencyCode") String currencyCode,
                          @JsonProperty("currencyUnit") BigDecimal currencyUnit,
                          @JsonProperty("transactionPrice") BigDecimal transactionPrice,
                          @JsonProperty("userWalletAmount") BigDecimal userWalletAmount,
                          @JsonProperty("transactionAmount") BigDecimal transactionAmount,
                          @JsonProperty("maxAllowedTransactionAmount") BigDecimal maxAllowedTransactionAmount,
                          @JsonProperty("transactionTypeConstant") TransactionTypeConstant transactionTypeConstant) {
        this.currencyRateId = currencyRateId;
        this.currencyCode = currencyCode;
        this.currencyUnit = currencyUnit;
        this.transactionPrice = transactionPrice;
        this.userWalletAmount = userWalletAmount;
        this.transactionAmount = transactionAmount;
        this.maxAllowedTransactionAmount = maxAllowedTransactionAmount;
        this.transactionTypeConstant = transactionTypeConstant;
    }
}
