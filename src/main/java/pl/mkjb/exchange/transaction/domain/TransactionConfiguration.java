package pl.mkjb.exchange.transaction.domain;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.user.domain.UserFacade;
import pl.mkjb.exchange.wallet.domain.WalletFacade;

@Configuration
class TransactionConfiguration {

    @Bean
    TransactionFacade transactionFacade(CurrencyFacade currencyFacade,
                                        WalletFacade walletFacade,
                                        UserFacade userFacade,
                                        TransactionRepository transactionRepository) {

        val exchangeService = new ExchangeService(currencyFacade, userFacade, transactionRepository);
        val transactionSellService = new TransactionSellService(walletFacade, currencyFacade, userFacade, exchangeService, transactionRepository);
        val transactionBuyService = new TransactionBuyService(walletFacade, currencyFacade, userFacade, exchangeService, transactionRepository);
        val transactionFacadeService = new TransactionFacadeService(transactionBuyService, transactionSellService);

        return new TransactionFacade(transactionFacadeService, transactionRepository);
    }
}
