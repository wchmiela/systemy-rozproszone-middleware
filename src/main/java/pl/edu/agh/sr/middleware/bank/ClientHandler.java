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
import pl.edu.agh.sr.middleware.thrift.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ClientHandler implements TCreateAccount.Iface, TStandardAccount.Iface {

    private final List<ClientTuple> clients;
    private final String bankName;
    private final BigDecimal premiumLimit;

    public ClientHandler(List<ClientTuple> clients, String bankName, BigDecimal premiumLimit) {
        this.clients = clients;
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
            TCheckMessage tCheckMessage = new TCheckMessage(tBank, tClient.getIncome());
            check.replyCheck(tCheckMessage);

            printCheckMessage(new Client(tClient));
        }

        socket.close();
    }

    private void printCheckMessage(Client client) {
        String message = String.format("Stan konta klienta o numerze PESEL %s: %s",
                client.getPESEL(), client.getIncome().toPlainString());

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

    @Override
    public void replyCheck(TCheckMessage tCheckMessage) throws TException {
        //should not be implemented
    }

    @Override
    public void confirm(TBank tBank) throws TException {
        //should not be implemented
    }

    @Override
    public void decline(TBank tBank) throws TException {
        //should not be implemented
    }
}
