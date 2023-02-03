package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ActStagePlanDto {

    private Long actId;

    private Long stageId;

    private String name;

    private Integer totalSeatsNr;

    private ActSpecificSectorDto[] sectorArray;

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

    public ActSpecificSectorDto[] getSectorArray() {
        return sectorArray;
    }

    public void setSectorArray(ActSpecificSectorDto[] sectorArray) {
        this.sectorArray = sectorArray;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }
}
