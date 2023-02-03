package at.ac.tuwien.sepm.groupphase.backend.entity;


import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity(name = "ticket")
public class Ticket {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer")
    private ApplicationUser buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_order")
    private TicketAcquisition ticketOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancellation")
    private TicketAcquisition cancellation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "act_id")
    private Act act;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_map_id")
    private SectorMap sectorMap;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "ticket_first_name")
    @NotNull
    private String ticketFirstName;

    @Column(name = "ticket_last_name")
    @NotNull
    private String ticketLastName;

    @Column(name = "section")
    private String section;

    @Column(name = "seat_no")
    private Integer seatNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation")
    private TicketStatus reservation;

    @Column(name = "cancelled")
    private boolean cancelled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActId() {
        return act.getId();
    }

    public ApplicationUser getBuyer() {
        return buyer;
    }

    public void setBuyer(ApplicationUser buyer) {
        this.buyer = buyer;
    }

    public TicketAcquisition getTicketOrder() {
        return ticketOrder;
    }

    public void setTicketOrder(TicketAcquisition ticketOrder) {
        this.ticketOrder = ticketOrder;
    }

    public Act getAct() {
        return act;
    }

    public void setAct(Act act) {
        this.act = act;
    }

    public SectorMap getSectorMap() {
        return sectorMap;
    }

    public void setSectorMap(SectorMap sectorMap) {
        this.sectorMap = sectorMap;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public String getTicketFirstName() {
        return ticketFirstName;
    }

    public void setTicketFirstName(String ticketFirstName) {
        this.ticketFirstName = ticketFirstName;
    }

    public String getTicketLastName() {
        return ticketLastName;
    }

    public void setTicketLastName(String ticketLastName) {
        this.ticketLastName = ticketLastName;
    }

    public TicketStatus getReservation() {
        return reservation;
    }

    public void setReservation(TicketStatus reservation) {
        this.reservation = reservation;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public TicketAcquisition getCancellation() {
        return cancellation;
    }

    public void setCancellation(TicketAcquisition cancellation) {
        this.cancellation = cancellation;
    }

    @Override
    public String toString() {
        return act.getEvent().getName() + (sectorMap.getSector().getStanding() ? " Standing" : " Seated") + " Ticket";
    }
}
