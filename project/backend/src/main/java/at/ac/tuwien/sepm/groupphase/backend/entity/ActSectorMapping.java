package at.ac.tuwien.sepm.groupphase.backend.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity(name = "act_sector_mapping")
public class ActSectorMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //stored in cents?
    @Column(nullable = false)
    private Integer price;

    //    @Column(name = "seat_status_map")
    //    private String seatStatusMap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_map_id")
    private SectorMap sectorMap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "act_id")
    private Act act;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    //    public String getSeatStatusMap() {
    //        return seatStatusMap;
    //    }
    //
    //    public void setSeatStatusMap(String seatStatusMap) {
    //        this.seatStatusMap = seatStatusMap;
    //    }

    public SectorMap getSectorMap() {
        return sectorMap;
    }

    public void setSectorMap(SectorMap sectorMap) {
        this.sectorMap = sectorMap;
    }

    public Act getAct() {
        return act;
    }

    public void setAct(Act act) {
        this.act = act;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActSectorMapping actSectorMapping)) {
            return false;
        }
        return Objects.equals(id, actSectorMapping.id)
            && Objects.equals(price, actSectorMapping.price)
            //&& Objects.equals(seatStatusMap, actSectorMapping.seatStatusMap)
            && Objects.equals(sectorMap, actSectorMapping.sectorMap)
            && Objects.equals(act, actSectorMapping.act);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, sectorMap, act);
    }

    @Override
    public String toString() {
        return "ActSectorMapping{"
            + "id=" + id
            + ", price=" + price
            //+ ", seatStatusMap='" + seatStatusMap + '\''
            + ", sectorMap=" + sectorMap.getId()
            + ", act=" + act.getId()
            + '}';
    }

    public static final class ActSectorMappingBuilder {
        private Long id;
        private Integer price;
        //private String seatStatusMap;
        private SectorMap sectorMap;
        private Act act;

        private ActSectorMappingBuilder() {
        }

        public static ActSectorMappingBuilder aActSectorMap() {
            return new ActSectorMappingBuilder();
        }

        public ActSectorMappingBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ActSectorMappingBuilder withPrice(Integer price) {
            this.price = price;
            return this;
        }

        //        public ActSectorMappingBuilder withSeatStatusMap(String seatStatusMap) {
        //            this.seatStatusMap = seatStatusMap;
        //            return this;
        //        }

        public ActSectorMappingBuilder withSectorMap(SectorMap sectorMap) {
            this.sectorMap = sectorMap;
            return this;
        }

        public ActSectorMappingBuilder withAct(Act act) {
            this.act = act;
            return this;
        }

        public ActSectorMapping build() {
            ActSectorMapping actSectorMapping = new ActSectorMapping();
            actSectorMapping.setId(id);
            actSectorMapping.setPrice(price);
            //sectorMap.setSeatStatusMap(seatStatusMap);
            actSectorMapping.setSectorMap(sectorMap);
            actSectorMapping.setAct(act);
            return actSectorMapping;
        }
    }
}