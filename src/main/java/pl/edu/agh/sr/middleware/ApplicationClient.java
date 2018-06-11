package pl.edu.agh.sr.middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApplicationClient {

    public static void main(String[] args) {
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

        Client client = null;
        try {
            client = new Client(firstName, lastName, pesel, income);
        } catch (Exception e) {
            System.out.println("Wystapil blad w konfigracji klienta.");
            System.exit(1);
        }

        System.out.println(client);
        System.out.println("===KONFIGURACJA KLIENTA STOP===");
    }
}
