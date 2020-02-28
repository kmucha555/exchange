package pl.mkjb.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "currencies")
public class CurrencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 3, nullable = false)
    private String code;

    @Column(nullable = false, precision = 4, scale = 0)
    private BigDecimal unit;

    @Column(name = "base_currency", nullable = false)
    private Boolean baseCurrency;

    @PrePersist
    public void prePersist() {
        this.baseCurrency = Boolean.FALSE;
    }
}
