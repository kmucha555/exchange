package pl.mkjb.exchange.model;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Data
public class CurrencyBundle {
    private String publicationDate;
    private Set<Currency> items;
}
