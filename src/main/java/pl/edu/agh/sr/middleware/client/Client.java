package pl.edu.agh.sr.middleware.client;

import com.google.common.collect.Lists;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;
import pl.edu.agh.sr.middleware.thrift.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static int bankPort;
    private static int clientPort;
    private static List<BankTuple> accounts = Lists.newArrayList();
    private final String firstName;
    private final String lastName;
    private final Pesel PESEL;
    private final BigDecimal income;
    private boolean first = true;

    public Client(int inputClientPort, int inputBankPort, String firstName, String lastName, String pesel, BigDecimal income) {
        if (firstName == null || lastName == null || pesel == null || income == null)
            throw new IllegalArgumentException();

        bankPort = inputBankPort;
        clientPort = inputClientPort;
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = new Pesel(pesel);
        this.income = income;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(Client::handleBank);
    }

    public Client(TClient tClient) {
        this.firstName = tClient.firstName;
        this.lastName = tClient.lastName;
        this.PESEL = new Pesel(tClient.pesel);
        this.income = new BigDecimal(tClient.income);
    }

    public static void handleBank() {
        TCreateAccount.Processor<BankHandler> processor1 = new TCreateAccount.Processor<>(new BankHandler(accounts));
        TStandardAccount.Processor<BankHandler> processor2 = new TStandardAccount.Processor<>(new BankHandler(accounts));
        TPremiumAccount.Processor<BankHandler> processor3 = new TPremiumAccount.Processor<>(new BankHandler(accounts));

        try {
            TServerTransport serverTransport = new TServerSocket(clientPort);
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            TMultiplexedProcessor multiplex = new TMultiplexedProcessor();
            multiplex.registerProcessor("S1", processor1);
            multiplex.registerProcessor("S2", processor2);
            multiplex.registerProcessor("S3", processor3);

            TServer server = new TSimpleServer(new TServer.Args(serverTransport).protocolFactory(protocolFactory).processor(multiplex));
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        try {
            clientAction("register");
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public void check() {
        try {
            clientAction("check");
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public void credit(String currency, String money, String days) {
        try {
            clientAction("credit", currency, money, days);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private void clientAction(String... operations) throws TException {
        TSocket socket = new TSocket("localhost", bankPort);
        TProtocol protocol = new TBinaryProtocol(socket, true, true);

        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        TCreateAccount.Client account = new TCreateAccount.Client(new TMultiplexedProtocol(protocol, "S1"));
        TStandardAccount.Client check = new TStandardAccount.Client(new TMultiplexedProtocol(protocol, "S2"));
        TPremiumAccount.Client credit = new TPremiumAccount.Client(new TMultiplexedProtocol(protocol, "S3"));

        TClient tClient = new TClient(firstName, lastName, PESEL.toString(), income.toPlainString(), clientPort);

        switch (operations[0].toLowerCase()) {
            case "register":
                account.addAccount(tClient);
                break;
            case "check":
                check.requestCheck(tClient);
                break;
            case "credit":
                if (first) {
                    credit.checkCurrencies(tClient);
                    first = false;
                    break;
                } else {
                    TCreditRequest tCreditRequest = new TCreditRequest(tClient, operations[1],
                            Integer.parseInt(operations[2]), Integer.parseInt(operations[3]));
                    credit.requestCredit(tCreditRequest);
                }
                break;
            default:
                break;
        }

        socket.close();
    }

    public Pesel getPESEL() {
        return PESEL;
    }

    public BigDecimal getIncome() {
        return income;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        return PESEL.equals(client.PESEL);
    }

    @Override
    public int hashCode() {
        return PESEL.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Klient: %s %s PESEL: %s o przychodach %s", firstName, lastName, PESEL, income.toPlainString());
    }


}
