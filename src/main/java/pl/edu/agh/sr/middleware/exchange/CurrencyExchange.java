package pl.edu.agh.sr.middleware.exchange;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class CurrencyExchange {

    private final int exchangeCurrencyPort;
    private final int bankPort;
    private final Server server;
    private final ImmutableList<Currency> baseCurrencies;
    private final String currencyExchangeName;
    private List<CurrencyCode> supportedCurrencies = Lists.newCopyOnWriteArrayList();
    private List<Currency> currencies = Lists.newCopyOnWriteArrayList();

    public CurrencyExchange(int exchangeCurrencyPort, int bankPort, String currencyExchangeName, String rawCurrencies) throws IOException {
        this.currencyExchangeName = currencyExchangeName;
        this.baseCurrencies = ImmutableList.<Currency>builder()
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.USD).setValue(3.62).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.EUR).setValue(4.27).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.CHF).setValue(3.67).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.GBP).setValue(4.84).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.PLN).setValue(1.00).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.SEK).setValue(0.41).build())
                .add(Currency.newBuilder().setCode1(CurrencyCode.PLN).setCode2(CurrencyCode.RUB).setValue(0.05).build())
                .build();

        Stream.of(rawCurrencies.split(",")).forEach(this::addCurrency);

        for (Currency baseCurrency : baseCurrencies) {
            if (supportedCurrencies.contains(baseCurrency.getCode2())) {
                currencies.add(baseCurrency);
            }
        }

        this.exchangeCurrencyPort = exchangeCurrencyPort;
        this.bankPort = bankPort;
        this.server = ServerBuilder.forPort(exchangeCurrencyPort).addService(new CurrencyService(currencies)).build().start();
    }

    public Currency getRandomCurrency() {
        int randomInteger = ThreadLocalRandom.current().nextInt(0, currencies.size());

        return currencies.get(randomInteger);
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
        return String.format("Kantor o nazwie: %s. Wspierajacy waluty: %s",
                currencyExchangeName, supportedCurrencies);
    }
}
