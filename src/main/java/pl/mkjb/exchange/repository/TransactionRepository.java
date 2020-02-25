package pl.mkjb.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.model.WalletModel;

import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("select " +
            "new pl.mkjb.exchange.model.WalletModel(" +
            "t.currencyEntity.code, " +
            "sum(t.amount)) " +
            "from TransactionEntity t where t.userEntity.id = :userId group by t.currencyEntity.id")
    Set<WalletModel> findUserWallet(@Param("userId") Long userId);
}
