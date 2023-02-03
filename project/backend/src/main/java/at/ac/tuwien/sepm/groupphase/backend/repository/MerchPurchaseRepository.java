package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MerchPurchaseRepository extends JpaRepository<MerchPurchase, Long> {

    /**
     * Finds all merch purchases of a user with the corresponding id.
     *
     * @param buyerId id of the buyer
     *
     * @return a collection of all merch purchases with the corresponding user id
     */
    List<MerchPurchase> findAllByBuyerId(Long buyerId);

    /**
     * Finds all merch purchases with a corresponding user id and purchase status.
     *
     * @param buyerId id of the buyer
     * @param purchased status of the merch purchase
     *
     * @return a collection of merch purchases with the corresponding user id and purchase status
     */
    List<MerchPurchase> findAllByBuyerIdAndPurchased(Long buyerId, Boolean purchased);


}
