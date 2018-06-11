package pl.edu.agh.sr.middleware;

import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;

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
        if (currencyCode == currency.getCode2()) {
            return credit.multiply(BigDecimal.valueOf(currency.getValue()));
        }
        return getCredit();
    }
}
