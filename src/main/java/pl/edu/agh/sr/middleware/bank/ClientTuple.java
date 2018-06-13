package pl.edu.agh.sr.middleware.bank;

import pl.edu.agh.sr.middleware.client.AccountType;
import pl.edu.agh.sr.middleware.client.Client;

public class ClientTuple {
    private final Client client;
    private final AccountType accountType;
    private final int port;

    public ClientTuple(Client client, AccountType accountType, int port) {
        this.port = port;
        this.client = client;
        this.accountType = accountType;
    }
}
