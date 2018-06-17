package pl.edu.agh.sr.middleware.bank;

import com.google.common.collect.Lists;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import pl.edu.agh.sr.middleware.commons.CurrencyUtil;
import pl.edu.agh.sr.middleware.proto.Currency;
import pl.edu.agh.sr.middleware.proto.CurrencyCode;
import pl.edu.agh.sr.middleware.proto.grpc.CurrencyServiceGrpc;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;
import pl.edu.agh.sr.middleware.thrift.TPremiumAccount;
import pl.edu.agh.sr.middleware.thrift.TStandardAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Bank extends CurrencyServiceGrpc.CurrencyServiceImplBase {

    private static int bankPort;
    private static String bankName;
    private static BigDecimal premiumLimit;
    private static List<ClientTuple> clients = Lists.newArrayList();
    private static List<Currency> currencies = Lists.newCopyOnWriteArrayList();
    private final int exchangeCurrencyPort;
    private List<CurrencyCode> supportedCurrencies;

    public Bank(int inputBankPort, int exchangeCurrencyPort, String inputBankName, String rawCurrencies, BigDecimal inputPremiumLimit) {
        if (inputBankName == null || rawCurrencies == null || inputPremiumLimit == null)
            throw new IllegalArgumentException();

        bankPort = inputBankPort;
        premiumLimit = inputPremiumLimit;
        bankName = inputBankName;
        this.exchangeCurrencyPort = exchangeCurrencyPort;
        this.supportedCurrencies = Lists.newArrayList();

        Stream.of(rawCurrencies.split(","))
                .forEach(currency -> CurrencyUtil.addCurrency(supportedCurrencies, currency));

        BankService bankService = new BankService(this);

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(bankService);
        executorService.submit(Bank::handleClient);
    }

    public static void handleClient() {
        TCreateAccount.Processor<ClientHandler> processor1 = new TCreateAccount.Processor<>(new ClientHandler(clients, currencies, bankName, premiumLimit));
        TStandardAccount.Processor<ClientHandler> processor2 = new TStandardAccount.Processor<>(new ClientHandler(clients, currencies, bankName, premiumLimit));
        TPremiumAccount.Processor<ClientHandler> processor3 = new TPremiumAccount.Processor<>(new ClientHandler(clients, currencies, bankName, premiumLimit));

        try {
            TServerTransport serverTransport = new TServerSocket(bankPort);
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

    public void updateCurrency(Currency currency) {
        CurrencyCode code = currency.getCode2();

        Optional<Currency> currencyToRemove = getCurrency(code);
        currencyToRemove.ifPresent(currencies::remove);

        currencies.add(currency);
    }

    private Optional<Currency> getCurrency(CurrencyCode code) {
        return currencies.stream().filter(currency -> currency.getCode2().equals(code)).findFirst();
    }

    public List<CurrencyCode> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public int getExchangeCurrencyPort() {
        return exchangeCurrencyPort;
    }

    @Override
    public String toString() {
        return String.format("Bank o nazwie: %s. Wspierajacy waluty: %s. Limit dla konta premium: %s",
                bankName, supportedCurrencies, premiumLimit.toPlainString());
    }
}
