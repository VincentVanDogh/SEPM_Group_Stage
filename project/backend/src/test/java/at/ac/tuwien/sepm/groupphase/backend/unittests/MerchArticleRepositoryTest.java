package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class MerchArticleRepositoryTest {

    @Autowired
    private MerchArticleRepository merchArticleRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private static final Faker faker = new Faker(new Random(1));

    private Artist artist1 = new Artist();
    private Artist artist2 = new Artist();
    private final String productName1 = "Fancy Medallion";
    private final String productName2 = "Rare Guitar";
    private final String image = "//cdn.shopify.com/s/files/1/2486/0276/products/ninbeanie3_1024x1024.png?v=1665151619";
    private final Long price = 1000L;
    private final Long bonusPointPrice = 50L;


    @BeforeEach
    public void beforeEach() {
        merchArticleRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void findMatchingMerchArticlesSuccessfully() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearch(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(5, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(0).getName()),
            () -> assertEquals(productName1, merchArticles.get(4).getName()),
            () -> assertEquals(price, merchArticles.get(0).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(0).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(0).getImage()),
            () -> assertEquals(artist1, merchArticles.get(0).getArtist())
        );
    }

    @Test
    public void findMatchingMerchArticlesWherePointIsNotNullSuccessfully() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndPointsIsNotNull(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(4, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(0).getName()),
            () -> assertEquals(price, merchArticles.get(0).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(0).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(0).getImage()),
            () -> assertEquals(artist1, merchArticles.get(0).getArtist())
        );
    }

    @Test
    public void findMatchingMerchArticlesWherePointIsNotNullSuccessfullyOrderByPriceAsc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndPointsIsNotNullOrderedByPriceAsc(
            productName1.toUpperCase(), 800L, 1200L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(4, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(0).getName()),
            () -> assertEquals(price, merchArticles.get(0).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(0).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(0).getImage()),
            () -> assertEquals(artist1, merchArticles.get(0).getArtist()),
            () -> assertTrue(merchArticles.get(0).getPrice() < merchArticles.get(1).getPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesWherePointIsNotNullSuccessfullyOrderByPriceDesc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndPointsIsNotNullOrderedByPriceDesc(
            productName1.toUpperCase(), 800L, 1200L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(4, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(3).getName()),
            () -> assertEquals(price, merchArticles.get(3).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(3).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(3).getImage()),
            () -> assertEquals(artist1, merchArticles.get(3).getArtist()),
            () -> assertTrue(merchArticles.get(0).getPrice() > merchArticles.get(1).getPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesWherePointIsNotNullSuccessfullyOrderByBonusPointsPriceAsc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndPointsIsNotNullOrderedByPointsAsc(
            productName1.toUpperCase(), 800L, 1200L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(4, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(0).getName()),
            () -> assertEquals(price, merchArticles.get(0).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(0).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(0).getImage()),
            () -> assertEquals(artist1, merchArticles.get(0).getArtist()),
            () -> assertTrue(merchArticles.get(0).getBonusPointPrice() < merchArticles.get(1).getBonusPointPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesWherePointIsNotNullSuccessfullyOrderByBonusPointsPriceDesc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndPointsIsNotNullOrderedByPointsDesc(
            productName1.toUpperCase(), 800L, 1200L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(4, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(3).getName()),
            () -> assertEquals(price, merchArticles.get(3).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(3).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(3).getImage()),
            () -> assertEquals(artist1, merchArticles.get(3).getArtist()),
            () -> assertTrue(merchArticles.get(0).getBonusPointPrice() > merchArticles.get(1).getBonusPointPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesSuccessfullyOrderByPriceAsc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndOrderedByPriceAsc(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(5, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(0).getName()),
            () -> assertEquals(price, merchArticles.get(0).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(0).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(0).getImage()),
            () -> assertEquals(artist1, merchArticles.get(0).getArtist()),
            () -> assertTrue(merchArticles.get(0).getPrice() < merchArticles.get(1).getPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesSuccessfullyOrderByPriceDesc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndOrderedByPriceDesc(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(5, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(4).getName()),
            () -> assertEquals(price, merchArticles.get(4).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(4).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(4).getImage()),
            () -> assertEquals(artist1, merchArticles.get(4).getArtist()),
            () -> assertTrue(merchArticles.get(0).getPrice() > merchArticles.get(1).getPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesSuccessfullyOrderByBonusPointsPriceAsc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndOrderedByPointsAsc(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(5, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(1).getName()),
            () -> assertEquals(price, merchArticles.get(1).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(1).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(1).getImage()),
            () -> assertEquals(artist1, merchArticles.get(1).getArtist()),
            () -> assertTrue(merchArticles.get(1).getBonusPointPrice() < merchArticles.get(2).getBonusPointPrice())
        );
    }

    @Test
    public void findMatchingMerchArticlesSuccessfullyOrderByBonusPointsPriceDesc() {

        gentTestData();

        List<MerchArticle> merchArticles = merchArticleRepository.findBySearchAndOrderedByPointsDesc(
            productName1.toUpperCase(), 800L, 1200L, 10L, 100L, PageRequest.of(0, 10));

        assertAll(
            () -> assertEquals(5, merchArticles.size()),
            () -> assertEquals(productName1, merchArticles.get(3).getName()),
            () -> assertEquals(price, merchArticles.get(3).getPrice()),
            () -> assertEquals(bonusPointPrice, merchArticles.get(3).getBonusPointPrice()),
            () -> assertEquals(image, merchArticles.get(3).getImage()),
            () -> assertEquals(artist1, merchArticles.get(3).getArtist()),
            () -> assertTrue(merchArticles.get(0).getBonusPointPrice() > merchArticles.get(1).getBonusPointPrice())
        );
    }

    private void gentTestData() {

        artist1.setFirstName(faker.name().firstName());
        artist1.setLastName(faker.name().lastName());
        artist1.setBandName(faker.rockBand().name());
        artist1.setStageName(faker.name().username());
        artistRepository.save(artist1);

        artist2.setFirstName(faker.name().firstName());
        artist2.setLastName(faker.name().lastName());
        artist2.setBandName(faker.rockBand().name());
        artist2.setStageName(faker.name().username());
        artistRepository.save(artist2);

        for (int i = 0; i < 5; i++) {
            MerchArticle article = new MerchArticle();
            article.setName(productName1);
            article.setPrice(price + i);
            if (i == 4) {
                article.setBonusPointPrice(null);
            } else {
                article.setBonusPointPrice(bonusPointPrice + i);
            }
            article.setArtist(artist1);
            article.setImage(image);
            merchArticleRepository.save(article);
        }

        for (int i = 0; i < 5; i++) {
            MerchArticle article = new MerchArticle();
            article.setName(productName2);
            article.setPrice(900L);
            article.setBonusPointPrice(30L);
            article.setArtist(artist2);
            article.setImage(image);
            merchArticleRepository.save(article);
        }
    }
}
