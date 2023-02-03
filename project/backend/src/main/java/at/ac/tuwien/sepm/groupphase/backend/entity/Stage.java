package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "stage")
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_template_id")
    private StageTemplate stageTemplate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "stage")
    private List<Act> acts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }

    public StageTemplate getStageTemplate() {
        return stageTemplate;
    }

    public void setStageTemplate(StageTemplate stageTemplate) {
        this.stageTemplate = stageTemplate;
    }
    //    public void addSectorMap(SectorMap sectorMap) {
    //        sectorMap.setStage(this);
    //        sectorMaps.add(sectorMap);
    //    }

    //    public void addAct(Act act) {
    //        act.setStage(this);
    //        acts.add(act);
    //    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stage stage)) {
            return false;
        }
        return Objects.equals(id, stage.id)
            && Objects.equals(name, stage.name)
            && Objects.equals(location, stage.location)
            && Objects.equals(stageTemplate, stage.stageTemplate)
            && Objects.equals(acts, stage.acts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, stageTemplate, acts);
    }

    @Override
    public String toString() {
        return "Stage{"
            + "id=" + id
            + ", name='" + name
            + ", location='" + location.getId() + '\''
            + ", stageTemplate=" + stageTemplate.getId()
            + ", acts=" + acts //LIST!
            + '}';
    }

    public static final class StageBuilder {
        private Long id;
        private String name;
        private Location location;
        private StageTemplate stageTemplate;
        private List<Act> acts;

        private StageBuilder() {
        }

        public static StageBuilder aStage() {
            return new StageBuilder();
        }

        public StageBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public StageBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public StageBuilder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public StageBuilder withStageTemplate(StageTemplate stageTemplate) {
            this.stageTemplate = stageTemplate;
            return this;
        }

        public StageBuilder withActs(List<Act> acts) {
            this.acts = acts;
            return this;
        }

        public Stage build() {
            Stage stage = new Stage();
            stage.setId(id);
            stage.setName(name);
            stage.setLocation(location);
            stage.setStageTemplate(stageTemplate);
            stage.setActs(acts);
            return stage;
        }
    }
}
