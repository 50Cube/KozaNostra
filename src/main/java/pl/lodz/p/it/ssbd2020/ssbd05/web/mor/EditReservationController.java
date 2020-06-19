package pl.lodz.p.it.ssbd2020.ssbd05.web.mor;


import lombok.Data;
import lombok.extern.java.Log;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mor.ExtraServiceDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mor.ReservationDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mor.UnavailableDate;
import pl.lodz.p.it.ssbd2020.ssbd05.dto.mos.HallDTO;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.ValidationException;
import pl.lodz.p.it.ssbd2020.ssbd05.exceptions.io.database.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.ssbd05.mor.endpoints.interfaces.EditReservationEndpointLocal;
import pl.lodz.p.it.ssbd2020.ssbd05.utils.DateFormatter;
import pl.lodz.p.it.ssbd2020.ssbd05.utils.ResourceBundles;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log
@Named
@ViewScoped
@Data
public class EditReservationController implements Serializable {

    @Inject
    private EditReservationEndpointLocal editReservationEndpointLocal;

    private List<UnavailableDate> unavailableDates;

    private ReservationDTO reservationDTO;

    private List<String> eventTypes;

    private List<ExtraServiceDTO> extraServices;

    private List<String> extraServicesNames;

    private List<String> selectedExtraServices;

    private HallDTO hallDTO;

    private String eventTypeName;

    private LocalDateTime today;

    private LocalDateTime startDate;

    private LocalDateTime endDate;



    @PostConstruct
    public void init() {
        try {
            reservationDTO = editReservationEndpointLocal.getReservationByNumber(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedReservationNumber").toString());
            log.severe("herb1");
            hallDTO = editReservationEndpointLocal.getHallByName(reservationDTO.getHallName());
            log.severe("herb2");
            extraServices = editReservationEndpointLocal.getAllExtraServices();
            log.severe("herb3");
            selectedExtraServices = new ArrayList<>(reservationDTO.getExtraServiceCollection());
            log.severe("herb4");
            eventTypeName = reservationDTO.getEventTypeName();
            log.severe("herb5");
            unavailableDates = editReservationEndpointLocal.getUnavailableDates(hallDTO.getName());
            log.severe("herb6");
            extraServicesNames = new ArrayList<>();
            log.severe("herb7");
            for (ExtraServiceDTO extraServiceDTO : extraServices) {
                extraServicesNames.add(extraServiceDTO.getServiceName());
            }
            log.severe("herb8");
            eventTypes = (List<String>) hallDTO.getEvent_type();
            log.severe("herb9");
        } catch (AppBaseException appBaseException) {
            appBaseException.printStackTrace();
        }
        eventModel = new DefaultScheduleModel();
        log.severe("herb10");
        today = LocalDateTime.now();
        log.severe("herb11");
        startDate = DateFormatter.stringToLocalDateTime(reservationDTO.getStartDate());
        log.severe("herb12");
        endDate = DateFormatter.stringToLocalDateTime(reservationDTO.getEndDate());
        log.severe("herb13");


        for (UnavailableDate unavailableDate : unavailableDates) {
            log.severe("herb14");
                if(!reservationDTO.getStartDate().equals(DateFormatter.formatDate(unavailableDate.getStartDate()))) {
                    log.severe("herb15");
                    DefaultScheduleEvent event = DefaultScheduleEvent.builder().editable(false)
                            .title(ResourceBundles.getTranslatedText("page.editreservation.title"))
                            .startDate(unavailableDate.getStartDate())
                            .endDate(unavailableDate.getEndDate()).overlapAllowed(false)
                            .build();
                    log.severe("herb16");
                    eventModel.addEvent(event);
                    log.severe("herb17");
                }
        }

    }

    @RolesAllowed("editReservationClient")
    public void editReservation() {

        reservationDTO.setEventTypeName(eventTypeName);
        reservationDTO.setExtraServiceCollection(selectedExtraServices);
        log.severe(startDate+ " herb1" + endDate  );
        reservationDTO.setStartDate(DateFormatter.formatDate(startDate));
        reservationDTO.setEndDate(DateFormatter.formatDate(endDate));
        boolean notGood = false;
        if (startDate.isAfter(endDate) || endDate.isBefore(startDate)) {
            ResourceBundles.emitErrorMessage(null, "page.editreservation.dates.error");
            notGood = true;
        } else {
            for (ScheduleEvent ev : eventModel.getEvents()) {
                if (ev.getEndDate().isAfter(startDate) && ev.getStartDate().isBefore(endDate)) {
                    ResourceBundles.emitErrorMessageWithFlash(null, "error.editreservation.dates.overlap");
                    notGood = true;
                }
            }
        }
        if (!notGood) {
            try {
                editReservationEndpointLocal.editReservation(reservationDTO);
                ResourceBundles.emitMessageWithFlash(null, "page.client.editreservation.success");
            } catch (AppOptimisticLockException ex) {
                log.severe(ex.getMessage() + ", " + LocalDateTime.now());
                ResourceBundles.emitErrorMessageWithFlash(null, "error.client.editreservation.optimisticlock");
            } catch (ValidationException e) {
                ResourceBundles.emitErrorMessageByPlainText(null, e.getMessage());
                log.severe(e.getMessage() + ", " + LocalDateTime.now());
            } catch (AppBaseException appBaseException) {
                ResourceBundles.emitErrorMessageWithFlash(null, "error.default");
                log.severe(appBaseException.getMessage() + ", " + LocalDateTime.now());

            }
        }
    }



    //Do kalendarza i wyświetlania okienek czasowych
    //start
    private ScheduleModel scheduleModel;

    private ScheduleModel eventModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private boolean datesRenderd = true;


    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject()).endDate(selectEvent.getObject()).overlapAllowed(false).editable(false).build();
        startDate=event.getStartDate();
        endDate=event.getEndDate();
    }

    public void addEvent(){
        if(event.getId() == null)
            eventModel.addEvent(event);
        else
            eventModel.updateEvent(event);

        event = new DefaultScheduleEvent();
        startDate = event.getStartDate();
        endDate = event.getEndDate();

    }

    public String translateExtraService(String eventTypeName) {
        return ResourceBundles.getTranslatedText(eventTypeName);
    }

    public String goBack(){
        return "goToDetails";
    }


}