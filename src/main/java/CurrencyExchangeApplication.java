import model.currencies.CurrencyUpdater;
import model.currencies.CurrencyExchange;

public class CurrencyExchangeApplication {

    public static void main(String[] args) {
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.initCurrencyExchange();

        Thread thread = new Thread(new CurrencyUpdater(currencyExchange));
        thread.start();
    }
}
