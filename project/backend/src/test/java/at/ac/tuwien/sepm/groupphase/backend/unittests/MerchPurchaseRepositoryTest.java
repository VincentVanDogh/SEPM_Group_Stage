package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchPurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import net.datafaker.Faker;
import net.datafaker.providers.base.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class MerchPurchaseRepositoryTest {

    @Autowired
    MerchPurchaseRepository merchPurchaseRepository;

    @Autowired
    MerchArticleRepository merchArticleRepository;

    @Autowired
    UserRepository userRepository;

    private static final Faker faker = new Faker(new Random(1));

    @BeforeEach
    public void beforeEach() {
        merchPurchaseRepository.deleteAll();
        merchArticleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void successfullyFindAllBuyersById() {

        Name name1 = faker.name();
        ApplicationUser user1 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name1.firstName())
            .withLastName(name1.lastName())
            .withEmail(name1.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        ApplicationUser user1WithId = userRepository.save(user1);

        Name name2 = faker.name();
        ApplicationUser user2 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name2.firstName())
            .withLastName(name2.lastName())
            .withEmail(name2.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        user2 = userRepository.save(user2);

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user1WithId);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user2);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }

        List<MerchPurchase> merchPurchases = merchPurchaseRepository.findAllByBuyerId(user1WithId.getId());

        assertAll(
            () -> assertEquals(3, merchPurchases.size()),
            () -> assertEquals(user1WithId, merchPurchases.get(0).getBuyer()),
            () -> assertEquals(user1WithId, merchPurchases.get(1).getBuyer()),
            () -> assertEquals(user1WithId, merchPurchases.get(2).getBuyer())
        );
    }

    @Test
    public void successfullyFindAllBuyersByIdAndPurchased() {

        Name name1 = faker.name();
        ApplicationUser user1 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name1.firstName())
            .withLastName(name1.lastName())
            .withEmail(name1.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        ApplicationUser user1WithId = userRepository.save(user1);

        Name name2 = faker.name();
        ApplicationUser user2 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name2.firstName())
            .withLastName(name2.lastName())
            .withEmail(name2.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        user2 = userRepository.save(user2);

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user1WithId);
            merchPurchase.setPurchased(false);
            merchPurchaseRepository.save(merchPurchase);
        }

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user1WithId);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user2);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }

        List<MerchPurchase> merchPurchases = merchPurchaseRepository.findAllByBuyerIdAndPurchased(user1WithId.getId(), true);

        assertAll(
            () -> assertEquals(3, merchPurchases.size()),
            () -> assertEquals(user1WithId, merchPurchases.get(0).getBuyer()),
            () -> assertEquals(user1WithId, merchPurchases.get(1).getBuyer()),
            () -> assertEquals(user1WithId, merchPurchases.get(2).getBuyer()),
            () -> assertEquals(true, merchPurchases.get(0).getPurchased()),
            () -> assertEquals(true, merchPurchases.get(1).getPurchased()),
            () -> assertEquals(true, merchPurchases.get(2).getPurchased())
        );
    }

}
