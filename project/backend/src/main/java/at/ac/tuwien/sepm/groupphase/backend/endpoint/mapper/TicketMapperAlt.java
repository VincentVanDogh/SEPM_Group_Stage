package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsWithPriceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.StagePlanService;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public abstract class TicketMapperAlt {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActRepository actRepository;
    @Autowired
    private SectorMapRepository sectorMapRepository;
    @Autowired
    private ActMapper actMapper;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private StagePlanService stagePlanService;

    /**
     * Method maps a Ticket object to a TicketDetailsDTO.
     *
     * @param ticket Ticket object to be mapped to a TicketDetailsDTO
     * @return TicketDetailsDTO with the information from the Ticket object
     */
    public TicketDetailsDto ticketToTicketDetailsDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        TicketDetailsDto result = new TicketDetailsDto();

        result.setId(ticket.getId());
        result.setBuyerId(ticket.getBuyer().getId());
        result.setAct(actMapper.actToActDetailsDto(ticket.getAct()));
        result.setSectorMapId(ticket.getSectorMap().getId());
        result.setCreationDate(ticket.getCreationDate());
        result.setSeatNo(ticket.getSeatNo());
        result.setTicketFirstName(ticket.getTicketFirstName());
        result.setTicketLastName(ticket.getTicketLastName());
        result.setReservation(ticket.getReservation());
        result.setCancelled(ticket.isCancelled());
        result.setPrice(ticketRepository.getPriceForTicket(ticket.getId()));

        if (result.getSeatNo() == 0) {
            result.setSectorDesignation(stagePlanService.getSectorDesignationForStandingTicket(result));
        } else {
            int[] rowNoAndSeatInRow = stagePlanService.getRowAndSeatNumberOfSeatForSeatedTicket(result);
            result.setRowNumber(rowNoAndSeatInRow[0]);
            result.setSeatNoInRow(rowNoAndSeatInRow[1]);
        }

        return result;
    }

    /**
     * Method maps a collection of Ticket objects to a List of TicketDetailsDTOs.
     *
     * @param tickets Collection of Ticket objects to be mapped to a Collection of TicketDetailsDTOs
     * @return Collection of TicketDetailsDTOs with the information from the Ticket objects
     */
    public abstract List<TicketDetailsDto> ticketToTicketDetailsDto(Collection<Ticket> tickets);

    /**
     * Method maps a Ticket object to a TicketDetailsWithPriceDTO.
     *
     * @param ticket Ticket object to be mapped to a TicketDetailsWithPriceDTO
     * @return TicketDetailsWithPriceDTO with the information from the Ticket object
     */
    public TicketDetailsWithPriceDto ticketToTicketDetailsWithPriceDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return new TicketDetailsWithPriceDto(
            ticket.getId(),
            ticket.getBuyer().getId(),
            actMapper.actToActDetailsDto(ticket.getAct()),
            ticket.getSectorMap().getId(),
            ticket.getCreationDate(),
            ticket.getSeatNo(),
            ticket.getTicketFirstName(),
            ticket.getTicketLastName(),
            ticket.getReservation(),
            ticket.isCancelled(),
            ticketRepository.getPriceForTicket(ticket.getId())
        );
    }

    /**
     * Method maps a Collection of Ticket objects to a List of TicketDetailsWithPriceDTOs.
     *
     * @param tickets List of Ticket objects to be mapped to a List of TicketDetailsWithPriceDTOs
     * @return List of TicketDetailsWithPriceDTOs with the information from the Ticket objects
     */
    public abstract List<TicketDetailsWithPriceDto> ticketToTicketDetailsWithPriceDto(Collection<Ticket> tickets);

    /**
     * Method maps a Ticket object to a Ticket DTO.
     *
     * @param ticket Ticket object to be mapped to a DTO
     * @return Ticket DTO with the information of the Ticket object
     */
    public TicketDto ticketToTicketDto(Ticket ticket) {
        TicketDto result = new TicketDto();

        result.setId(ticket.getId());
        if (ticket.getBuyer() != null) {
            result.setBuyerId(ticket.getBuyer().getId());
        }
        result.setActId(ticket.getAct().getId());
        result.setSectorMapId(ticket.getSectorMap().getId());
        if (ticket.getCreationDate() != null) {
            result.setCreationDate(ticket.getCreationDate());
        } else {
            result.setCreationDate(ticketRepository.findTicketById(result.getId()).getCreationDate());
        }
        result.setSeatNo(ticket.getSeatNo());
        result.setTicketFirstName(ticket.getTicketFirstName());
        result.setTicketLastName(ticket.getTicketLastName());
        result.setReservation(ticket.getReservation());
        result.setCancelled(ticket.isCancelled());

        return result;
    }

    /**
     * Method maps a collection of Ticket objects to a list of Ticket DTOs.
     *
     * @param ticket Collection of Ticket objects to be mapped to Ticket DTOs
     * @return Collection of Ticket DTOs with the information of the Ticket objects
     */
    public abstract List<TicketDto> ticketToTicketDto(Collection<Ticket> ticket);

    /**
     * Method maps a Ticket DTO to a Ticket object, finding the relevant objects contained in the Ticket Object. (eg.,
     * ApplicationUser object for buyer)
     *
     * @param ticketDto Ticket DTO to be mapped to a Ticket Object
     * @return Ticket object created from the information contained in the Ticket DTO
     */
    public Ticket ticketDtoToTicket(TicketDto ticketDto) {
        Ticket result = new Ticket();

        result.setId(ticketDto.getId());

        if (ticketDto.getBuyerId() != null) {
            // If Buyer ID is present, take this ID number.
            // Assumption: ticketDto is already validated
            Optional<ApplicationUser> buyer = userRepository.findById(ticketDto.getBuyerId());
            buyer.ifPresent(result::setBuyer);
        } else {
            // If no Buyer ID present, take ID of user logged in
            if (SecurityContextHolder.getContext().getAuthentication() != null) { // To avoid NullPointerException
                String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                ApplicationUser user = userRepository.findApplicationUserByEmail(username);
                result.setBuyer(user);
            }
        }

        Optional<Act> act = actRepository.findById(ticketDto.getActId());
        act.ifPresent(result::setAct);

        Optional<SectorMap> sectorMap = sectorMapRepository.findById(ticketDto.getSectorMapId());
        sectorMap.ifPresent(result::setSectorMap);

        if (ticketDto.getCreationDate() != null) {
            result.setCreationDate(ticketDto.getCreationDate());
        } else {
            if (result.getId() != null) {
                result.setCreationDate(ticketRepository.findTicketById(result.getId()).getCreationDate());
            }
        }
        result.setSeatNo(ticketDto.getSeatNo());
        result.setTicketFirstName(ticketDto.getTicketFirstName());
        result.setTicketLastName(ticketDto.getTicketLastName());
        result.setReservation(ticketDto.getReservation());
        result.setCancelled(ticketDto.isCancelled());

        return result;
    }

    /**
     * Method maps a collection of Ticket DTOs to a list of Ticket objects, finding the relevant objects contained
     * in the Ticket Object. (eg., ApplicationUser object for buyer)
     *
     * @param ticketDto Collection of Ticket DTOs to be mapped to a collection of Ticket Objects
     * @return Collection of Ticket objects created from the information contained in the Ticket DTOs
     */
    public abstract List<Ticket> ticketDtoToTicket(Collection<TicketDto> ticketDto);

    /**
     * Method maps a TicketDetailsDTO to a Ticket object, finding the relevant objects contained in the Ticket Object. (eg.,
     * ApplicationUser object for buyer)
     *
     * @param ticketDto TicketDetailsDTO to be mapped to a Ticket Object
     * @return Ticket object created from the information contained in the TicketDetailsDTO
     */
    public Ticket ticketDetailsDtoToTicket(TicketDetailsDto ticketDto) {
        Ticket result = new Ticket();

        result.setId(ticketDto.getId());

        if (ticketDto.getBuyerId() != null) {
            // If Buyer ID is present, take this ID number.
            // Assumption: ticketDto is already validated
            Optional<ApplicationUser> buyer = userRepository.findById(ticketDto.getBuyerId());
            buyer.ifPresent(result::setBuyer);
        } else {
            // If no Buyer ID present, take ID of user logged in
            if (SecurityContextHolder.getContext().getAuthentication() != null) { // To avoid NullPointerException
                String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                ApplicationUser user = userRepository.findApplicationUserByEmail(username);
                result.setBuyer(user);
            }
        }

        Optional<Act> act = actRepository.findById(ticketDto.getAct().id());
        act.ifPresent(result::setAct);

        Optional<SectorMap> sectorMap = sectorMapRepository.findById(ticketDto.getSectorMapId());
        sectorMap.ifPresent(result::setSectorMap);

        if (ticketDto.getCreationDate() != null) {
            result.setCreationDate(ticketDto.getCreationDate());
        } else {
            if (result.getId() != null) {
                result.setCreationDate(ticketRepository.findTicketById(result.getId()).getCreationDate());
            }
        }
        result.setSeatNo(ticketDto.getSeatNo());
        result.setTicketFirstName(ticketDto.getTicketFirstName());
        result.setTicketLastName(ticketDto.getTicketLastName());
        result.setReservation(ticketDto.getReservation());
        result.setCancelled(ticketDto.isCancelled());

        return result;
    }

    /**
     * Method maps a collection of TicketDetailsDTOs to a List of Ticket objects, finding the relevant objects contained
     * in the Ticket Object.(eg., ApplicationUser object for buyer)
     *
     * @param ticketDto Collection of TicketDetailsDTOs to be mapped to a list of Ticket Objects
     * @return List of Ticket objects created from the information contained in the TicketDetailsDTOs
     */
    public abstract List<Ticket> ticketDetailsDtoToTicket(Collection<TicketDetailsDto> ticketDto);
}
