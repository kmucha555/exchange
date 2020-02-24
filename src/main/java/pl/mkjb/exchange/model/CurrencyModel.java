package pl.mkjb.exchange.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class CurrencyModel {
    private String name;
    private String code;
    private int unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    private BigDecimal averagePrice;
}
