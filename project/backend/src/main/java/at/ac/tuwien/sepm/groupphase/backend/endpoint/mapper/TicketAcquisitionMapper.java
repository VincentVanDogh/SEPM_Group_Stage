package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketAcquisitionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketAcquisition;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TicketAcquisitionMapper {

    /**
     * Method maps TicketAcquisition object to TicketAcquisitionDTO.
     *
     * @param ticketAcquisition TicketAcquisition object to be mapped to TicketAcquistionDTO
     * @return TicketAcquisitionDTO with information from TicketAcquisition
     */
    @Named("ticketAcquisition")
    TicketAcquisitionDto ticketAcquisitionToTicketAcquisitionDto(TicketAcquisition ticketAcquisition);

    /**
     * Method maps list of TicketAcquisition objects to list of TicketAcquisitionDTOs.
     *
     * @param ticketAcquisition List of TicketAcquisition objects to be mapped to list of TicketAcquistionDTOs
     * @return List of TicketAcquisitionDTOs with information from TicketAcquisitions
     */
    @IterableMapping(qualifiedByName = "ticketAcquisition")
    List<TicketAcquisitionDto> ticketAcquisitionToTicketAcquisitionDto(List<TicketAcquisition> ticketAcquisition);

    /**
     * Method maps TicketAcquisitionDTO to TicketAcquisition, fetching all the required objects needed for the creation
     * of the TicketAcquisition object (eg., ApplicationUser for buyer).
     *
     * @param ticketAcquisitionDto TicketAcquisitionDTO to be mapped to TicketAcquisition object
     * @return TicketAcquisition object created from TicketAcquisitonDTO
     */
    TicketAcquisition ticketAcquisitionDtoToTicketAcquisition(TicketAcquisitionDto ticketAcquisitionDto);
}
