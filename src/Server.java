import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.ClientHandler;
import server.ConsoleOutputClient;
import server.InputClient;

public class Server {

    private static final int SERVER_PORT = 12345;
    private static final String LOOPBACK_ADDRESS = "localhost";
    private static final int MY_PORT = 12346;

    public static void main(String[] args) {
        ExecutorService clientHandlerExecutor = Executors.newFixedThreadPool(10);
        ExecutorService consoleOutputExecutor = Executors.newSingleThreadExecutor();
        ExecutorService inputHandlerExecutor = Executors.newSingleThreadExecutor();
        ServerSocket serverSocket = null;
        ServerSocket miniServerSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            miniServerSocket = new ServerSocket(MY_PORT);
            serverSocket.setReuseAddress(true); // Enable reuse of address

            System.out.println("Server started.");

            // Start the input handling thread as a client
            inputHandlerExecutor.submit(new InputClient(LOOPBACK_ADDRESS, MY_PORT));
            Socket inputSocket = miniServerSocket.accept();
            clientHandlerExecutor.submit(new ClientHandler(inputSocket));

            // Start the console output thread as a client
            consoleOutputExecutor.submit(new ConsoleOutputClient(LOOPBACK_ADDRESS, MY_PORT));
            Socket consoleSocket = miniServerSocket.accept();
            clientHandlerExecutor.submit(new ClientHandler(consoleSocket));

            // Accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientHandlerExecutor.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Main: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                System.out.println("El servidor se ha desconectado. Saliendo...");
                try {
                    serverSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else if (serverSocket != null) {
                System.out.println("Socket cerrado - Proceso interrumpido por el cliente. Saliendo...");
            }
            if (miniServerSocket != null && !miniServerSocket.isClosed()) {
                try {
                    miniServerSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            clientHandlerExecutor.shutdown();
            consoleOutputExecutor.shutdown();
            inputHandlerExecutor.shutdown();
            System.out.println("Main: cerrando todo");
        }
    }
}
