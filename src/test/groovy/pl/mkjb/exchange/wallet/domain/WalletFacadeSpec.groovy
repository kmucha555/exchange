package pl.mkjb.exchange.wallet.domain


import io.vavr.collection.HashSet
import io.vavr.control.Option
import pl.mkjb.exchange.currency.domain.CurrencyFacade
import spock.lang.Shared
import spock.lang.Specification

class WalletFacadeSpec extends Specification implements SampleWallet {
    @Shared
    def currencyFacadeStub = Stub(CurrencyFacade)

    @Shared
    WalletFacade facade = new WalletConfiguration().walletFacade(currencyFacadeStub)

    def setupSpec() {
        currencyFacadeStub.getNewestCurrencyRates() >> HashSet.of(usdCurrencyRate, czkCurrencyRate)
        currencyFacadeStub.findBillingCurrency() >> Option.of(billingCurrencyRate)
    }

    def "should return user wallet with setup currencies"() {
        when: "facade is asked for user wallet"
        def actualWallet = facade.getUserWallet(userDetails)

        then: "facade returns user wallet"
        actualWallet == wallet
    }

    def "should return empty set where unknown user is provided"() {
        when: "facade is asked for wallet of unknown user"
        def actualWallet = facade.getUserWallet(unknownUser)

        then: "facade returns empty set"
        actualWallet == HashSet.empty()
    }
}
