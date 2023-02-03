package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface TicketAcquisitionService {

    /**
     * Method fetches a Ticket Acquisition with the given ID number as a TicketAcquisitionDTO.
     *
     * @param id ID number of the Ticket Acquisition
     * @return TicketAcquisitionDTO of the Ticket Acquisition with the given ID number
     * @throws ValidationException if ID is null
     * @throws NotFoundException if Ticket Acquisition does not exist
     * @throws ForbiddenException if user does not have the right to view the given Ticket Acquisition
     */
    TicketAcquisitionDto get(Long id) throws ValidationException, NotFoundException, ForbiddenException;

    /**
     * Method fetches all the TicketAcquisitions saved in the system.
     *
     * @return List of TicketAcquisitionDTOs of TAs saved in system
     */
    List<TicketAcquisitionDto> getAll();

    /**
     * Method fetches all TicketAcquisitions belonging to specific logged-in user and returns them as TicketAcquisitionDTO.
     *
     * @return List of TicketAcquisitionDTOs of TAs belonging to logged-in user saved in system
     */
    List<TicketAcquisitionDto> getAllUser();

    /**
     * Method fetches all TicketAcquisitions belonging to specific logged-in user and returns them as TicketAcquisitionDetailsDto.
     *
     * @return List of TicketAcquisitionDetailsDTOs of TAs belonging to logged-in user saved in system
     */
    List<TicketAcquisitionDetailsDto> getAllForUser();

    /**
     * Returns all reservations of the logged-in user stored in the system.
     * Limited to 3 reservations per page.
     *
     * @param page the page of reservations that is returned
     * @return the reservations
     */
    ReservationPageDto getAllReservationsForUser(int page);

    /**
     * Returns all reservations of the logged-in user stored in the system containing tickets for future acts.
     * Limited to 3 reservations per page
     *
     * @param page the page of reservations that is returned
     * @return ReservationPageDto containing the total number of pages stored and 3 reservations matching the page requested.
     */
    ReservationPageDto getAllCurrentReservationsForUser(int page);

    /**
     * Save a new TicketAcquisition in the system.
     *
     * @param details TicketAcquisitionDTO with the details of the TA
     * @return TicketAcquisitionDTO with the details of the TA just saved in the system
     * @throws ValidationException if validation of TA did not succeed
     * @throws ConflictException if saving of TA would cause a conflict in the system
     */
    TicketAcquisitionDto save(TicketAcquisitionDto details) throws ValidationException, ConflictException;

    /**
     * Stores all tickets stored in the shopping cart of a user.
     *
     * @return newly created ticket acquisition
     *
     * @throws ValidationException if validation of TA did not succeed
     * @throws ConflictException if saving of TA would cause a conflict in the system
     */
    TicketAcquisitionDetailsDto save() throws ValidationException, ConflictException;

    /**
     * Stores all tickets stored in the shopping cart of a user.
     *
     * @param details details of each ticket within the shopping cart
     *
     * @return newly created ticket acquisition
     *
     * @throws ValidationException if validation of TA did not succeed
     * @throws ConflictException if saving of TA would cause a conflict in the system
     */
    TicketAcquisition saveRaw(TicketAcquisitionDto details) throws ValidationException, ConflictException;

    /**
     * Stores all tickets stored in the shopping cart of a user.
     *
     * @return newly created ticket acquisition
     *
     * @throws ValidationException if validation of TA did not succeed
     * @throws ConflictException if saving of TA would cause a conflict in the system
     */
    TicketAcquisition saveRaw() throws ValidationException, ConflictException;

    /**
     * Cancel a TA by setting the cancelled boolean in the TA and its tickets to true.
     *
     * @param id ID number of TA to be cancelled
     * @throws ValidationException if ID is not valid
     * @throws NotFoundException if TA could not be found in the system
     */
    void cancel(Long id) throws ValidationException, NotFoundException;

    /**
     * Save a cancellation for the contained (previously purchased) tickets.
     *
     * @param ta a ticket acquisition containing the tickets to cancel
     * @return ticket acquisition containing the cancelled tickets
     * @throws NotFoundException if ta can't be matched to a purchase
     * @throws ValidationException validation of ta did not succeed
     * @throws ConflictException ta is in conflict with the database
     * */
    TicketAcquisitionDto cancel(TicketAcquisitionDetailsWithPricesDto ta) throws NotFoundException, ValidationException, ConflictException;

    /**
     * Method takes all the reserved (reservation == TicketStatus.RESERVED) tickets in the TA with the given ID number,
     * removes the connection to the TA and saves the tickets in the repository, so they can be displayed in the shopping
     * cart again. Removes TA from system if it is to subsequently be empty.
     *
     * @param id ID number of the TA from which the ticket reservations are to be removed for purchase
     * @throws NotFoundException if TA could not be found
     * @throws ForbiddenException if logged in user does not have permission to manipulate the given TA
     */
    void buyReservedTickets(Long id) throws NotFoundException, ForbiddenException;

    /**
     * Find all tickets that are not yet purchased by the user but are stored in the shopping cart.
     *
     * @return a list of tickets in the shopping cart
     */
    List<Ticket> ticketsInCart();
}
