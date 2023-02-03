package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.datafaker.internal.helper.WordUtils.capitalize;

//@Profile("generateData")
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 200;
    private final MessageRepository messageRepository;
    private static final Faker faker = new Faker(new Random(1));

    public MessageDataGenerator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void generateMessage() {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.info("News already generated");
            return;
        }
        //generateWithImages();
        LOGGER.info("Generating News");
        generateRandomNews();
    }

    private void generateRandomNews() {
        LOGGER.debug("generate {} random messages", NUMBER_OF_MESSAGES_TO_GENERATE);

        List<String> imageLinks = new ArrayList<>();
        imageLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Oldetonian_v_blackbrovers.jpg"
            + "/375px-Oldetonian_v_blackbrovers.jpg");
        imageLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Football_in_Bloomington%2C_"
            + "Indiana%2C_1996.jpg/450px-Football_in_Bloomington%2C_Indiana%2C_1996.jpg");
        imageLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Forcejeo_Real_Madrid_-_"
            + "FC_Barcelona.jpg/330px-Forcejeo_Real_Madrid_-_FC_Barcelona.jpg");
        imageLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Nigeria_Japan_U-20_Women_2010_"
            + "Advantage.jpg/290px-Nigeria_Japan_U-20_Women_2010_Advantage.jpg");
        imageLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Fourth_official_%28football"
            + "%29.jpg/170px-Fourth_official_%28football%29.jpg");

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
            Message message = Message.MessageBuilder.aMessage()
                .withPublishedAt(LocalDateTime.now().minusDays(faker.number().positive() % 30))
                .withTitle(faker.football().teams() + " is " + capitalize(faker.mood().feeling()) + "!")
                .withSummary(faker.greekPhilosopher().quote())
                .withText(faker.lorem().paragraph(5))
                .withImage(imageLinks.get(i % 5))
                .build();

            messages.add(message);
        }
        messageRepository.saveAll(messages);
    }

    /*private void generateWithImages() {
        LOGGER.debug("generate random messages with Pictures");

        List<String> pictureLinks = new ArrayList<>();
        pictureLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Oldetonian_v_blackbrovers.jpg"
            + "/375px-Oldetonian_v_blackbrovers.jpg");
        pictureLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Football_in_Bloomington%2C_"
            + "Indiana%2C_1996.jpg/450px-Football_in_Bloomington%2C_Indiana%2C_1996.jpg");
        pictureLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Forcejeo_Real_Madrid_-_"
            + "FC_Barcelona.jpg/330px-Forcejeo_Real_Madrid_-_FC_Barcelona.jpg");
        pictureLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Nigeria_Japan_U-20_Women_2010_"
            + "Advantage.jpg/290px-Nigeria_Japan_U-20_Women_2010_Advantage.jpg");
        pictureLinks.add("https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Fourth_official_%28football"
            + "%29.jpg/170px-Fourth_official_%28football%29.jpg");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Message message = Message.MessageBuilder.aMessage()
                .withPublishedAt(LocalDateTime.now().minusDays(faker.number().positive() % 30))
                .withTitle(faker.football().teams() + " is " + capitalize(faker.mood().feeling()) + "!")
                .withSummary(faker.greekPhilosopher().quote())
                .withText(faker.text().text(1000))
                .withImage(pictureLinks.get(i))
                .build();

            messages.add(message);
        }
        messageRepository.saveAll(messages);
    }*/

}
