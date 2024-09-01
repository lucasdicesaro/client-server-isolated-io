package common;

public class PackagingUtil {

    public static final String CLIENT_PREFFIX = "C#";
    public static final String KEYBOARD_PREFFIX = "K#";
    public static final String MESSAGE_TO_CLIENTS_COMMAND = "/clients";
    public static final String MESSAGE_TO_CONSOLE_COMMAND = "/console";

    public static String addClientPreffix(String message) {
        return CLIENT_PREFFIX + message;
    }

    public static String addKeyboardPreffix(String message) {
        return KEYBOARD_PREFFIX + message;
    }

    public static boolean isFromKeyboardToClients(String message) {
        return message.startsWith(KEYBOARD_PREFFIX + MESSAGE_TO_CLIENTS_COMMAND);
    }

    public static boolean isFromKeyboardToConsole(String message) {
        return message.startsWith(KEYBOARD_PREFFIX + MESSAGE_TO_CONSOLE_COMMAND);
    }

    public static boolean isFromClientToClients(String message) {
        return message.startsWith(CLIENT_PREFFIX);
    }
}
