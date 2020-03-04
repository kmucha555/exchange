package pl.mkjb.exchange.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

import java.math.BigDecimal;
import java.util.UUID;

@JsonDeserialize(builder = CurrencyModel.CurrencyModelBuilder.class)
@Builder(builderClassName = "CurrencyModelBuilder", toBuilder = true)
@Data
public class CurrencyModel {
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

    public static CurrencyModel buildCurrencyModel(CurrencyRateEntity currencyRate) {
        return CurrencyModel.builder()
                .currencyRateId(currencyRate.getId())
                .currencyId(currencyRate.getCurrencyEntity().getId())
                .name(currencyRate.getCurrencyEntity().getName())
                .code(currencyRate.getCurrencyEntity().getCode())
                .unit(currencyRate.getCurrencyEntity().getUnit())
                .purchasePrice(currencyRate.getPurchasePrice())
                .sellPrice(currencyRate.getSellPrice())
                .averagePrice(currencyRate.getAveragePrice())
                .billingCurrency(currencyRate.getCurrencyEntity().getBillingCurrency())
                .build();
    }
}
