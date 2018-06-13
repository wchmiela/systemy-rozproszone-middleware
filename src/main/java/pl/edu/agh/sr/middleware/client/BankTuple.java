package pl.edu.agh.sr.middleware.client;

import pl.edu.agh.sr.middleware.thrift.TAccountType;
import pl.edu.agh.sr.middleware.thrift.TBank;

public class BankTuple {
    private final String bankName;
    private final AccountType accountType;

    public BankTuple(TBank tBank) {
        this.bankName = tBank.bankName;
        this.accountType = tBank.type == TAccountType.STANDARD
                ? AccountType.STANDARD : AccountType.PREMIUM;
    }

    public String getBankName() {
        return bankName;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
