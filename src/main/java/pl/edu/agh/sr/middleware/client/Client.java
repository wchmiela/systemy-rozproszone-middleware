package pl.edu.agh.sr.middleware.client;

import com.google.common.collect.Lists;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;

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

        connect(bankPort);

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
        TCreateAccount.Processor<BankHandler> processor = new TCreateAccount.Processor<>(new BankHandler(accounts));

        try {
            TServerTransport serverTransport = new TServerSocket(9001);
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            TServer server = new TSimpleServer(new TServer.Args(serverTransport).protocolFactory(protocolFactory).processor(processor));
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private void connect(int serverPort) {

        TSocket socket = new TSocket("localhost", serverPort);

        TProtocol protocol = new TBinaryProtocol(socket);
        TCreateAccount.Client account = new TCreateAccount.Client(protocol);

        Optional.of((TTransport) socket).ifPresent(tr -> {
            try {
                tr.open();
            } catch (TTransportException e) {
                System.out.println("transport error " + e.getMessage());
            }
        });

        TClient tClient = new TClient(firstName, lastName, PESEL.toString(), income.toPlainString(), clientPort);
        try {
            account.addAccount(tClient);
        } catch (TException e) {
            e.printStackTrace();
        }

        socket.close();
    }

    public BigDecimal getIncome() {
        return income;
    }

    @Override
    public String toString() {
        return String.format("Klient: %s %s PESEL: %s o przychodach %s", firstName, lastName, PESEL, income.toPlainString());
    }
}
