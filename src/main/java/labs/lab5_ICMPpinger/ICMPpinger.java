package labs.lab5_ICMPpinger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class ICMPpinger {
    public static void main(String[] args) {
        String host = "localhost";
        int timeout = 1000;
        int count = 4;

        try {
            InetAddress ip = InetAddress.getByName(host);
            ArrayList<Long> rttList = new ArrayList<>();
            int lostCount = 0;

            System.out.println("Обмен пакетами с " + host + ":");

            for (int i = 0; i < count; i++) {
                long startTime = System.currentTimeMillis();

                try {
                    if (ip.isReachable(timeout)) {
                        long rtt = System.currentTimeMillis() - startTime;
                        rttList.add(rtt);
                        System.out.println("Ответ от " + ip + ":  время=" + rtt + "мс");
                    } else {
                        System.out.println("PING: сбой передачи. Общий сбой.");
                        lostCount++;
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }

                try {
                    Thread.sleep(1000); // Ждем 1 секунду перед отправкой следующего пакета
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!rttList.isEmpty()) {
                long minRTT = rttList.stream().min(Long::compareTo).orElse(0L);
                long maxRTT = rttList.stream().max(Long::compareTo).orElse(0L);
                long sumRTT = rttList.stream().mapToLong(Long::longValue).sum();
                double avgRTT = (double) sumRTT / rttList.size();
                double lossPercentage = (double) lostCount / count * 100;

                System.out.println("\nСтатистика Ping для " + host + ":");
                System.out.println("\tПакетов отправлено = " + count + ", получено =  " + (count - lostCount) +
                        ", потеряно = " + lossPercentage + "%");
                System.out.println("Приблизительное время приема-передачи в мс:");
                System.out.println("\tМинимальное = " + minRTT + "мсек, Максимальное = " + maxRTT +
                        "мсек, Среднее = " + avgRTT + "мсек");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}