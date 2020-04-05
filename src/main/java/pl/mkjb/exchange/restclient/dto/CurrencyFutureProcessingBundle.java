package pl.mkjb.exchange.restclient.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@JsonDeserialize(builder = CurrencyFutureProcessingBundle.CurrencyRatesModelBuilder.class)
@Builder(builderClassName = "CurrencyRatesModelBuilder", toBuilder = true)
@RequiredArgsConstructor(staticName = "of")
@Data
public class CurrencyFutureProcessingBundle {
    private final LocalDateTime publicationDate;
    private final Set<CurrencyFutureProcessingDto> items;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CurrencyRatesModelBuilder {
    }
}
