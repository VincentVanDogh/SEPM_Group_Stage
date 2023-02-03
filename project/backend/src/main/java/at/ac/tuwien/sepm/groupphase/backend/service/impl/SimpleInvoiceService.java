package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.InvoiceMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.StagePlanService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class SimpleInvoiceService implements InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final TicketService ticketService;
    private final AddressRepository addressRepository;
    private final StagePlanService stagePlanService;
    private final TicketMapperAlt ticketMapper;

    private final PdfGenerator pdfGenerator;

    public SimpleInvoiceService(InvoiceRepository invoiceRepository,
                                InvoiceMapper invoiceMapper,
                                TicketService ticketService,
                                AddressRepository addressRepository,
                                StagePlanService stagePlanService,
                                TicketMapperAlt ticketMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.ticketService = ticketService;
        this.addressRepository = addressRepository;
        this.stagePlanService = stagePlanService;
        this.ticketMapper = ticketMapper;
        this.pdfGenerator = new PdfGenerator();
    }

    public List<Invoice> getAllForUser(Long userId) {
        LOGGER.debug("Find invoices for user with id {}", userId);
        return invoiceRepository.findAllByUserId(userId);
    }

    public List<Invoice> getAllRegularInvoicesForUser(Long userId) {
        LOGGER.debug("Find invoices for user with id {}", userId);
        return invoiceRepository.findAllRegularInvoicesByUserId(userId);
    }

    public void getById(HttpServletResponse response, Long id, Long userId) throws IOException {
        LOGGER.debug("Find invoice with id {}", id);
        Invoice invoice = invoiceRepository.findByIdAndUserId(id, userId);
        if (invoice != null) {
            pdfGenerator.export(invoice, response);
        } else {
            throw new NotFoundException(String.format("Could not find invoice with id %s", id));
        }
    }

    public void getInvoiceById(HttpServletResponse response, Long referenceNr, Long userId) throws IOException {
        LOGGER.debug("Find invoice with referenceNr {}", referenceNr);
        Invoice invoice = invoiceRepository.findInvoiceByIdAndUserId(referenceNr, userId);
        if (invoice != null) {
            pdfGenerator.export(invoice, response);
        } else {
            throw new NotFoundException(String.format("Could not find invoice with referenceNr %s", referenceNr));
        }
    }

    public void getCancellationById(HttpServletResponse response, Long referenceNr, Long userId) throws IOException {
        LOGGER.debug("Find invoice with referenceNr {}", referenceNr);
        Invoice invoice = invoiceRepository.findCancellationByIdAndUserId(referenceNr, userId);
        if (invoice != null) {
            pdfGenerator.export(invoice, response);
        } else {
            throw new NotFoundException(String.format("Could not find invoice with referenceNr %s", referenceNr));
        }
    }

    public void save(CreateInvoiceDto createInvoiceDto, TicketAcquisition ticketAcquisition, MerchPurchase merchPurchase) {
        LOGGER.debug("Save invoice");
        Invoice invoice = invoiceMapper.createInvoiceDtoToInvoice(createInvoiceDto);
        invoice.setTicketAcquisition(ticketAcquisition);
        invoice.setMerchPurchase(merchPurchase);
        invoice.setDate(LocalDate.now());
        Address add = invoice.getAddress();

        if (add.getId() != null) {
            invoice.setAddress(addressRepository.findById(invoice.getAddress().getId()).get());
            LOGGER.debug("Address found for invoice");
        } else {
            Address a = addressRepository.findFirstByStreetAndCityAndCountryAndPostalCode(
                add.getStreet(), add.getCity(), add.getCountry(), add.getPostalCode());
            if (a != null) {
                LOGGER.debug("Address found for invoice");
                invoice.setAddress(a);
            }
        }

        //inefficient, but found no good alternatives
        //possible venue of investigation: use sequences
        invoice = invoiceRepository.save(invoice);
        int i = 0;
        while (true) {
            i++;
            try {
                Long possibleReferenceNr = invoiceRepository.getLargestReferenceNrForInvoiceType(invoice.getInvoiceType());
                if (possibleReferenceNr == null) {
                    possibleReferenceNr = 1L;
                } else {
                    possibleReferenceNr++;
                }
                //System.err.println(possibleReferenceNr);
                invoice.setReferenceNr(possibleReferenceNr);
                invoiceRepository.saveAndFlush(invoice);
                break;
            } catch (DataIntegrityViolationException e) {
                //try again, but not too often
                if (i > 20) {
                    throw new RuntimeException("Concurrency error while attempting to save invoice");
                }
            }
        }

    }

    public void saveAll(List<Invoice> invoices) {
        invoices.sort(Comparator.comparing(Invoice::getDate));
        Long j = 1L;
        for (Invoice i : invoices) {
            i.setReferenceNr(j++);
        }
        invoiceRepository.saveAll(invoices);
    }

    public List<Invoice> getCancellationsForInvoiceReferenceNo(Long invoiceReferenceNo) {
        return invoiceRepository.getCancellationsForInvoiceReferenceNo(invoiceReferenceNo);
    }

    private Long getInvoiceReferenceNoForCancellation(Long cancellationReferenceNo) {
        return invoiceRepository.getInvoiceReferenceNoForCancellation(cancellationReferenceNo);
    }

    private class PdfGenerator {
        private final Color darkBlueBackground = new Color(2, 31, 84);
        private final Color buttonBlueBackground = new Color(13, 110, 253);
        private final Color white = Color.white;
        private final Color black = Color.black;
        private final String standardFont = FontFactory.HELVETICA;

        private final Address ownAddress = Address.AddressBuilder.anAddress()
            .withStreet("TOHA-Straße 42")
            .withCity("Vienna")
            .withCountry("Austria")
            .withPostalCode(1040)
            .build();
        private final String ownPhone = "Tel. +43 1 588010";
        private final String ownEmail = "E-Mail: support@ticketline.com";

        private final String ownUid = "UID Nr. ATU 4266613";
        private final String ownBank = "Bank: Sparkasse Wien";
        private final String ownFn = "FN 40000 k";
        private final String ownIban = "IBAN: AT93 2026 7000 0340 2938";
        private final String ownCourt = "Court of company registration: Commercial Court Vienna";
        private final String ownBic = "BIC: SPK20267";


        private void export(Invoice invoice, HttpServletResponse response) throws IOException {

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, response.getOutputStream());
            Phrase phrase = new Phrase("");
            phrase.add(new Phrase(
                "Ticketline\n",
                FontFactory.getFont(standardFont, 18, white)));
            phrase.add(new Phrase(ownAddress.getStreet() + " | " + ownAddress.getPostalCode() + " " + ownAddress.getCity() + "\n "
                + ownPhone + " | " + ownEmail,
                FontFactory.getFont(standardFont, 12, white)));
            HeaderFooter header = new HeaderFooter(phrase, false);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(darkBlueBackground);

            document.setHeader(header);

            HeaderFooter footer = new HeaderFooter(new Phrase(ownUid + " | " + ownBank + "\n"
                + ownFn + " | " + ownIban + "\n"
                + ownCourt + " | " + ownBic,
                FontFactory.getFont(standardFont, 12, black)), false);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setBorderWidthBottom(0);

            document.setFooter(footer);

            document.open();
            Address receiverAddress = invoice.getAddress();
            Paragraph recipient = new Paragraph();
            recipient.add(new Paragraph(invoice.getRecipientName()));
            recipient.add(new Paragraph(receiverAddress.getStreet()));
            recipient.add(new Paragraph(receiverAddress.getPostalCode() + " " + receiverAddress.getCity()));
            recipient.add(new Paragraph(receiverAddress.getCountry().toUpperCase()));
            document.add(recipient);

            InvoiceType pdfType = invoice.getInvoiceType();
            Font fontInvoiceDetails = FontFactory.getFont(standardFont);
            fontInvoiceDetails.setSize(12);
            Paragraph invoiceDetails = new Paragraph("Reference no.: \t %s%d \n Date: \t\t %s".formatted(
                pdfType == InvoiceType.REGULAR ? "I" : "C",
                invoice.getReferenceNr(),
                invoice.getDate().format(DateTimeFormatter.ISO_DATE)),
                fontInvoiceDetails);
            invoiceDetails.setAlignment(Paragraph.ALIGN_LEFT);
            invoiceDetails.setIndentationLeft(380f);
            document.add(invoiceDetails);

            Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            fontTitle.setSize(20);
            Paragraph title = new Paragraph("%s".formatted(pdfType == InvoiceType.REGULAR
                ? "Invoice"
                : "Cancellation for tickets from I" + getInvoiceReferenceNoForCancellation(invoice.getReferenceNr())), fontTitle);
            title.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            // Setting width of the table, its columns and spacing
            table.setWidthPercentage(100f);
            table.setWidths(new int[] {1, 3, 1, 1});
            table.setSpacingBefore(5);
            // Create Table Cells for the table header
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(darkBlueBackground);
            cell.setPadding(5);
            Font font = FontFactory.getFont(standardFont, 12);
            font.setColor(CMYKColor.WHITE);
            cell.setPhrase(new Phrase("Quantity", font));
            table.addCell(cell);
            //cell.setPhrase(new Phrase("Unit", font));
            //table.addCell(cell);
            cell.setPhrase(new Phrase("Description", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Unit Price", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Amount", font));
            table.addCell(cell);
            Integer totalPrice = 0;
            if (invoice.getTicketAcquisition() != null) {
                // Iterating the list of tickets
                if (invoice.getInvoiceType().equals(InvoiceType.REGULAR)) {
                    for (Ticket ticket : invoice.getTicketAcquisition().getTickets()) {
                        if (ticket.getReservation() == TicketStatus.PURCHASED) {
                            // Adding ticket amount
                            table.addCell("1");
                            //// Adding ticket unit
                            //table.addCell("piece");
                            // Adding ticket description
                            String ticketName = ticket.toString();
                            if (ticket.getSectorMap().getSector().getStanding()) {
                                ticketName += " Sector " + stagePlanService.getSectorDesignationForStandingTicket(
                                    ticketMapper.ticketToTicketDetailsDto(ticket));
                            } else {
                                int[] rowSeat = stagePlanService.getRowAndSeatNumberOfSeatForSeatedTicket(
                                    ticketMapper.ticketToTicketDetailsDto(ticket));
                                ticketName += " Row " + rowSeat[0] + " Seat " + rowSeat[1];
                            }
                            table.addCell(ticketName);
                            Integer priceInt = ticketService.getPrice(ticket.getId());
                            totalPrice += priceInt;
                            String price = "%.2f".formatted(priceInt / 100.);
                            // Adding ticket unit price
                            table.addCell(price);
                            // Adding ticket total price
                            table.addCell(price);
                        }
                    }
                } else {
                    for (Ticket ticket : invoice.getTicketAcquisition().getCancelledTickets()) {
                        // Adding ticket amount
                        table.addCell("1");
                        //// Adding ticket unit
                        //table.addCell("piece");
                        // Adding ticket description
                        String ticketName = ticket.toString();
                        if (ticket.getSectorMap().getSector().getStanding()) {
                            ticketName += " Sector " + stagePlanService.getSectorDesignationForStandingTicket(
                                ticketMapper.ticketToTicketDetailsDto(ticket));
                        } else {
                            int[] rowSeat = stagePlanService.getRowAndSeatNumberOfSeatForSeatedTicket(
                                ticketMapper.ticketToTicketDetailsDto(ticket));
                            ticketName += " Row " + rowSeat[0] + " Seat " + rowSeat[1];
                        }
                        table.addCell(ticketName);
                        Integer priceInt = ticketService.getPrice(ticket.getId());
                        totalPrice += priceInt;
                        String price = "%.2f".formatted(priceInt / 100.);
                        // Adding ticket unit price
                        table.addCell(price);
                        // Adding ticket total price
                        table.addCell(price);
                    }
                }
            }
            if (invoice.getMerchPurchase() != null) {
                // Iterating the list of merch articles
                for (ArticlePurchaseMapping purchaseMapping : invoice.getMerchPurchase().getArticlePurchaseMapping()) {
                    //ActSectorMapping asm = actSectorMappingRepository.findPriceForTicket(ticket.getId());
                    Integer articleTotalPrice = purchaseMapping.getArticleCount() * purchaseMapping.getMerchArticle().getPrice().intValue();
                    totalPrice += articleTotalPrice;
                    // Adding article amount
                    table.addCell(purchaseMapping.getArticleCount().toString());
                    // Adding article description
                    table.addCell(purchaseMapping.getMerchArticle().getName());
                    // Adding article unit price
                    table.addCell("%.2f".formatted(purchaseMapping.getMerchArticle().getPrice() / 100.));
                    // Adding article total price
                    table.addCell("%.2f".formatted(articleTotalPrice / 100.));
                }
            }

            // Adding the created table to the document
            document.add(table);


            PdfPTable total = new PdfPTable(4);
            // Setting width of the table, its columns and spacing
            total.setWidthPercentage(100f);
            total.setWidths(new int[] {1, 3, 1, 1});
            total.setSpacingBefore(5);

            //brutto amount
            total.addCell((String) null);
            total.addCell("Brutto amount");
            total.addCell("EUR");
            String price = "%.2f".formatted(totalPrice / 100.);
            total.addCell(price);

            // Adding the created table to the document
            document.add(total);


            Paragraph p3 = new Paragraph("Prices contain 20 % USt. Delivery date is payment date.",
                FontFactory.getFont(standardFont, 12));
            document.add(p3);

            document.close();

        }
    }

}
