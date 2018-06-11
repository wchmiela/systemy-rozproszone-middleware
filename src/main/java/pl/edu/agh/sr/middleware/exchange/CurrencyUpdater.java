package pl.edu.agh.sr.middleware.exchange;

import pl.edu.agh.sr.middleware.proto.Currency;

import java.util.concurrent.ThreadLocalRandom;

public class CurrencyUpdater implements Runnable {

    private final CurrencyExchange currencyExchange;

    public CurrencyUpdater(CurrencyExchange currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    @Override
    public void run() {
        while (true) {
            Currency currency = currencyExchange.getRandomCurrency();

            int integerRandom = ThreadLocalRandom.current().nextInt(-1, 1);
            Currency newCurrency;
            if (integerRandom > 0) {
                newCurrency = Currency.newBuilder()
                        .setCode1(currency.getCode1())
                        .setCode2(currency.getCode2())
                        .setValue(currency.getValue() * (100.0 + integerRandom) / 100.0)
                        .build();

            } else {
                newCurrency = Currency.newBuilder()
                        .setCode1(currency.getCode1())
                        .setCode2(currency.getCode2())
                        .setValue(currency.getValue() * (100 - Math.abs(integerRandom)) / 100.0)
                        .build();
            }

            currencyExchange.getCurrencies().remove(currency);
            currencyExchange.getCurrencies().add(newCurrency);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Mam problemy ze snem...");
            }
        }
    }
}
