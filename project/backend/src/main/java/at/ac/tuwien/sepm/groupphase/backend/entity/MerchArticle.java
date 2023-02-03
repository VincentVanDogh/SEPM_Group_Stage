package at.ac.tuwien.sepm.groupphase.backend.entity;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.util.List;

@Entity(name = "merch_article")
public class MerchArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Domain model calls it articleNr, but ID is more universal in regard to other entities
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name = "bonus_point_price")
    private Long bonusPointPrice;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "merchArticle", orphanRemoval = true)
    private List<ArticlePurchaseMapping> articlePurchaseMappings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event")
    private Event event;

    @Column(nullable = false)
    private String image;

    public MerchArticle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getBonusPointPrice() {
        return bonusPointPrice;
    }

    public void setBonusPointPrice(Long bonusPointPrice) {
        this.bonusPointPrice = bonusPointPrice;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ArticlePurchaseMapping> getArticlePurchaseMappings() {
        return articlePurchaseMappings;
    }

    public void setArticlePurchaseMappings(List<ArticlePurchaseMapping> articlePurchaseMappings) {
        this.articlePurchaseMappings = articlePurchaseMappings;
    }

    @Override
    public String toString() {
        return "MerchArticle{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", price=" + price
                + ", bonusPointPrice=" + bonusPointPrice
                + ", artist=" + artist
                + ", event=" + event
                + ", image='" + image + '\''
                + '}';
    }

    public void removeArticlePurchaseMapping(ArticlePurchaseMapping mapping) {
        mapping.setMerchArticle(null);
        // articlePurchaseMappings.remove(mapping);
    }

    public String getArtistOrEventName() {
        if (artist != null) {
            return artist.merchName();
        }
        if (event != null) {
            return event.getName();
        }
        // This case should never occur
        return "";
    }
}
