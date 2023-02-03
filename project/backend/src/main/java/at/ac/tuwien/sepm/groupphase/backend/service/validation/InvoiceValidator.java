package at.ac.tuwien.sepm.groupphase.backend.service.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AddressRepository addressRepository;

    public InvoiceValidator(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void validateInvoice(CreateInvoiceDto invoiceDto) throws ValidationException {

        AddressDto addressDto =  invoiceDto.address();
        if (addressDto == null) {
            throw new ValidationException("No address provided", null);
        }
        if (addressDto.id() != null && !addressRepository.existsById(addressDto.id())) {
            throw new ValidationException("Address not found", null);
        } else if (addressDto.city() == null || addressDto.street() == null || addressDto.country() == null || addressDto.postalCode() == null) {
            List<String> missingAttributes = new ArrayList<>();
            if (addressDto.city() == null) {
                missingAttributes.add("city missing");
            }
            if (addressDto.street() == null) {
                missingAttributes.add("street missing");
            }
            if (addressDto.country() == null) {
                missingAttributes.add("country missing");
            }
            if (addressDto.postalCode() == null) {
                missingAttributes.add("postal code missing missing");
            }
            throw new ValidationException("Address has missing attributes:", missingAttributes);
        } else if (addressDto.postalCode() < 0) {
            throw new ValidationException("Postal code can not be negative", null);
        }
    }
}
