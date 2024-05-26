package mainScripts;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Desktop;
import java.io.File;

public class Client {
    private static final int PORT = 1050;
    private final String server;
    private static final int TIMEOUT = 5000; // 5 seconds timeout
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public Client(String server) {
        this.server = server;
    }

    public boolean connect() {
        try {
            // Attempt to connect to the server
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(server, PORT), TIMEOUT);
            new Client(server).run();
            return true; // Connection successful
        } catch (IOException e) {
            return false; // Connection failed
        }
    }

    public void run() {
        try (Socket client = new Socket()) {
            // Connection successful
            client.connect(new InetSocketAddress(InetAddress.getByName(server), PORT), TIMEOUT);
            logger.info("Connected to server: " + server);
            receiveMessages(client);
            client.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred with connection to " + server, e);
        }
    }

    private void receiveMessages(Socket client) {
        try (BufferedInputStream in = new BufferedInputStream(client.getInputStream())) {
            byte[] buffer = new byte[1024 * 2];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                String msg = new String(buffer, 0, bytesRead, "utf-8");
                System.out.println("Received message from " + server + ": " + msg);
            }
            System.err.println("Connection with server " + server + " closed by the server.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while receiving messages from " + server, e);
        }
    }

    public static void main(String[] args) {
        String[] servers = { "10.206.34.154", "10.206.36.202",
                "192.168.160.125", "10.113.195.139", "10.206.37.143", "10.206.42.221" };

        ArrayList<String> successfulConnections = new ArrayList<>();
        ArrayList<String> unsuccessfulConnections = new ArrayList<>();

        // Try to connect to each server in a separate thread
        for (String server : servers) {
            new Thread(() -> {
                Client client = new Client(server);
                if (client.connect()) {

                    synchronized (successfulConnections) {
                        successfulConnections.add(server);
                        System.out.println("Successful connections: " + successfulConnections);
                    }
                } else {

                    synchronized (unsuccessfulConnections) {
                        unsuccessfulConnections.add(server);
                        System.out.println("Unsuccessful connections: " + unsuccessfulConnections);
                    }
                }

                if (successfulConnections.size() + unsuccessfulConnections.size() == servers.length) {
                    generateHTML(successfulConnections, unsuccessfulConnections);
                    openHTMLFileInBrowser();
                }
            }).start();
        }

        // Schedule the check_connection task to run every three hours
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkConnection(servers);
            }
        }, 0, TimeUnit.HOURS.toMillis(3));
    }

    private static void generateHTML(ArrayList<String> successfulConnections,
            ArrayList<String> unsuccessfulConnections) {
        try (FileWriter fileWriter = new FileWriter("connections.html")) {
            fileWriter.write("<html>\n<head>\n<title>Connection Status</title>\n</head>\n<body>\n");
            fileWriter.write("<h1>Successful Connections</h1>\n<ul>\n");
            for (String connection : successfulConnections) {
                fileWriter.write("<li>" + connection + "</li>\n");
            }
            fileWriter.write("</ul>\n");
            fileWriter.write("<h1>Unsuccessful Connections</h1>\n<ul>\n");
            for (String connection : unsuccessfulConnections) {
                fileWriter.write("<li>" + connection + "</li>\n");
            }
            fileWriter.write("</ul>\n</body>\n</html>");
            System.out.println("HTML file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error occurred while generating HTML: " + e.getMessage());
        }
    }

    private static void openHTMLFileInBrowser() {
        try {
            File file = new File("connections.html");
            if (file.exists()) {
                String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"; // Path to Chrome
                                                                                                  // executable
                Runtime.getRuntime().exec(new String[] { chromePath, file.getAbsolutePath() });
                System.out.println("Opened HTML file in Google Chrome.");
            } else {
                System.err.println("HTML file not found in the specified location.");
            }
        } catch (IOException e) {
            System.err.println("Error occurred while opening HTML file in Google Chrome: " + e.getMessage());
        }
    }

    private static void checkConnection(String[] servers) {
        for (String server : servers) {
            new Thread(() -> {
                Client client = new Client(server);
                if (client.connect()) {
                    System.out.println("Connection to " + server + " is still active.");
                } else {
                    System.out.println("Connection to " + server + " is lost.");
                }
            }).start();
        }
    }
}
x   