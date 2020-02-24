package pl.mkjb.exchange.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Currency {
    private final String name;
    private final String code;
    private final int unit;
    private final String purchasePrice;
    private final String sellPrice;
    private final String averagePrice;
}
