package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyModel {
    private UUID currencyRateId;
    private int currencyId;
    private String name;
    private String code;
    private BigDecimal unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    private BigDecimal averagePrice;
    private boolean baseCurrency;

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
                .baseCurrency(currencyRate.getCurrencyEntity().getBaseCurrency())
                .build();
    }
}
