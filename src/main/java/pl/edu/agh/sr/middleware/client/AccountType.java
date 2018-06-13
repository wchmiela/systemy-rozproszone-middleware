package pl.edu.agh.sr.middleware.client;

import pl.edu.agh.sr.middleware.thrift.TAccountType;

public enum AccountType {
    STANDARD, PREMIUM;

    public static TAccountType toTAccountType(AccountType type) {
        return type == AccountType.STANDARD
                ? TAccountType.STANDARD : TAccountType.PREMIUM;
    }
}


