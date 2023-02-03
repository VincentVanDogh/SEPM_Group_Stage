package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderPageDto;

public interface OrderService {

    /**
     * Find all orders for a given user ordered by date (descending).
     *
     * @param userId id of the user to get orders for
     * @param pageId the page you want to get
     * @return Dto of the page with 3 orders for the user of all orders
     */
    OrderPageDto getAll(Long userId, int pageId);

}
