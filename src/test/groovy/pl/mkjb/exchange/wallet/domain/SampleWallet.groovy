package pl.mkjb.exchange.wallet.domain

import groovy.transform.CompileStatic
import io.vavr.collection.HashSet
import io.vavr.collection.Set
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import pl.mkjb.exchange.currency.domain.SampleCurrencies
import pl.mkjb.exchange.wallet.dto.UserWalletDto

@CompileStatic
trait SampleWallet extends SampleCurrencies {
    String user = "test"
    UserDetails userDetails = new User(user, "password", java.util.Set.of())
    UserDetails unknownUser = new User("unknown", "password", java.util.Set.of())

    UUID usdUUID = UUID.fromString("db64b6bc-f5e4-4eb8-9d53-5529f0691896")
    UUID czkUUID = UUID.fromString("d1d1570a-f705-41ef-98d9-fb5c1a01c71d")

    String usdCode = "USD"
    BigDecimal usdUnit = BigDecimal.ONE
    BigDecimal usdPurchasePrice = BigDecimal.valueOf(4d)

    String czkCode = "CZK"
    BigDecimal czkUnit = 100d
    BigDecimal czkPurchasePrice = BigDecimal.valueOf(14d)

    UserWalletDto usd = UserWalletDto.builder()
            .currencyRateId(usdUUID)
            .code(usdCode)
            .amount(BigDecimal.TEN)
            .purchasePrice(usdPurchasePrice)
            .unit(usdUnit)
            .build()

    UserWalletDto czk = UserWalletDto.builder()
            .currencyRateId(czkUUID)
            .code(czkCode)
            .amount(BigDecimal.valueOf(100))
            .purchasePrice(czkPurchasePrice)
            .unit(czkUnit)
            .build()

    Set<UserWalletDto> wallet = HashSet.of(usd, czk)
}
