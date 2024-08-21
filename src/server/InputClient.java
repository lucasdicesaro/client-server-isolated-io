package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Client handler for keyboard input
public class InputClient implements Runnable {
    private String serverAddress;
    private int port;
    private Socket socket;

    public InputClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverAddress, port);
            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = keyboardReader.readLine()) != null) {
                System.out.println("Recibido teclado: " + inputLine);
                out.println(inputLine);
                System.out.println("Enviado al proceso principal: " + inputLine);
            }

        } catch (IOException e) {
            System.err.println("InputClient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("InputClient: cerrando ");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}