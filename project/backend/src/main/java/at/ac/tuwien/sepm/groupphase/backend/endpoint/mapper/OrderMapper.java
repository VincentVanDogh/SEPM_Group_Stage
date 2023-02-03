package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CancellationForOrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Mapper
public abstract class OrderMapper {

    @Autowired
    private TicketAcquisitionMapperAlt ticketAcquisitionMapper;

    @Autowired
    private MerchPurchaseMapper merchPurchaseMapper;

    @Autowired
    private InvoiceService invoiceService;

    public OrderDto toOrderDto(Invoice invoice) {
        TicketAcquisition ticketAcquisition = invoice.getTicketAcquisition();
        MerchPurchase merchPurchase = invoice.getMerchPurchase();

        TicketAcquisitionDetailsWithPricesDto taDto = null;
        if (ticketAcquisition != null) {
            taDto = invoice.getInvoiceType() == InvoiceType.REGULAR
                ? ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDetailsWithPricesDto(ticketAcquisition)
                : ticketAcquisitionMapper.ticketCancellationToTicketAcquisitionDetailsWithPricesDto(ticketAcquisition);
        }

        return new OrderDto(
            invoice.getReferenceNr(),
            taDto,
            merchPurchase == null ? null : merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(merchPurchase),
            invoice.getDate(),
            toCancellationForOrderDto(invoiceService.getCancellationsForInvoiceReferenceNo(invoice.getReferenceNr())));
    }

    public abstract List<OrderDto> toOrderDto(Collection<Invoice> invoices);

    public CancellationForOrderDto toCancellationForOrderDto(Invoice cancellation) {
        TicketAcquisition ticketAcquisition = cancellation.getTicketAcquisition();

        TicketAcquisitionDetailsWithPricesDto taDto = ticketAcquisitionMapper.ticketCancellationToTicketAcquisitionDetailsWithPricesDto(ticketAcquisition);

        return new CancellationForOrderDto(
            cancellation.getReferenceNr(),
            taDto,
            cancellation.getDate());
    }

    public abstract CancellationForOrderDto[] toCancellationForOrderDto(Collection<Invoice> cancellations);
}

