package labs.lab3_smtp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class client {
    public static void main(String[] args) {
        String hostSMTP = "smtp.yandex.ru";
        Integer port = 465;
        String username = "davic-labs@yandex.ru";
        String password = "zsarvbiadxkabash";

        String toAddress = "daxavic@yandex.ru";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", hostSMTP);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject("lab3_SMTP");
            msg.setText("I love computer networks!");

            Transport.send(msg);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}