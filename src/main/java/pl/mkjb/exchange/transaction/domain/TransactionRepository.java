package pl.mkjb.exchange.transaction.domain;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    @Query("select sum(t.amount) from TransactionEntity t where t.userEntity.id = :userId and t.currencyEntity.id = :currencyId")
    Option<BigDecimal> sumCurrencyAmountForUser(@Param("userId") Long userId, @Param("currencyId") Integer currencyId);
}
