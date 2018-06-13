package pl.edu.agh.sr.middleware.bank;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import pl.edu.agh.sr.middleware.client.AccountType;
import pl.edu.agh.sr.middleware.client.Client;
import pl.edu.agh.sr.middleware.thrift.TAccountType;
import pl.edu.agh.sr.middleware.thrift.TBank;
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ClientHandler implements TCreateAccount.Iface {

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
        Client client = new Client(tClient);
        AccountType accountType = client.getIncome().compareTo(premiumLimit) > 0
                ? AccountType.PREMIUM : AccountType.STANDARD;
        clients.add(new ClientTuple(client, accountType, tClient.getPort()));

        printMessage(client, accountType);

        TSocket socket = new TSocket("localhost", tClient.getPort());
        TProtocol protocol = new TBinaryProtocol(socket);

        TCreateAccount.Client account = new TCreateAccount.Client(protocol);

        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        TBank tBank = new TBank(bankName, accountType == AccountType.STANDARD
                ? TAccountType.STANDARD : TAccountType.PREMIUM);
        try {
            account.confirm(tBank);
        } catch (TException e) {
            e.printStackTrace();
        }

        socket.close();
    }

    @Override
    public void confirm(TBank tBank) throws TException {
        //should not be implemented
    }

    private void printMessage(Client client, AccountType accountType) {
        String message = String.format("Nowy klient banku nr: %d %s. Typ konta: %s",
                clients.size(), client, accountType);

        System.out.println(message);
    }
}
