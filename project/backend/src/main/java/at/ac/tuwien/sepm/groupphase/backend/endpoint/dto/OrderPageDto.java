package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.stream.Stream;

public class OrderPageDto {

    private int numberOfPages;
    private Stream<OrderDto> orders;


    public OrderPageDto(int numberOfPages, Stream<OrderDto> orders) {
        this.numberOfPages = numberOfPages;
        this.orders = orders;
    }

    public OrderPageDto() {
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Stream<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Stream<OrderDto> orders) {
        this.orders = orders;
    }
}
