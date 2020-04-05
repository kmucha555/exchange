package pl.mkjb.exchange.transaction.domain;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.user.domain.UserService;
import pl.mkjb.exchange.wallet.domain.WalletFacade;

@Configuration
class TransactionConfiguration {

    @Bean
    TransactionFacade transactionFacade(CurrencyFacade currencyFacade,
                                        WalletFacade walletFacade,
                                        UserService userService,
                                        TransactionRepository transactionRepository) {

        val exchangeService = new ExchangeService(currencyFacade, userService, transactionRepository);
        val transactionSellService = new TransactionSellService(walletFacade, currencyFacade, userService, exchangeService, transactionRepository);
        val transactionBuyService = new TransactionBuyService(walletFacade, currencyFacade, userService, exchangeService, transactionRepository);
        val transactionFacadeService = new TransactionFacadeService(transactionBuyService, transactionSellService);

        return new TransactionFacade(transactionFacadeService);
    }
}
