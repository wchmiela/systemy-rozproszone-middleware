package pl.edu.agh.sr.middleware.client;

import org.apache.thrift.TException;
import pl.edu.agh.sr.middleware.thrift.TBank;
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;

import java.util.List;

public class BankHandler implements TCreateAccount.Iface {


    private final List<BankTuple> accounts;

    public BankHandler(List<BankTuple> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void addAccount(TClient tClient) throws TException {
        //should not be implemented
    }

    @Override
    public void confirm(TBank tBank) throws TException {
        BankTuple bankTuple = new BankTuple(tBank);
        accounts.add(bankTuple);

        printMessage(bankTuple);
    }

    private void printMessage(BankTuple bankTuple) {
        String message = String.format("Gratulujemy! Zalozyles konto w %s. Typ konta: %s", bankTuple.getBankName(), bankTuple.getAccountType());

        System.out.println(message);
    }
}
