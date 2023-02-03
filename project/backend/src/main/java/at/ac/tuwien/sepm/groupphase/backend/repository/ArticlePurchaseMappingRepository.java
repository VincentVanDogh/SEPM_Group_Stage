package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticlePurchaseMappingRepository extends JpaRepository<ArticlePurchaseMapping, Long> {
    /**
     * Finds an article-purchase-mapping with the corresponding buyer id and purchased status.
     *
     * @param buyerId id of the buyer
     * @param purchased status of the merch purchase
     *
     * @return article-purchase-mapping stored in the persistent data storage meeting the criteria above
     */
    List<ArticlePurchaseMapping> findByMerchPurchase_Buyer_IdAndMerchPurchase_Purchased(Long buyerId, Boolean purchased);

    /**
     * Finds an article-purchase-mapping with the corresponding buyer id, purchased status and article id.
     *
     * @param buyerId id of the buyer
     * @param purchased status of the merch purchase
     * @param articleId id of the id
     *
     * @return article-purchase-mapping stored in the persistent data storage meeting the criteria above
     */
    ArticlePurchaseMapping findByMerchPurchase_Buyer_IdAndMerchPurchase_PurchasedAndMerchArticle_Id(Long buyerId, Boolean purchased, Long articleId);
}
