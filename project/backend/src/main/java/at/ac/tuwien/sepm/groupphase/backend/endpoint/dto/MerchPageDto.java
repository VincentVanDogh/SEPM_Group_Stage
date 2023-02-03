package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;


public class MerchPageDto {

    private int numberOfPages;

    private List<MerchArticleDto> merchArticles;

    public MerchPageDto(int numberOfPages, List<MerchArticleDto> merchArticles) {
        this.numberOfPages = numberOfPages;
        this.merchArticles = merchArticles;
    }

    public MerchPageDto() {

    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<MerchArticleDto> getMerchArticles() {
        return merchArticles;
    }

    public void setMerchArticles(List<MerchArticleDto> merchArticles) {
        this.merchArticles = merchArticles;
    }
}
