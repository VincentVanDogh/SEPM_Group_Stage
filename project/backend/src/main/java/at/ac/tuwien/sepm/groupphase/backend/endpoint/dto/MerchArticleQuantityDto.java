package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class MerchArticleQuantityDto {
    private Long articleNr;
    private Integer quantity;
    private Boolean bonusUsed;

    public Long getArticleNr() {
        return articleNr;
    }

    public void setArticleNr(Long articleNr) {
        this.articleNr = articleNr;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getBonusUsed() {
        return bonusUsed;
    }

    public void setBonusUsed(Boolean bonusUsed) {
        this.bonusUsed = bonusUsed;
    }

    @Override
    public String toString() {
        return "MerchArticleQuantityDto{"
                + "articleNr=" + articleNr
                + ", quantity=" + quantity
                + ", bonusUsed=" + bonusUsed
                + '}';
    }
}
