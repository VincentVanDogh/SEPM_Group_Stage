package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDetailsWithPricesDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public abstract class TicketAcquisitionMapperAlt {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketMapperAlt ticketMapper;

    /**
     * Method maps TicketAcquisition object to TicketAcquisitionDTO.
     *
     * @param ticketAcquisition TicketAcquisition object to be mapped to TicketAcquistionDTO
     * @return TicketAcquisitionDTO with information from TicketAcquisition
     */
    public TicketAcquisitionDto ticketAcquisitionToTicketAcquisitionDto(TicketAcquisition ticketAcquisition) {
        if (ticketAcquisition == null) {
            return null;
        }
        TicketAcquisitionDto result = new TicketAcquisitionDto();

        result.setId(ticketAcquisition.getId());
        result.setBuyerId(ticketAcquisition.getBuyer().getId());

        //LazyInitializationException workaround:
        List<Ticket> ticketsInTa = ticketRepository.findTicketsByTicketOrderId(ticketAcquisition.getId());
        result.setTickets(ticketMapper.ticketToTicketDto(ticketsInTa));

        result.setCancelled(ticketAcquisition.isCancelled());

        return result;
    }

    /**
     * Method maps list of TicketAcquisition objects to list of TicketAcquisitionDTOs.
     *
     * @param ticketAcquisition List of TicketAcquisition objects to be mapped to list of TicketAcquistionDTOs
     * @return List of TicketAcquisitionDTOs with information from TicketAcquisitions
     */
    public abstract List<TicketAcquisitionDto> ticketAcquisitionToTicketAcquisitionDto(List<TicketAcquisition> ticketAcquisition);

    /**
     * Method maps TicketAcquisitionDTO to TicketAcquisition, fetching all the required objects needed for the creation
     * of the TicketAcquisition object. (eg., ApplicationUser for buyer)
     *
     * @param ticketAcquisitionDto TicketAcquisitionDTO to be mapped to TicketAcquisition object
     * @return TicketAcquisition object created from TicketAcquisitonDTO
     */
    public TicketAcquisition ticketAcquisitionDtoToTicketAcquisition(TicketAcquisitionDto ticketAcquisitionDto) {
        if (ticketAcquisitionDto == null) {
            return null;
        }
        TicketAcquisition result = new TicketAcquisition();

        result.setId(ticketAcquisitionDto.getId());

        if (ticketAcquisitionDto.getId() != null) {
            Optional<ApplicationUser> u = userRepository.findById(ticketAcquisitionDto.getBuyerId());
            u.ifPresent(result::setBuyer);
        } else {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            result.setBuyer(user);
        }

        result.setTickets(ticketMapper.ticketDtoToTicket(ticketAcquisitionDto.getTickets()));
        result.setCancelled(ticketAcquisitionDto.isCancelled());

        return result;
    }

    /**
     * Method maps TicketAcquisitionDetailsWithPricesDTO to TicketAcquisition, fetching all the required objects needed for the creation
     * of the TicketAcquisition object. (eg., ApplicationUser for buyer)
     *
     * @param ticketAcquisitionDto TicketAcquisitionDetailsWithPricesDTO to be mapped to TicketAcquisition object
     * @return TicketAcquisition object created from TicketAcquisitionDetailsWithPricesDTO
     */
    public TicketAcquisition ticketAcquisitionDtoToTicketAcquisition(TicketAcquisitionDetailsWithPricesDto ticketAcquisitionDto) {
        if (ticketAcquisitionDto == null) {
            return null;
        }
        TicketAcquisition result = new TicketAcquisition();

        result.setId(ticketAcquisitionDto.id());

        if (ticketAcquisitionDto.id() != null) {
            Optional<ApplicationUser> u = userRepository.findById(ticketAcquisitionDto.buyerId());
            u.ifPresent(result::setBuyer);
        } else {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            result.setBuyer(user);
        }

        result.setTickets(ticketMapper.ticketDetailsDtoToTicket(ticketAcquisitionDto.tickets()));
        result.setCancelled(ticketAcquisitionDto.cancelled());

        return result;
    }

    /**
     * Method maps TicketAcquisition object to TicketAcquisitionDetailsDTO.
     *
     * @param ticketAcquisition TicketAcquisition object to be mapped to TicketAcquisitionDetailsDTO
     * @return TicketAcquisitionDetailsDTO with information from TicketAcquisition
     */
    public TicketAcquisitionDetailsDto ticketAcquisitionToTicketAcquisitionDetailsDto(TicketAcquisition ticketAcquisition) {
        if (ticketAcquisition == null) {
            return null;
        }
        TicketAcquisitionDetailsDto result = new TicketAcquisitionDetailsDto();

        result.setId(ticketAcquisition.getId());
        result.setBuyerId(ticketAcquisition.getBuyer().getId());
        result.setTickets(ticketMapper.ticketToTicketDetailsDto(ticketAcquisition.getTickets()));
        result.setCancelled(ticketAcquisition.isCancelled());

        return result;
    }

    /**
     * Method maps collection of TicketAcquisition objects to list of TicketAcquisitionDetailsDTOs.
     *
     * @param ticketAcquisition Collection of TicketAcquisition objects to be mapped to TicketAcquisitionDetailsDTOs
     * @return List of TicketAcquisitionDetailsDTO with information from TicketAcquisitions
     */
    public abstract List<TicketAcquisitionDetailsDto> ticketAcquisitionToTicketAcquisitionDetailsDto(Collection<TicketAcquisition> ticketAcquisition);

    /**
     * Method maps TicketAcquisition used for ticket cancellation to TicketAcquisitionDetailsWithPricesDTO.
     *
     * @param ticketCancellation TicketAcquisition used for ticket cancellation to be mapped to TicketAcquisitionDetailsWithPricesDTO
     * @return TicketAcquisitionDetailsWithPricesDTO with information from given TicketAcquisition used for ticket cancellation
     */
    @DoIgnore
    public TicketAcquisitionDetailsWithPricesDto ticketCancellationToTicketAcquisitionDetailsWithPricesDto(TicketAcquisition ticketCancellation) {
        if (ticketCancellation == null) {
            return null;
        }

        return new TicketAcquisitionDetailsWithPricesDto(
            ticketCancellation.getId(),
            ticketCancellation.getBuyer().getId(),
            ticketMapper.ticketToTicketDetailsDto(ticketCancellation.getCancelledTickets()),
            ticketCancellation.isCancelled()
        );
    }

    /**
     * Method maps TicketAcquisition to TicketAcquisitionDetailsWithPricesDTO.
     *
     * @param ticketAcquisition TicketAcquisition to be mapped to TicketAcquisitionDetailsWithPricesDTO
     * @return TicketAcquisitionDetailsWithPricesDTO with information from given TicketAcquisition
     */
    public TicketAcquisitionDetailsWithPricesDto ticketAcquisitionToTicketAcquisitionDetailsWithPricesDto(TicketAcquisition ticketAcquisition) {
        if (ticketAcquisition == null) {
            return null;
        }

        return new TicketAcquisitionDetailsWithPricesDto(
            ticketAcquisition.getId(),
            ticketAcquisition.getBuyer().getId(),
            ticketMapper.ticketToTicketDetailsDto(ticketAcquisition.getTickets()),
            ticketAcquisition.isCancelled()
        );
    }

    /**
     * Method maps collection of TicketAcquisition objects to list of TicketAcquisitionDetailsWithPricesDTOs.
     *
     * @param ticketAcquisition Collection of TicketAcquisition objects to be mapped to TicketAcquisitionDetailsWithPricesDTOs
     * @return List of TicketAcquisitionDetailsWithPricesDTO with information from given TicketAcquisitions
     */
    public abstract List<TicketAcquisitionDetailsWithPricesDto> ticketAcquisitionToTicketAcquisitionDetailsWithPricesDto(Collection<TicketAcquisition> ticketAcquisition);

    /**
     * Method maps a TicketAcquisition object to a Reservation DTO.
     *
     * @param ticketAcquisition TicketAcquisition object to be mapped to a Reservation DTO
     * @return Reservation DTO with information from TicketAcquisition
     */
    public ReservationDto ticketAcquisitionToReservationDto(TicketAcquisition ticketAcquisition) {
        if (ticketAcquisition == null) {
            return null;
        }

        return new ReservationDto(
            ticketAcquisition.getId(),
            ticketAcquisition.getBuyer().getId(),
            ticketMapper.ticketToTicketDetailsDto(ticketAcquisition.getTickets())
        );
    }

    /**
     * Method maps a collection of TicketAcquisition objects to a list of Reservation DTOs.
     *
     * @param ticketAcquisitions Collection of TicketAcquisition objects to be mapped to Reservation DTOs
     * @return List of Reservation DTOs with information from TicketAcquisitions
     */
    public abstract List<ReservationDto> ticketAcquisitionToReservationDto(Collection<TicketAcquisition> ticketAcquisitions);

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface DoIgnore {
    }
}
