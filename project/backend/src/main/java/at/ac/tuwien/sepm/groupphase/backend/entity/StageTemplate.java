package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

@Entity(name = "stage_template")
public class StageTemplate {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "stageTemplate")
    private List<SectorMap> sectorMaps;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "stageTemplate")
    private List<Stage> stages;

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

    public List<SectorMap> getSectorMaps() {
        return sectorMaps;
    }

    public void setSectorMaps(List<SectorMap> sectorMaps) {
        this.sectorMaps = sectorMaps;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
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
        if (!(o instanceof StageTemplate stage)) {
            return false;
        }
        return Objects.equals(id, stage.id)
            && Objects.equals(name, stage.name)
            && Objects.equals(sectorMaps, stage.sectorMaps)
            && Objects.equals(stages, stage.stages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sectorMaps, stages);
    }

    @Override
    public String toString() {
        return "Stage{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", sectorMaps=" + sectorMaps //LIST!
            + ", stages=" + stages //LIST!
            + '}';
    }

    public static final class StageTemplateBuilder {
        private Long id;
        private String name;
        private List<SectorMap> sectorMaps;
        private List<Stage> stages;

        private StageTemplateBuilder() {
        }

        public static StageTemplateBuilder aStage() {
            return new StageTemplateBuilder();
        }

        public StageTemplateBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public StageTemplateBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public StageTemplateBuilder withSectorMaps(List<SectorMap> sectorMaps) {
            this.sectorMaps = sectorMaps;
            return this;
        }

        public StageTemplateBuilder withStages(List<Stage> stages) {
            this.stages = stages;
            return this;
        }

        public StageTemplate build() {
            StageTemplate stage = new StageTemplate();
            stage.setId(id);
            stage.setName(name);
            stage.setSectorMaps(sectorMaps);
            stage.setStages(stages);
            return stage;
        }
    }
}
