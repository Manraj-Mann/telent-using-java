import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    private static String getCommandPrefix() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return "cmd /c ";
        } else {
            return "";
        }
    }

    private static File getCurrentDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public static String executeCommand(String command) {
        String commandPrefix = getCommandPrefix();
        File currentDirectory = getCurrentDirectory();

        // Parse the command string and extract the first word as the command name
        String[] parts = command.trim().split("\\s+", 2);
        String commandName = parts[0];

        // If the command is "cd", change the current directory instead of executing the
        // command
        if (commandName.equals("cd")) {
            String argument = parts.length > 1 ? parts[1] : "";
            changeDirectory(argument);
            return "";
        }

        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(commandPrefix + command, null, currentDirectory);
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = outputReader.readLine()) != null) {
                output.append(line);
                output.append(System.lineSeparator());
            }

            outputReader.close();
        } catch (IOException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }

        return output.toString();
    }

    public static void changeDirectory(String newDirectory) {
        File currentDirectory = getCurrentDirectory();

        if (newDirectory.equals("..")) {
            currentDirectory = currentDirectory.getParentFile();
        } else {
            File file = new File(currentDirectory, newDirectory);
            if (file.isDirectory()) {
                currentDirectory = file;
            } else {
                System.err.println("Not a directory: " + newDirectory);
            }
        }

        System.setProperty("user.dir", currentDirectory.getAbsolutePath());
    }

    public static String currentDirectory() {
        return getCurrentDirectory().getAbsolutePath();
    }
}
