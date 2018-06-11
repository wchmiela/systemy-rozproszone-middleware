package pl.edu.agh.sr.middleware;

import pl.edu.agh.sr.middleware.exchange.CurrencyExchange;
import pl.edu.agh.sr.middleware.exchange.CurrencyPrinter;
import pl.edu.agh.sr.middleware.exchange.CurrencyUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationCurrencyExchange {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Podaj numer portu.");
            return;
        }

        int port = Integer.parseInt(args[0]);

        System.out.println("======KONFIGURACJA KANTORU START======");
        String currencyExchangeName = null;
        String rawCurrencies = null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Podaj nazwe Kantoru: ");
            currencyExchangeName = bufferedReader.readLine();
            System.out.print("Podaj obslugiwane waluty oddzielone przecinkiem: ");
            rawCurrencies = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Wystapil blad w pobieraniu danych Kantoru.");
            System.exit(1);
        }

        CurrencyExchange currencyExchange = null;
        try {
            currencyExchange = new CurrencyExchange(port, currencyExchangeName, rawCurrencies);
        } catch (IOException e) {
            System.out.println("Wystapil blad w konfigracji Kantoru. " + e.getMessage());
            System.exit(1);
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new CurrencyUpdater(currencyExchange));
        executorService.submit(new CurrencyPrinter(currencyExchange));

        System.out.println(currencyExchange);
        System.out.println("======KONFIGURACJA KANTORU STOP======");
    }
}
