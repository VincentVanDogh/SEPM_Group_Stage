package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AddressMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LocationServiceImpl implements LocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StageTemplateRepository stageTemplateRepository;
    private final LocationRepository locationRepository;
    private final AddressRepository addressRepository;
    private final StageRepository stageRepository;
    private final LocationMapper locationMapper;
    private final AddressMapper addressMapper;
    private final StageMapper stageMapper;


    public LocationServiceImpl(
        StageTemplateRepository stageTemplateRepository,
        LocationRepository locationRepository,
        AddressRepository addressRepository,
        StageRepository stageRepository,
        LocationMapper locationMapper,
        AddressMapper addressMapper,
        StageMapper stageMapper
    ) {
        this.stageTemplateRepository = stageTemplateRepository;
        this.locationRepository = locationRepository;
        this.addressRepository = addressRepository;
        this.stageRepository = stageRepository;
        this.locationMapper = locationMapper;
        this.addressMapper = addressMapper;
        this.stageMapper = stageMapper;
    }

    @Override
    @Transactional
    public CreateLocationDto save(CreateLocationDto createLocationDto) {
        LOGGER.debug("save({})", createLocationDto);

        Address address = addressMapper.createLocationDtoToAddress(createLocationDto);
        address = addressRepository.save(address);

        Location location = locationMapper.createLocationDtoToLocation(createLocationDto);
        location.setAddress(address);
        location = locationRepository.save(location);

        List<CreateNewStageDto> createNewStageDtos = createLocationDto.stages();
        List<Stage> stages = new ArrayList<>();

        for (int i = 0; i < createNewStageDtos.size(); i++) {
            CreateNewStageDto createNewStageDto = createNewStageDtos.get(i);
            Optional<StageTemplate> template = stageTemplateRepository.findById(createNewStageDto.stageTemplateId());
            if (template.isEmpty()) {
                throw new NotFoundException("No Stage Template with ID %d found".formatted(createNewStageDto.stageTemplateId()));
                // Todo: nicht schön, wäre besser die Notfoundexceptions zu bündeln, aber dann wär schon ziemlich viel falsch
            }
            stages.add(Stage.StageBuilder.aStage().withName(createNewStageDto.name()).withStageTemplate(template.get()).withLocation(location).build());
        }

        stages = stageRepository.saveAll(stages);
        createNewStageDtos = stages.stream().map(stageMapper::stageToCreateNewStageDto).toList();

        return locationMapper.locationAndStagesToCreateLocationDto(location, createNewStageDtos);
    }

    @Override
    public Stream<LocationDto> getAll() {
        LOGGER.debug("getAll()");

        return locationRepository.findAll().stream().map(locationMapper::locationToLocationDto);
    }

    @Override
    @Transactional
    public Stream<LocationDto> searchByName(SearchLocationDto searchParams) {
        LOGGER.debug("searchByName({})", searchParams);

        List<Location> locations = locationRepository.findTop10ByVenueNameContainingIgnoreCaseOrderByVenueName(searchParams.venueName());
        return locations.stream().map(locationMapper::locationToLocationDto);
    }
}
