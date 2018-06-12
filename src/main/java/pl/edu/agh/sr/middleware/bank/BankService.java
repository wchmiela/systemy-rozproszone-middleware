package pl.edu.agh.sr.middleware.bank;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.proto.CurrencyRequest;
import pl.edu.agh.sr.middleware.proto.grpc.CurrencyServiceGrpc;

import java.util.Iterator;

public class BankService implements Runnable {

    private final Bank bank;
    private final CurrencyServiceGrpc.CurrencyServiceBlockingStub blockingStub;
    private final CurrencyServiceGrpc.CurrencyServiceStub asyncStub;
    private final Channel channel;

    public BankService(Bank bank) {
        this.bank = bank;
        this.channel = ManagedChannelBuilder.forAddress("127.0.0.1", bank.getExchangeCurrencyPort()).usePlaintext().build();
        this.blockingStub = CurrencyServiceGrpc.newBlockingStub(channel);
        this.asyncStub = CurrencyServiceGrpc.newStub(channel);
    }


    @Override
    public void run() {
        while (true) {
            getLiveCurrencies();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getLiveCurrencies() {

        bank.getSupportedCurrencies().forEach(currencyCode -> {
            CurrencyRequest request = CurrencyRequest.newBuilder()
                    .setCode1(CurrencyCode.PLN).setCode2(currencyCode)
                    .build();

            Iterator<Currency> currencyIterator = blockingStub.currencyChat(request);

            while (currencyIterator.hasNext()) {
                Currency currency = currencyIterator.next();
                bank.updateCurrency(currency);
            }
        });
    }
}
