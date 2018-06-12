package pl.edu.agh.sr.middleware.exchange;

import io.grpc.stub.StreamObserver;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.proto.CurrencyRequest;
import pl.edu.agh.sr.middleware.proto.grpc.CurrencyServiceGrpc;

import java.util.List;
import java.util.Optional;

public class CurrencyService extends CurrencyServiceGrpc.CurrencyServiceImplBase {

    private final int refreshTime = 1000;

    List<Currency> supportedCurrencies;

    public CurrencyService(List<Currency> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    @Override
    public void currencyChat(CurrencyRequest request, StreamObserver<Currency> responseObserver) {
        CurrencyCode first = request.getCode1();
        CurrencyCode second = request.getCode2();

        Optional<Currency> currency = getCurrency(first, second);
        currency.ifPresent(responseObserver::onNext);

        try {
            Thread.sleep(refreshTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    private Optional<Currency> getCurrency(CurrencyCode first, CurrencyCode second) {
        return supportedCurrencies.stream()
                .filter(currency -> currency.getCode1().equals(first) && currency.getCode2().equals(second))
                .findFirst();
    }
}
