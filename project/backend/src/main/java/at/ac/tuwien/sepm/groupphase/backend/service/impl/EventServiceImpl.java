package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchTop10Events;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ActMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActSectorMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ArtistRepository artistRepository;
    private final StageRepository stageRepository;
    private final ActRepository actRepository;
    private final EventMapper eventMapper;
    private final ActMapper actMapper;
    private final SectorMapRepository sectorMapRepository;
    private final ActSectorMappingRepository actSectorMappingRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            LocationRepository locationRepository,
                            ArtistRepository artistRepository,
                            StageRepository stageRepository,
                            ActRepository actRepository,
                            EventMapper eventMapper,
                            ActMapper actMapper,
                            SectorMapRepository sectorMapRepository,
                            ActSectorMappingRepository actSectorMappingRepository,
                            EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.artistRepository = artistRepository;
        this.stageRepository = stageRepository;
        this.actRepository = actRepository;
        this.eventMapper = eventMapper;
        this.actMapper = actMapper;
        this.sectorMapRepository = sectorMapRepository;
        this.actSectorMappingRepository = actSectorMappingRepository;
    }

    @Override
    @Transactional
    public CreateEventDto save(CreateEventDto createEventDto) {
        LOGGER.debug("save({})", createEventDto);

        /* save events */
        Event event = eventMapper.createEventDtoToEvent(createEventDto);
        Optional<Location> location = locationRepository.findById(createEventDto.locationId());
        List<int[]> prices = createEventDto.pricesPerAct();

        if (location.isPresent()) {
            event.setLocation(location.get());
        } else {
            throw new NotFoundException("No Location with ID %d found".formatted(createEventDto.locationId()));
        }

        List<Long> artistIds = createEventDto.artistIds();
        List<Artist> artists = artistRepository.findAllById(artistIds);
        if (artists.size() != artistIds.size()) {
            throw new NotFoundException("Could not find all artists in the database.");
        }

        event.setFeaturedArtists(artists);
        event = eventRepository.save(event);
        artistIds = event.getFeaturedArtists().stream().map(Artist::getId).toList();

        /* save acts */
        List<Act> acts = createEventDto.acts().stream().map(actMapper::actDtoToAct).toList();
        List<Long> stageIds = createEventDto.acts().stream().map(ActDto::stageId).toList();

        for (int i = 0; i < acts.size(); i++) {

            Optional<Stage> stage = stageRepository.findById(stageIds.get(i));
            if (stage.isPresent()) {
                acts.get(i).setStage(stage.get());
            } else {
                throw new NotFoundException("No Stage with ID %d found".formatted(stageIds.get(i)));
            }
            acts.get(i).setEvent(event);
        }

        acts = actRepository.saveAll(acts);
        List<ActDto> actDtos = acts.stream().map(actMapper::actToActDto).toList();

        for (ActDto actDto :
            actDtos) {
            Act act = actRepository.getReferenceById(actDto.id());
            List<SectorMap> sectorMaps = act.getStage().getStageTemplate().getSectorMaps();
            for (SectorMap sectorMap : sectorMaps) {
                ActSectorMapping actSectorMapping = new ActSectorMapping();
                int positionInPricesArray = 24 + sectorMap.getSectorX() + sectorMap.getSectorY() * 7;
                actSectorMapping.setPrice(prices.get(actDtos.indexOf(actDto))[positionInPricesArray]);
                actSectorMapping.setAct(act);
                actSectorMapping.setSectorMap(sectorMapRepository.findById(sectorMap.getId()).get());
                actSectorMappingRepository.save(actSectorMapping);
            }
        }

        return eventMapper.eventAndActsAndArtistsToCreateEventDto(event, actDtos, artistIds);
    }

    @Override
    public List<EventDetailsDto> getAll(int page) {
        LOGGER.debug("getAll()");

        return eventRepository.findAll(PageRequest.of(page - 1, 10)).stream().map(eventMapper::eventToEventDetailsDto).toList();
    }

    @Override
    public int getNumberOfPages() {
        LOGGER.debug("getAllSize()");
        int noOfPages = (int) (eventRepository.count());
        if (noOfPages % 10 == 0) {
            return noOfPages / 10;
        } else {
            return noOfPages / 10 + 1;
        }
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.getReferenceById(id);
    }

    @Override
    public List<EventDetailsDto> searchEvents(SearchEventDto searchParams, int page) throws ValidationException {
        LOGGER.debug("Searching for events");
        LocalDateTime dateFrom;
        LocalDateTime dateTo;
        if (searchParams.getSearch() == null) {
            searchParams.setSearch("");
        }
        if (searchParams.getLocation() == null) {
            searchParams.setLocation("");
        }
        if (searchParams.getDateFrom() == null || searchParams.getDateFrom().equals("")) {
            dateFrom = LocalDateTime.now().withHour(0);
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateFrom());
            dateFrom = dateWithoutTime.atStartOfDay();
        }
        if (searchParams.getDateTo() == null || searchParams.getDateTo().equals("")) {
            dateTo = LocalDateTime.of(9999, 12, 31, 0, 0);
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateTo());
            dateTo = dateWithoutTime.atTime(23, 59);
        }
        if (dateTo.isBefore(dateFrom)) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Date must not lie in the future selection");
            throw new ValidationException("Search Error", errors);
        }
        if (searchParams.getCategory() == null) {
            return eventRepository.findBySearchWithoutCategory(
                searchParams.getSearch().toUpperCase(),
                dateFrom,
                dateTo,
                searchParams.getLocation().toUpperCase(),
                PageRequest.of(page - 1, 10)
                ).stream().map(eventMapper::eventToEventDetailsDto).toList();
        } else {
            return eventRepository.findBySearchWithCategory(
                searchParams.getSearch().toUpperCase(),
                searchParams.getCategory(),
                dateFrom,
                dateTo,
                searchParams.getLocation().toUpperCase(),
                PageRequest.of(page - 1, 10)
                ).stream().map(eventMapper::eventToEventDetailsDto).toList();
        }
    }

    @Override
    public int getSearchSize(SearchEventDto searchParams) throws ValidationException {
        LOGGER.debug("getSearchSize()");

        LocalDateTime dateFrom;
        LocalDateTime dateTo;
        if (searchParams.getSearch() == null) {
            searchParams.setSearch("");
        }
        if (searchParams.getLocation() == null) {
            searchParams.setLocation("");
        }
        if (searchParams.getDateFrom() == null || searchParams.getDateFrom().equals("")) {
            dateFrom = LocalDateTime.now().withHour(0);
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateFrom());
            dateFrom = dateWithoutTime.atStartOfDay();
        }
        if (searchParams.getDateTo() == null || searchParams.getDateTo().equals("")) {
            dateTo = LocalDateTime.of(9999, 12, 31, 0, 0);
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateTo());
            dateTo = dateWithoutTime.atTime(23, 59);
        }
        if (dateTo.isBefore(dateFrom)) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Date must not lie in the future selection");
            throw new ValidationException("Search Error", errors);
        }
        if (searchParams.getCategory() == null) {
            int noOfPages = eventRepository.getSizeOfSearchResultsWithoutCategory(
                searchParams.getSearch().toUpperCase(),
                dateFrom,
                dateTo,
                searchParams.getLocation().toUpperCase()
            );
            if (noOfPages % 10 == 0) {
                return noOfPages / 10;
            } else {
                return noOfPages / 10 + 1;
            }
        } else {
            int noOfPages = eventRepository.getSizeOfSearchResultsWithCategory(
                searchParams.getSearch().toUpperCase(),
                searchParams.getCategory(),
                dateFrom,
                dateTo,
                searchParams.getLocation().toUpperCase()
            );
            if (noOfPages % 10 == 0) {
                return noOfPages / 10;
            } else {
                return noOfPages / 10 + 1;
            }
        }
    }

    @Override
    @Transactional
    public Stream<EventDetailsDto> getTop10(SearchTop10Events searchParams) throws ValidationException {
        LOGGER.debug("getTop10({})", searchParams);

        ArrayList<String> errors = new ArrayList<>();

        if (searchParams.category() == null) {
            errors.add("No category given");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Top 10 Error", errors);
        }

        List<Event> events = eventRepository.findTopByNrTicketsSoldAndByCategoryAndByMonthAndYear(
            searchParams.category().toString(), searchParams.yearMonth().getMonthValue(),
            searchParams.yearMonth().getYear(), 10L);


        List<EventDetailsDto> eventDetailsDtos = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            List<Act> acts = events.get(i).getActs();
            int totalNrOfTicketsSold = 0;
            for (Act act : acts) {
                totalNrOfTicketsSold += act.getNrTicketsSold();
            }
            eventDetailsDtos.add(eventMapper.eventToEventDetailsDto(events.get(i), totalNrOfTicketsSold));
        }

        return eventDetailsDtos.stream();
    }
}
