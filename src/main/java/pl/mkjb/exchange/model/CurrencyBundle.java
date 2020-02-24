package pl.mkjb.exchange.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class CurrencyBundle {
    private final String publicationDate;
    private final Set<Currency> items;
}
