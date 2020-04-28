package pl.lodz.p.it.ssbd2020.ssbd05.web.auth;

import lombok.Data;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mok.AccountDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.entities.mok.*;
import pl.lodz.p.it.ssbd2020.ssbd05.mok.endpoints.RegisterAccountEndpoint;
import pl.lodz.p.it.ssbd2020.ssbd05.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2020.ssbd05.utils.EmailController;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@Data
@RequestScoped
public class RegistrationController implements Serializable {

    @Inject
    RegisterAccountEndpoint registerAccountEndpoint;

    private String login;
    private String password;
    private String confirmPassword;
    private String firstname;
    private String lastname;
    private String emailAddress;


    public void register() {
        try {
            if (password.equals(confirmPassword)) {
                AccountDTO account = new AccountDTO();
                account.setLogin(this.getLogin());
                account.setPassword(password);
                account.setFirstname(this.getFirstname());
                account.setLastname(this.getLastname());
                account.setEmail(this.getEmailAddress());
                account.getAccessLevelCollection().add("CLIENT");
                account.setActive(true);
                account.setConfirmed(false);

                this.registerAccountEndpoint.addNewAccount(account);

                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage("Account created !"));
                clear();
            }
        } catch (Exception ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
