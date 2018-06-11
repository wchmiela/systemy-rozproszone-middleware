package pl.edu.agh.sr.middleware.bank;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.proto.CurrencyRequest;
import pl.edu.agh.sr.middleware.proto.grpc.CurrencyServiceGrpc;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class BankService implements Runnable {

    private final Bank bank;
    private final CurrencyServiceGrpc.CurrencyServiceBlockingStub blockingStub;
    private final CurrencyServiceGrpc.CurrencyServiceStub asyncStub;
    private final Channel channel;

    public BankService(Bank bank) {
        this.bank = bank;
        this.channel = ManagedChannelBuilder.forAddress("127.0.0.1", 4001).usePlaintext().build();
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

        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<Currency> responseObserver = new StreamObserver<Currency>() {
            @Override
            public void onNext(Currency currency) {
                System.out.println(currency);

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error");
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("finished");

                finishLatch.countDown();
            }
        };

        bank.getSupportedCurrencies().forEach(currencyCode -> {
            CurrencyRequest request = CurrencyRequest.newBuilder()
                    .setCode1(CurrencyCode.PLN).setCode2(currencyCode)
                    .build();

            Iterator<Currency> currencyIterator;

            currencyIterator = blockingStub.currencyChat(request);

            while (currencyIterator.hasNext()) {
                Currency c = currencyIterator.next();

                System.out.println(c.getCode1Value() + " " + c.getCode2Value() + " " + c.getValue());
            }
        });

        // Send numPoints points randomly selected from the features list.
    }
}
