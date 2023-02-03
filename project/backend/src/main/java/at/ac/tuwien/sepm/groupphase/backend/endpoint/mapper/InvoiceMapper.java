package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import org.mapstruct.Mapper;

@Mapper
public interface InvoiceMapper {

    Invoice createInvoiceDtoToInvoice(CreateInvoiceDto createInvoiceDto);

}
