package model;

import model.currencies.CurrencyCode;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Bank {

    private final String name;
    private final BigDecimal premiumLimit;
    private List<CurrencyCode> supportedCurrencies;

    public Bank(String name, String rawCurrencies, BigDecimal premiumLimit) {
        if (name == null || rawCurrencies == null || premiumLimit == null)
            throw new IllegalArgumentException();

        this.name = name;
        this.premiumLimit = premiumLimit;
        this.supportedCurrencies = new LinkedList<>();

        Stream.of(rawCurrencies.split(",")).forEach(this::addCurrency);
    }

    private void addCurrency(String currencyCode) {
        String upperCaseCurrencyCode = currencyCode.toUpperCase();
        switch (upperCaseCurrencyCode) {
            case "USD":
                this.supportedCurrencies.add(CurrencyCode.USD);
                break;
            case "EUR":
                this.supportedCurrencies.add(CurrencyCode.EUR);
                break;
            case "CHF":
                this.supportedCurrencies.add(CurrencyCode.CHF);
                break;
            case "GBP":
                this.supportedCurrencies.add(CurrencyCode.GBP);
                break;
            case "PLN":
                this.supportedCurrencies.add(CurrencyCode.PLN);
                break;
            case "SEK":
                this.supportedCurrencies.add(CurrencyCode.SEK);
                break;
            case "RUB":
                this.supportedCurrencies.add(CurrencyCode.RUB);
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("model.Bank o nazwie: %s. Wspierajacy waluty: %s. Limit dla konta premium: %s",
                name, supportedCurrencies, premiumLimit.toPlainString());
    }
}
