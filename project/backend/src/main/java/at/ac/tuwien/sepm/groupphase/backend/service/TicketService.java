package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface TicketService {
    /**
     * Method fetches Ticket with given ID number from system and provides it as a TicketDTO.
     *
     * @param id ID number of the Ticket to be fetched
     * @return TicketDTO with the information of the Ticket object fetched
     * @throws ValidationException if ID is not valid
     * @throws NotFoundException if Ticket could not be found in system
     */
    TicketDto get(Long id) throws ValidationException, NotFoundException;

    /**
     * Method fetches all tickets saved in the entire system.
     *
     * @return List of TicketDTOs with information about all tickets saved in system
     */
    List<TicketDto> getAll();

    /**
     * Fetches all tickets belonging to logged in user that have not been bought/ordered yet.
     * Purchase/order is understood as being in a TicketAcquisition.
     *
     * @return List of tickets as described above in TicketDetailsDTO
     */
    List<TicketDetailsDto> getAllDetailedTickets();

    /**
     * Fetches all tickets that are reservations belonging to logged in user that have not been bought/ordered yet.
     * Purchase/order is understood as being in a TicketAcquisition.
     *
     * @return List of tickets as described above in TicketDetailsDTO
     */
    List<TicketDetailsDto> getAllDetailedReservations();

    /**
     * Fetches all tickets that are purchases belonging to logged in user that have not been bought/ordered yet.
     * Purchase/order is understood as being in a TicketAcquisition.
     *
     * @return List of tickets as described above in TicketDetailsDTO
     */
    List<TicketDetailsDto> getAllDetailedPurchases();

    /**
     * Creates a temporary ticket and returns the updated stage plan.
     * */
    ActStagePlanDto claimSeat(TicketDto ticketDto) throws ValidationException, ConflictException;

    /**
     * Method saves a new ticket in the system.
     *
     * @param ticketDto Ticket DTO with information about new ticket to be saved in system
     * @return Ticket DTO with information about ticket just saved in the system
     * @throws ValidationException if validation of ticket did not succeed
     * @throws ConflictException if saving the ticket would cause a conflict in the system
     */
    TicketDto save(TicketDto ticketDto) throws ValidationException, ConflictException;

    /**
     * Method saves a list of new tickets in the system.
     *
     * @param ticket List of Ticket DTOs with information about new tickets to be saved in system
     * @return List of Ticket DTOs with information about tickets just saved in the system
     * @throws ValidationException if validation of any of the tickets did not succeed
     * @throws ConflictException if saving any of the tickets would cause a conflict in the system
     */
    List<TicketDto> save(List<TicketDto> ticket) throws ValidationException, ConflictException;

    /**
     * Method updates an existing ticket in the system.
     *
     * @param ticket Ticket DTO with information about the ticket to be updated
     * @return Ticket DTO with information about the ticket just updated in the system
     * @throws ValidationException if validation of ticket did not succeed
     * @throws ConflictException if saving ticket would cause a conflict in the system
     * @throws NotFoundException if ticket which is to be updated is not found in the system (Search by ID number)
     */
    TicketDto update(TicketDto ticket) throws ValidationException, ConflictException, NotFoundException;

    /**
     * Method updates a list of existing tickets in the system.
     *
     * @param ticket List of Ticket DTOs with information about the tickets to be updated
     * @return List of Ticket DTOs with information about the tickets just updated in the system
     * @throws ValidationException if validation of any of the tickets did not succeed
     * @throws ConflictException if saving any of the tickets would cause a conflict in the system
     * @throws NotFoundException if any of the tickets which is to be updated is not found in the system (Search by ID number)
     */
    List<TicketDto> update(List<TicketDto> ticket) throws ValidationException, ConflictException, NotFoundException;

    /**
     * Deletes a ticket from the persistent data store.
     *
     * @param id id of the to be deleted ticket
     * @throws NotFoundException if the ticket with the provided id could not be found
     */
    void delete(Long id) throws NotFoundException;

    /**
     * Deletes all tickets (based on their status) stored in the shopping cart.
     *
     * @param status status of the ticket
     */
    void deleteAllTicketsInCart(String status) throws ValidationException;

    /**
     * Method removes connection to ticket acquisition in ticket with given ID number in order for it to be displayed in
     * the shopping cart of given user for purchase.
     *
     * @param id ID number of ticket to be moved into shopping cart for purchase
     * @throws NotFoundException if ticket was not found in system
     * @throws ForbiddenException if logged in user does not have the right to manipulate the ticket
     */
    void buyReservedTicket(Long id) throws NotFoundException, ForbiddenException;

    /**
     * Returns the price of the given ticket.
     *
     * @param id id of ticket to find price for
     * @return price of ticket
     * @throws NotFoundException if the ticket does not exist
     * */
    Integer getPrice(Long id) throws NotFoundException;

    /**
     * Find all tickets by ids.
     *
     * @param ticketIds ids of the tickets
     * @return list of tickets
     */
    List<Ticket> getAllTicketsForTicketIds(List<Long> ticketIds);
}
