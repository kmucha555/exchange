package pl.mkjb.exchange.wallet.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.val;
import pl.mkjb.exchange.wallet.dto.UserWalletDto;

import java.math.BigDecimal;

class InMemoryWalletRepository implements WalletRepository {
    private String user = "test";
    private Map<String, Set<UserWalletDto>> wallet;

    {
        val usd = UserWalletDto.builder()
                .code("USD")
                .amount(BigDecimal.TEN)
                .build();

        val czk = UserWalletDto.builder()
                .code("CZK")
                .amount(BigDecimal.valueOf(100))
                .build();

        wallet = HashMap.of(user, HashSet.of(usd, czk));
    }

    @Override
    public Set<UserWalletDto> findUserWallet(String username) {
        return wallet.getOrElse(username, HashSet.empty());
    }
}
