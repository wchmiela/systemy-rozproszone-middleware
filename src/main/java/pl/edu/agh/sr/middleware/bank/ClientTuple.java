package pl.edu.agh.sr.middleware.bank;

import pl.edu.agh.sr.middleware.client.AccountType;
import pl.edu.agh.sr.middleware.client.Client;
import pl.edu.agh.sr.middleware.client.Pesel;

public class ClientTuple {
    private final Client client;
    private final AccountType accountType;
    private final int port;

    public ClientTuple(Client client, AccountType accountType, int port) {
        this.port = port;
        this.client = client;
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientTuple)) return false;

        ClientTuple that = (ClientTuple) o;

        if (port != that.port) return false;
        if (!client.equals(that.client)) return false;
        return accountType == that.accountType;
    }

    @Override
    public int hashCode() {
        int result = client.hashCode();
        result = 31 * result + accountType.hashCode();
        result = 31 * result + port;
        return result;
    }

    public Pesel getPesel() {
        return client.getPESEL();
    }
}
