package pl.edu.agh.sr.middleware.bank;

import com.google.common.collect.Lists;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.proto.grpc.CurrencyServiceGrpc;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Bank extends CurrencyServiceGrpc.CurrencyServiceImplBase {

    private final int port;
    private final String bankName;
    private final BigDecimal premiumLimit;

    private List<CurrencyCode> supportedCurrencies;
    private List<Currency> currencies = Lists.newCopyOnWriteArrayList();

    public Bank(int port, String bankName, String rawCurrencies, BigDecimal premiumLimit) {
        if (bankName == null || rawCurrencies == null || premiumLimit == null)
            throw new IllegalArgumentException();

        this.port = port;
        this.bankName = bankName;
        this.premiumLimit = premiumLimit;
        this.supportedCurrencies = Lists.newArrayList();

        Stream.of(rawCurrencies.split(",")).forEach(this::addCurrency);

        BankService bankService = new BankService(this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(bankService);
    }


    public List<CurrencyCode> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public List<Currency> getCurrencies() {
        return currencies;
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
        return String.format("Bank o nazwie: %s. Wspierajacy waluty: %s. Limit dla konta premium: %s",
                bankName, supportedCurrencies, premiumLimit.toPlainString());
    }
}
