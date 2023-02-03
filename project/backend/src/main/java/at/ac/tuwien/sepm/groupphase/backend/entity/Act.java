package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity(name = "act")
public class Act {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false, name = "nr_tickets_reserved")
    private Integer nrTicketsReserved = 0;

    @Column(nullable = false, name = "nr_tickets_sold")
    private Integer nrTicketsSold = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "act")
    private List<Ticket> tickets;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "act")
    private List<ActSectorMapping> sectorMaps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Integer getNrTicketsReserved() {
        return nrTicketsReserved;
    }

    public void setNrTicketsReserved(Integer nrTicketsReserved) {
        this.nrTicketsReserved = nrTicketsReserved;
    }

    public Integer getNrTicketsSold() {
        return nrTicketsSold;
    }

    public void setNrTicketsSold(Integer nrTicketsSold) {
        this.nrTicketsSold = nrTicketsSold;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public List<ActSectorMapping> getSectorMaps() {
        return sectorMaps;
    }

    public void setSectorMaps(List<ActSectorMapping> sectorMaps) {
        this.sectorMaps = sectorMaps;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    //    public void addActSectorMapping(ActSectorMapping actSectorMapping) {
    //        actSectorMapping.setAct(this);
    //        sectorMaps.add(actSectorMapping);
    //    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Act act)) {
            return false;
        }
        return Objects.equals(id, act.id)
            && Objects.equals(start, act.start)
            && Objects.equals(nrTicketsReserved, act.nrTicketsReserved)
            && Objects.equals(nrTicketsSold, act.nrTicketsSold)
            && Objects.equals(stage, act.stage)
            && Objects.equals(event, act.event)
            && Objects.equals(sectorMaps, act.sectorMaps)
            && Objects.equals(tickets, act.tickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, nrTicketsReserved, nrTicketsSold, stage, event, sectorMaps, tickets);
    }

    @Override
    public String toString() {
        return "Act{"
            + "id=" + id
            + ", start=" + start
            + ", nrTicketsReserved=" + nrTicketsReserved
            + ", nrTicketsSold=" + nrTicketsSold
            + ", stage=" + stage.getId()
            + ", event=" + event.getId()
            + ", sectorMaps=" + sectorMaps //LIST!
            + ", tickets=" + tickets //LIST!
            + '}';
    }

    public static final class ActBuilder {
        private Long id;
        private LocalDateTime start;
        private Integer nrTicketsReserved;
        private Integer nrTicketsSold;
        private Stage stage;
        private Event event;
        private List<ActSectorMapping> sectorMaps;
        private List<Ticket> tickets;

        private ActBuilder() {
        }

        public static ActBuilder anAct() {
            return new ActBuilder();
        }

        public ActBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ActBuilder withStart(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public ActBuilder withNrTicketsReserved(Integer nrTicketsReserved) {
            this.nrTicketsReserved = nrTicketsReserved;
            return this;
        }

        public ActBuilder withNrTicketsSold(Integer nrTicketsSold) {
            this.nrTicketsSold = nrTicketsSold;
            return this;
        }

        public ActBuilder withStage(Stage stage) {
            this.stage = stage;
            return this;
        }

        public ActBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }


        public ActBuilder withSectorMaps(List<ActSectorMapping> sectorMaps) {
            this.sectorMaps = sectorMaps;
            return this;
        }

        public ActBuilder withTickets(List<Ticket> tickets) {
            this.tickets = tickets;
            return this;
        }

        public Act build() {
            Act act = new Act();
            act.setId(id);
            act.setStart(start);
            act.setNrTicketsReserved(nrTicketsReserved);
            act.setNrTicketsSold(nrTicketsSold);
            act.setStage(stage);
            act.setEvent(event);
            act.setSectorMaps(sectorMaps);
            act.setTickets(tickets);
            return act;
        }
    }
}
