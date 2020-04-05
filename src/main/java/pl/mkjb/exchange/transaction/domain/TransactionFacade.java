package pl.mkjb.exchange.transaction.domain;

import io.vavr.Function1;
import io.vavr.Function2;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;
import pl.mkjb.exchange.transaction.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class TransactionFacade {
    private final TransactionFacadeService transactionFacadeService;

    public Function1<TransactionTypeConstant, Function2<CurrencyRateDto, UserDetails, BigDecimal>> estimateMaxTransactionAmount() {
        return transactionFacadeService.estimateMaxTransactionAmount();
    }

    public Function1<TransactionTypeConstant, Function2<UUID, UserDetails, TransactionDto>> getTransactionDto() {
        return transactionFacadeService.getTransactionDto();
    }

    public Function1<TransactionTypeConstant, BiConsumer<TransactionDto, UserDetails>> saveTransaction() {
        return transactionFacadeService.saveTransaction();
    }
}
