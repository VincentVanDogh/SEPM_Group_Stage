package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchArticleRepository extends JpaRepository<MerchArticle, Long> {
    /**
     * Find merch article matching search parameters.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
        """)
    List<MerchArticle> findBySearch(String productName,
                                    Long minPrice,
                                    Long maxPrice,
                                    Long minPoints,
                                    Long maxPoints,
                                    PageRequest pageRequest);

    /**
     * Find merch article matching search parameters and can that can be bought using bonus points.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL and article.bonusPointPrice BETWEEN ?4 AND ?5
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
        """)
    List<MerchArticle> findBySearchAndPointsIsNotNull(String productName,
                                                      Long minPrice,
                                                      Long maxPrice,
                                                      Long minPoints,
                                                      Long maxPoints,
                                                      PageRequest pageRequest);

    /**
     * Find merch article matching search parameters and can that can be bought
     * using bonus points ordered by price ascending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by price ascending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    ORDER BY article.price ASC
        """)
    List<MerchArticle> findBySearchAndPointsIsNotNullOrderedByPriceAsc(String productName,
                                                                    Long minPrice,
                                                                    Long maxPrice,
                                                                    PageRequest pageRequest);

    /**
     * Find merch article matching search parameters and can that can be bought
     * using bonus points ordered by price descending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by price descending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    ORDER BY article.price DESC
        """)
    List<MerchArticle> findBySearchAndPointsIsNotNullOrderedByPriceDesc(String productName,
                                                                       Long minPrice,
                                                                       Long maxPrice,
                                                                       PageRequest pageRequest);

    /**
     * Find merch article matching search parameters and can that can be bought
     * using bonus points ordered by point price ascending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by point price ascending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    ORDER BY article.bonusPointPrice ASC
        """)
    List<MerchArticle> findBySearchAndPointsIsNotNullOrderedByPointsAsc(String productName,
                                                                        Long minPrice,
                                                                        Long maxPrice,
                                                                        PageRequest pageRequest);

    /**
     * Find merch article matching search parameters and can that can be bought
     * using bonus points ordered by point price descending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by point price descending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND article.bonusPointPrice IS NOT NULL
    )
    ORDER BY article.bonusPointPrice DESC
        """)
    List<MerchArticle> findBySearchAndPointsIsNotNullOrderedByPointsDesc(String productName,
                                                                        Long minPrice,
                                                                        Long maxPrice,
                                                                        PageRequest pageRequest);

    /**
     * Find merch article matching search parameters ordered by price ascending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by price ascending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    ORDER BY article.price ASC
        """)
    List<MerchArticle> findBySearchAndOrderedByPriceAsc(String productName,
                                                        Long minPrice,
                                                        Long maxPrice,
                                                        Long minPoints,
                                                        Long maxPoints,
                                                        PageRequest pageRequest);

    /**
     * Find merch article matching search parameters ordered by price descending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by price descending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    ORDER BY article.price DESC
        """)
    List<MerchArticle> findBySearchAndOrderedByPriceDesc(String productName,
                                                        Long minPrice,
                                                        Long maxPrice,
                                                        Long minPoints,
                                                        Long maxPoints,
                                                        PageRequest pageRequest);

    /**
     * Find merch article matching search parameters ordered by point price ascending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by point price ascending
     */
    @Query(value = """
    SELECT article FROM merch_article article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    ORDER BY article.bonusPointPrice ASC
        """)
    List<MerchArticle> findBySearchAndOrderedByPointsAsc(String productName,
                                                         Long minPrice,
                                                         Long maxPrice,
                                                         Long minPoints,
                                                         Long maxPoints,
                                                         PageRequest pageRequest);

    /**
     * Find merch article matching search parameters ordered by point price descending.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @param pageRequest the size of and id of current page
     * @return list of filtered merch articles ordered by point price descending
     */
    @Query(value = """
    SELECT article FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    ORDER BY article.bonusPointPrice DESC
        """)
    List<MerchArticle> findBySearchAndOrderedByPointsDesc(String productName,
                                                         Long minPrice,
                                                         Long maxPrice,
                                                         Long minPoints,
                                                         Long maxPoints,
                                                         PageRequest pageRequest);

    /**
     * Find amount of  merch article matching search parameters.
     *
     * @param productName the search input to match the name of the product
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param minPoints the minimum point price
     * @param maxPoints the maximum point price
     * @return amount of  merch article matching search parameters
     */
    @Query(value = """
    SELECT count(article) FROM merch_article  article
    WHERE article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER(
                CONCAT (
                    COALESCE(CONCAT(article.artist.firstName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.lastName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.bandName, ' '), '' ),
                    COALESCE(CONCAT(article.artist.stageName, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
    OR article.id IN
    (
        SELECT article FROM merch_article article
        WHERE (
            UPPER (
                CONCAT (
                    COALESCE(CONCAT(article.event.name, ' '), '' ),
                    article.name
                )
            ) LIKE %?1%
        )
        AND article.price BETWEEN ?2 AND ?3
        AND (article.bonusPointPrice IS NULL OR article.bonusPointPrice BETWEEN ?4 AND ?5)
    )
        """)
    int findSizeBySearch(String productName,
                        Long minPrice,
                        Long maxPrice,
                        Long minPoints,
                        Long maxPoints);

}
