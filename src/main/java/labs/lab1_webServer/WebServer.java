package labs.lab1_webServer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
;

public class WebServer {
    public static void main(String[] args) throws IOException {
        java.net.InetAddress ip = InetAddress.getLocalHost();
        System.out.println("\n" + ip.getHostAddress());

        ServerSocket serverSocket = new ServerSocket(6789);
        System.out.println("Готов к обслуживанию...");

        while (true) {
            Socket clientSocket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            String request = in.readLine();
            if (request == null) {
                clientSocket.close();
                continue;
            }
            String filename = request.split(" ")[1];

            File file = new File("src/main/resources/" + filename);
            if (file.exists()) {
                BufferedReader fileReader = new BufferedReader(new FileReader(file));

                out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.write((line + "\n").getBytes());
                }
                fileReader.close();
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                out.write("404 Not Found".getBytes());
            }

            out.flush();
            clientSocket.close();
        }
    }
}