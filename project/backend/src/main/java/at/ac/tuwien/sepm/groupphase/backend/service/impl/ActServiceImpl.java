package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchActDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ActMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ActService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class ActServiceImpl implements ActService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActRepository actRepository;
    private final ActMapper mapper;

    public ActServiceImpl(ActRepository actRepository, ActMapper mapper) {

        this.actRepository = actRepository;
        this.mapper = mapper;
    }

    @Override
    public Stream<ActDto> getByEventId(Long eventId) {
        LOGGER.debug("getByEventId({})", eventId);

        return actRepository.findActsByEventId(eventId).stream().map(mapper::actToActDto);
    }

    @Override
    public Stream<ActDto> searchActsByEventId(Long eventId, SearchActDto searchParams) throws ValidationException {
        LOGGER.debug("searchActsByEventId({})", eventId);
        LocalDateTime dateFrom;
        LocalDateTime dateTo;
        if (searchParams.getDateFrom() == null || searchParams.getDateFrom().equals("") || searchParams.getDateFrom().equals("null")) {
            dateFrom = LocalDateTime.now();
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateFrom());
            dateFrom = dateWithoutTime.atStartOfDay();
        }
        if (searchParams.getDateTo() == null || searchParams.getDateTo().equals("") || searchParams.getDateTo().equals("null")) {
            dateTo = LocalDateTime.of(9999, 12, 31, 00, 00);
        } else {
            LocalDate dateWithoutTime = LocalDate.parse(searchParams.getDateTo());
            dateTo = dateWithoutTime.atTime(23, 59);
        }
        if (dateTo.isBefore(dateFrom)) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Invalid date selection");
            throw new ValidationException("Date Filter Error", errors);
        }
        if (searchParams.getMinPrice() == null || searchParams.getMinPrice() < 0) {
            searchParams.setMinPrice(0L);
        }
        if (searchParams.getMaxPrice() == null) {
            searchParams.setMaxPrice(1000L);
        }
        if (searchParams.getMinPrice() > searchParams.getMaxPrice()) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Invalid price selection");
            throw new ValidationException("Price Filter Error", errors);
        }
        return actRepository.findActsByEventIdAndSearchParams(eventId, dateFrom, dateTo, searchParams.getMinPrice(),
            searchParams.getMaxPrice()).stream().map(mapper::actToActDto);
    }
}
