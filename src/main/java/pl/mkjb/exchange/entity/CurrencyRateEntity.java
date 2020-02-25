package pl.mkjb.exchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "currency_rates")
public class CurrencyRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
