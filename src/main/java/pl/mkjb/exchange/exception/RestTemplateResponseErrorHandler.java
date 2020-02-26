package pl.mkjb.exchange.exception;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        throw new BadResourceException("Exception during fetching currency rates. Http response: " + clientHttpResponse.getStatusCode());
    }
}
