package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleQuantityDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service for working with merch purchases and articles stored within.
 */
public interface MerchPurchaseService {

    /**
     * Retrieve all merch purchases stored in the persistent data store.
     *
     * @param purchased Optional parameter specifying the status of the purchase
     * @return all merch purchases (filtered if purchases is not null)
     */
    List<MerchPurchaseDto> getMerchPurchases(Boolean purchased);

    /**
     * Stores a merch article in the shopping cart of a user.
     *
     * @param merchArticleQuantityDto specifying the quantity and payment type (money or bonus points)
     * @return the merch purchase in the shopping cart with the newly added article
     */
    MerchPurchaseDto saveMerchArticleInShoppingList(MerchArticleQuantityDto merchArticleQuantityDto) throws ValidationException;

    /**
     * Edits the merch article with a corresponding id stored in the shopping cart.
     *
     * @param quantityDto specifying the quantity and payment type (money or bonus points)
     * @return the merch purchase in the shopping cart with the newly updated article
     */
    MerchPurchaseDto editArticle(MerchArticleQuantityDto quantityDto);

    /**
     * Deletes a merch article from the purchase (that is still in the shopping cart).
     *
     * @param id specifying the id of the merch article
     */
    void deleteMerchArticle(Long id);

    /**
     * Deletes all merch articles stored in the shopping cart of the logged-in user.
     */
    void deleteAllMerchArticlesInCart();

    /**
     * Sets the purchased status of a MerchPurchase to true, meaning it is no longer contained within the shopping cart.
     * Returns a DTO, otherwise identical to {@link #finalizeMerchPurchaseRaw() }.
     *
     * @return merch purchase and the article with the respective mappings containing the article count and
     *     payment type (money or points)
     * @throws ValidationException when attempting to finalize a shopping cart not containing any merch articles
     */
    MerchPurchaseDto finalizeMerchPurchase() throws ValidationException;

    /**
     * Sets the purchased status of a MerchPurchase to true, meaning it is no longer contained within the shopping cart.
     * Returns an entity, otherwise identical to {@link #finalizeMerchPurchase() }.
     *
     * @return merch purchase and the article with the respective mappings containing the article count and
     *     payment type (money or points)
     * @throws ValidationException when attempting to finalize a shopping cart not containing any merch articles
     */
    MerchPurchase finalizeMerchPurchaseRaw() throws ValidationException;

    /**
     * Checks if there are any merch articles stored in the shopping cart.
     *
     * @return true if there are no merch articles in the shopping cart
     */
    boolean isCartEmpty();

    /**
     * Retrieves the bonus point balance of finalized merch purchases for a certain user.
     *
     * @return the bonus point balance
     */
    Long getBonusPoints();

    /**
     * Retrieves the current bonus point balance contained in the shopping cart.
     *
     * @return the bonus point balance
     */
    Long getBonusPointsInCart();
}
