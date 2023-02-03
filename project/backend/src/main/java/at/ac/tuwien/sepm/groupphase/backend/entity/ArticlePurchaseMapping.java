package at.ac.tuwien.sepm.groupphase.backend.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Column;

@Entity
public class ArticlePurchaseMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merch_purchase_id")
    private MerchPurchase merchPurchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merch_article_id")
    private MerchArticle merchArticle;

    @Column
    private Integer articleCount;

    @Column(columnDefinition = "boolean default false")
    private Boolean bonusUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MerchPurchase getMerchPurchase() {
        return merchPurchase;
    }

    public void setMerchPurchase(MerchPurchase merchPurchase) {
        this.merchPurchase = merchPurchase;
    }

    public MerchArticle getMerchArticle() {
        return merchArticle;
    }

    public void setMerchArticle(MerchArticle merchArticle) {
        this.merchArticle = merchArticle;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public Boolean getBonusUsed() {
        return bonusUsed;
    }

    public void setBonusUsed(Boolean bonusUsed) {
        this.bonusUsed = bonusUsed;
    }

    @Override
    public String toString() {
        return "ArticlePurchaseMapping{"
                + "id=" + id
                + ", merchPurchase=" + merchPurchase
                + ", merchArticle=" + merchArticle
                + ", articleCount=" + articleCount
                + ", bonusUsed=" + bonusUsed
                + '}';
    }
}
