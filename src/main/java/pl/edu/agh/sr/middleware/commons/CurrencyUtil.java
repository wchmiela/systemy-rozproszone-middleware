package pl.edu.agh.sr.middleware.commons;

import pl.edu.agh.sr.middleware.proto.CurrencyCode;

import java.util.List;

public class CurrencyUtil {

    public static void addCurrency(List<CurrencyCode> currencies, String currencyCode) {
        String upperCaseCurrencyCode = currencyCode.toUpperCase();
        switch (upperCaseCurrencyCode) {
            case "USD":
                currencies.add(CurrencyCode.USD);
                break;
            case "EUR":
                currencies.add(CurrencyCode.EUR);
                break;
            case "CHF":
                currencies.add(CurrencyCode.CHF);
                break;
            case "GBP":
                currencies.add(CurrencyCode.GBP);
                break;
            case "PLN":
                currencies.add(CurrencyCode.PLN);
                break;
            case "SEK":
                currencies.add(CurrencyCode.SEK);
                break;
            case "RUB":
                currencies.add(CurrencyCode.RUB);
                break;
        }
    }
}
