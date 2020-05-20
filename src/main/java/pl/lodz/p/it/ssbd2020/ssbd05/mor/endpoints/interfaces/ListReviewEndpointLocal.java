package pl.lodz.p.it.ssbd2020.ssbd05.mor.endpoints.interfaces;

import pl.lodz.p.it.ssbd2020.ssbd05.dto.mor.ReviewDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.AppBaseException;

import javax.ejb.Local;

@Local
public interface ListReviewEndpointLocal {
    ReviewDTO getReviewByReviewNumber(String reviewNumber) throws AppBaseException;
    void removeReview(ReviewDTO reviewDTO) throws AppBaseException;
}