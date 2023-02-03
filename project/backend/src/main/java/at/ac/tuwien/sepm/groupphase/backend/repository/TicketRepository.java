package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Finds a ticket based on an id.
     *
     * @param id id of the ticket
     *
     * @return a ticket with a corresponding id
     */
    Ticket findTicketById(Long id);

    /**
     * Finds a ticket based on a ticket order id.
     *
     * @param ticketOrderId id of the ticket order
     *
     * @return a ticket that is part of a ticket order with the corresponding {@code ticketOrderId}
     */
    List<Ticket> findTicketsByTicketOrderId(Long ticketOrderId);

    /**
     * Finds tickets belonging to a user with the corresponding user id.
     *
     * @param buyerId id of the user that possesses the ticket
     *
     * @return a collection of tickets
     */
    List<Ticket> findTicketsByBuyerIdAndTicketOrderIsNull(Long buyerId);

    /**
     * Finds tickets with corresponding user id's and reservation statuses.
     *
     * @param buyerId id of the ticket holder
     * @param reservation status of the ticket
     *
     * @return a collection of tickets with corresponding user id's and reservation statuses
     */
    List<Ticket> findTicketsByBuyerIdAndTicketOrderIsNullAndReservation(Long buyerId, TicketStatus reservation);

    /**
     * Finds tickets with no ticket order assigned to it with the corresponding user ID, a reservation status other than
     * the given one and a creation date after a specified DateTime.
     *
     * @param buyerId ID of the ticket holder
     * @param reservation Reservation status of the tickets not to be searched for
     * @param creationDate DateTime after which the ticket's creation date is to be
     *
     * @return List of tickets with no ticket order assigned to them with the given user as the buyer, a reservation status other than the given one, created after the given time
     */
    List<Ticket> findTicketsByBuyerIdAndTicketOrderIsNullAndReservationNotAndCreationDateIsAfter(Long buyerId,
                                                                                                 TicketStatus reservation,
                                                                                                 LocalDateTime creationDate);

    /**
     * Finds tickets with no ticket order assigned to it with the corresponding user ID, the given reservation status
     * and a creation date after a specified DateTime.
     *
     * @param buyerId ID of the ticket holder
     * @param reservation Reservation status of the tickets to be searched for
     * @param creationDate DateTime after which the tickets' creation date is to be
     *
     * @return List of tickets with no ticket order assigned to them with the given user as the buyer, the given reservation status created after the given time
     */
    List<Ticket> findTicketsByBuyerIdAndTicketOrderIsNullAndReservationAndCreationDateIsAfter(Long buyerId,
                                                                                              TicketStatus reservation,
                                                                                              LocalDateTime creationDate);

    /**
     * Finds tickets with no ticket order assigned to it with the creation date before the specified DateTime.
     *
     * @param creationDate DateTime before which the tickets' creation date is to be
     *
     * @return List of tickets with no ticket order assigned to it with the creation date before the specified DateTime
     */
    List<Ticket> findTicketsByCreationDateIsBeforeAndTicketOrderIsNull(LocalDateTime creationDate);

    /**
     * Find all tickets for a given user and act.
     *
     * @param email email of the user
     * @param actId id of the act
     *
     * @return List of tickets for a given user and act
     * */
    @Query("SELECT t FROM ticket t LEFT JOIN t.ticketOrder ta LEFT JOIN ta.buyer u LEFT JOIN t.act a WHERE UPPER(u.email) LIKE %?1% AND a.id = ?2")
    List<Ticket> findTicketsByUsernameAndAct(String email, Long actId);

    /**
     * Get the price of a given ticket.
     *
     * @param ticketId the id of the ticket
     *
     * @return price of the ticket
     */
    @Query("""
SELECT asm1.price FROM ticket t LEFT JOIN t.act a LEFT JOIN t.sectorMap sm LEFT JOIN sm.actSectorMaps asm1 LEFT JOIN a.sectorMaps asm2
WHERE t.id = ?1 AND asm1.id = asm2.id
        """)
    Integer getPriceForTicket(Long ticketId);

    /**
     * Find all tickets that are not assigned yet to a ticket acquisition - meaning they are still in the shopping cart.
     *
     * @param userId id of the user
     * @return list of tickets of a certain user still stored in the shopping cart
     */
    List<Ticket> findAllByBuyerIdAndTicketOrderIsNull(Long userId);

    /**
     * Find all uncancelled purchased tickets by ids.
     *
     * @param ticketIds ids of the tickets from which any amount might be purchased
     * @return list of tickets
     */
    @Query("""
SELECT t FROM ticket t
WHERE t.id IN :ids AND t.reservation = at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus.PURCHASED AND t.cancelled = false
        """)
    List<Ticket> findUncancelledPurchasedByIds(@Param("ids") List<Long> ticketIds);

    /**
     * Find all uncancelled reserved tickets by ids.
     *
     * @param ticketIds ids of the tickets from which any amount might be reserved
     * @return list of tickets
     */
    @Query("""
SELECT t FROM ticket t
WHERE t.id IN :ids AND t.reservation = at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus.RESERVED AND t.cancelled = false
        """)
    List<Ticket> findUncancelledReservedByIds(@Param("ids") List<Long> ticketIds);

    /**
     * Find all tickets by ids.
     *
     * @param ticketIds ids of the tickets
     * @return list of tickets
     */
    @Query("SELECT t FROM ticket t WHERE t.id IN :ids")
    List<Ticket> findAllByIds(@Param("ids") List<Long> ticketIds);
}
