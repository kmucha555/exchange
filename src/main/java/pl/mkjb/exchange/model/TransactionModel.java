package pl.mkjb.exchange.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import pl.mkjb.exchange.util.TransactionTypeConstant;
import pl.mkjb.exchange.validator.TransactionAmount;
import pl.mkjb.exchange.validator.TransactionType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@JsonDeserialize(builder = TransactionModel.TransactionModelBuilder.class)
@Builder(builderClassName = "TransactionModelBuilder", toBuilder = true)
@Data
@TransactionAmount(transactionAmountFieldName = "transactionAmount")
public class TransactionModel {
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

    @JsonPOJOBuilder(withPrefix = "")
    public static class TransactionModelBuilder {
    }
}
