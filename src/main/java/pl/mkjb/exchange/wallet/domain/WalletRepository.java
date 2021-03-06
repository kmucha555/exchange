package pl.mkjb.exchange.wallet.domain;

import io.vavr.collection.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import pl.mkjb.exchange.transaction.domain.TransactionEntity;
import pl.mkjb.exchange.wallet.dto.UserWalletDto;

interface WalletRepository extends Repository<TransactionEntity, Long> {
    @Query("select " +
            "new pl.mkjb.exchange.wallet.dto.UserWalletDto(" +
            "t.currencyEntity.code, " +
            "sum(t.amount)) " +
            "from TransactionEntity t where t.userEntity.username = :username " +
            "group by t.currencyEntity.id")
    Set<UserWalletDto> findUserWallet(@Param("username") String username);
}
