package pl.mkjb.exchange.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.model.WalletModel;

import java.math.BigDecimal;
import java.util.Set;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    @Query("select " +
            "new pl.mkjb.exchange.model.WalletModel(" +
            "t.currencyEntity.code, " +
            "sum(t.amount)) " +
            "from TransactionEntity t where t.userEntity.id = :userId group by t.currencyEntity.id")
    Set<WalletModel> findUserWallet(@Param("userId") Long userId);

    @Query("select sum(t.amount) from TransactionEntity t where t.userEntity.id = :userId and t.currencyEntity.id = :currencyId")
    Option<BigDecimal> sumCurrencyAmountForUser(@Param("userId") Long userId, @Param("currencyId") Integer currencyId);
}
