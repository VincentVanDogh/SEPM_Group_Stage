package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

import java.util.List;
import java.util.Objects;

@Entity(name = "sector_map")
public class SectorMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "sector_x")
    private Integer sectorX;

    @Column(nullable = false, name = "sector_y")
    private Integer sectorY;

    @Column(nullable = false)
    private Orientation orientation;

    @Column(name = "first_seat_nr")
    private Integer firstSeatNr;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_template_id")
    private StageTemplate stageTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sectorMap")
    private List<Ticket> tickets;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sectorMap")
    private List<ActSectorMapping> actSectorMaps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSectorX() {
        return sectorX;
    }

    public void setSectorX(Integer x) {
        this.sectorX = x;
    }

    public Integer getSectorY() {
        return sectorY;
    }

    public void setSectorY(Integer y) {
        this.sectorY = y;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Integer getFirstSeatNr() {
        return firstSeatNr;
    }

    public void setFirstSeatNr(Integer firstSeatNr) {
        this.firstSeatNr = firstSeatNr;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<ActSectorMapping> getActSectorMaps() {
        return actSectorMaps;
    }

    public void setActSectorMaps(List<ActSectorMapping> actSectorMaps) {
        this.actSectorMaps = actSectorMaps;
    }

    public StageTemplate getStageTemplate() {
        return stageTemplate;
    }

    public void setStageTemplate(StageTemplate stageTemplate) {
        this.stageTemplate = stageTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectorMap map)) {
            return false;
        }
        return Objects.equals(id, map.id)
            && Objects.equals(sectorX, map.sectorX)
            && Objects.equals(sectorY, map.sectorY)
            && Objects.equals(firstSeatNr, map.firstSeatNr)
            && Objects.equals(sector, map.sector)
            && Objects.equals(tickets, map.tickets)
            && Objects.equals(orientation, map.orientation)
            && Objects.equals(actSectorMaps, map.actSectorMaps)
            && Objects.equals(stageTemplate, map.stageTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sectorX, sectorY, firstSeatNr, stageTemplate, sector, tickets, orientation, actSectorMaps);
    }

    @Override
    public String toString() {
        return "SectorMap{"
            + "id=" + id
            + ", x=" + sectorX
            + ", y=" + sectorY
            + ", firstSeatNr=" + firstSeatNr
            + ", stageTemplate=" + stageTemplate.getId()
            + ", sector=" + sector.getId()
            + ", tickets=" + tickets //LIST!
            + ", orientation=" + orientation
            + ", actSectorMaps=" + actSectorMaps //LIST!
            + '}';
    }

    public static final class SectorMapBuilder {
        private Long id;
        private Integer sectorX;
        private Integer sectorY;
        private Integer firstSeatNr;
        private StageTemplate stageTemplate;
        private Sector sector;
        private Orientation orientation;
        private List<Ticket> tickets;
        private List<ActSectorMapping> actSectorMaps;

        private SectorMapBuilder() {
        }

        public static SectorMapBuilder aSectorMap() {
            return new SectorMapBuilder();
        }

        public SectorMapBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SectorMapBuilder withSectorX(Integer x) {
            this.sectorX = x;
            return this;
        }

        public SectorMapBuilder withSectorY(Integer y) {
            this.sectorY = y;
            return this;
        }

        public SectorMapBuilder withFirstSeatNr(Integer firstSeatNr) {
            this.firstSeatNr = firstSeatNr;
            return this;
        }

        public SectorMapBuilder withStageTemplate(StageTemplate stageTemplate) {
            this.stageTemplate = stageTemplate;
            return this;
        }

        public SectorMapBuilder withSector(Sector sector) {
            this.sector = sector;
            return this;
        }

        public SectorMapBuilder withOrientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public SectorMapBuilder withTickets(List<Ticket> tickets) {
            this.tickets = tickets;
            return this;
        }

        public SectorMapBuilder withActSectorMaps(List<ActSectorMapping> actSectorMaps) {
            this.actSectorMaps = actSectorMaps;
            return this;
        }

        public SectorMap build() {
            SectorMap sectorMap = new SectorMap();
            sectorMap.setId(id);
            sectorMap.setSectorX(sectorX);
            sectorMap.setSectorY(sectorY);
            sectorMap.setFirstSeatNr(firstSeatNr);
            sectorMap.setStageTemplate(stageTemplate);
            sectorMap.setSector(sector);
            sectorMap.setOrientation(orientation);
            sectorMap.setTickets(tickets);
            sectorMap.setActSectorMaps(actSectorMaps);
            return sectorMap;
        }
    }
}