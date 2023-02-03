package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import at.ac.tuwien.sepm.groupphase.backend.type.SeatStatus;

public class ActSpecificSectorDto {

    private Long id;

    private Orientation orientation;

    private Integer firstSeatNr;

    private Integer numSeats;

    private Integer price;

    private Integer numReservedPlaces;

    private Integer numBoughtPlaces;

    private boolean standing;

    private SeatStatus[] seatStatusMap;

    private Integer numRows;

    private Integer numColumns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(Integer numSeats) {
        this.numSeats = numSeats;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getNumReservedPlaces() {
        return numReservedPlaces;
    }

    public void setNumReservedPlaces(Integer numReservedPlaces) {
        this.numReservedPlaces = numReservedPlaces;
    }

    public Integer getNumBoughtPlaces() {
        return numBoughtPlaces;
    }

    public void setNumBoughtPlaces(Integer numBoughtPlaces) {
        this.numBoughtPlaces = numBoughtPlaces;
    }

    public boolean isStanding() {
        return standing;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }

    public SeatStatus[] getSeatStatusMap() {
        return seatStatusMap;
    }

    public void setSeatStatusMap(SeatStatus[] seatStatusMap) {
        this.seatStatusMap = seatStatusMap;
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
