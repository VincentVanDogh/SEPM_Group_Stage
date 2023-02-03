package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TicketMapper {

    /**
     * Method maps a Ticket object to a Ticket DTO.
     *
     * @param ticket Ticket object to be mapped to a DTO
     * @return Ticket DTO with the information of the Ticket object
     */
    @Named("ticket")
    TicketDto ticketToTicketDto(Ticket ticket);

    /**
     * Method maps a list of Ticket objects to a list of Ticket DTOs.
     *
     * @param ticket List of Ticket objects to be mapped to Ticket DTOs
     * @return List of Ticket DTOs with the information of the Ticket objects
     */
    @IterableMapping(qualifiedByName = "ticket")
    List<TicketDto> ticketToTicketDto(List<Ticket> ticket);

    /**
     * Method maps a Ticket DTO to a Ticket object, finding the relevant objects contained in the Ticket Object. (eg.,
     * ApplicationUser object for buyer)
     *
     * @param ticketDto Ticket DTO to be mapped to a Ticket Object
     * @return Ticket object created from the information contained in the Ticket DTO
     */
    Ticket ticketDtoToTicket(TicketDto ticketDto);

    /**
     * Method maps a Ticket object to a TicketDetailsDTO.
     *
     * @param ticket Ticket object to be mapped to a TicketDetailsDTO
     * @return TicketDetailsDTO with the information from the Ticket object
     */
    @Named("ticket")
    TicketDetailsDto ticketToTicketDetailsDto(Ticket ticket);

    /**
     * Method maps a list of Ticket objects to a list of TicketDetailsDTOs.
     *
     * @param tickets List of Ticket objects to be mapped to Ticket DTOs
     * @return List of TicketDetailsDTOs with the information of the Ticket objects
     */
    @IterableMapping(qualifiedByName = "ticket")
    List<TicketDetailsDto> ticketsToTicketDetailsDto(List<Ticket> tickets);
}
