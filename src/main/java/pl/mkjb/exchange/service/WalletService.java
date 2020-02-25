package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final CurrencyService currencyService;
    private final TransactionRepository transactionRepository;

    public Set<WalletModel> getUserWallet(Long userId) {
        final CurrencyRatesModel newestRates = currencyService.getNewestRates();
        final Set<WalletModel> userWallet = transactionRepository.findUserWallet(userId);

        userWallet.forEach(walletModel -> newestRates.getItems()
                .stream()
                .filter(currencyModel -> currencyModel.getCode().equals(walletModel.getCode()))
                .forEach(currencyModel -> {
                    walletModel.setCurrencyRateId(currencyModel.getId());
                    walletModel.setPurchasePrice(currencyModel.getPurchasePrice());
                }));

        return userWallet;
    }
}
