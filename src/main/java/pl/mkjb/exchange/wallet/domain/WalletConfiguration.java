package pl.mkjb.exchange.wallet.domain;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;

@Configuration
class WalletConfiguration {

    WalletFacade walletFacade(CurrencyFacade currencyFacade) {
        final WalletRepository inMemoryWalletRepository = new InMemoryWalletRepository();
        return walletFacade(currencyFacade, inMemoryWalletRepository);
    }

    @Bean
    WalletFacade walletFacade(CurrencyFacade currencyFacade, WalletRepository walletRepository) {
        val walletService = new WalletService(currencyFacade, walletRepository);
        return new WalletFacade(walletService);
    }
}