package pl.mkjb.exchange.currency.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class CurrencyRateDto {
    private final UUID id;
    private final CurrencyDto currencyDto;
    private final BigDecimal purchasePrice;
    private final BigDecimal sellPrice;
    private final BigDecimal averagePrice;
    private final LocalDateTime publicationDate;
    private final boolean active;
    private final LocalDateTime createdAt;
}
