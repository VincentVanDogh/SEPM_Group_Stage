package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanTemplateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;

import java.util.stream.Stream;

public interface StagePlanService {

    /**
     * Find all stage plans ordered by name (ascending).
     *
     * @return stream of all stage plan entries in order
     */
    Stream<StagePlanDto> getAll();

    /**
     * Find a single stage plan by id.
     *
     * @param id the id of the stage plan
     * @return the stage plan
     */
    StagePlanDto getGenericById(Long id);

    /**
     * Find all stage plans templates ordered by name (ascending).
     *
     * @return stream of all stage plan template entries in order
     */
    Stream<StagePlanTemplateDto> getAllTemplates();

    /**
     * Find a single stage plan template by id.
     *
     * @param id the id of the stage plan
     * @return the stage plan template
     */
    StagePlanTemplateDto getTemplateById(Long id);

    /**
     * Find a single stage plan linked to an act by id.
     *
     * @param id the id of the act
     * @return the stage plan linked to an act
     */
    ActStagePlanDto getActSpecificPlanById(Long id);

    /**
     * Find a single stage plan linked to an act by id and a specific user.
     * Only seats bought/reserved by the user will be shown as occupied.
     *
     * @param id the id of the act
     * @param username the id of the act
     * @return the stage plan linked to an act
     */
    ActStagePlanDto getActSpecificPlanByIdAndUser(Long id, String username);

    /**
     * Save the prices for sectors of a given act.
     *
     * @param actStagePlanDto the DTO containing the necessary information
     * */
    void saveActPrices(ActStagePlanDto actStagePlanDto);

    /**
     * Save the new stage.
     *
     * @param createNewStageDto the DTO containing all necessary information
     * @return the stage plan of the new stage
     * */
    StagePlanDto saveStage(CreateNewStageDto createNewStageDto);

    /**
     * Get the row number and the seat number in row for a seated ticket.
     *
     * @param ticketDetailsDto the DTO of the ticket
     * @return int array with row number on index 0 and seat number on index 1
     * */
    int[] getRowAndSeatNumberOfSeatForSeatedTicket(TicketDetailsDto ticketDetailsDto);


    /**
     * Get the sector designation letter for a standing ticket.
     *
     * @param ticketDetailsDto the DTO of the ticket
     * @return the sector designation
     * */
    char getSectorDesignationForStandingTicket(TicketDetailsDto ticketDetailsDto);

}
