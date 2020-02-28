package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class CurrencyRatesModel {
    private LocalDateTime publicationDate;
    private Set<CurrencyModel> items;
}
