package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.SortBy;

public class MerchArticleSearchDto {
    private String term;
    private Double minPrice;
    private Double maxPrice;
    private Long minPointPrice;
    private Long maxPointPrice;
    private Boolean pointPurchaseAvailable;
    private SortBy sortBy;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getMinPrice() {
        return minPrice != null ? (long) (minPrice * 100) : null;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice != null ? (long) (maxPrice * 100) : null;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getMinPointPrice() {
        return minPointPrice;
    }

    public void setMinPointPrice(Long minPointPrice) {
        this.minPointPrice = minPointPrice;
    }

    public Long getMaxPointPrice() {
        return maxPointPrice;
    }

    public void setMaxPointPrice(Long maxPointPrice) {
        this.maxPointPrice = maxPointPrice;
    }

    public Boolean getPointPurchaseAlwaysAvailable() {
        return pointPurchaseAvailable;
    }

    public void setPointPurchaseAvailable(Boolean pointPurchaseAvailable) {
        this.pointPurchaseAvailable = pointPurchaseAvailable;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "MerchArticleSearchDto{"
                + "term='" + term + '\''
                + ", minPrice=" + minPrice
                + ", maxPrice=" + maxPrice
                + ", minPointPrice=" + minPointPrice
                + ", maxPointPrice=" + maxPointPrice
                + ", pointPurchaseAvailable=" + pointPurchaseAvailable
                + ", sortBy=" + sortBy
                + '}';
    }
}
