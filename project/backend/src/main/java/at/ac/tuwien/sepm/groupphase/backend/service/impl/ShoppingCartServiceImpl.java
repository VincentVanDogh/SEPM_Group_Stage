package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShoppingCartDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchPurchaseMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketAcquisitionMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapperAlt;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchPurchaseService;
import at.ac.tuwien.sepm.groupphase.backend.service.ShoppingCartService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketAcquisitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.validation.InvoiceValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketAcquisitionService ticketAcquisitionService;
    private final MerchPurchaseService merchPurchaseService;
    private final MerchPurchaseMapper merchPurchaseMapper;
    private final TicketMapperAlt ticketMapper;
    private final TicketAcquisitionMapperAlt ticketAcquisitionMapper;
    private final InvoiceService invoiceService;
    private final InvoiceValidator invoiceValidator;

    public ShoppingCartServiceImpl(TicketAcquisitionService ticketAcquisitionService,
                                   MerchPurchaseService merchPurchaseService,
                                   MerchPurchaseMapper merchPurchaseMapper,
                                   TicketMapperAlt ticketMapper, TicketAcquisitionMapperAlt ticketAcquisitionMapper,
                                   InvoiceService invoiceService,
                                   InvoiceValidator invoiceValidator) {
        this.ticketAcquisitionService = ticketAcquisitionService;
        this.merchPurchaseService = merchPurchaseService;
        this.merchPurchaseMapper = merchPurchaseMapper;
        this.ticketMapper = ticketMapper;
        this.ticketAcquisitionMapper = ticketAcquisitionMapper;
        //this.ticketService = ticketService;
        this.invoiceService = invoiceService;
        this.invoiceValidator = invoiceValidator;
    }

    @Override
    public ShoppingCartDto finalizePurchase(CreateInvoiceDto invoiceDto) throws ValidationException, ConflictException {
        LOGGER.debug("Items stored in the shopping cart finalized as purchases");
        List<Ticket> ticketsInCart = ticketAcquisitionService.ticketsInCart();
        boolean ticketsEmpty = ticketsInCart.isEmpty();
        boolean merchEmpty =  merchPurchaseService.isCartEmpty();
        int size = 0;

        invoiceValidator.validateInvoice(invoiceDto);
        if (ticketsEmpty && merchEmpty) {
            throw new ValidationException(
                    "Error purchasing merch/tickets",
                    List.of("Cannot submit an empty shopping cart")
            );
        }
        TicketAcquisition ticketPurchase = null;
        MerchPurchase merchPurchase = null;
        boolean noInvoiceForTickAcquNeeded = true;
        if (!ticketsEmpty) {
            for (Ticket t : ticketsInCart) {
                if (t.getReservation().equals(TicketStatus.PURCHASED)) {
                    noInvoiceForTickAcquNeeded = false;
                    break;
                }
            }
            ticketPurchase = ticketAcquisitionService.saveRaw();
        }
        if (!merchEmpty) {
            merchPurchase =  merchPurchaseService.finalizeMerchPurchaseRaw();
            size += merchPurchase.getArticlePurchaseMapping().size();
        }

        if (!merchEmpty || !noInvoiceForTickAcquNeeded) {
            invoiceService.save(
                    invoiceDto,
                    ticketPurchase,
                    merchPurchase);
        }

        return createShoppingCartDto(
                ticketAcquisitionMapper.ticketAcquisitionToTicketAcquisitionDetailsDto(ticketPurchase),
                merchPurchaseMapper.merchPurchaseToMerchPurchaseDto(merchPurchase),
                size,
                merchPurchaseService.getBonusPoints()
        );
    }

    @Override
    public ShoppingCartDto getShoppingCart() {
        // Merch
        List<MerchPurchaseDto> merch = merchPurchaseService.getMerchPurchases(false);
        MerchPurchaseDto merchInCart = merch == null || merch.isEmpty() ? null : merch.get(0);
        int size = 0;

        if (merchInCart != null) {
            size += merchInCart.getArticles().size();
        }

        // Tickets
        TicketAcquisitionDetailsDto ticketAcq = new TicketAcquisitionDetailsDto();
        ticketAcq.setId(null);
        ticketAcq.setBuyerId(null);
        ticketAcq.setCancelled(false);
        List<TicketDetailsDto> ticketsInCart = ticketMapper.ticketToTicketDetailsDto(ticketAcquisitionService.ticketsInCart());
        ticketAcq.setTickets(ticketsInCart);

        if (ticketsInCart != null) {
            size += ticketsInCart.size();
        }

        return createShoppingCartDto(
                ticketAcq,
                merchInCart,
                size,
                merchPurchaseService.getBonusPointsInCart()
        );
    }

    private ShoppingCartDto createShoppingCartDto(TicketAcquisitionDetailsDto ticketAcq,
                                                  MerchPurchaseDto merchPurchase,
                                                  int size,
                                                  Long bonusPoints) {
        ShoppingCartDto cart = new ShoppingCartDto();
        cart.setTicketAcquisition(ticketAcq);
        cart.setMerchPurchase(merchPurchase);
        cart.setSize(size);
        cart.setBonusPoints(bonusPoints);
        return cart;
    }
}

