package pl.edu.agh.sr.middleware.bank;

import org.apache.thrift.TException;
import pl.edu.agh.sr.middleware.client.Client;
import pl.edu.agh.sr.middleware.thrift.TClient;
import pl.edu.agh.sr.middleware.thrift.TCreateAccount;

import java.util.List;

public class ClientHandler implements TCreateAccount.Iface {

    private final List<Client> clients;

    public ClientHandler(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public void addAccount(TClient tClient) throws TException {
        Client client = new Client(tClient);
        clients.add(client);

        printMessage(client);
    }

    private void printMessage(Client client) {
        String message = String.format("Klient banku nr: %d %s", clients.size(), client);

        System.out.println(message);
    }


}
