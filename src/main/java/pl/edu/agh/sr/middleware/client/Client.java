package pl.edu.agh.sr.middleware.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import pl.edu.agh.sr.middleware.Account;
import pl.edu.agh.sr.middleware.Pesel;
import pl.edu.agh.sr.middleware.bank.Bank;
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Client {

    private final String firstName;
    private final String lastName;
    private final Pesel PESEL;
    private final BigDecimal income;
    private Map<Account, Bank> accounts;

    public Client(int clientPort, int serverPort, String firstName, String lastName, String pesel, BigDecimal income) {
        if (firstName == null || lastName == null || pesel == null || income == null)
            throw new IllegalArgumentException();

        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = new Pesel(pesel);
        this.income = income;

        this.accounts = new HashMap<>();

        connect(serverPort);
    }

    public Client(String firstName, String lastName, Pesel PESEL, BigDecimal income) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = PESEL;
        this.income = income;
    }

    public Client(TClient tClient) {
        this.firstName = tClient.firstName;
        this.lastName = tClient.lastName;
        this.PESEL = new Pesel(tClient.pesel);
        this.income = new BigDecimal(tClient.income);
    }

    private void connect(int serverPort) {

        TSocket socket = new TSocket("localhost", serverPort);
        TTransport transport = socket;

        TProtocol protocol = new TBinaryProtocol(transport);
        TCreateAccount.Client account = new TCreateAccount.Client(protocol);

        Optional.of(transport).ifPresent(x -> {
            try {
                x.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        TClient tClient = new TClient(firstName, lastName, PESEL.toString(), income.toPlainString());
        try {
            account.addAccount(tClient);
        } catch (TException e) {
            e.printStackTrace();
        }


        transport.close();
    }


    @Override
    public String toString() {
        return String.format("Klient: %s %s PESEL: %s o przychodach %s", firstName, lastName, PESEL, income.toPlainString());
    }
}
