package pl.lodz.p.it.ssbd2020.ssbd05.web.mor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mor.ReviewDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.io.database.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.mor.ReviewNotFoundException;
import pl.lodz.p.it.ssbd2020.ssbd05.mor.endpoints.interfaces.EditReviewEndpointLocal;
import pl.lodz.p.it.ssbd2020.ssbd05.utils.DateFormatter;
import pl.lodz.p.it.ssbd2020.ssbd05.utils.ResourceBundles;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;

@Log
@Named
@ViewScoped
public class EditReviewController implements Serializable {
    @Inject
    private EditReviewEndpointLocal editReviewEndpointLocal;

    @Getter
    @Setter
    private ReviewDTO reviewDTO;

    @PostConstruct
    public void init() {
        String selectedReview = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedReview");
        try {
            this.reviewDTO = editReviewEndpointLocal.getReviewByReviewNumber(selectedReview);
        }
        catch (AppOptimisticLockException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessage(null, ex.getMessage());
        } catch (AppBaseException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessage(null, ex.getMessage());
        }
    }
    public void removeReview() {
        try {
            editReviewEndpointLocal.removeReview(reviewDTO);
            ResourceBundles.emitMessageWithFlash(null, "page.reviews.remove.success");
        } catch (AppOptimisticLockException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessageWithFlash(null, "page.reviews.edit.optimisticlock");
        } catch (ReviewNotFoundException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessageWithFlash(null, "page.reviews.remove.failed.notfound");
        } catch (AppBaseException e) {
            ResourceBundles.emitErrorMessageWithFlash(null, "error.default");
            log.severe(e.getMessage() + ", " + LocalDateTime.now());
        }
    }

    public String goBack() {
        return "listReviews";
    }

    public void editReview(){
        try {
            reviewDTO.setDate(DateFormatter.formatDate(LocalDateTime.now()));
            editReviewEndpointLocal.editReview(reviewDTO);
            ResourceBundles.emitMessageWithFlash(null, "page.reviews.remove.success");
        } catch (AppOptimisticLockException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessageWithFlash(null, "page.reviews.edit.optimisticlock");
        } catch (ReviewNotFoundException ex) {
            log.severe(ex.getMessage() + ", " + LocalDateTime.now());
            ResourceBundles.emitErrorMessageWithFlash(null, "page.reviews.remove.failed.notfound");
        } catch (AppBaseException e) {
            ResourceBundles.emitErrorMessageWithFlash(null, "error.default");
            log.severe(e.getMessage() + ", " + LocalDateTime.now());
        }
    }

}
