package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArticlePurchaseMappingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ArticlePurchaseMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ArticlePurchaseMappingMapper {
    @Mapping(target = "articleNr", source = "merchArticle.id")
    @Mapping(target = "bonusPointPrice", source = "merchArticle.bonusPointPrice")
    @Mapping(target = "image", source = "merchArticle.image")
    @Mapping(target = "name", source = "merchArticle.name")
    @Mapping(target = "price", source = "merchArticle.price")
    @Mapping(target = "artistOrEventName", expression = "java(mapping.getMerchArticle().getArtistOrEventName())")
    ArticlePurchaseMappingDto articlePurchaseMappingToArticlePurchaseMappingDto(ArticlePurchaseMapping mapping);
}
