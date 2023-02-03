package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find the invoice/cancellation entry with the given id and linked to the user given by the user id.
     *
     * @param id the id of the invoice/cancellation to find
     * @param userId the id of the user
     *
     * @return invoice/cancellation for specified user and id
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE i.id = ?1 AND (tab.id = ?2 OR mpb.id = ?2)
        """)
    Invoice findByIdAndUserId(Long id, Long userId);

    /**
     * Find the invoice entry with the given referenceNr and linked to the user given by the user id.
     *
     * @param referenceNr the referenceNr of the invoice to find
     * @param userId the id of the user
     *
     * @return invoice for specified user and referenceNr
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE i.referenceNr = ?1 AND (tab.id = ?2 OR mpb.id = ?2) AND i.invoiceType = 0
        """)
    Invoice findInvoiceByIdAndUserId(Long referenceNr, Long userId);

    /**
     * Find the cancellation entry with the given referenceNr and linked to the user given by the user id.
     *
     * @param referenceNr the referenceNr of the cancellation to find
     * @param userId the id of the user
     *
     * @return cancellation for specified user and referenceNr
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE i.referenceNr = ?1 AND (tab.id = ?2 OR mpb.id = ?2) AND i.invoiceType = 1
        """)
    Invoice findCancellationByIdAndUserId(Long referenceNr, Long userId);

    /**
     * Find all invoices/cancellations entries linked to the user given by the user id.
     *
     * @param userId the id of the user
     *
     * @return invoices for specified user
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE (tab.id = ?1 OR mpb.id = ?1) ORDER BY i.id DESC
        """)
    List<Invoice> findAllByUserId(Long userId);

    /**
     * Find all invoices entries linked to the user given by the user id. Newest first.
     *
     * @param userId the id of the user
     *
     * @return regular invoices for specified user
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE (tab.id = ?1 OR mpb.id = ?1) AND i.invoiceType = 0 ORDER BY i.referenceNr DESC
        """)
    List<Invoice> findAllRegularInvoicesByUserId(Long userId);

    /**
     * Find all cancellation entries linked to the user given by the user id. Newest first.
     *
     * @param userId the id of the user
     *
     * @return cancellations for specified user
     */
    @Query("""
SELECT i FROM invoice i LEFT JOIN i.ticketAcquisition ta LEFT JOIN ta.buyer tab LEFT JOIN i.merchPurchase.buyer mpb
WHERE (tab.id = ?1 OR mpb.id = ?1) AND i.invoiceType = 1 ORDER BY i.referenceNr DESC
        """)
    List<Invoice> findAllCancellationsByUserId(Long userId);

    /**
     * Get the reference no. of the original invoice for the given cancellation.
     *
     * @param cancellationReferenceNo the reference no. of the cancellation to find the original invoice for
     *
     * @return the reference no. of the original invoice
     */
    @Query("""
SELECT DISTINCT i.referenceNr FROM invoice c LEFT JOIN c.ticketAcquisition tc LEFT JOIN tc.cancelledTickets t LEFT JOIN t.ticketOrder ta LEFT JOIN ta.invoice i
WHERE c.referenceNr = ?1 AND i.invoiceType = 0
        """)
    Long getInvoiceReferenceNoForCancellation(Long cancellationReferenceNo);

    /**
     * Get cancellations for a given reference number of an invoice.
     *
     * @param invoiceReferenceNo the reference number of the invoice to find cancellations for
     *
     * @return list of cancellations of the invoice
     */
    @Query("""
SELECT DISTINCT c FROM invoice c LEFT JOIN c.ticketAcquisition tc LEFT JOIN tc.cancelledTickets t LEFT JOIN t.ticketOrder ta LEFT JOIN ta.invoice i
WHERE i.referenceNr = ?1 ORDER BY c.referenceNr DESC
        """)
    List<Invoice> getCancellationsForInvoiceReferenceNo(Long invoiceReferenceNo);


    /**
     * Get the largest reference number for a given invoice type saved in the database.
     *
     * @param type an invoice type
     * @return largest reference number for invoice type
     * */
    @Query("""
SELECT MAX(i.referenceNr) FROM invoice i WHERE i.invoiceType = ?1
        """)
    Long getLargestReferenceNrForInvoiceType(InvoiceType type);

}

