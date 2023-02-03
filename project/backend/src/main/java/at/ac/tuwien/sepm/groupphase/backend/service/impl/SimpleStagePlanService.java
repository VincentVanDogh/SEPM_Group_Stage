package at.ac.tuwien.sepm.groupphase.backend.service.impl;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActSpecificSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanTemplateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActSectorMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.StagePlanService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StagePlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.type.Orientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SimpleStagePlanService implements StagePlanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StageRepository stageRepository;
    private final StageTemplateRepository stageTemplateRepository;
    private final SectorMapRepository sectorMapRepository;
    private final ActRepository actRepository;
    private final ActSectorMappingRepository actSectorMappingRepository;
    private final TicketRepository ticketRepository;

    private final StagePlanMapper stagePlanMapper;

    public SimpleStagePlanService(StageRepository stageRepository,
                                  StageTemplateRepository stageTemplateRepository,
                                  SectorMapRepository sectorMapRepository,
                                  ActRepository actRepository,
                                  ActSectorMappingRepository actSectorMappingRepository,
                                  TicketRepository ticketRepository,
                                  StagePlanMapper stagePlanMapper) {
        this.stageRepository = stageRepository;
        this.stageTemplateRepository = stageTemplateRepository;
        this.sectorMapRepository = sectorMapRepository;
        this.actRepository = actRepository;
        this.actSectorMappingRepository = actSectorMappingRepository;
        this.ticketRepository = ticketRepository;
        this.stagePlanMapper = stagePlanMapper;
    }

    @Override
    public Stream<StagePlanDto> getAll() {
        LOGGER.debug("Find all stages");
        return stagePlanMapper.toStagePlanDto(stageRepository.findAllByOrderByNameAsc()).stream();
    }

    @Override
    public StagePlanDto getGenericById(Long id) {
        LOGGER.debug("Find stage with id {}", id);
        Optional<Stage> stage = stageRepository.findById(id);
        if (stage.isPresent()) {
            return stagePlanMapper.toStagePlanDto(stage.get());
        } else {
            throw new NotFoundException(String.format("Could not find stage with id %s", id));
        }
    }

    @Override
    public Stream<StagePlanTemplateDto> getAllTemplates() {
        LOGGER.debug("Find all stages");
        return stagePlanMapper.toStagePlanTemplateDto(stageTemplateRepository.findAllByOrderByNameAsc()).stream();
    }

    @Override
    public StagePlanTemplateDto getTemplateById(Long id) {
        LOGGER.debug("Find stage template with id {}", id);
        Optional<StageTemplate> stage = stageTemplateRepository.findById(id);
        if (stage.isPresent()) {
            return stagePlanMapper.toStagePlanTemplateDto(stage.get());
        } else {
            throw new NotFoundException(String.format("Could not find stage with id %s", id));
        }
    }

    public ActStagePlanDto getActSpecificPlanById(Long id) {
        LOGGER.debug("Find stage for act with id {}", id);
        Optional<Act> act = actRepository.findById(id);
        if (act.isPresent()) {
            return stagePlanMapper.toActStagePlanDto(act.get());
        } else {
            throw new NotFoundException(String.format("Could not find act with id %s", id));
        }
    }

    public ActStagePlanDto getActSpecificPlanByIdAndUser(Long id, String username) {
        LOGGER.debug("Find stage for act with id {} for user {}", id, username);

        Optional<Act> act = actRepository.findById(id);
        if (act.isPresent()) {
            Act actualAct = act.get();
            List<Ticket> tickets = ticketRepository.findTicketsByUsernameAndAct(username, actualAct.getId());
            return stagePlanMapper.toActStagePlanDtoForUser(actualAct, tickets);
        } else {
            throw new NotFoundException(String.format("Could not find act with id %s", id));
        }
    }

    public void saveActPrices(ActStagePlanDto actStagePlanDto) {
        LOGGER.debug("Save prices for act with id {}", actStagePlanDto.getActId());

        Optional<Act> optionalAct = actRepository.findById(actStagePlanDto.getActId());
        ActSpecificSectorDto[] sectorArray = actStagePlanDto.getSectorArray();
        Integer[] pricesArray = new Integer[sectorArray.length];
        for (int i = 0; i < sectorArray.length; i++) {
            if (sectorArray[i] != null) {
                pricesArray[i] = sectorArray[i].getPrice();
            }
        }
        if (optionalAct.isPresent()) {
            Act act = optionalAct.get();

            for (int i = 0; i < pricesArray.length; i++) {
                if (pricesArray[i] != null) {
                    Integer sectorX = i % 7 - 3;
                    Integer sectorY = i / 7 - 3;

                    SectorMap sectorMap = sectorMapRepository.findSectorMapForActAndCoordinates(act.getId(), sectorX, sectorY);
                    ActSectorMapping actSectorMap = ActSectorMapping.ActSectorMappingBuilder.aActSectorMap()
                        .withAct(act)
                        .withSectorMap(sectorMap)
                        .withPrice(pricesArray[i])
                        .build();
                    actSectorMappingRepository.save(actSectorMap);
                }
            }

        }
    }

    public StagePlanDto saveStage(CreateNewStageDto createNewStageDto) {
        LOGGER.debug("Save stage with name {}", createNewStageDto.name());
        Optional<StageTemplate> stageTemplate = stageTemplateRepository.findById(createNewStageDto.stageTemplateId());
        if (stageTemplate.isPresent()) {
            Stage stage = Stage.StageBuilder.aStage()
                .withName(createNewStageDto.name())
                .withStageTemplate(stageTemplate.get())
                .build();
            return stagePlanMapper.toStagePlanDto(stage);

        } else {
            throw new NotFoundException(String.format("Could not find stage template with id %s", createNewStageDto.stageTemplateId()));
        }
    }

    @Transactional //annotation could/should be removed since they should be in endpoints
    public int[] getRowAndSeatNumberOfSeatForSeatedTicket(TicketDetailsDto ticketDetailsDto) {

        ActStagePlanDto actStagePlanForTicket = getActSpecificPlanById(ticketDetailsDto.getAct().id());
        ActSpecificSectorDto[] sectorArray = actStagePlanForTicket.getSectorArray();
        int rowNumberOfTicket = 0;
        int numberInRowOfTicket = 0;
        if (ticketDetailsDto.getSeatNo() != 0) {
            Orientation orientationOfTicket = Orientation.NORTH;
            for (ActSpecificSectorDto sector : sectorArray) {
                if (sector != null && Objects.equals(ticketDetailsDto.getSectorMapId(), sector.getId())) {
                    orientationOfTicket = sector.getOrientation();
                    break;
                }
            }
            for (ActSpecificSectorDto sector : sectorArray) {
                if (sector != null) {
                    if (!sector.isStanding() && sector.getOrientation() == orientationOfTicket) {
                        for (int i = 0; i < sector.getNumRows(); i++) {
                            int firstSeatInRow = sector.getFirstSeatNr() + i * sector.getNumColumns();
                            if (firstSeatInRow <= ticketDetailsDto.getSeatNo()) {
                                rowNumberOfTicket++;
                            }
                        }
                    }
                    if (Objects.equals(sector.getId(), ticketDetailsDto.getSectorMapId())) {
                        numberInRowOfTicket = ((ticketDetailsDto.getSeatNo() - sector.getFirstSeatNr()) % sector.getNumColumns()) + 1;
                    }
                }
            }
        }

        return new int[]{rowNumberOfTicket, numberInRowOfTicket};
    }

    @Transactional //annotation could/should be removed since they should be in endpoints
    public char getSectorDesignationForStandingTicket(TicketDetailsDto ticketDetailsDto) {

        ActStagePlanDto actStagePlanForTicket = getActSpecificPlanById(ticketDetailsDto.getAct().id());
        ActSpecificSectorDto[] sectorArray = actStagePlanForTicket.getSectorArray();
        char[] identifiers = "ABCDEFGHIJKLMNOPQRSTUVWXY".toCharArray();
        int counter = -1;

        if (ticketDetailsDto.getSeatNo() == 0) {
            for (ActSpecificSectorDto sector : sectorArray) {
                if (sector != null) {
                    if (sector.isStanding()) {
                        counter++;
                    }
                    if (Objects.equals(sector.getId(), ticketDetailsDto.getSectorMapId())) {
                        return identifiers[counter];
                    }
                }
            }
        }

        return ' ';
    }

}
