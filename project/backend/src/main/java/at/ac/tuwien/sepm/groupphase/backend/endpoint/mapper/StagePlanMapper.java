package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActSpecificSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpecificSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanTemplateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.type.SeatStatus;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper
public abstract class StagePlanMapper {

    public StagePlanDto toStagePlanDto(Stage stage) {
        StagePlanDto stagePlanDto = new StagePlanDto();
        stagePlanDto.setStageId(stage.getId());
        stagePlanDto.setName(stage.getName());
        stagePlanDto.setSectorArray(sectorMapToSpecificSectorDto(stage.getStageTemplate().getSectorMaps()));
        Integer totalSeatsNr = 0;
        if (stagePlanDto.getSectorArray() != null) {
            for (SpecificSectorDto map : stagePlanDto.getSectorArray()) {
                if (map != null) {
                    totalSeatsNr += map.getNumSeats();
                }
            }
        }
        stagePlanDto.setTotalSeatsNr(totalSeatsNr);
        return stagePlanDto;
    }

    public abstract List<StagePlanDto> toStagePlanDto(Collection<Stage> stages);

    public StagePlanTemplateDto toStagePlanTemplateDto(StageTemplate stageTemplate) {
        SpecificSectorDto[] specificSectorDtos = sectorMapToSpecificSectorDto(stageTemplate.getSectorMaps());
        Integer totalSeatsNr = 0;
        if (specificSectorDtos != null) {
            for (SpecificSectorDto map : specificSectorDtos) {
                if (map != null) {
                    totalSeatsNr += map.getNumSeats();
                }
            }
        }
        return new StagePlanTemplateDto(
            stageTemplate.getId(),
            stageTemplate.getName(),
            totalSeatsNr,
            specificSectorDtos
        );
    }

    public abstract List<StagePlanTemplateDto> toStagePlanTemplateDto(Collection<StageTemplate> stageTemplates);

    @Mapping(target = "numSeats", source = "sectorMap.sector.numberOfSeats")
    @Mapping(target = "standing", source = "sectorMap.sector.standing")
    @Mapping(target = "numRows", source = "sectorMap.sector.numberRows")
    @Mapping(target = "numColumns", source = "sectorMap.sector.numberColumns")
    public abstract SpecificSectorDto sectorMapToSpecificSectorDto(SectorMap sectorMap);

    public SpecificSectorDto[] sectorMapToSpecificSectorDto(Collection<SectorMap> sectorMap) {
        if (sectorMap == null) {
            return null;
        }
        SpecificSectorDto[] sectorMapGrid = new SpecificSectorDto[7 * 7];
        for (SectorMap map : sectorMap) {
            sectorMapGrid[(map.getSectorX() + 3) + 7 * (map.getSectorY() + 3)] = sectorMapToSpecificSectorDto(map);
        }
        return sectorMapGrid;
    }

    public ActStagePlanDto toActStagePlanDto(Act act) {
        Stage stage = act.getStage();
        ActStagePlanDto stagePlanDto = new ActStagePlanDto();
        stagePlanDto.setActId(act.getId());
        stagePlanDto.setStageId(stage.getId());
        stagePlanDto.setName(stage.getName());

        stagePlanDto.setSectorArray(toActSpecificSectorDto(stage.getStageTemplate().getSectorMaps(), act));
        Integer totalSeatsNr = 0;
        if (stagePlanDto.getSectorArray() != null) {
            for (ActSpecificSectorDto map : stagePlanDto.getSectorArray()) {
                if (map != null) {
                    totalSeatsNr += map.getNumSeats();
                }
            }
        }
        stagePlanDto.setTotalSeatsNr(totalSeatsNr);
        return stagePlanDto;
    }

    public abstract List<ActStagePlanDto> toActStagePlanDto(Collection<Act> acts);

    public ActSpecificSectorDto toActSpecificSectorDto(SectorMap sectorMap, Act act) {
        ActSpecificSectorDto actSpecificSectorDto = new ActSpecificSectorDto();
        //basic stuff
        actSpecificSectorDto.setOrientation(sectorMap.getOrientation());
        actSpecificSectorDto.setFirstSeatNr(sectorMap.getFirstSeatNr());
        actSpecificSectorDto.setNumSeats(sectorMap.getSector().getNumberOfSeats());
        actSpecificSectorDto.setStanding(sectorMap.getSector().getStanding());
        actSpecificSectorDto.setNumRows(sectorMap.getSector().getNumberRows());
        actSpecificSectorDto.setNumColumns(sectorMap.getSector().getNumberColumns());
        actSpecificSectorDto.setId(sectorMap.getId());

        //here we actually need to do some work
        ActSectorMapping actSectorMap = null;
        for (ActSectorMapping actSectorMapping : act.getSectorMaps()) {
            if (actSectorMapping.getSectorMap().equals(sectorMap)) {
                actSectorMap = actSectorMapping;
            }
        }

        if (actSectorMap != null) {
            List<Ticket> tickets =  act.getTickets();
            Integer lastSeatNr = actSpecificSectorDto.getFirstSeatNr() + actSpecificSectorDto.getNumSeats() - 1;
            Integer numsReservedPlaces = 0;
            Integer numsSoldPlaces = 0;
            SeatStatus[] transformedSeatsStatusMap = new SeatStatus[sectorMap.getSector().getNumberOfSeats()];
            Arrays.fill(transformedSeatsStatusMap, SeatStatus.FREE);
            for (Ticket ticket : tickets) {
                Integer seatNr = ticket.getSeatNo();
                if (ticket.isCancelled()) {
                    continue;
                }
                if (seatNr >= actSpecificSectorDto.getFirstSeatNr() && seatNr <= lastSeatNr) {
                    TicketStatus ticketStatus = ticket.getReservation();
                    if (ticketStatus == TicketStatus.PURCHASED) {
                        transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.SOLD;
                        numsSoldPlaces++;
                    } else if (ticketStatus == TicketStatus.RESERVED) {
                        transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.RESERVED;
                        numsReservedPlaces++;
                    } else if (ticketStatus == TicketStatus.INITIALISED) {
                        if (ticket.getCreationDate().isAfter(LocalDateTime.now().minusMinutes(10L))) {
                            transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.RESERVED;
                            numsReservedPlaces++;
                        }
                    } else {
                        throw new RuntimeException("Ticket without ticket status");
                    }
                } else if (seatNr == 0 && Objects.equals(ticket.getSectorMap().getId(), actSpecificSectorDto.getId())) {
                    TicketStatus ticketStatus = ticket.getReservation();
                    if (ticketStatus == TicketStatus.PURCHASED) {
                        if (!ticket.isCancelled()) {
                            numsSoldPlaces++;
                        }
                    } else if (ticketStatus == TicketStatus.RESERVED) {
                        if (!ticket.isCancelled()) {
                            numsReservedPlaces++;
                        }
                    } else if (ticketStatus == TicketStatus.INITIALISED) {
                        if (!ticket.isCancelled() && ticket.getCreationDate().isAfter(LocalDateTime.now().minusMinutes(10L))) {
                            numsReservedPlaces++;
                        }
                    } else {
                        throw new RuntimeException("Ticket without ticket status");
                    }
                }
            }

            actSpecificSectorDto.setNumReservedPlaces(numsReservedPlaces);
            actSpecificSectorDto.setNumBoughtPlaces(numsSoldPlaces);
            actSpecificSectorDto.setSeatStatusMap(transformedSeatsStatusMap);
            actSpecificSectorDto.setPrice(actSectorMap.getPrice());
        } else {
            throw new RuntimeException("Error matching sectors in toActSpecificSectorDto");
        }
        return actSpecificSectorDto;
    }

    public ActSpecificSectorDto[] toActSpecificSectorDto(Collection<SectorMap> sectorMap, Act act) {
        if (sectorMap == null) {
            return null;
        }
        ActSpecificSectorDto[] sectorMapGrid = new ActSpecificSectorDto[7 * 7];
        for (SectorMap map : sectorMap) {
            sectorMapGrid[(map.getSectorX() + 3) + 7 * (map.getSectorY() + 3)] = toActSpecificSectorDto(map, act);
        }
        return sectorMapGrid;
    }

    public ActStagePlanDto toActStagePlanDtoForUser(Act act, List<Ticket> tickets) {
        Stage stage = act.getStage();
        ActStagePlanDto stagePlanDto = new ActStagePlanDto();
        stagePlanDto.setActId(act.getId());
        stagePlanDto.setStageId(stage.getId());
        stagePlanDto.setName(stage.getName());

        stagePlanDto.setSectorArray(toActSpecificSectorDtoForUser(stage.getStageTemplate().getSectorMaps(), act, tickets));
        Integer totalSeatsNr = 0;
        if (stagePlanDto.getSectorArray() != null) {
            for (ActSpecificSectorDto map : stagePlanDto.getSectorArray()) {
                if (map != null) {
                    totalSeatsNr += map.getNumSeats();
                }
            }
        }
        stagePlanDto.setTotalSeatsNr(totalSeatsNr);
        return stagePlanDto;
    }

    public ActSpecificSectorDto toActSpecificSectorDtoForUser(SectorMap sectorMap, Act act, List<Ticket> tickets) {
        ActSpecificSectorDto actSpecificSectorDto = new ActSpecificSectorDto();
        //basic stuff
        actSpecificSectorDto.setOrientation(sectorMap.getOrientation());
        actSpecificSectorDto.setFirstSeatNr(sectorMap.getFirstSeatNr());
        actSpecificSectorDto.setNumSeats(sectorMap.getSector().getNumberOfSeats());
        actSpecificSectorDto.setStanding(sectorMap.getSector().getStanding());
        actSpecificSectorDto.setNumRows(sectorMap.getSector().getNumberRows());
        actSpecificSectorDto.setNumColumns(sectorMap.getSector().getNumberColumns());
        actSpecificSectorDto.setId(sectorMap.getId());

        //here we actually need to do some work
        ActSectorMapping actSectorMap = null;
        for (ActSectorMapping actSectorMapping : act.getSectorMaps()) {
            if (actSectorMapping.getSectorMap().equals(sectorMap)) {
                actSectorMap = actSectorMapping;
            }
        }

        if (actSectorMap != null) {
            Integer lastSeatNr = actSpecificSectorDto.getFirstSeatNr() + actSpecificSectorDto.getNumSeats() - 1;
            Integer numsReservedPlaces = 0;
            Integer numsSoldPlaces = 0;
            SeatStatus[] transformedSeatsStatusMap = new SeatStatus[sectorMap.getSector().getNumberOfSeats()];
            Arrays.fill(transformedSeatsStatusMap, SeatStatus.FREE);
            for (Ticket ticket : tickets) {
                Integer seatNr = ticket.getSeatNo();
                if (seatNr >= actSpecificSectorDto.getFirstSeatNr() && seatNr <= lastSeatNr) {
                    TicketStatus ticketStatus = ticket.getReservation();
                    if (ticketStatus == TicketStatus.PURCHASED) {
                        transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.SOLD;
                        numsSoldPlaces++;
                    } else if (ticketStatus == TicketStatus.RESERVED) {
                        transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.RESERVED;
                        numsReservedPlaces++;
                    } else if (ticketStatus == TicketStatus.INITIALISED) {
                        if (ticket.getCreationDate().isAfter(LocalDateTime.now().minusMinutes(10L))) {
                            transformedSeatsStatusMap[seatNr - actSpecificSectorDto.getFirstSeatNr()] = SeatStatus.RESERVED;
                            numsReservedPlaces++;
                        }
                    } else {
                        throw new RuntimeException("Ticket without ticket status");
                    }
                }
            }

            actSpecificSectorDto.setNumReservedPlaces(numsReservedPlaces);
            actSpecificSectorDto.setNumBoughtPlaces(numsSoldPlaces);
            actSpecificSectorDto.setSeatStatusMap(transformedSeatsStatusMap);
            actSpecificSectorDto.setPrice(actSectorMap.getPrice());
        } else {
            throw new RuntimeException("Error matching sectors in toActSpecificSectorDto");
        }
        return actSpecificSectorDto;
    }

    public ActSpecificSectorDto[] toActSpecificSectorDtoForUser(Collection<SectorMap> sectorMap, Act act, List<Ticket> tickets) {
        if (sectorMap == null) {
            return null;
        }
        ActSpecificSectorDto[] sectorMapGrid = new ActSpecificSectorDto[7 * 7];
        for (SectorMap map : sectorMap) {
            sectorMapGrid[(map.getSectorX() + 3) + 7 * (map.getSectorY() + 3)] = toActSpecificSectorDtoForUser(map, act, tickets);
        }
        return sectorMapGrid;
    }

}

