package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

//@Profile("generateData")
@Component
public class DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AddressAndLocationDataGenerator addressAndLocationDataGenerator;
    private final ArtistGenerator artistGenerator;
    private final StagePlanDataGenerator stagePlanDataGenerator;
    private final ActAndEventDataGenerator actAndEventDataGenerator;
    private final ActStageMappingDataGenerator actStageMappingDataGenerator;
    private final UserDataGenerator userDataGenerator;
    private final TicketDataGenerator ticketDataGenerator;
    private final MerchDataGenerator merchDataGenerator;
    private final Environment environment;
    private final MessageDataGenerator messageDataGenerator;

    public DataGenerator(AddressAndLocationDataGenerator addressAndLocationDataGenerator,
                         ArtistGenerator artistGenerator,
                         StagePlanDataGenerator stagePlanDataGenerator,
                         ActAndEventDataGenerator actAndEventDataGenerator,
                         ActStageMappingDataGenerator actStageMappingDataGenerator,
                         UserDataGenerator userDataGenerator,
                         TicketDataGenerator ticketDataGenerator,
                         MerchDataGenerator merchDataGenerator,
                         Environment environment,
                         MessageDataGenerator messageDataGenerator) {
        this.addressAndLocationDataGenerator = addressAndLocationDataGenerator;
        this.artistGenerator = artistGenerator;
        this.stagePlanDataGenerator = stagePlanDataGenerator;
        this.actAndEventDataGenerator = actAndEventDataGenerator;
        this.actStageMappingDataGenerator = actStageMappingDataGenerator;
        this.userDataGenerator = userDataGenerator;
        this.ticketDataGenerator = ticketDataGenerator;
        this.messageDataGenerator = messageDataGenerator;
        this.merchDataGenerator = merchDataGenerator;
        this.environment = environment;
    }

    @PostConstruct
    public void generateData() {
        LOGGER.info("Start Data Generator");
        LOGGER.debug("generateData()");
        if (Arrays.asList(this.environment.getActiveProfiles()).contains("test")) {
            return;
        }

        userDataGenerator.genUsers();
        messageDataGenerator.generateMessage();
        stagePlanDataGenerator.generateStagePlans();
        addressAndLocationDataGenerator.generateAddressesAndLocationsAndStages();
        artistGenerator.generateArtists();
        actAndEventDataGenerator.generate();
        merchDataGenerator.generateMerchArticle();
        actStageMappingDataGenerator.generateActSectorMappings();
        ticketDataGenerator.generateTickets();
        LOGGER.info("Finish Data Generator");
    }
}
