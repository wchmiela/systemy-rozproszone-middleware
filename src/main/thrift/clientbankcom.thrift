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

//struct Work {
//  1: i32 num1 = 0,
//  2: i32 num2,
//  3: OperationType op,
//  4: optional string comment,
//}
//
///**
// * Structs can also be exceptions, if they are nasty.
// */
//exception InvalidOperation {
//  1: i32 whatOp,
//  2: string why
//}
//
//exception InvalidArguments {
//  1: i32 argNo,
//  2: string reason
//}
//
//
//
//exception PermissionViolation {};
//exception UserDoesNotExist {};
exception NotSupportedCurrency {
  1: TCurrencyCode code,
  2: string reason
}

struct TClient  {
    1: required string firstName,
    2: required string lastName,
    3: required string pesel,
    4: required string income
    5: i32 port;
}

struct TBank  {
    1: required string bankName,
    2: required TAccountType type
}


//service Calculator {
//   i32 add(1:i32 num1, 2:i32 num2),
//   i32 subtract(1:i32 num1, 2:i32 num2),
//}

service TCreateAccount {
    void addAccount(1:TClient tClient)
    void confirm(1:TBank tBank)
}

service TStandardAccount{


}

service TPremiumAccount{

}
