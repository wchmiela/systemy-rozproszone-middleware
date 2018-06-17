package pl.edu.agh.sr.middleware.bank;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import pl.edu.agh.sr.middleware.client.AccountType;
import pl.edu.agh.sr.middleware.client.Client;
import pl.edu.agh.sr.middleware.client.Pesel;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.thrift.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class ClientHandler implements TCreateAccount.Iface, TStandardAccount.Iface, TPremiumAccount.Iface {

    private final List<ClientTuple> clients;
    private final List<Currency> currencies;
    private final String bankName;
    private final BigDecimal premiumLimit;

    public ClientHandler(List<ClientTuple> clients, List<Currency> currencies, String bankName, BigDecimal premiumLimit) {
        this.clients = clients;
        this.currencies = currencies;
        this.bankName = bankName;
        this.premiumLimit = premiumLimit;
    }

    @Override
    public void addAccount(TClient tClient) {
        TSocket socket = new TSocket("localhost", tClient.getPort());
        TProtocol protocol = new TBinaryProtocol(socket, true, true);

        TCreateAccount.Client account = new TCreateAccount.Client(new TMultiplexedProtocol(protocol, "S1"));

        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        Client client = new Client(tClient);
        AccountType accountType = client.getIncome().compareTo(premiumLimit) > 0
                ? AccountType.PREMIUM : AccountType.STANDARD;

        ClientTuple clientTuple = new ClientTuple(client, accountType, tClient.getPort());

        TBank tBank = new TBank(bankName, AccountType.toTAccountType(accountType));

        try {
            if (clients.contains(clientTuple)) {
                account.decline(tBank);
            } else {
                clients.add(new ClientTuple(client, accountType, tClient.getPort()));
                account.confirm(tBank);
                printRegisterMessage(client, accountType);
            }
        } catch (TException e) {
            e.printStackTrace();
        }

        socket.close();
    }

    @Override
    public void requestCheck(TClient tClient) throws TException {

        TSocket socket = new TSocket("localhost", tClient.getPort());
        TProtocol protocol = new TBinaryProtocol(socket, true, true);

        TStandardAccount.Client check = new TStandardAccount.Client(new TMultiplexedProtocol(protocol, "S2"));

        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        Optional<ClientTuple> clientTuple = getClientByPesel(new Pesel(tClient.getPesel()));
        if (clientTuple.isPresent()) {
            TBank tBank = new TBank(bankName, AccountType.toTAccountType(clientTuple.get().getAccountType()));
            BigDecimal currentBalance = new BigDecimal(tClient.getIncome()).subtract(clientTuple.get().getCredit());
            TCheckMessage tCheckMessage = new TCheckMessage(tBank, currentBalance.toPlainString());
            check.replyCheck(tCheckMessage);

            printCheckMessage(new Client(tClient), currentBalance);
        }

        socket.close();
    }

    @Override
    public void checkCurrencies(TClient tClient) throws TException {
        TSocket socket = new TSocket("localhost", tClient.getPort());
        TProtocol protocol = new TBinaryProtocol(socket, true, true);

        TPremiumAccount.Client credit = new TPremiumAccount.Client(new TMultiplexedProtocol(protocol, "S3"));
        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        Optional<ClientTuple> clientTuple = getClientByPesel(new Pesel(tClient.getPesel()));
        if (clientTuple.isPresent()) {
            TBank tBank = new TBank(bankName, AccountType.toTAccountType(clientTuple.get().getAccountType()));

            List<TCurrencyCode> codes = currencies.stream()
                    .map(Currency::getCode2)
                    .map(currencyCode -> TCurrencyCode.findByValue(currencyCode.getNumber()))
                    .collect(Collectors.toList());

            credit.tellCurrencies(tBank, codes);

            printSupportedCurrencies();
        }

        socket.close();
    }

    @Override
    public void requestCredit(TCreditRequest tCreditRequest) throws TException {
        TSocket socket = new TSocket("localhost", tCreditRequest.getTClient().getPort());
        TProtocol protocol = new TBinaryProtocol(socket, true, true);

        TPremiumAccount.Client credit = new TPremiumAccount.Client(new TMultiplexedProtocol(protocol, "S3"));
        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        Optional<ClientTuple> clientTuple = getClientByPesel(new Pesel(tCreditRequest.getTClient().getPesel()));
        Optional<Currency> currency = getCurrencyByCode(tCreditRequest.getCurrency());
        if (clientTuple.isPresent() && currency.isPresent()) {
            TBank tBank = new TBank(bankName, AccountType.toTAccountType(clientTuple.get().getAccountType()));

            Double exchangeRate = currency.get().getValue();

            TCredit tCredit = new TCredit(tBank,
                    TCurrencyCode.findByValue(currency.get().getCode2().getNumber()),
                    tCreditRequest.getMoney(), exchangeRate, tCreditRequest.getDays(), generateInterest());

            credit.replyCredit(tCredit);

            clientTuple.get().addCredit(BigDecimal.valueOf(tCredit.getMoney() * tCredit.getExchangeRate() * tCredit.getInterest()));

            printCreditConfirmation(tCredit, clientTuple.get().getPesel());
        }

        socket.close();
    }

    private double generateInterest() {
        int a = 101;
        int b = 300;
        int randomInt = new Random().nextInt(b - a) + a;

        return (double) randomInt / (double) 100;
    }

    private void printCreditConfirmation(TCredit tCredit, Pesel pesel) {
        String message = String.format("Udzielono kredyt klientowi o numerze PESEL: %s w wysokosci: %d w walucie %s. Wartosc w %s: %.4f. Okres: %d dni.",
                pesel,
                tCredit.getMoney(),
                tCredit.getTCode().name(),
                TCurrencyCode.PLN.name(),
                (double) tCredit.getMoney() * tCredit.getExchangeRate() * tCredit.getInterest(),
                tCredit.getDays());

        System.out.println(message);
    }

    private void printSupportedCurrencies() {
        String message = String.format("Wspierane waluty przez bank %s: %s",
                bankName, currencies.stream().map(Currency::getCode2).collect(Collectors.toList()));

        System.out.println(message);
    }

    private void printCheckMessage(Client client, BigDecimal currentBalance) {
        String message = String.format("Stan konta klienta o numerze PESEL %s: %s",
                client.getPESEL(), currentBalance.toPlainString());

        System.out.println(message);
    }

    private void printRegisterMessage(Client client, AccountType accountType) {
        String message = String.format("Nowy klient banku nr: %d %s. Typ konta: %s",
                clients.size(), client, accountType);

        System.out.println(message);
    }

    private Optional<ClientTuple> getClientByPesel(Pesel pesel) {
        return clients.stream().filter(clientTuple -> clientTuple.getPesel().equals(pesel)).findFirst();
    }

    private Optional<Currency> getCurrencyByCode(String currencyCode) {
        return currencies.stream().filter(c -> c.getCode2().name().equals(currencyCode)).findFirst();
    }

    @Override
    public void replyCheck(TCheckMessage tCheckMessage) {
        //should not be implemented
    }

    @Override
    public void confirm(TBank tBank) {
        //should not be implemented
    }

    @Override
    public void decline(TBank tBank) {
        //should not be implemented
    }

    @Override
    public void tellCurrencies(TBank tbank, List<TCurrencyCode> tcodes) {
        //should not be implemented
    }

    @Override
    public void replyCredit(TCredit tCretit) {
        //should not be implemented
    }
}
