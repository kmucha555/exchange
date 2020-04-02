package pl.mkjb.exchange.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
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

    @Column(name = "billing_currency", nullable = false)
    private Boolean billingCurrency;

    @PrePersist
    public void prePersist() {
        this.billingCurrency = Boolean.FALSE;
    }
}
