package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public class TicketAcquisitionDetailsDto {
    private Long id;
    private Long buyerId;
    private List<TicketDetailsDto> tickets;
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

    public List<TicketDetailsDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDetailsDto> tickets) {
        this.tickets = tickets;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
