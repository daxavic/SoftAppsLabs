package labs.lab4_ProxyServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServer {

    private static final ConcurrentHashMap<String, byte[]> myCache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int myPort;
        myPort = Integer.parseInt(args[0]);
        HttpServer myServer = null;
        try {
            myServer = HttpServer.create(new InetSocketAddress(myPort), 0);
        } catch (IOException e) {
            System.out.println("Failed to initiate server on port" + myPort);
            System.exit(1);
        }
        myServer.createContext("/", new MyProxyHandler(myPort));
        myServer.setExecutor(null);
        myServer.start();
        System.out.println("Server initiated on port " + myPort);
    }

    static class MyProxyHandler implements HttpHandler {
        private final int myPort;
        public MyProxyHandler(int myPort) {
            this.myPort = myPort;
        }
        public void handle(HttpExchange t) {
            String myMethod = t.getRequestMethod();

            try {
                String myUrl;
                if (t.getRequestHeaders().get("Referer") != null) {
                    myUrl = t.getRequestHeaders().get("Referer").get(0)
                            .replace("localhost:" + myPort+"/", "") + t.getRequestURI().toString();
                } else {
                    myUrl = "http://" + t.getRequestURI().toString().substring(1);
                }

                System.out.println("Incoming Request: " + myMethod + " " + myUrl);

                if (myCache.containsKey(myUrl)) {
                    System.out.println("Cache hit for URL: " + myUrl);
                    serveFromCache(t, myCache.get(myUrl));
                } else {
                    System.out.println("Cache miss for URL: " + myUrl);
                    serveFromRemoteServer(t, myMethod, myUrl);
                }
            } catch (Exception e) {
                System.out.println("Failed to load resource " + t.getRequestURI());
            }
        }

        private void serveFromCache(HttpExchange t, byte[] myCachedResponse) throws IOException {
            t.sendResponseHeaders(200, myCachedResponse.length);
            t.getResponseBody().write(myCachedResponse);
            t.getResponseBody().close();
        }

        private void serveFromRemoteServer(HttpExchange t, String myMethod, String myUrl) throws IOException {
            OkHttpClient myClient = new OkHttpClient();
            Response myResponse = null;

            try {
                System.out.println("Requesting URL: " + myUrl);

                Request myRequest = null;
                if ("GET".equalsIgnoreCase(myMethod)) {
                    myRequest = new Request.Builder()
                            .url(myUrl)
                            .build();
                } else if ("POST".equalsIgnoreCase(myMethod)) {
                    RequestBody myBody = RequestBody.create(MediaType.parse("text/plain"), t.getRequestBody().readAllBytes());
                    myRequest = new Request.Builder()
                            .url(myUrl)
                            .post(myBody)
                            .build();
                }

                if (myRequest != null) {
                    myResponse = myClient.newCall(myRequest).execute();
                    byte[] myResponseBody = myResponse.body().bytes();
                    myCache.put(myUrl, myResponseBody);

                    HashMap<String, List<String>> myHeaders = new HashMap<>();
                    myHeaders.put("Content-Type", Collections.singletonList(myResponse.header("Content-Type")));
                    myHeaders.put("Content-Length", Collections.singletonList(String.valueOf(myResponseBody.length)));
                    t.getResponseHeaders().putAll(myHeaders);
                    t.sendResponseHeaders(myResponse.code(), myResponseBody.length);

                    t.getResponseBody().write(myResponseBody);
                    t.getResponseBody().close();
                }
            } catch (Exception e) {
                String myErrorPage = "<html><body><h1>Error 404: Not Found</h1></body></html>";
                t.sendResponseHeaders(404, myErrorPage.getBytes().length);
                t.getResponseBody().write(myErrorPage.getBytes());
                t.getResponseBody().close();
            } finally {
                if (myResponse != null) {
                    myResponse.close();
                }
            }
        }
    }
}