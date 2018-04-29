package model.account;

import model.Credit;

public class Account {

    private final AccountType accountType;
    private final Credit credit;

    public Account(AccountType accountType, Credit credit) {
        this.accountType = accountType;
        this.credit = credit;
    }
}
