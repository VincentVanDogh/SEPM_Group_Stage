package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


public class ArticlePurchaseMappingDto {
    private Long articleNr;
    private String name;
    private Long price;
    private Long bonusPointPrice;
    private String image;
    private Integer articleCount;
    private Boolean bonusUsed;
    private String artistOrEventName;

    public Long getArticleNr() {
        return articleNr;
    }

    public void setArticleNr(Long articleNr) {
        this.articleNr = articleNr;
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

    public String getArtistOrEventName() {
        return artistOrEventName;
    }

    public void setArtistOrEventName(String artistOrEventName) {
        this.artistOrEventName = artistOrEventName;
    }

    @Override
    public String toString() {
        return "ArticlePurchaseMappingDto{"
                + "articleNr=" + articleNr
                + ", name='" + name + '\''
                + ", price=" + price
                + ", bonusPointPrice=" + bonusPointPrice
                + ", image='" + image + '\''
                + ", articleCount=" + articleCount
                + ", bonusUsed=" + bonusUsed
                + ", artistOrEventName='" + artistOrEventName + '\''
                + '}';
    }
}
