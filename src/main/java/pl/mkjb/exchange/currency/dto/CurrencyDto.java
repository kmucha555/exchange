package pl.mkjb.exchange.currency.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.mkjb.exchange.currency.domain.CurrencyEntity;

import java.math.BigDecimal;

@Builder
@Getter
@EqualsAndHashCode
public class CurrencyDto {
    private final int id;
    private final String name;
    private final String code;
    private final BigDecimal unit;
    private final Boolean billingCurrency;

    public static CurrencyEntity toEntity(CurrencyDto currencyDto) {
        return CurrencyEntity.builder()
                .id(currencyDto.id)
                .name(currencyDto.name)
                .code(currencyDto.code)
                .unit(currencyDto.unit)
                .billingCurrency(currencyDto.billingCurrency)
                .build();
    }
}
