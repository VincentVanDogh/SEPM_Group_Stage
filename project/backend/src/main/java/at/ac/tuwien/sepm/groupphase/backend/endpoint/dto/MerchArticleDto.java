package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class MerchArticleDto {
    private Long id;
    private String name;
    private Long price;
    private Long bonusPointPrice;
    private String image;
    private String artistOrEventName;

    public MerchArticleDto() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtistOrEventName() {
        return artistOrEventName;
    }

    public void setArtistOrEventName(String artistOrEventName) {
        this.artistOrEventName = artistOrEventName;
    }

    @Override
    public String toString() {
        return "MerchArticleDto{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", price=" + price
                + ", bonusPointPrice=" + bonusPointPrice
                + ", image='" + image + '\''
                + ", artistOrEventName='" + artistOrEventName + '\''
                + '}';
    }
}
