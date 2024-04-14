package labs.lab2_UdpPinger;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UDPPingerClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getLocalHost();
        int serverPort = 12000;

        for (int i = 1; i <= 10; i++) {
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String message = "Ping " + i + " " + time.format(formatter);

            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                clientSocket.setSoTimeout(1000); // Устанавливаем таймаут в 1 секунду
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                long rtt = (LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli() -
                        time.atZone(ZoneOffset.UTC).toInstant().toEpochMilli());

                System.out.println(response);
                System.out.println("RTT: " + rtt + " ms");

            } catch (SocketTimeoutException e) {
                System.out.println("Request timed out");
            }
        }
        clientSocket.close();
    }
}