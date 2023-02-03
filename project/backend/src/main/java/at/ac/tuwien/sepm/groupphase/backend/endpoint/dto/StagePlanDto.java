package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class StagePlanDto {

    private Long stageId;

    private String name;

    private Integer totalSeatsNr;

    private SpecificSectorDto[] sectorArray;

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSeatsNr() {
        return totalSeatsNr;
    }

    public void setTotalSeatsNr(Integer totalSeatsNr) {
        this.totalSeatsNr = totalSeatsNr;
    }

    public SpecificSectorDto[] getSectorArray() {
        return sectorArray;
    }

    public void setSectorArray(SpecificSectorDto[] sectorArray) {
        this.sectorArray = sectorArray;
    }
}
