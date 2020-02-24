package pl.mkjb.exchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "currency_rates")
public class CurrencyRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "sell_price")
    private BigDecimal sellPrice;

    @Column(name = "average_price")
    private BigDecimal averagePrice;

    @Column(name = "publication_date")
    private LocalDateTime publicationDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
