package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public record OrderDto(
    Long invoiceId,
    TicketAcquisitionDetailsWithPricesDto ticketAcquisition,
    MerchPurchaseDto merchPurchase,
    LocalDate orderDate,
    CancellationForOrderDto[] cancellations
) {
}
