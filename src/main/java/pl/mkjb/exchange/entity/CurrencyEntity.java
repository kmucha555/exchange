package pl.mkjb.exchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "currencies", indexes = @Index(columnList = "code", unique = true))
public class CurrencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 3, nullable = false)
    private String code;

    @Column(nullable = false)
    private short unit;
}
