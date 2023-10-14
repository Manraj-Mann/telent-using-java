import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class telnet {

    public static void main(String[] args) {

        Thread t1 = new Thread(new telnetServer());
        Thread t2 = new Thread(new telentSender());
        t1.start();
        t2.start();

    }
}

class telnetServer implements Runnable {

    // receive all the user connections and run them in receiver thread

    public void run() {

        try {

            ServerSocket ss = new ServerSocket(50000);
            while (true) {
                Socket s = ss.accept();
                Thread t = new Thread(new telentreceiver(s));
                t.start();

            }

        } catch (Exception e) {

        }

    }
}

class telentreceiver implements Runnable {

    Socket s;

    telentreceiver(Socket s) {
        this.s = s;
    }

    // receive all the commands from the user and run them in receiver thread

    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            // communicating with client
            out.writeObject(os());
            out.writeObject(commands());
            Terminal t = new Terminal();
            while (true) {

                out.writeObject(t.currentDirectory());
                String command = (String) in.readObject();
                if (command.equals("exit")) {
                    out.writeObject("closed");
                    break;
                } else {
                    String result = t.executeCommand(command);
                    out.writeObject(result);
                }
                // ArrayList<String> result = executeCommands(coms);
                // out.writeObject(result);
            }
            s.close();

        } catch (Exception e) {

        }
    }

    public static String os() {
        // return the os of the machine
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("mac")) {
            return "Mac OS";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return "Unix-like OS";
        } else if (os.contains("sunos")) {
            return "Solaris OS";
        } else {
            return "Unknown OS";
        }
    }

    public static ArrayList<String> commands() {
        // return the commands of the machine
        ArrayList<String> commands_os = new ArrayList<String>();
        String os = System.getProperty("os.name").toLowerCase();
        String[] commands;
        if (os.contains("win")) {
            commands = new String[] { "cmd", "/c", "help" };
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")
                || os.contains("sunos")) {
            commands = new String[] { "sh", "-c", "compgen -c" };
        } else {
            System.out.println("Unsupported OS");
            return commands_os;
        }
        try {
            Process process = new ProcessBuilder(commands).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                // System.out.println(line);
                commands_os.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return commands_os;
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception ex) {
            // Exception handling
        }
    }

}

class telentSender implements Runnable {

    // send all the commands to the user and run them in sender thread

    public static void send_commands() {
        // send the commands to the user and run them in sender thread
    }

    public static void print_output(ArrayList<String> output) {
        // Set the ANSI color code for green
        String ANSI_GREEN = "\u001B[32m";
        // Set the ANSI color code for the default console background color
        String ANSI_DEFAULT_BG = "\u001B[49m";

        // Print the output to the user and run them in sender thread
        System.out.println();

        for (String string : output) {
            System.out.println(ANSI_GREEN + string + ANSI_DEFAULT_BG);
        }

        System.out.println();
    }

    public static void connect() {
        // connect the user to the remote user
        try {
            clearConsole();
            System.out.println("Enter the IP address of the machine : ");
            String ipm = System.console().readLine();
            Socket s = new Socket(ipm, 50000);
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            String os = (String) in.readObject();
            System.out.println("Connected to " + os + " machine");
            ArrayList<String> commands = (ArrayList<String>) in.readObject();
            System.out.println("Commands: ");
            print_output(commands);
            String result;
            String command;

            while (true) {
                System.out.println("\033[1m");
                System.out.print("\033[38;5;160m" + "┌──");
                System.out.print("\033[38;5;75m" + "\033[32m" + "[");
                System.out.print("\033[38;5;75m" + (String) in.readObject());
                System.out.print("\033[38;5;75m" + "\033[32m"+ "\033[1m]\033[0m" + "\n");
                System.out.print("\033[38;5;160m" + "└─");
                System.out.print("\033[38;5;75m" + "\033[1m" + "[$] ");
                
                System.out.print("\033[38;5;251m"); // lighter shade of white
                command = System.console().readLine();
                out.writeObject(command);
                result = (String) in.readObject();
                System.out.print("\033[38;5;75m" + "\033[32m" + result + "\n");
                System.out.print("\033[0m");
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception ex) {
            // Exception handling
        }
    }

    @Override
    public void run() {
        connect();
        clearConsole();
    }

}
