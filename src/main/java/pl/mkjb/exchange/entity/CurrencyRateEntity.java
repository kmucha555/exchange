package pl.mkjb.exchange.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Entity
@Table(name = "currency_rates")
public class CurrencyRateEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 16, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "currency_id", updatable = false, nullable = false)
    private CurrencyEntity currencyEntity;

    @Column(name = "purchase_price", updatable = false, nullable = false, precision = 7, scale = 4)
    private BigDecimal purchasePrice;

    @Column(name = "sell_price", updatable = false, nullable = false, precision = 7, scale = 4)
    private BigDecimal sellPrice;

    @Column(name = "average_price", updatable = false, nullable = false, precision = 7, scale = 4)
    private BigDecimal averagePrice;

    @Column(name = "publication_date", updatable = false, nullable = false, precision = 7, scale = 4)
    private LocalDateTime publicationDate;

    private Boolean active;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.active = Boolean.TRUE;
        this.createdAt = LocalDateTime.now();
    }
}
