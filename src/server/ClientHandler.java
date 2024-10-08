package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import common.PackagingUtil;

// Handler for clients
public class ClientHandler implements Runnable {

    private static final int KEYBOARD_ID = 1;
    private static final int CONSOLE_ID = 2;

    private static Set<server.Client> clients = new HashSet<>();
    private static final AtomicInteger clientIdCounter = new AtomicInteger(1);

    private Socket socket;
    private int clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            clientId = clientIdCounter.getAndIncrement();

            synchronized (clients) {
                clients.add(new server.Client(clientId, "", socket, -1, out));
            }

            String message;
            while ((message = in.readLine()) != null) {
                if (PackagingUtil.isFromKeyboardToClients(message)) {
                    // Forward keyboard message to all connected clients
                    System.out.println("Received from Keyboard to all client: " + message);
                    forwardToAllClients(message);
                } else if (PackagingUtil.isFromKeyboardToConsole(message)) {
                    // Forward keyboard message to console
                    System.out.println("Received from Keyboard to console: " + message);
                    forwardToConsole(message);
                } else if (PackagingUtil.isFromClientToClients(message)) {
                    // Forward received message from client to all connected clients
                    System.out.println("Received from Client to all client: " + message);
                    forwardToAllClients(message);
                }
            }

        } catch (IOException e) {
            System.err.println("ClientHandler: " + e.getMessage());
        } finally {
            System.out.println("ClientHandler: cerrando ");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (clients) {
                clients.removeIf(c -> c.getId() == clientId);
                // Se informa al resto de los clientes que clientId se ha desconectado
                // TODO
            }
        }
    }

    private void forwardToAllClients(String message) {
        System.out.println("Enviando a todos [" + message + "]");
        for (server.Client client : clients) {
            if (client.getId() != KEYBOARD_ID && client.getId() != CONSOLE_ID) {
                System.out.println("Enviando a [" + client.getId() + "] [" + message + "]");
                client.getWriter().println(message);
            }
        }
    }

    private void forwardToConsole(String message) {
        System.out.println("Enviando a console [" + message + "]");
        for (server.Client client : clients) {
            if (client.getId() == CONSOLE_ID) {
                System.out.println("Enviando a [" + client.getId() + "] [" + message + "]");
                client.getWriter().println(message);
                return;
            }
        }
    }
}
