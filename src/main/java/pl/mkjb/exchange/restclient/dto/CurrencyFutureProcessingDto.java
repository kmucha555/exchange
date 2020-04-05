package pl.mkjb.exchange.restclient.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@JsonDeserialize(builder = CurrencyFutureProcessingDto.CurrencyModelBuilder.class)
@Builder(builderClassName = "CurrencyModelBuilder", toBuilder = true)
@Data
public class CurrencyFutureProcessingDto {
    private final UUID currencyRateId;
    private final int currencyId;
    private final String name;
    private final String code;
    private final BigDecimal unit;
    private final BigDecimal purchasePrice;
    private final BigDecimal sellPrice;
    private final BigDecimal averagePrice;
    private final boolean billingCurrency;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CurrencyModelBuilder {
    }
}
