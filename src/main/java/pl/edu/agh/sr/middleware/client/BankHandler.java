package pl.edu.agh.sr.middleware.client;

import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.thrift.*;

import java.util.List;
import java.util.stream.Collectors;

public class BankHandler implements TCreateAccount.Iface, TStandardAccount.Iface, TPremiumAccount.Iface {

    private final List<BankTuple> accounts;

    public BankHandler(List<BankTuple> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void confirm(TBank tBank) {
        BankTuple bankTuple = new BankTuple(tBank);
        accounts.add(bankTuple);

        printConfirmMessage(bankTuple);
    }

    @Override
    public void decline(TBank tBank) {
        BankTuple bankTuple = new BankTuple(tBank);

        printDeclineMessage(bankTuple);
    }

    @Override
    public void replyCheck(TCheckMessage tCheckMessage) {
        String message = String.format("Stan konta w %s: %s", tCheckMessage.getTBank().getBankName(), tCheckMessage.getBalance());

        System.out.println(message);
    }

    @Override
    public void tellCurrencies(TBank tbank, List<TCurrencyCode> tcodes) {
        List<CurrencyCode> codes = tcodes.stream()
                .map(tCurrencyCode -> CurrencyCode.forNumber(tCurrencyCode.getValue()))
                .collect(Collectors.toList());

        String message = String.format("Waluty wspierane przez bank %s: %s", tbank.getBankName(), codes);

        System.out.println(message);
    }

    @Override
    public void replyCredit(TCredit tCredit) {
        String message = String.format("Otrzymano kredyt w wysokosci: %d w walucie %s. Wartosc w %s: %.4f. Okres: %d dni.",
                tCredit.getMoney(),
                tCredit.getTCode().name(),
                TCurrencyCode.PLN.name(),
                (double) tCredit.getMoney() * tCredit.getExchangeRate() * tCredit.getInterest(),
                tCredit.getDays());

        System.out.println(message);
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
    public void requestCredit(TCreditRequest tCreditRequest) {
        //should not be implemented
    }

    @Override
    public void requestCheck(TClient tClient) {
        //should not be implemented
    }

    @Override
    public void addAccount(TClient tClient) {
        //should not be implemented
    }

    @Override
    public void checkCurrencies(TClient tClient) {
        //should not be implemented
    }
}
