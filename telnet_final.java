import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class telnet_final {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public static void request() {

        System.out.println("enter the machine ip you want to connect to: ");
        String mip = System.console().readLine();

        try {

            Socket s = new Socket(mip, 50000);
            System.out.println("connected to " + mip + " on port 50000");
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            out.writeObject(true);
            boolean auth = (boolean) in.readObject();
            if (auth) {
                System.out.println("Authentification successfull");
            } else {
                System.out.println("Authentification failed");
            }

            clearConsole();
            String os = (String) in.readObject();
            ArrayList<String> commands = (ArrayList<String>) in.readObject();
            System.out.println("Commands you execute : ");
            for (String command : commands) {
                System.out.println(command);
            }
            while (true) {

                System.out.println("Enter a command > ");
                String command = System.console().readLine();
                out.writeObject(command);
                ArrayList<String> result = (ArrayList<String>) in.readObject();
                for (String string : result) {
                    System.out.println(string);
                }
                System.out.println();

            }

        } catch (Exception e) {

        }

    }

    public static void receive() {

        try {
            ServerSocket ss = new ServerSocket(50000);
            while (true) {
                Socket s = ss.accept();
                Thread t = new Thread(new handleClient(s));
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

}

class handleClient implements Runnable {

    Socket s;

    handleClient(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            boolean auth = (boolean) in.readObject();
            if (auth) {

                out.writeObject(true);
                System.out.println("Authentification successfull");
            } else {
                out.writeObject(false);
                System.out.println("Authentification failed");
            }

            out.writeObject(detectOS());
            out.writeObject(OSCommands());

            while (true) {
                
                String command = (String) in.readObject();

            }

        } catch (Exception e) {


        }

    }

    public static String detectOS() {
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
    

    public static ArrayList<String> OSCommands() {

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commands_os;
    }

    public static   ArrayList<String> executeCommand(String command) {
        ArrayList<String> output = new ArrayList<String>();
        String[] commands ={"cmd" , "/c"  , command};
        try {
            Process process = new ProcessBuilder(commands).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }

    

}