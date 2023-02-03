package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;

import java.time.LocalDateTime;

public class TicketDto {
    private Long id;
    private Long buyerId;
    private Long actId;
    private Long sectorMapId;
    private LocalDateTime creationDate;
    private Integer seatNo;
    private String ticketFirstName;
    private String ticketLastName;
    private TicketStatus reservation;
    private boolean cancelled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public Long getSectorMapId() {
        return sectorMapId;
    }

    public void setSectorMapId(Long sectorMapId) {
        this.sectorMapId = sectorMapId;
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
}
