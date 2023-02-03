package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArticlePurchaseMappingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArticlePurchaseMappingMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchArticleMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArticlePurchaseMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchArticleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchArticleService;
import at.ac.tuwien.sepm.groupphase.backend.type.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MerchArticleServiceImpl implements MerchArticleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchArticleRepository repository;
    private final ArticlePurchaseMappingRepository mappingRepository;
    private final UserRepository userRepository;
    private final MerchArticleMapper mapper;
    private final ArticlePurchaseMappingMapper mappingMapper;

    @Autowired
    public MerchArticleServiceImpl(MerchArticleRepository repository,
                                   ArticlePurchaseMappingRepository mappingRepository,
                                   UserRepository userRepository,
                                   MerchArticleMapper mapper,
                                   ArticlePurchaseMappingMapper mappingMapper) {
        this.repository = repository;
        this.mappingRepository = mappingRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.mappingMapper = mappingMapper;
    }

    @Override
    public MerchPageDto getAllMerchArticles(MerchArticleSearchDto searchDto, int page) {

        if (searchDto == null) {
            return new MerchPageDto(
                repository.count() % 20 == 0 ? (int) (repository.count() / 20) : (int) (repository.count() / 20 + 1),
                repository.findAll(PageRequest.of(page - 1, 15)).stream().map(mapper::merchArticleToMerchArticleDto).toList());
        } else {
            return searchMerchArticles(searchDto, page);
        }
    }


    @Override
    public MerchPageDto searchMerchArticles(MerchArticleSearchDto searchDto, int page) {
        searchDto = searchDtoSqlParams(searchDto);
        List<MerchArticle> merchArticles = null;
        if ((searchDto.getPointPurchaseAlwaysAvailable() == null || !searchDto.getPointPurchaseAlwaysAvailable()) && searchDto.getSortBy() == null) {
            merchArticles = repository.findBySearch(
                    searchDto.getTerm(),
                    searchDto.getMinPrice(),
                    searchDto.getMaxPrice(),
                    searchDto.getMinPointPrice(),
                    searchDto.getMaxPointPrice(),
                    PageRequest.of(page - 1, 20)
            );
        } else if (searchDto.getPointPurchaseAlwaysAvailable() != null && searchDto.getPointPurchaseAlwaysAvailable()) {
            if (searchDto.getSortBy() == null) {
                merchArticles = repository.findBySearchAndPointsIsNotNull(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), searchDto.getMinPointPrice(), searchDto.getMaxPointPrice(), PageRequest.of(page - 1, 20));
            } else if (searchDto.getSortBy().equals(SortBy.PRICE_ASC)) {
                merchArticles = repository.findBySearchAndPointsIsNotNullOrderedByPriceAsc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else if (searchDto.getSortBy().equals(SortBy.PRICE_DESC)) {
                merchArticles = repository.findBySearchAndPointsIsNotNullOrderedByPriceDesc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else if (searchDto.getSortBy().equals(SortBy.POINTS_ASC)) {
                merchArticles = repository.findBySearchAndPointsIsNotNullOrderedByPointsAsc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else {
                merchArticles = repository.findBySearchAndPointsIsNotNullOrderedByPointsDesc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            }
        } else {
            if (searchDto.getSortBy().equals(SortBy.PRICE_ASC)) {
                merchArticles = repository.findBySearchAndOrderedByPriceAsc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), searchDto.getMinPointPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else if (searchDto.getSortBy().equals(SortBy.PRICE_DESC)) {
                merchArticles = repository.findBySearchAndOrderedByPriceDesc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), searchDto.getMinPointPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else if (searchDto.getSortBy().equals(SortBy.POINTS_ASC)) {
                merchArticles = repository.findBySearchAndOrderedByPointsAsc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), searchDto.getMinPointPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            } else {
                merchArticles = repository.findBySearchAndOrderedByPointsDesc(
                        searchDto.getTerm(), searchDto.getMinPrice(), searchDto.getMaxPrice(), searchDto.getMinPointPrice(), searchDto.getMaxPrice(), PageRequest.of(page - 1, 20));
            }
        }
        if (merchArticles == null) {
            return null;
        } else {
            int numberOfPages = repository.findSizeBySearch(searchDto.getTerm(),
                searchDto.getMinPrice(),
                searchDto.getMaxPrice(),
                searchDto.getMinPointPrice(),
                searchDto.getMaxPointPrice());
            if (numberOfPages % 20 == 0) {
                numberOfPages = numberOfPages / 20;
            } else {
                numberOfPages = numberOfPages / 20 + 1;
            }
            return new MerchPageDto(
                (int) numberOfPages,
                merchArticles.stream().map(mapper::merchArticleToMerchArticleDto).toList()
            );
        }
    }

    // TODO: Set limit to approximately 20
    public List<ArticlePurchaseMappingDto> getAllMerchArticlesInRelationToUser() {
        List<ArticlePurchaseMappingDto> mappingDtoList = new ArrayList<>();
        for (MerchArticle article : repository.findAll()) {
            /*
            ArticlePurchaseMapping mapping =
                    mappingRepository.findByMerchPurchase_Buyer_IdAndMerchPurchase_PurchasedAndMerchArticle_Id(
                            getUser().getId(), false, article.getId()
                    );

            ArticlePurchaseMappingDto mappingDto;
            if (mapping != null) {
                mappingDto = mappingMapper.articlePurchaseMappingToArticlePurchaseMappingDto(mapping);
            } else {
                mappingDto = new ArticlePurchaseMappingDto();
                mappingDto.setArticleNr(article.getId());
                mappingDto.setArticleCount(0);
                mappingDto.setImage(article.getImage());
                mappingDto.setName(article.getName());
                mappingDto.setPrice(article.getPrice());
                mappingDto.setBonusUsed(false);
                mappingDto.setBonusPointPrice(article.getBonusPointPrice());
                mappingDto.setArtistOrEventName(article.getArtistOrEventName());
            }
            mappingDtoList.add(mappingDto);
             */
            mappingDtoList.add(getMerchArticleInRelationToUser(article.getId()));
        }
        return mappingDtoList;
    }

    @Override
    public ArticlePurchaseMappingDto getMerchArticleInRelationToUser(Long id) {
        ApplicationUser user = getUserOptional();
        if (user != null) {
            ArticlePurchaseMapping mapping =
                    mappingRepository.findByMerchPurchase_Buyer_IdAndMerchPurchase_PurchasedAndMerchArticle_Id(
                            user.getId(), false, id
                    );
            if (mapping != null) {
                return mappingMapper.articlePurchaseMappingToArticlePurchaseMappingDto(mapping);
            }
        }

        Optional<MerchArticle> articleOptional = repository.findById(id);
        if (articleOptional.isEmpty()) {
            throw new NotFoundException();
        }

        MerchArticle article = articleOptional.get();
        ArticlePurchaseMappingDto mappingDto = new ArticlePurchaseMappingDto();
        mappingDto.setArticleNr(article.getId());
        mappingDto.setArticleCount(0);
        mappingDto.setImage(article.getImage());
        mappingDto.setName(article.getName());
        mappingDto.setPrice(article.getPrice());
        mappingDto.setBonusUsed(false);
        mappingDto.setBonusPointPrice(article.getBonusPointPrice());
        mappingDto.setArtistOrEventName(article.getArtistOrEventName());

        return mappingDto;
    }


    // TODO: Add ValidationErrors
    @Override
    public MerchArticleDto saveMerchArticle(MerchArticleDto merchArticleDto) {
        return mapper.merchArticleToMerchArticleDto(
                repository.save(
                        mapper.merchArticleDtoToMerchArticle(merchArticleDto)
                )
        );
    }

    // TODO: Implement
    @Override
    public MerchArticleDto editMerchArticle(Long id, MerchArticleDto merchArticleDto) throws NotFoundException {
        return null;
    }

    private ApplicationUser getUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        if (user == null) {
            throw new FatalException("Cannot find an existing user");
        }
        return user;
    }

    private ApplicationUser getUserOptional() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser user = userRepository.findApplicationUserByEmail(username);
        return user == null || user.getId() == null ? null : user;
    }

    private MerchArticleSearchDto searchDtoSqlParams(MerchArticleSearchDto searchDto) {
        if (searchDto.getTerm() == null) {
            searchDto.setTerm("");
        } else {
            searchDto.setTerm(searchDto.getTerm().toUpperCase());
        }
        if (searchDto.getMinPrice() == null) {
            searchDto.setMinPrice(0.0);
        }
        if (searchDto.getMaxPrice() == null) {
            searchDto.setMaxPrice(Double.MAX_VALUE);
        }
        if (searchDto.getMinPointPrice() == null) {
            searchDto.setMinPointPrice(0L);
        }
        if (searchDto.getMaxPointPrice() == null) {
            searchDto.setMaxPointPrice(Long.MAX_VALUE);
        }
        return searchDto;
    }
}
