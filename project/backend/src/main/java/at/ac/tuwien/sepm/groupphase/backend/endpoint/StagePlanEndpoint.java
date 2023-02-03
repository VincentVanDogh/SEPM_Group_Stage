package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ActStagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateNewStageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StagePlanTemplateDto;
import at.ac.tuwien.sepm.groupphase.backend.service.StagePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = StagePlanEndpoint.BASE_PATH)
public class StagePlanEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/stage_plan";
    private final StagePlanService stagePlanService;

    @Autowired
    public StagePlanEndpoint(StagePlanService stagePlanService) {
        this.stagePlanService = stagePlanService;
    }

    @Secured("ROLE_USER")
    @GetMapping("generic")
    @Transactional
    @Operation(summary = "Get stream of stage plans not linked to an act", security = @SecurityRequirement(name = "apiKey"))
    public Stream<StagePlanDto> getAll() {
        LOGGER.info("GET " + BASE_PATH + "/generic");
        LOGGER.debug("getAll()");
        return stagePlanService.getAll();
    }

    @Secured("ROLE_USER")
    @GetMapping("generic/{id}")
    @Transactional
    @Operation(summary = "Get a stage plan not linked to an act", security = @SecurityRequirement(name = "apiKey"))
    public StagePlanDto getGenericById(@PathVariable long id) {
        LOGGER.info("GET " + BASE_PATH + "/generic/{}", id);
        LOGGER.debug("getGenericById({})", id);
        return stagePlanService.getGenericById(id);
    }


    @Secured("ROLE_USER")
    @GetMapping("template")
    @Transactional
    @Operation(summary = "Get stream of stage plan templates", security = @SecurityRequirement(name = "apiKey"))
    public Stream<StagePlanTemplateDto> getAllTemplates() {
        LOGGER.info("GET " + BASE_PATH + "/template");
        LOGGER.debug("getAllTemplates()");
        return stagePlanService.getAllTemplates();
    }

    @Secured("ROLE_USER")
    @GetMapping("template/{id}")
    @Transactional
    @Operation(summary = "Get a stage plan template", security = @SecurityRequirement(name = "apiKey"))
    public StagePlanTemplateDto getTemplateById(@PathVariable long id) {
        LOGGER.info("GET " + BASE_PATH + "/template/{}", id);
        LOGGER.debug("getTemplateById({})", id);
        return stagePlanService.getTemplateById(id);
    }


    @Secured("ROLE_USER")
    @GetMapping("act_specific/{id}")
    @Transactional
    @Operation(summary = "Get a stage plan linked to an act", security = @SecurityRequirement(name = "apiKey"))
    public ActStagePlanDto getActSpecificPlanById(@PathVariable long id) {
        LOGGER.info("GET " + BASE_PATH +  "/act_specific/{}", id);
        LOGGER.debug("getActSpecificPlanById({})", id);
        return stagePlanService.getActSpecificPlanById(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("act_user_specific/{id}")
    @Transactional
    @Operation(summary = "Get a stage plan linked to an act and user", security = @SecurityRequirement(name = "apiKey"))
    public ActStagePlanDto getActSpecificPlanByIdAndUser(@PathVariable long id) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("GET " + BASE_PATH +  "/act_user_specific/{}", id);
        LOGGER.debug("getActSpecificPlanByIdAndUser({})", id);
        return stagePlanService.getActSpecificPlanByIdAndUser(id, username);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Transactional
    @Operation(summary = "Save a new stage", security = @SecurityRequirement(name = "apiKey"))
    public StagePlanDto create(@RequestBody CreateNewStageDto stageDto) {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("create({})", stageDto);
        return stagePlanService.saveStage(stageDto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    @Transactional
    @Operation(summary = "Update prices for a stage", security = @SecurityRequirement(name = "apiKey"))
    public ActStagePlanDto updatePricesForAct(@RequestBody ActStagePlanDto stageDto) {
        LOGGER.info("PUT " + BASE_PATH);
        LOGGER.debug("updatePricesForAct({})", stageDto);
        stagePlanService.saveActPrices(stageDto);
        return stageDto;
    }

}
