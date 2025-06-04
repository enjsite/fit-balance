package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.UserAccount;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAccountMapper {
    UserAccount toEntity(UserAccountDto userAccountDto);

    UserAccountDto toUserAccountDto(UserAccount userAccount);

    UserAccount updateWithNull(UserAccountDto userAccountDto, @MappingTarget UserAccount userAccount);
}