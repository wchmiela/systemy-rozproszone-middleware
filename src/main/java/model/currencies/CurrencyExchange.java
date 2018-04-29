package model.currencies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CurrencyExchange {

    private List<Currency> currencies;

    public CurrencyExchange() {
        this.currencies = new LinkedList<>();
    }

    public void initCurrencyExchange() {
        Currency currency1 = new Currency(CurrencyCode.PLN, CurrencyCode.EUR, new BigDecimal(4.22)
                .setScale(2, RoundingMode.HALF_UP));
        Currency currency2 = new Currency(CurrencyCode.PLN, CurrencyCode.USD, new BigDecimal(3.50)
                .setScale(2, RoundingMode.HALF_UP));
        Currency currency3 = new Currency(CurrencyCode.PLN, CurrencyCode.CHF, new BigDecimal(3.53)
                .setScale(2, RoundingMode.HALF_UP));
        Currency currency4 = new Currency(CurrencyCode.PLN, CurrencyCode.GBP, new BigDecimal(4.83)
                .setScale(2, RoundingMode.HALF_UP));

        this.currencies.add(currency1);
        this.currencies.add(currency2);
        this.currencies.add(currency3);
        this.currencies.add(currency4);
    }

    public Currency getRandomCurrency() {
        int randomInteger = ThreadLocalRandom.current().nextInt(0, currencies.size());

        return currencies.get(randomInteger);
    }
}
