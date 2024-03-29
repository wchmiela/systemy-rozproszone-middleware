package pl.edu.agh.sr.middleware;

import pl.edu.agh.sr.middleware.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApplicationClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Podaj poprawne numery portow. [PORT KLIENTA] [PORT BANKU]");
            return;
        }

        int clientPort = Integer.parseInt(args[0]);
        int bankPort = Integer.parseInt(args[1]);

        System.out.println("===KONFIGURACJA KLIENTA START===");
        String firstName = null;
        String lastName = null;
        String pesel = null;
        BigDecimal income = null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Podaj swoje Imie: ");
            firstName = bufferedReader.readLine();
            System.out.print("Podaj swoje Nazwisko: ");
            lastName = bufferedReader.readLine();
            System.out.print("Podaj swoj PESEL: ");
            pesel = bufferedReader.readLine();
            System.out.print("Podaj deklarowany prog miesiecznych wplywow: ");
            income = new BigDecimal(bufferedReader.readLine()).setScale(2, RoundingMode.HALF_UP);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Wystapil blad w pobieraniu danych klienta.");
            System.exit(1);
        }

        Client client;
        try {
            client = new Client(clientPort, bankPort, firstName, lastName, pesel, income);
            System.out.println(client);
            System.out.println("===KONFIGURACJA KLIENTA STOP===");

            menu(client);
        } catch (IllegalArgumentException e) {
            System.out.println("Podano niepoprawny numer PESEL");
        } catch (Exception e) {
            System.out.println("Wystapil blad w konfigracji klienta.");
            System.exit(1);
        }
    }

    private static void menu(Client client) throws IOException {
        boolean first = true;

        System.out.println("================MENU================");
        System.out.println("register               [zaloz konto]");
        System.out.println("check      [sprawdz dostepne srodki]");
        System.out.println("credit        [wez kredyt w walucie]");
        System.out.println("exit                       [zamknij]");
        System.out.println("====================================");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String currency = null;
        String money = null;
        String days = null;

        while (true) {
            System.out.print("Wczytaj operacje: ");
            String operation = bufferedReader.readLine();

            switch (operation.toLowerCase()) {
                case "register":
                    client.register();
                    break;
                case "check":
                    client.check();
                    break;
                case "credit":
                    if (first) {
                        System.out.print("Podaj walute: ");
                        currency = bufferedReader.readLine().toUpperCase();
                        System.out.print("Podaj ilosc: ");
                        money = bufferedReader.readLine();
                        System.out.print("Podaj ilosc dni: ");
                        days = bufferedReader.readLine();
                        first = false;
                    }
                    client.credit(currency, money, days);
                    break;
                case "exit":
//                    client.close(); todo implement
                    System.out.println("Zamkniecie aplikacji klienta");
                    return;
                default:
                    System.out.println("Wczytano niepoprawna opcje");
                    menu(client);
                    break;
            }
        }
    }
}
