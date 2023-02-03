package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchArticle;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArticlePurchaseMappingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = ArticlePurchaseMappingRepository.class)
public interface MerchArticleMapper {
    MerchArticle merchArticleDtoToMerchArticle(MerchArticleDto merchArticleDto);

    @Mapping(target = "artistOrEventName", expression = "java(merchArticle.getArtistOrEventName())")
    MerchArticleDto merchArticleToMerchArticleDto(MerchArticle merchArticle);
}
