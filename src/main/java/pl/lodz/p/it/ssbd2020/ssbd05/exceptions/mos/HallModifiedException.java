package pl.lodz.p.it.ssbd2020.ssbd05.exceptions.mos;

import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.AppBaseException;

public class HallModifiedException extends AppBaseException {
    public static final String HALL_MODIFIED = "error.hall.not.active";

    public HallModifiedException() {
        super(HALL_MODIFIED);
    }

}