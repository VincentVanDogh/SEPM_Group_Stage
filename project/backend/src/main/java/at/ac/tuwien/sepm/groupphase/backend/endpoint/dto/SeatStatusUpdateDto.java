package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.SeatStatus;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;

//TODO: update to fit requirements (add stage id?, seatNr to list of seat nrs, newStatus to list of new statuses)
public class SeatStatusUpdateDto {

    @NotNull(message = "newStatus must not be null")
    private SeatStatus[] newStatus;

    @NotNull(message = "Seat nr must not be null")
    private Integer[] seatNr;

    @NotNull(message = "Stage ID must not be null")
    private Long stageId;

    @NotNull(message = "Act ID must not be null")
    private Long actId;

    public SeatStatus[] getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(SeatStatus[] newStatus) {
        this.newStatus = newStatus;
    }

    public Integer[] getSeatNr() {
        return seatNr;
    }

    public void setSeatNr(Integer[] seatNr) {
        this.seatNr = seatNr;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeatStatusUpdateDto that)) {
            return false;
        }
        return Arrays.equals(newStatus, that.newStatus)
            && Arrays.equals(seatNr, that.seatNr)
            && Objects.equals(actId, that.actId)
            && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(newStatus), Arrays.hashCode(seatNr), stageId, actId);
    }

    @Override
    public String toString() {
        return "SeatStatusUpdateDto{"
            + "newStatus=" + Arrays.toString(newStatus)
            + ", seatNr=" + Arrays.toString(seatNr)
            + ", stageId=" + stageId
            + ", actId=" + actId
            + '}';
    }

    public static final class SeatStatusUpdateDtoBuilder {

        private SeatStatus[] newStatus;
        private Integer[] seatNr;
        private Long stageId;
        private Long actId;

        private SeatStatusUpdateDtoBuilder() {
        }

        public static SeatStatusUpdateDtoBuilder aSeatStatusUpdateDto() {
            return new SeatStatusUpdateDtoBuilder();
        }

        public SeatStatusUpdateDtoBuilder withNewStatus(SeatStatus[] newStatus) {
            this.newStatus = newStatus;
            return this;
        }

        public SeatStatusUpdateDtoBuilder withSeatNr(Integer[] seatNr) {
            this.seatNr = seatNr;
            return this;
        }

        public SeatStatusUpdateDtoBuilder withStageId(Long stageId) {
            this.stageId = stageId;
            return this;
        }

        public SeatStatusUpdateDtoBuilder withActId(Long actId) {
            this.actId = actId;
            return this;
        }

        public SeatStatusUpdateDto build() {
            SeatStatusUpdateDto seatStatusUpdateDto = new SeatStatusUpdateDto();
            seatStatusUpdateDto.setNewStatus(newStatus);
            seatStatusUpdateDto.setSeatNr(seatNr);
            seatStatusUpdateDto.setStageId(stageId);
            seatStatusUpdateDto.setActId(actId);
            return seatStatusUpdateDto;
        }
    }
}
