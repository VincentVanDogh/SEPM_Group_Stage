package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArticlePurchaseMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchPurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import net.datafaker.Faker;
import net.datafaker.providers.base.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MerchPurchaseEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MerchPurchaseRepository merchPurchaseRepository;

    @Autowired
    private MerchArticleRepository merchArticleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticlePurchaseMappingRepository articlePurchaseMappingRepository;

    private static final Faker faker = new Faker(new Random(1));

    @BeforeEach
    public void beforeEach() {
        articlePurchaseMappingRepository.deleteAll();
        merchPurchaseRepository.deleteAll();
        merchArticleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getMerchPurchasesWithPurchasedTrueReturnsOk() throws Exception {

        genTestData();

        mockMvc.perform(get(MERCH_PURCHASE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("purchased", "true"))
            .andExpect(status().isOk());

        mockMvc.perform(get(MERCH_PURCHASE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("purchased", "false"))
            .andExpect(status().isOk());

        mockMvc.perform(get(MERCH_PURCHASE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk());

    }

    @Test
    public void getMerchPurchasesWithPurchasedTrueReturnsBadRequest() throws Exception {

        genTestData();

        mockMvc.perform(get(MERCH_PURCHASE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("purchased", "null"))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void deleteMerchFromCartIsOk() throws Exception {

        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(ADMIN_USER)
            .withFirstName("user")
            .withLastName("stduser")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        ApplicationUser userWithId = userRepository.save(user);

        MerchPurchase merchPurchase = new MerchPurchase();
        merchPurchase.setBuyer(userWithId);
        merchPurchase.setPurchased(false);
        merchPurchase = merchPurchaseRepository.save(merchPurchase);

        MerchArticle merchArticle = new MerchArticle();
        merchArticle.setName(faker.appliance().equipment());
        merchArticle.setBonusPointPrice(50L);
        merchArticle.setPrice(100L);
        merchArticle.setImage("//cdn.shopify.com/s/files/1/2486/0276/products/ninbeanie3_1024x1024.png?v=1665151619");
        merchArticle = merchArticleRepository.save(merchArticle);

        ArticlePurchaseMapping articlePurchaseMapping = new ArticlePurchaseMapping();
        articlePurchaseMapping.setMerchPurchase(merchPurchase);
        articlePurchaseMapping.setArticleCount(5);
        articlePurchaseMapping.setMerchArticle(merchArticle);
        articlePurchaseMapping.setBonusUsed(false);
        articlePurchaseMappingRepository.save(articlePurchaseMapping);

        mockMvc.perform(delete(MERCH_PURCHASE_BASE_URI + "/" + merchArticle.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllMerchFromCartIsOk() throws Exception {

        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(ADMIN_USER)
            .withFirstName("user")
            .withLastName("stduser")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        ApplicationUser userWithId = userRepository.save(user);


        MerchPurchase merchPurchase = new MerchPurchase();
        merchPurchase.setBuyer(userWithId);
        merchPurchase.setPurchased(false);
        merchPurchase = merchPurchaseRepository.save(merchPurchase);

        MerchArticle merchArticle = new MerchArticle();
        merchArticle.setName(faker.appliance().equipment());
        merchArticle.setBonusPointPrice(50L);
        merchArticle.setPrice(100L);
        merchArticle.setImage("//cdn.shopify.com/s/files/1/2486/0276/products/ninbeanie3_1024x1024.png?v=1665151619");
        merchArticle = merchArticleRepository.save(merchArticle);

        ArticlePurchaseMapping articlePurchaseMapping = new ArticlePurchaseMapping();
        articlePurchaseMapping.setMerchPurchase(merchPurchase);
        articlePurchaseMapping.setArticleCount(5);
        articlePurchaseMapping.setMerchArticle(merchArticle);
        articlePurchaseMapping.setBonusUsed(false);
        articlePurchaseMappingRepository.save(articlePurchaseMapping);

        mockMvc.perform(delete(MERCH_PURCHASE_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk());
    }

    private void genTestData() {

        ApplicationUser user = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withEmail(ADMIN_USER)
            .withFirstName("user")
            .withLastName("stduser")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        userRepository.save(user);

        Name name1 = faker.name();
        ApplicationUser user1 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name1.firstName())
            .withLastName(name1.lastName())
            .withEmail(name1.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        userRepository.save(user1);

        Name name2 = faker.name();
        ApplicationUser user2 = ApplicationUser.ApplicationUserBuilder.anApplicationUser()
            .withFirstName(name2.firstName())
            .withLastName(name2.lastName())
            .withEmail(name2.username() + "@email.com")
            .withPassword("password")
            .withTimesWrongPwEntered(0)
            .withBonusPoints(0L)
            .build();
        userRepository.save(user2);

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user1);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }

        for (int i = 0; i < 3; i++) {
            MerchPurchase merchPurchase = new MerchPurchase();
            merchPurchase.setBuyer(user2);
            merchPurchase.setPurchased(true);
            merchPurchaseRepository.save(merchPurchase);
        }
    }
}
