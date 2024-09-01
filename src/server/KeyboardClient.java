package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.PackagingUtil;

// Client handler for keyboard input
public class KeyboardClient implements Runnable {
    private String serverAddress;
    private int port;
    private Socket socket;

    public KeyboardClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverAddress, port);
            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            showUsage();
            String inputLine;
            while ((inputLine = keyboardReader.readLine()) != null) {
                System.out.println("Recibido teclado: " + inputLine);
                out.println(PackagingUtil.addKeyboardPreffix(inputLine));
                showUsage();
            }

        } catch (IOException e) {
            System.err.println("KeyboardClient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("KeyboardClient: cerrando ");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void showUsage() {
        System.out.print("Opciones:\n" +
                PackagingUtil.MESSAGE_TO_CLIENTS_COMMAND + " <message> - Mensaje a los clientes.\n" +
                PackagingUtil.MESSAGE_TO_CONSOLE_COMMAND + " <message> - Mensaje a la consola.\n" +
                "> ");
    }
}
