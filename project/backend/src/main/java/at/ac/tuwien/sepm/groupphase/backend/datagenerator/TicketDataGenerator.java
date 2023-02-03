package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketAcquisitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Optional;

@Component
public class TicketDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ActRepository actRepository;
    private final TicketAcquisitionRepository ticketAcquisitionRepository;
    private final InvoiceService invoiceService;

    private static final Faker faker = new Faker(new Random(1));
    private static final Faker localFaker = new Faker(new Locale("de", "at"), new Random(1));

    public TicketDataGenerator(TicketRepository ticketRepository,
                               UserRepository userRepository,
                               ActRepository actRepository,
                               TicketAcquisitionRepository ticketAcquisitionRepository,
                               InvoiceService invoiceService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.actRepository = actRepository;
        this.ticketAcquisitionRepository = ticketAcquisitionRepository;
        this.invoiceService = invoiceService;
    }

    @Transactional
    public void generateTickets() {
        if (!ticketRepository.findAll().isEmpty()) {
            LOGGER.info("Tickets were already generated");
            return;
        }

        LOGGER.info("Generating Tickets");
        generatePurchases();
    }

    private void generatePurchases() {
        LOGGER.debug("generatePurchases()");

        List<ApplicationUser> applicationUsers = new ArrayList<>(userRepository.findAll().stream().limit(501).toList());
        applicationUsers.remove(0); //
        List<Act> acts = actRepository.findAll();
        int actsSize = acts.size();
        acts = acts.stream().toList().stream().limit((int) (actsSize * 0.7)).toList();

        List<Ticket> tickets = new ArrayList<>();
        List<TicketAcquisition> ticketAcquisitions = new ArrayList<>();
        List<Invoice> invoices = new ArrayList<>();

        for (int i = 0; i < 2000; i++) {

            ApplicationUser buyer = applicationUsers.get(faker.number().positive() % applicationUsers.size());

            TicketAcquisition ticketAcquisition = new TicketAcquisition();
            ticketAcquisition.setBuyer(buyer);
            ticketAcquisition.setCancelled(false);
            ticketAcquisitions.add(ticketAcquisition);

            Act act = acts.get(i % acts.size());
            List<ActSectorMapping> actSectorMappings = act.getSectorMaps();
            SectorMap sectorMap = actSectorMappings.get(faker.number().positive() % actSectorMappings.size()).getSectorMap();
            Sector sector = sectorMap.getSector();
            LocalDateTime date = LocalDateTime.now().minusDays(faker.number().positive() % 30);


            Ticket ticket = new Ticket();
            ticket.setAct(act);
            ticket.setBuyer(buyer);
            ticket.setCreationDate(date);
            ticket.setSectorMap(sectorMap);
            ticket.setTicketFirstName(buyer.getFirstName());
            ticket.setTicketLastName(buyer.getLastName());
            ticket.setSeatNo(sectorMap.getFirstSeatNr() + (faker.number().positive() % sector.getNumberOfSeats()));
            ticket.setReservation(TicketStatus.PURCHASED);
            ticket.setCancelled(false);
            ticket.setTicketOrder(ticketAcquisition);
            tickets.add(ticket);

            net.datafaker.providers.base.Address fakeAdress = localFaker.address();
            Address address = new Address(fakeAdress.city(), fakeAdress.city(), "Austria", Integer.parseInt(fakeAdress.postcode()));
            Invoice invoice = Invoice.InvoiceBuilder.anInvoice()
                .withInvoiceType(InvoiceType.REGULAR)
                .withAddress(address)
                .withDate(date.toLocalDate())
                .withRecipientName(buyer.getFirstName() + " " + buyer.getLastName())
                .withTicketAcquisition(ticketAcquisition)
                .build();
            invoices.add(invoice);
        }

        ticketAcquisitionRepository.saveAll(ticketAcquisitions);
        ticketRepository.saveAll(tickets);
        invoiceService.saveAll(invoices);

        for (Ticket ticket : tickets) {
            Long actId = ticket.getActId();
            Optional<Act> optionalAct = actRepository.findById(actId);
            if (optionalAct.isPresent()) {
                Act act = optionalAct.get();
                int nrOfTicketsSold = act.getNrTicketsSold() + 1;
                act.setNrTicketsSold(nrOfTicketsSold);

                actRepository.save(act);
            } else {
                throw new NotFoundException("Act with Id " + actId + " not found");
            }
        }
    }
}
