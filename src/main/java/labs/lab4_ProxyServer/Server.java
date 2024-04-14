package labs.lab4_ProxyServer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) {
        int port = 12000;
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            System.out.println("Не удалось загрузить сервер " + port);
            return;
        }
        server.createContext("/", new Helper(port));
        server.start();
        System.out.println("Сервер загружен на порту " + port);
    }

}