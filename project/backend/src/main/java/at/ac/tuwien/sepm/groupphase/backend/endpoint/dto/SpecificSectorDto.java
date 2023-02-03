package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;

public class SpecificSectorDto {

    private Orientation orientation;

    private Integer firstSeatNr;

    private Integer numSeats;

    private boolean standing;

    private Integer numRows;

    private Integer numColumns;

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

    public Integer getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(Integer numSeats) {
        this.numSeats = numSeats;
    }

    public boolean isStanding() {
        return standing;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }

    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer numRows) {
        this.numRows = numRows;
    }

    public Integer getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(Integer numColumns) {
        this.numColumns = numColumns;
    }
}
