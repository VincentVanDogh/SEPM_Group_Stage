package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;

import javax.validation.constraints.NotNull;

public record CreateInvoiceDto(

    //@NotNull(message = "Invoice date must not be null") //unnecessary, date will be set in backend
    //LocalDate date,

    @NotNull(message = "Invoice type must not be null")
    InvoiceType invoiceType,

    //@NotNull(message = "Invoice type must not be null") can be null for cancellations, since we can get the address from the invoice
    AddressDto address,

    String recipientName //can be null for cancellations, since we can get the name from the invoice

) {
}
