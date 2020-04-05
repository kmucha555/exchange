package pl.mkjb.exchange.currency.domain;

import lombok.*;
import pl.mkjb.exchange.currency.dto.CurrencyDto;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currencies")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
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

    public static CurrencyDto toDto(CurrencyEntity currencyEntity) {
        return CurrencyDto.builder()
                .id(currencyEntity.id)
                .code(currencyEntity.code)
                .name(currencyEntity.name)
                .unit(currencyEntity.unit)
                .billingCurrency(currencyEntity.billingCurrency)
                .build();
    }
}
