package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class MerchDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchArticleRepository repository;
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    public MerchDataGenerator(MerchArticleRepository repository, ArtistRepository artistRepository,
                              EventRepository eventRepository) {
        this.repository = repository;
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
    }

    //@PostConstruct
    public void generateMerchArticle() {
        List<Artist> artists = artistRepository.findAll();
        List<Event> events = eventRepository.findAll();

        if (repository.findAll().isEmpty()) {
            LOGGER.info("Generating merch articles");
            // Creating merch articles referencing an artist
            createMerchArticleWithArtist("Beanie", 1000L, null, artists.get(0), "//cdn.shopify.com/s/files/1/2486/0276/products/ninbeanie3_1024x1024.png?v=1665151619");
            createMerchArticleWithArtist("Keychain", 200L, 200L, artists.get(1 % artists.size()), "https://ae01.alicdn.com/kf/HTB1JvvGcPgy_uJjSZKzq6z_jXXay/Red-Hot-Chili-Peppers-Keychain-Rock-Music-Band-Jewelry-Personalised-Keyrings-for-Fans-Key-Chains.jpg");
            createMerchArticleWithArtist("Smiley Pin", 10L, 10L, artists.get(2 % artists.size()), "https://i.pinimg.com/originals/97/ef/79/97ef799f617567ad3d7df2718dc5c4bd.jpg");
            createMerchArticleWithArtist("Necklace", 3000L, null, artists.get(3 % artists.size()), "https://cdn.shopify.com/s/files/1/0064/3439/0071/products/Product22_274.jpg?v=1659057736&width=1680");
            createMerchArticleWithArtist("Shirt Size M", 2000L, 2000L, artists.get(4 % artists.size()), "https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcSe5xJQbLjyjO0Qg4p_j91zd7yj2agUFmH-Mu90AWQR3M9jTO07bcVR-uXhGEoIMAW5-Zr732-ttSYNSouoMGPp5QBpnpm4ZE6z73Sypjge76YW2lGnv9mI");

            // Creating merch articles referencing an events
            createMerchArticleWithEvent("Hat", 5000L, 5000L, events.get(0), "https://rhcp.scot/wp-content/uploads/2016/05/cap.jpg");
            createMerchArticleWithEvent("Sunglasses", 100000L, 10000L, events.get(1), "https://i5.walmartimages.com/asr/52b6b99d-e771-44f3-bf17-01a0faa88184_1.61b500e54f7995afbd9245313d5924ae.jpeg");
        } else {
            LOGGER.info("Merch articles already generated");
        }
    }

    private void createMerchArticleWithArtist(String name, Long price, Long pointsPrice, Artist artist, String image) {
        MerchArticle article = new MerchArticle();
        article.setName(name);
        article.setPrice(price);
        article.setBonusPointPrice(pointsPrice);
        article.setArtist(artist);
        article.setImage(image);
        repository.save(article);
    }

    private void createMerchArticleWithEvent(String name, Long price, Long pointsPrice, Event event, String image) {
        MerchArticle article = new MerchArticle();
        article.setName(name);
        article.setPrice(price);
        article.setBonusPointPrice(pointsPrice);
        article.setEvent(event);
        article.setImage(image);
        repository.save(article);
    }
}
