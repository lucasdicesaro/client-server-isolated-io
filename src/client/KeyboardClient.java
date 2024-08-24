package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

// Client handler for keyboard input
public class KeyboardClient implements Runnable {
    private PrintWriter serverOut;

    public KeyboardClient(PrintWriter serverOut) {
        this.serverOut = serverOut;
    }

    @Override
    public void run() {
        try (BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in))) {

            String inputLine;
            while ((inputLine = keyboardReader.readLine()) != null) {
                serverOut.println(inputLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("KeyboardClient: cerrando ");
    }
}