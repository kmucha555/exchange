package pl.mkjb.exchange.transaction.domain;


import lombok.*;
import pl.mkjb.exchange.currency.domain.CurrencyEntity;
import pl.mkjb.exchange.user.domain.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_id", updatable = false, nullable = false)
    private CurrencyEntity currencyEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private UserEntity userEntity;

    @Column(precision = 9, scale = 2, updatable = false, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency_rate", precision = 7, scale = 4, updatable = false, nullable = false)
    private BigDecimal currencyRate;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
