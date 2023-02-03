package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;

import java.time.LocalDateTime;

public class TicketDetailsDto {
    private Long id;
    private Long buyerId;
    private ActDetailsDto act;
    private Long sectorMapId;
    private LocalDateTime creationDate;
    private Integer seatNo;
    private String ticketFirstName;
    private String ticketLastName;
    private TicketStatus reservation;
    private boolean cancelled;
    private Integer price;
    private Integer rowNumber;
    private Integer seatNoInRow;
    private char sectorDesignation;

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

    public ActDetailsDto getAct() {
        return act;
    }

    public void setAct(ActDetailsDto act) {
        this.act = act;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getSeatNoInRow() {
        return seatNoInRow;
    }

    public void setSeatNoInRow(Integer seatNoInRow) {
        this.seatNoInRow = seatNoInRow;
    }

    public char getSectorDesignation() {
        return sectorDesignation;
    }

    public void setSectorDesignation(char sectorDesignation) {
        this.sectorDesignation = sectorDesignation;
    }
}
