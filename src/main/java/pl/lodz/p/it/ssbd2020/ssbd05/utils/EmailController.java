package pl.lodz.p.it.ssbd2020.ssbd05.utils;

import com.sun.mail.smtp.SMTPTransport;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;


public class EmailController {

    private Properties emailProperties;

    public EmailController() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("email.properties");
        emailProperties = new Properties();
        try {
            if(inputStream != null)
                emailProperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRegistrationEmail(String mail, String token, String login)  {
        String subject = "Confirm your account";
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String link = request.getRequestURL()
                .substring(0, (request.getRequestURL().length() - request.getServletPath().length())).concat("/confirmAccount.xhtml?token=");
        String body = "<a href=\"" + link + token + "&login=" + login + "\">Click here to confirm your account</a>";

        this.sendEmail(mail, subject, body);
    }

    private void sendEmail(String mail, String subject, String body) {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", emailProperties.getProperty("host"));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", emailProperties.getProperty("port"));
        prop.put("mail.smtp.ssl.trust", emailProperties.getProperty("host"));
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(body, "UTF-8", "html");
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);

            msg.setFrom(new InternetAddress(emailProperties.getProperty("username")));
            msg.setContent(mimeMultipart);
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mail, false));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(emailProperties.getProperty("host"), emailProperties.getProperty("username"), emailProperties.getProperty("password"));
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
