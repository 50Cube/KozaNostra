package pl.lodz.p.it.ssbd2020.ssbd05.mok.controllers.authentication;

import lombok.Data;
import pl.lodz.p.it.ssbd2020.ssbd05.mok.entities.*;
import pl.lodz.p.it.ssbd2020.ssbd05.mok.facades.AccessLevelFacade;
import pl.lodz.p.it.ssbd2020.ssbd05.mok.facades.AccountFacade;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Collection;

@Named
@Data
@RequestScoped
public class RegistrationController implements Serializable {

    @Inject
    private AccountFacade accountFacade;
    @Inject
    private AccessLevelFacade accessLevelFacade;
    private String login;
    private String password;
    private String confirmPassword;
    private String firstname;
    private String lastname;
    private String emailAddress;


    public void register() throws IOException {
        String gRecaptchaResponse = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("g-recaptcha-response");
        boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
        if (verify)
            if (!checkIfEmailAlreadyExists(this.emailAddress)) {
                if (!checkIfLoginAlreadyExists(this.login)) {
                    if (password.equals(confirmPassword)) {
                        Account account = new Account();
                        account.setLogin(this.getLogin());
                        account.setPassword(sha256(password));
                        account.setFirstname(this.getFirstname());
                        account.setLastname(this.getLastname());
                        account.setEmail(this.getEmailAddress());
                        account.setActive(true);
                        this.getAccountFacade().create(account);
                        account.getAccessLevelCollection().addAll(generateAccessLevels(account));
                        clear();
                    }
                }
            }
    }

    public boolean checkIfLoginAlreadyExists(String username) {
        Collection<Account> accounts = this.accountFacade.getAllAccounts();
        for (Account account : accounts) {
            if (account.getLogin().equals(username)) return true;
        }
        return false;
    }

    public boolean checkIfEmailAlreadyExists(String emailAddress) {
        Collection<Account> accounts = this.accountFacade.getAllAccounts();
        for (Account account : accounts) {
            if (account.getEmail().equals(emailAddress)) return true;
        }
        return false;
    }

    Collection<AccessLevel> generateAccessLevels(Account account) {
        Collection<AccessLevel> accessLevels = new ArrayDeque<>();
        Client client = new Client();
        client.setAccountId(account);
        client.setAccessLevel("CLIENT");
        client.setActive(true);
        accessLevelFacade.create(client);

        Manager manager = new Manager();
        manager.setAccountId(account);
        manager.setAccessLevel("MANAGER");
        manager.setActive(false);
        accessLevelFacade.create(manager);

        Admin admin = new Admin();
        admin.setAccountId(account);
        admin.setAccessLevel("ADMIN");
        admin.setActive(false);
        accessLevelFacade.create(admin);

        accessLevels.add(client);
        accessLevels.add(manager);
        accessLevels.add(admin);
        return accessLevels;
    }

    private String sha256(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = new byte[0];
        if (digest != null) {
            hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void clear() {
        this.setLogin("");
        this.setPassword("");
        this.setConfirmPassword("");
        this.setEmailAddress("");
        this.setFirstname("");
        this.setLastname("");
    }
}