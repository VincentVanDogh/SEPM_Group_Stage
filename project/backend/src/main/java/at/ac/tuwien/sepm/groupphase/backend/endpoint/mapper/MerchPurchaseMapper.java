package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.MerchPurchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class MerchPurchaseMapper {

    @Autowired
    private ArticlePurchaseMappingMapper articlePurchaseMappingMapper;

    @Mapping(target = "articles", source = "articlePurchaseMapping")
    public abstract MerchPurchaseDto merchPurchaseToMerchPurchaseDto(MerchPurchase merchPurchase);

    // Generated mapper does not automatically use the mapper required for articlePurchaseMapping, therefore done manually
    public MerchPurchaseDto merchPurchaseToMerchPurchaseDtoAlt(MerchPurchase merchPurchase) {
        MerchPurchaseDto dto = new MerchPurchaseDto();
        dto.setId(merchPurchase.getId());
        dto.setPurchased(merchPurchase.getPurchased());
        dto.setArticles(
            merchPurchase.getArticlePurchaseMapping().stream().map(
                articlePurchaseMappingMapper::articlePurchaseMappingToArticlePurchaseMappingDto
            ).toList()
        );
        return dto;
    }
}
