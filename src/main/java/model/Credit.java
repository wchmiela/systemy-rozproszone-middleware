package model;

import model.currencies.Currency;
import model.currencies.CurrencyCode;

import java.math.BigDecimal;

public class Credit {

    private final CurrencyCode currencyCode;
    private final BigDecimal credit;

    public Credit(CurrencyCode currencyCode, BigDecimal credit) {
        this.currencyCode = currencyCode;
        this.credit = credit;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public BigDecimal getCredit(Currency currency) {
        if (currencyCode == currency.getSecond()) {
            return credit.multiply(currency.getValue());
        }
        return getCredit();
    }
}
