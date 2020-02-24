package pl.mkjb.exchange.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Currency {
    private String name;
    private String code;
    private int unit;
    private String purchasePrice;
    private String sellPrice;
    private String averagePrice;
}
