package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderPageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleOrderService implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final InvoiceService invoiceService;

    private final OrderMapper orderMapper;

    public SimpleOrderService(InvoiceService invoiceService,
                              OrderMapper orderMapper) {
        this.invoiceService = invoiceService;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderPageDto getAll(Long userId, int page) {
        LOGGER.debug("Find all orders for user {}, page number {}", userId, page);
        List<Invoice> invoices = invoiceService.getAllRegularInvoicesForUser(userId);

        return new OrderPageDto(
            invoices.size() % 3 == 0
                ? invoices.size() / 3 : invoices.size() / 3 + 1,
            orderMapper.toOrderDto(invoices).stream().skip((page - 1) * 3L).limit(3)
        );
    }

}
