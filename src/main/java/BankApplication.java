import model.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankApplication {

    public static void main(String[] args) {
        System.out.println("===KONFIGURACJA BANKU START===");
        String name = null;
        String rawCurrencies = null;
        BigDecimal premiumLimit = null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Podaj nazwe Banku: ");
            name = bufferedReader.readLine();
            System.out.print("Podaj obslugiwane waluty oddzielone przecinkiem: ");
            rawCurrencies = bufferedReader.readLine();
            System.out.print("Podaj dolny limit dla konta Premium: ");
            premiumLimit = new BigDecimal(bufferedReader.readLine()).setScale(2, RoundingMode.HALF_UP);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Wystapil blad w pobieraniu danych Banku.");
            System.exit(1);
        }

        Bank bank = null;
        try {
            bank = new Bank(name, rawCurrencies, premiumLimit);
        } catch (Exception e) {
            System.out.println("Wystapil blad w konfigracji Banku.");
            System.exit(1);
        }

        System.out.println(bank);
        System.out.println("===KONFIGURACJA BANKU STOP===");
    }
}
