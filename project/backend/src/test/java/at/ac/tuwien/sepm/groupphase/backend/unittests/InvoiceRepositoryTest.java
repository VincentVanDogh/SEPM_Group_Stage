package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchPurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketAcquisitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class InvoiceRepositoryTest implements TestData {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketAcquisitionRepository ticketAcquisitionRepository;

    @Autowired
    private MerchPurchaseRepository merchPurchaseRepository;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        invoiceRepository.deleteAll();
        userRepository.deleteAll();
        ticketAcquisitionRepository.deleteAll();
        merchPurchaseRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    @Transactional
    public void givenNothing_whenSaveAct_thenFindListWithOneElementAndFindActById() {
        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            //.withId(1L)
            .withFirstName(FIRST_NAME)
            .withLastName(LAST_NAME)
            .withEmail(EMAIL)
            .withPassword(PASSWORD)
            .build();
        user = userRepository.save(user);

        Address address = Address.AddressBuilder.anAddress()
            .withCity("Manufactorum Tertium")
            .withStreet("3rd Plasma Generator")
            .withCountry("Mars")
            .withPostalCode(1234).build();
        address = addressRepository.save(address);


        MerchPurchase merchPurchase = new MerchPurchase();
        merchPurchase.setBuyer(user);
        merchPurchase.setPurchased(true);
        merchPurchase = merchPurchaseRepository.save(merchPurchase);

        TicketAcquisition ticketAcquisition = new TicketAcquisition();
        ticketAcquisition.setBuyer(user);
        ticketAcquisition = ticketAcquisitionRepository.save(ticketAcquisition);

        Invoice invoice = Invoice.InvoiceBuilder.anInvoice()
            .withReferenceNr(1L)
            .withRecipientName("Max Mustermann")
            .withAddress(address)
            .withInvoiceType(InvoiceType.REGULAR)
            .withDate(LocalDate.now())
            .withMerchPurchase(merchPurchase)
            .build();

        Invoice cancellation = Invoice.InvoiceBuilder.anInvoice()
            .withReferenceNr(1L)
            .withRecipientName("Max Mustermann")
            .withAddress(address)
            .withInvoiceType(InvoiceType.CANCELLATION)
            .withDate(LocalDate.now())
            .withTicketAcquisition(ticketAcquisition)
            .build();

        invoice = invoiceRepository.save(invoice);
        cancellation = invoiceRepository.save(cancellation);

        ApplicationUser finalUser = user;
        Invoice finalCancellation = cancellation;
        Invoice finalInvoice = invoice;
        assertAll(
            () -> assertNull(invoiceRepository.findByIdAndUserId(-2L, -2L)),
            () -> assertNotNull(invoiceRepository.findByIdAndUserId(finalInvoice.getId(), finalUser.getId())),
            () -> assertNotNull(invoiceRepository.findByIdAndUserId(finalCancellation.getId(), finalUser.getId()))
        );
    }

}
