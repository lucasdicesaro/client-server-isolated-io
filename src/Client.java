import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.KeyboardClient;
import common.ConsoleClient;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private static final String LOOPBACK_ADDRESS = "localhost";

    public static void main(String[] args) {
        ExecutorService consoleInputHandlerExecutor = Executors.newFixedThreadPool(2);
        ServerSocket localMiniServerSocket = null;
        Socket mainServerSocket = null;
        try {
            int miniServerPort = nextFreePort();
            localMiniServerSocket = new ServerSocket(miniServerPort);
            mainServerSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(mainServerSocket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(mainServerSocket.getOutputStream(), true);
            System.out.println("Client started.");

            // Start the console output thread as a client
            consoleInputHandlerExecutor.submit(new ConsoleClient(LOOPBACK_ADDRESS, miniServerPort));
            Socket consoleSocket = localMiniServerSocket.accept();
            // We open PrintWriter to write in console.
            PrintWriter consoleOut = new PrintWriter(consoleSocket.getOutputStream(), true);

            // Start the input handling thread as a client
            consoleInputHandlerExecutor.submit(new KeyboardClient(serverOut));

            String tcpMessageFromServer;
            while ((tcpMessageFromServer = serverIn.readLine()) != null) {
                System.out.println("Mensaje del servidor: " + tcpMessageFromServer);
                consoleOut.println(tcpMessageFromServer);
            }

        } catch (IOException e) {
            // e.printStackTrace();
            if (mainServerSocket != null && !mainServerSocket.isClosed()) {
                System.out.println("El servidor se ha desconectado. Saliendo...");
                try {
                    mainServerSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else if (mainServerSocket != null) {
                System.out.println("Socket cerrado - Proceso interrumpido por el cliente. Saliendo...");
            }

            if (!localMiniServerSocket.isClosed()) {
                try {
                    localMiniServerSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

        } finally {
            System.out.println("Main: cerrando");
            consoleInputHandlerExecutor.shutdown();
        }
    }

    public static int nextFreePort() throws IOException {
        try (ServerSocket tempSocket = new ServerSocket(0)) {
            return tempSocket.getLocalPort();
        }
    }
}
