package model.currencies;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Currency {

    private final CurrencyCode first;
    private final CurrencyCode second;
    private BigDecimal value;

    public Currency(CurrencyCode first, CurrencyCode second, BigDecimal value) {
        this.first = first;
        this.second = second;
        this.value = value;
    }

    public void update(BigDecimal newValue) {
        System.out.println("Zaktualizowano " + this + "z " + this.value + " na " + newValue);

        this.value = newValue;
    }

    public void update(double change) {
        BigDecimal newValue = this.value.multiply(BigDecimal.valueOf(change))
                .setScale(2, RoundingMode.HALF_UP);

        System.out.println("Zaktualizowano " + this + " z " + this.value + " na " + newValue);

        this.value = newValue;
    }

    @Override
    public String toString() {
        return first + "/" + second;
    }

    public BigDecimal getValue() {
        return value;
    }

    public CurrencyCode getFirst() {
        return first;
    }

    public CurrencyCode getSecond() {
        return second;
    }
}
