package pl.mkjb.exchange.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Data
public class CurrencyRatesModel {
    private LocalDateTime publicationDate;
    private Set<CurrencyModel> items;
}
