package pl.edu.agh.sr.middleware;

public class Account {

    private final AccountType accountType;
    private final Credit credit;

    public Account(AccountType accountType, Credit credit) {
        this.accountType = accountType;
        this.credit = credit;
    }
}
