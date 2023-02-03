package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.StageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StageServiceImpl implements StageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StageRepository stageRepository;
    private final StageMapper mapper;

    public StageServiceImpl(StageRepository stageRepository, StageMapper mapper) {
        this.stageRepository = stageRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Stream<StageDto> getByLocationId(Long locationId) {
        LOGGER.debug("getByLocationId({})", locationId);

        List<Stage> stages = stageRepository.findAllByLocationIdOrderById(locationId);
        return stages.stream().map(mapper::stageToStageDto);
    }

}
