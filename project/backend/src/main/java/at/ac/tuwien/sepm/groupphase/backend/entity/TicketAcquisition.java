package at.ac.tuwien.sepm.groupphase.backend.entity;




import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.OrderBy;
import java.util.List;

@Entity(name = "ticket_acquisition")
public class TicketAcquisition {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer")
    private ApplicationUser buyer;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "ticketOrder") //CHANGED THIS TO LAZY, does that break anything?
    @OrderBy("id ASC")
    private List<Ticket> tickets;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "cancellation")
    @OrderBy("id ASC")
    private List<Ticket> cancelledTickets;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "ticketAcquisition")
    private Invoice invoice;

    @Column(name = "cancelled")
    private boolean cancelled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getBuyer() {
        return buyer;
    }

    public void setBuyer(ApplicationUser buyer) {
        this.buyer = buyer;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<Ticket> getCancelledTickets() {
        return cancelledTickets;
    }

    public void setCancelledTickets(List<Ticket> cancelledTickets) {
        this.cancelledTickets = cancelledTickets;
    }

    @Override
    public String toString() {
        return "TicketAcquisition{"
            + "id=" + id
            + ", buyer=" + buyer
            + ", tickets=" + tickets
            + ", invoice=" + invoice
            + ", cancelled=" + cancelled
            + '}';
    }
}
