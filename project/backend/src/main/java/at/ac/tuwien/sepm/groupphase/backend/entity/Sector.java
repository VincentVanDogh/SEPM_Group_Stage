package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.Id;

@Entity(name = "sector")
public class Sector {

    @Id
    private Long id;

    @Column(nullable = false, name = "number_of_seats")
    private Integer numberOfSeats;

    @Column(nullable = false)
    private Boolean standing;

    @Column(name = "number_rows")
    private Integer numberRows;

    @Column(name = "number_columns")
    private Integer numberColumns;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sector")
    private List<SectorMap> sectorMaps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Boolean getStanding() {
        return standing;
    }

    public void setStanding(Boolean standing) {
        this.standing = standing;
    }

    public Integer getNumberRows() {
        return numberRows;
    }

    public void setNumberRows(Integer numberRows) {
        this.numberRows = numberRows;
    }

    public Integer getNumberColumns() {
        return numberColumns;
    }

    public void setNumberColumns(Integer numberColumns) {
        this.numberColumns = numberColumns;
    }

    public List<SectorMap> getSectorMaps() {
        return sectorMaps;
    }

    public void setSectorMaps(List<SectorMap> sectorMaps) {
        this.sectorMaps = sectorMaps;
    }


    //    public void addSectorMap(SectorMap sectorMap) {
    //        sectorMap.setSector(this);
    //        sectorMaps.add(sectorMap);
    //    }

    //    public void addActSectorMapping(ActSectorMapping actSectorMapping) {
    //        actSectorMapping.setSector(this);
    //        actSectorMaps.add(actSectorMapping);
    //    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sector sector)) {
            return false;
        }
        return Objects.equals(id, sector.id)
            && Objects.equals(numberOfSeats, sector.numberOfSeats)
            && Objects.equals(numberRows, sector.numberRows)
            && Objects.equals(numberColumns, sector.numberColumns)
            && Objects.equals(standing, sector.standing)
            && Objects.equals(sectorMaps, sector.sectorMaps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberOfSeats, numberRows, numberColumns, standing, sectorMaps);
    }

    @Override
    public String toString() {
        return "Sector{"
            + "id=" + id
            + ", numberOfSeats=" + numberOfSeats
            + ", standing=" + standing
            + ", numberRows=" + numberRows
            + ", numberColumns=" + numberColumns
            + ", sectorMaps=" + sectorMaps //LIST!
            + '}';
    }

    public static final class SectorBuilder {
        private Long id;
        private Integer numberOfSeats;
        private Integer numberRows;
        private Integer numberColumns;
        private Boolean standing;
        private List<SectorMap> sectorMaps;

        private SectorBuilder() {
        }

        public static SectorBuilder aSector() {
            return new SectorBuilder();
        }

        public SectorBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SectorBuilder withNumberOfSeats(Integer numberOfSeats) {
            this.numberOfSeats = numberOfSeats;
            return this;
        }

        public SectorBuilder withNumberRows(Integer numberRows) {
            this.numberRows = numberRows;
            return this;
        }

        public SectorBuilder withNumberColumns(Integer numberColumns) {
            this.numberColumns = numberColumns;
            return this;
        }

        public SectorBuilder withStanding(Boolean standing) {
            this.standing = standing;
            return this;
        }

        public SectorBuilder withSectorMaps(List<SectorMap> sectorMaps) {
            this.sectorMaps = sectorMaps;
            return this;
        }

        public Sector build() {
            Sector sector = new Sector();
            sector.setId(id);
            sector.setNumberOfSeats(numberOfSeats);
            sector.setNumberRows(numberRows);
            sector.setNumberColumns(numberColumns);
            sector.setStanding(standing);
            sector.setSectorMaps(sectorMaps);
            return sector;
        }
    }
}