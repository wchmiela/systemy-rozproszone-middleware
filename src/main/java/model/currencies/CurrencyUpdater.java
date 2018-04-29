package model.currencies;

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
            if (integerRandom > 0) {
                currency.update((100.0 + integerRandom) / 100.0);
            } else {
                currency.update((100 - Math.abs(integerRandom)) / 100.0);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Mam problemy ze snem...");
            }
        }
    }
}
