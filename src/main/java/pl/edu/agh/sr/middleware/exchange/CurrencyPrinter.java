package pl.edu.agh.sr.middleware.exchange;

import com.google.common.collect.ComparisonChain;
import pl.edu.agh.sr.middleware.proto.Currency;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CurrencyPrinter implements Runnable {

    private final CurrencyExchange currencyExchange;

    public CurrencyPrinter(CurrencyExchange currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    @Override
    public void run() {
        while (true) {
            printCurrencies();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printCurrencies() {
        System.out.println("=====TABELA KURSOW=====CZAS: " + getTime());
        currencyExchange.getCurrencies().stream().sorted((o1, o2) ->
                ComparisonChain.start()
                        .compare(o1.getCode2(), o2.getCode2())
                        .result()).forEachOrdered(this::printCurrency);
        System.out.println("=====================================");
    }

    private void printCurrency(Currency currency) {
        String line = String.format("%s/%s %.4f", currency.getCode1(), currency.getCode2(), currency.getValue());
        System.out.println(line);
    }

    private String dummyFrontZeroAdder(int number) {
        if (number / 10 == 0) {
            return String.format("0%d", number);
        } else {
            return String.valueOf(number);
        }
    }

    private String getTime() {
        LocalDateTime localtDateAndTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        ZonedDateTime dateAndTimeInLA = ZonedDateTime.of(localtDateAndTime, zoneId);
        return String.format("%s:%s:%s",
                dummyFrontZeroAdder(dateAndTimeInLA.getHour()),
                dummyFrontZeroAdder(dateAndTimeInLA.getMinute()),
                dummyFrontZeroAdder(dateAndTimeInLA.getSecond()));
    }
}
