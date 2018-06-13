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
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;
import pl.edu.agh.sr.middleware.thrift.TStandardAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static int bankPort;
    private static List<BankTuple> accounts = Lists.newArrayList();
    private final int clientPort;
    private final String firstName;
    private final String lastName;
    private final Pesel PESEL;
    private final BigDecimal income;

    public Client(int clientPort, int inputBankPort, String firstName, String lastName, String pesel, BigDecimal income) {
        if (firstName == null || lastName == null || pesel == null || income == null)
            throw new IllegalArgumentException();

        bankPort = inputBankPort;
        this.clientPort = clientPort;
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = new Pesel(pesel);
        this.income = income;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(Client::handleBank);

        ClientService clientService = new ClientService();
        executorService.submit(clientService);
    }

    public Client(int clientPort, String firstName, String lastName, Pesel PESEL, BigDecimal income) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESEL = PESEL;
        this.income = income;
        this.clientPort = clientPort;
    }

    public Client(TClient tClient) {
        this.firstName = tClient.firstName;
        this.lastName = tClient.lastName;
        this.PESEL = new Pesel(tClient.pesel);
        this.income = new BigDecimal(tClient.income);
        this.clientPort = tClient.port;
    }

    public static void handleBank() {
        TCreateAccount.Processor<BankHandler> processor1 = new TCreateAccount.Processor<>(new BankHandler(accounts));
        TStandardAccount.Processor<BankHandler> processor2 = new TStandardAccount.Processor<>(new BankHandler(accounts));

        try {
            TServerTransport serverTransport = new TServerSocket(9001);
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            TMultiplexedProcessor multiplex = new TMultiplexedProcessor();
            multiplex.registerProcessor("S1", processor1);
            multiplex.registerProcessor("S2", processor2);

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

    public Pesel getPESEL() {
        return PESEL;
    }

    private void clientAction(String operation) throws TException {
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

        TClient tClient = new TClient(firstName, lastName, PESEL.toString(), income.toPlainString(), clientPort);

        switch (operation.toLowerCase()) {
            case "register":
                account.addAccount(tClient);
                break;
            case "check":
                check.requestCheck(tClient);
                break;
            case "credit":
                break;
            default:
                break;
        }


        socket.close();
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
