package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Client handler for console output
public class ConsoleOutputClient implements Runnable {
    private String serverAddress;
    private int port;

    public ConsoleOutputClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from server: " + message);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("ConsoleOutputClient: cerrando ");
            try {
                socket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}