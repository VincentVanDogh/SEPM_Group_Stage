package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import java.util.List;

public class MerchPurchaseDto {
    private Long id;
    private Boolean purchased;
    private List<ArticlePurchaseMappingDto> articles;

    public MerchPurchaseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ArticlePurchaseMappingDto> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticlePurchaseMappingDto> articles) {
        this.articles = articles;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    @Override
    public String toString() {
        return "MerchPurchaseDto{"
                + "id=" + id
                + ", articlePurchaseMappingDtoList=" + articles
                + ", purchased=" + purchased
                + '}';
    }
}
