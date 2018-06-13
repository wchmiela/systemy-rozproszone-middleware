namespace java pl.edu.agh.sr.middleware.thrift

enum TCurrencyCode {
    USD = 0,
    EUR = 1,
    CHF = 2,
    GBP = 3,
    PLN = 4,
    SEK = 5,
    RUB = 6
}

enum TAccountType {
    STANDARD = 0,
    PREMIUM = 1
}

struct TClient  {
    1: required string firstName,
    2: required string lastName,
    3: required string pesel,
    4: required string income
    5: required i32 port
}

struct TBank  {
    1: required string bankName,
    2: required TAccountType type
}

struct TCheckMessage{
    1: required TBank tBank,
    2: required string balance
}

struct TCreditRequest{
    1: required TClient tClient,
    2: required string currency,
    3: required i32 money,
    4: required i32 days
}

struct TCredit{
    1: required TBank tBank,
    2: required TCurrencyCode tCode,
    3: required i32 money,
    4: required double exchangeRate,
    5: required i32 days,
    6: required double interest
}

service TCreateAccount {
    void addAccount(1:TClient tClient),
    void confirm(1:TBank tBank),
    void decline(1:TBank tBank)
}

service TStandardAccount{
    void requestCheck(1:TClient tClient),
    void replyCheck(1:TCheckMessage tCheckMessage)
}

service TPremiumAccount{
    void checkCurrencies(1:TClient tClient),
    void tellCurrencies(1: TBank tBank, 2:list<TCurrencyCode> tCodes),
    void requestCredit(1:TCreditRequest tCreditRequest),
    void replyCredit(1:TCredit tCredit),
}
