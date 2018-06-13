package pl.edu.agh.sr.middleware.client;

import org.apache.thrift.TException;
import pl.edu.agh.sr.middleware.thrift.*;

import java.util.List;

public class BankHandler implements TCreateAccount.Iface, TStandardAccount.Iface {


    private final List<BankTuple> accounts;

    public BankHandler(List<BankTuple> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void confirm(TBank tBank) throws TException {
        BankTuple bankTuple = new BankTuple(tBank);
        accounts.add(bankTuple);

        printConfirmMessage(bankTuple);
    }

    @Override
    public void decline(TBank tBank) throws TException {
        BankTuple bankTuple = new BankTuple(tBank);

        printDeclineMessage(bankTuple);
    }

    private void printDeclineMessage(BankTuple bankTuple) {
        String message = String.format("Niestety! Posiadasz juz konto w %s. Typ konta: %s", bankTuple.getBankName(), bankTuple.getAccountType());

        System.out.println(message);
    }


    private void printConfirmMessage(BankTuple bankTuple) {
        String message = String.format("Gratulujemy! Zalozyles konto w %s. Typ konta: %s", bankTuple.getBankName(), bankTuple.getAccountType());

        System.out.println(message);
    }

    @Override
    public void replyCheck(TCheckMessage tCheckMessage) throws TException {
        String message = String.format("Stan konta w %s: %s", tCheckMessage.getTBank().getBankName(), tCheckMessage.getBalance());

        System.out.println(message);
    }

    @Override
    public void requestCheck(TClient tClient) throws TException {
        //should not be implemented
    }

    @Override
    public void addAccount(TClient tClient) throws TException {
        //should not be implemented
    }
}
