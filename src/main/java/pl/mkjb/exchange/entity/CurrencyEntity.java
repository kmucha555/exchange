package pl.mkjb.exchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(nullable = false)
    private Integer unit;

    @Column(name = "base_currency", nullable = false)
    private Boolean baseCurrency;

    @PrePersist
    public void prePersist() {
        this.baseCurrency = Boolean.FALSE;
    }
}
