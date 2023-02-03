package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArticlePurchaseMappingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;
import java.util.stream.Stream;

public interface MerchArticleService {
    /**
     * Retrieves all merch articles matching the search parameters within the search DTO.
     * If a search DTO is left out, all merch articles stored in the persistent data store are returned.
     *
     * @param searchDto entity containing search parameters
     * @param page merch articles on the n-th page in the frontend.
     *
     * @return all merch articles (that match the search parameters)
     */
    MerchPageDto getAllMerchArticles(MerchArticleSearchDto searchDto, int page);

    /**
     * Saves a merch article in the persistent data store.
     *
     * @param merchArticleDto merch article
     * @return stored merch article
     */
    MerchArticleDto saveMerchArticle(MerchArticleDto merchArticleDto);

    /**
     * Edits a merch article stored in the persistent data store.
     *
     * @param id id of the corresponding merch article
     * @param merchArticleDto merch article containing the information update
     *
     * @return newly edited merch article
     * @throws NotFoundException if a merch article with a corresponding id could not be located
     */
    MerchArticleDto editMerchArticle(Long id, MerchArticleDto merchArticleDto) throws NotFoundException;

    /**
     * Retrieves all merch articles stored in the persistent data store.
     * Additionally, it provides information about the quantity and payment option a user picked for the merch article
     * if it is contained within the shopping cart.
     *
     * @return all merch articles in relation to a user
     */
    List<ArticlePurchaseMappingDto> getAllMerchArticlesInRelationToUser();

    /**
     * Retrieves all merch articles matching the search parameters within the search DTO.
     *
     * @param searchDto entity containing search parameters
     * @param page merch articles on the n-th page in the frontend.
     *
     * @return all merch articles that match the search parameters
     */
    MerchPageDto searchMerchArticles(MerchArticleSearchDto searchDto, int page);

    /**
     * Retrieves a merch article.
     * Additionally, it provides information about the payment option and quantity chosen by the user, if the merch
     * article is stored in the shopping cart. If it is not in the shopping cart, the merch information in relation
     * to the user is filled with default values.
     *
     * @param id id of the corresponding merch article
     *
     * @return merch article in relation to a user
     */
    ArticlePurchaseMappingDto getMerchArticleInRelationToUser(Long id);
}
