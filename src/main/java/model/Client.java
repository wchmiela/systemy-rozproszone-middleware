package model;

import model.account.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private final String firstName;
    private final String lastName;
    private final Pesel PESEL;
    private final BigDecimal income;
    private Map<Account, Bank> accounts;

    public Client(String firstName, String lastName, String pesel, BigDecimal income) {
        if (firstName == null || lastName == null || pesel == null || income == null)
            throw new IllegalArgumentException();

        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = new Pesel(pesel);
        this.income = income;

        this.accounts = new HashMap<>();
    }

    @Override
    public String toString() {
        return String.format("Klient: %s %s PESEL: %s o przychodach %s", firstName, lastName, PESEL, income.toPlainString());
    }
}
