package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.model.UserWalletModel;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    @Query("select " +
            "new pl.mkjb.exchange.model.UserWalletModel(" +
            "t.currencyEntity.code, " +
            "sum(t.amount)) " +
            "from TransactionEntity t where t.userEntity.username = :username " +
            "group by t.currencyEntity.id")
    Set<UserWalletModel> findUserWallet(@Param("username") String username);

    @Query("select sum(t.amount) from TransactionEntity t where t.userEntity.id = :userId and t.currencyEntity.id = :currencyId")
    Option<BigDecimal> sumCurrencyAmountForUser(@Param("userId") Long userId, @Param("currencyId") Integer currencyId);
}
