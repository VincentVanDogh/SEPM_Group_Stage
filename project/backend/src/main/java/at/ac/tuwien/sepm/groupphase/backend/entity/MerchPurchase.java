package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import java.util.List;

@Entity(name = "merch_purchase")
public class MerchPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer", nullable = false)
    private ApplicationUser buyer;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "merchPurchase", orphanRemoval = true)
    private List<ArticlePurchaseMapping> articlePurchaseMapping;

    @Column(nullable = false)
    private Boolean purchased;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "merchPurchase")
    private Invoice invoice;

    public MerchPurchase() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getBuyer() {
        return buyer;
    }

    public void setBuyer(ApplicationUser buyer) {
        this.buyer = buyer;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public List<ArticlePurchaseMapping> getArticlePurchaseMapping() {
        return articlePurchaseMapping;
    }

    public void setArticlePurchaseMapping(List<ArticlePurchaseMapping> articlePurchaseMapping) {
        this.articlePurchaseMapping = articlePurchaseMapping;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "MerchPurchase{"
                + "id=" + id
                + ", articlePurchaseMapping=" + articlePurchaseMapping
                + ", purchased=" + purchased
                + '}';
    }

    public void removeArticlePurchaseMapping(ArticlePurchaseMapping mapping) {
        mapping.setMerchPurchase(null);
        articlePurchaseMapping.remove(mapping);
    }
}
