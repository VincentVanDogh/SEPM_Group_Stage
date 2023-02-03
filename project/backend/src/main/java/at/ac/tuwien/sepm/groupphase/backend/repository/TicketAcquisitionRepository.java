package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketAcquisitionRepository extends JpaRepository<TicketAcquisition, Long> {

    /**
     * Find all ticket acquisition entries by buyer id.
     *
     * @param buyerId id of the buyer requesting tickets with corresponding buyerId's
     * @return list of all ticket acquisitions belonging to the buyer
     */
    List<TicketAcquisition> findAllByBuyerId(Long buyerId);

    /**
     * Find all ticket acquisition entries by buyer id with attached invoices. These ticket acquisitions contain purchases.
     *
     * @param buyerId id of the buyer to find purchases for
     * @return list of all purchases belonging to the buyer
     */
    List<TicketAcquisition> findAllByBuyerIdAndInvoiceNotNull(Long buyerId);

    /**
     * Find all ticket acquisition entries by buyer id with no attached invoices. These ticket acquisitions are reservations only.
     *
     * @param buyerId id of the buyer to find reservations for
     * @return list of all reservations belonging to the buyer
     */
    @Query("""
SELECT DISTINCT ta FROM ticket_acquisition ta LEFT JOIN ta.invoice i LEFT JOIN ta.tickets t
WHERE ta.buyer.id = ?1 AND i IS NULL
        """)
    List<TicketAcquisition> findAllReservationsForUser(Long buyerId);

    /**
     * Find all ticket acquisition entries containing tickets for future acts by buyer id with no attached invoices.
     * These ticket acquisitions are reservations only.
     *
     * @param buyerId id of the buyer to find reservations for
     * @return list of all reservations belonging to the buyer containing tickets for future acts.
     */
    @Query("""
SELECT DISTINCT ta FROM ticket_acquisition ta LEFT JOIN ta.invoice i LEFT JOIN ta.tickets t
WHERE ta.buyer.id = ?1 AND i IS NULL AND t.act.start > CURRENT_TIMESTAMP
        """)
    List<TicketAcquisition> findAllCurrentReservationsForUser(Long buyerId);

    /**
     * Find the ticket acquisition storing the purchase of the given ticket by the given buyer.
     *
     * @param buyerId id of the buyer to find ticket acquisition for
     * @param ticketId if of the ticket contained in the acquisition
     * @return the ticket acquisition storing the purchase of the given ticket by the given buyer.
     */
    @Query("""
SELECT DISTINCT ta FROM ticket_acquisition ta LEFT JOIN ta.invoice i LEFT JOIN ta.tickets t
WHERE ta.buyer.id = ?1 AND i.invoiceType = 0 AND t.id = ?2
        """)
    TicketAcquisition findTicketAcquisitionForBuyerAndContainingTicket(Long buyerId, Long ticketId);
}
