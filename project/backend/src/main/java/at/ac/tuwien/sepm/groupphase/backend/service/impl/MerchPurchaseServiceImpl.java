package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArticlePurchaseMappingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleQuantityDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchPurchaseMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArticlePurchaseMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchPurchaseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchPurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MerchPurchaseServiceImpl implements MerchPurchaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchPurchaseRepository repository;
    private final MerchPurchaseMapper merchPurchaseMapper;
    private final UserRepository userRepository;
    private final MerchArticleRepository merchArticleRepository;
    private final ArticlePurchaseMappingRepository articlePurchaseMappingRepository;

    @Autowired
    public MerchPurchaseServiceImpl(MerchPurchaseRepository repository,
                                    MerchPurchaseMapper merchPurchaseMapper,
                                    UserRepository userRepository,
                                    MerchArticleRepository merchArticleRepository,
                                    ArticlePurchaseMappingRepository articlePurchaseMappingRepository) {
        this.repository = repository;
        this.merchPurchaseMapper = merchPurchaseMapper;
        this.userRepository = userRepository;
        this.merchArticleRepository = merchArticleRepository;
        this.articlePurchaseMappingRepository = articlePurchaseMappingRepository;
    }

    @Override
    public List<MerchPurchaseDto> getMerchPurchases(Boolean purchased) {
        LOGGER.debug("Getting merch purchases");
        Long userId = getUser().getId();

        return purchased == null
                ?
                repository.findAllByBuyerId(userId).stream().map(merchPurchaseMapper::merchPurchaseToMerchPurchaseDtoAlt).toList() :
                repository.findAllByBuyerIdAndPurchased(userId, purchased).stream().map(merchPurchaseMapper::merchPurchaseToMerchPurchaseDtoAlt).toList();
    }

    @Override
    public MerchPurchaseDto saveMerchArticleInShoppingList(MerchArticleQuantityDto merchArticleQuantityDto) throws ValidationException {
        LOGGER.debug("Saving merch article in the shopping cart");

        // Validate if the user is trying to purchase the article with bonus points and has enough points to do so
        if (merchArticleQuantityDto.getBonusUsed()) {
            Long pointsBalance = getBonusPoints();
            Long articleId = merchArticleQuantityDto.getArticleNr();
            Optional<MerchArticle> merchArticle = merchArticleRepository.findById(articleId);
            if (merchArticle.isPresent()) {
                Long pointsPrice = merchArticle.get().getBonusPointPrice();
                if (pointsPrice == null) {
                    throw new ValidationException(
                            "Error saving merch article within the shopping list",
                            List.of("Cannot purchase merch that not provide the option of point purchase to "
                                    + "purchase with points")
                    );
                }
                if (pointsBalance - pointsPrice < 0) {
                    throw new ValidationException(
                            "Error saving merch article within the shopping list",
                            List.of("Cannot add an article to a shopping list with the purchase option "
                                    + "\"bonus points\" as the user does not possess the sufficient amount")
                    );
                }
            } else {
                throw new NotFoundException("Could not find merch article with id " + articleId + " within the shopping cart");
            }
        }

        // 1. Get a merch purchase which was not yet finalized, meaning it is still in the shopping cart
        MerchPurchase purchase = getPurchaseInCart();

        if (purchase != null) {
            List<ArticlePurchaseMapping> purchaseMappingList = purchase.getArticlePurchaseMapping();
            // 2. Check if merch purchase already contains an article with the same id as merchArticleQuantityDto
            for (ArticlePurchaseMapping mapping : purchaseMappingList) {
                // 2.1 Merch purchase contains an article with the same id as merchArticleQuantityDto - update mapping
                if (Objects.equals(mapping.getMerchArticle().getId(), merchArticleQuantityDto.getArticleNr())) {
                    mapping.setBonusUsed(merchArticleQuantityDto.getBonusUsed());
                    mapping.setArticleCount(merchArticleQuantityDto.getQuantity());
                    articlePurchaseMappingRepository.save(mapping);
                    return merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(purchase);
                }
            }
            // 2.2 Merch purchase does not contain an article with the same id as merchArticleQuantityDto
            ArticlePurchaseMapping articlePurchaseMapping = createArticlePurchaseMapping(merchArticleQuantityDto);

            // 2.2.1 Update ArticlePurchaseMapping
            List<ArticlePurchaseMapping> articlePurchaseMappingNew = Stream.concat(
                    Stream.of(articlePurchaseMapping),
                    purchase.getArticlePurchaseMapping().stream()
            ).toList();

            purchase.setArticlePurchaseMapping(articlePurchaseMappingNew);

            return merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(storePurchase(purchase, articlePurchaseMapping));
        }

        // Create new merchPurchase
        purchase = new MerchPurchase();

        // Set user
        purchase.setBuyer(getUser());

        // Set purchase status
        purchase.setPurchased(false);

        // Set ArticlePurchaseMapping
        ArticlePurchaseMapping articlePurchaseMapping = createArticlePurchaseMapping(merchArticleQuantityDto);
        purchase.setArticlePurchaseMapping(List.of(articlePurchaseMapping));

        return merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(storePurchase(purchase, articlePurchaseMapping));
    }

    @Override
    public MerchPurchaseDto editArticle(MerchArticleQuantityDto quantityDto) {
        LOGGER.debug("Updating article stored in the shopping cart");

        ArticlePurchaseMapping mapping = findArticlePurchaseMappingInShoppingCart(quantityDto.getArticleNr());
        if (mapping == null) {
            throw new NotFoundException("Could not find merch article with id " + quantityDto.getArticleNr()
                    + " within the shopping cart");
        }
        mapping.setBonusUsed(quantityDto.getBonusUsed());
        mapping.setArticleCount(quantityDto.getQuantity());
        articlePurchaseMappingRepository.save(mapping);
        return merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(mapping.getMerchPurchase());
    }

    @Override
    public MerchPurchaseDto finalizeMerchPurchase() throws ValidationException {
        return merchPurchaseMapper.merchPurchaseToMerchPurchaseDtoAlt(finalizeMerchPurchaseRaw());
    }

    @Override
    public MerchPurchase finalizeMerchPurchaseRaw() throws ValidationException {
        LOGGER.debug("Finalizing articles stored in the shopping cart as a purchase");

        MerchPurchase purchaseInCart = getPurchaseInCart();
        if (purchaseInCart == null  || merchInCartIsEmpty(purchaseInCart)) {
            throw new ValidationException(
                "Error finalizing merch purchase",
                List.of("Cannot finalize an empty merch purchase, it must contain at least one merch article")
            );
        }
        purchaseInCart.setPurchased(true);
        return repository.save(purchaseInCart);
    }

    @Override
    public void deleteMerchArticle(Long id) {
        LOGGER.debug("Deleting merch article from the shopping cart");

        MerchPurchase purchaseInCart = getPurchaseInCart();
        if (purchaseInCart == null || merchInCartIsEmpty(purchaseInCart)) {
            throw new NotFoundException("Could not find merch article with id " + id + " within the shopping cart");
        }
        List<ArticlePurchaseMapping> articles = purchaseInCart.getArticlePurchaseMapping();
        for (ArticlePurchaseMapping article : articles) {
            if (article.getMerchArticle().getId().equals(id)) {
                article.getMerchPurchase().removeArticlePurchaseMapping(article);
                article.getMerchArticle().removeArticlePurchaseMapping(article);
                articlePurchaseMappingRepository.delete(article);
                break;
            }
        }
    }

    @Override
    public void deleteAllMerchArticlesInCart() {
        LOGGER.debug("Deleting all merch articles from the shopping cart");

        MerchPurchase purchaseInCart = getPurchaseInCart();
        if (purchaseInCart == null || merchInCartIsEmpty(purchaseInCart)) {
            return;
        }
        List<ArticlePurchaseMapping> articles = purchaseInCart.getArticlePurchaseMapping();

        for (ArticlePurchaseMapping article : new HashSet<>(articles)) {
            article.getMerchPurchase().removeArticlePurchaseMapping(article);
            article.getMerchArticle().removeArticlePurchaseMapping(article);
            articlePurchaseMappingRepository.delete(article);
        }
    }

    @Override
    public Long getBonusPoints() {
        List<MerchPurchaseDto> purchases  = getMerchPurchases(true);
        Double bonusPoints = 0.0;
        for (MerchPurchaseDto purchase : purchases) {
            for (ArticlePurchaseMappingDto article : purchase.getArticles()) {
                bonusPoints += article.getArticleCount() * article.getPrice() * 0.01 * 0.05;
                if (article.getBonusUsed()) {
                    bonusPoints -= article.getBonusPointPrice();
                }
            }
        }
        return bonusPoints.longValue();
    }


    @Override
    public Long getBonusPointsInCart() {
        List<MerchPurchaseDto> purchases  = getMerchPurchases(false);
        Double bonusPoints = getBonusPoints().doubleValue();
        for (MerchPurchaseDto purchase : purchases) {
            for (ArticlePurchaseMappingDto article : purchase.getArticles()) {
                if (article.getBonusUsed()) {
                    bonusPoints -= article.getBonusPointPrice() * article.getArticleCount();
                }
            }
        }
        return bonusPoints.longValue();
    }

    @Override
    public boolean isCartEmpty() {
        return getPurchaseInCart() == null || merchInCartIsEmpty(getPurchaseInCart());
    }

    private ArticlePurchaseMapping createArticlePurchaseMapping(MerchArticleQuantityDto quantityDto) {
        ArticlePurchaseMapping articlePurchaseMapping = new ArticlePurchaseMapping();
        articlePurchaseMapping.setArticleCount(quantityDto.getQuantity());
        articlePurchaseMapping.setBonusUsed(quantityDto.getBonusUsed());
        Optional<MerchArticle> merchArticle = merchArticleRepository.findById(quantityDto.getArticleNr());
        if (merchArticle.isPresent()) {
            articlePurchaseMapping.setMerchArticle(merchArticle.get());
            return articlePurchaseMapping;
        }
        throw new NotFoundException("Could not find merch article");
    }

    private MerchPurchase storePurchase(MerchPurchase purchase, ArticlePurchaseMapping mapping) {
        MerchPurchase storedPurchase = repository.save(purchase);
        mapping.setMerchPurchase(storedPurchase);
        articlePurchaseMappingRepository.save(mapping);
        return storedPurchase;
    }

    private ApplicationUser getUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        if (user == null) {
            throw new FatalException("Cannot find an existing user");
        }
        return user;
    }

    private ArticlePurchaseMapping findArticlePurchaseMappingInShoppingCart(Long id) {
        MerchPurchase purchase = getPurchaseInCart();
        if (purchase != null) {
            List<ArticlePurchaseMapping> articles = purchase.getArticlePurchaseMapping();
            for (ArticlePurchaseMapping article : articles) {
                Long articleId = article.getMerchArticle().getId();
                if (articleId.equals(id)) {
                    return article;
                }
            }
        }
        return null;
    }

    private MerchPurchase getPurchaseInCart() {
        List<MerchPurchase> purchaseList = repository.findAllByBuyerIdAndPurchased(getUser().getId(), false);
        if (purchaseList.size() > 1) {
            throw new FatalException(
                    "Too many merch purchases contained within the shopping cart. "
                            + "A user should always have only one purchase in the cart"
            );
        }
        return purchaseList.isEmpty() ? null : purchaseList.get(0);
    }

    private boolean merchInCartIsEmpty(MerchPurchase merchInCart) {
        List<ArticlePurchaseMapping> mapping = articlePurchaseMappingRepository.findByMerchPurchase_Buyer_IdAndMerchPurchase_Purchased(
                merchInCart.getBuyer().getId(),
                false
        );
        return mapping.isEmpty();
        // return merchInCart.getArticlePurchaseMapping().isEmpty();
    }
}
