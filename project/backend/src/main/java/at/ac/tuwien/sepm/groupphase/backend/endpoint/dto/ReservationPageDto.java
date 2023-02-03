package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.stream.Stream;

public class ReservationPageDto {

    private int numberOfPages;
    private Stream<ReservationDto> reservations;

    public ReservationPageDto(int numberOfPages, Stream<ReservationDto> reservations) {
        this.numberOfPages = numberOfPages;
        this.reservations = reservations;
    }

    public ReservationPageDto() {
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Stream<ReservationDto> getReservations() {
        return reservations;
    }

    public void setReservations(Stream<ReservationDto> reservations) {
        this.reservations = reservations;
    }
}
