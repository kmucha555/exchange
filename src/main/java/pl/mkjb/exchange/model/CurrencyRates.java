package pl.mkjb.exchange.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Data
public class CurrencyRates {
    private LocalDateTime publicationDate;
    private Set<Currency> items;
}
