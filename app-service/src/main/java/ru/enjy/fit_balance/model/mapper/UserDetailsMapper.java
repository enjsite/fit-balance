package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.enjy.fit_balance.model.dto.UserDetailsDto;
import ru.enjy.fit_balance.model.entity.UserDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserAccountMapper.class})
public interface UserDetailsMapper {
    UserDetails toEntity(UserDetailsDto userDetailsDto);

    UserDetailsDto toUserDetailsDto(UserDetails userDetails);

    UserDetails updateWithNull(UserDetailsDto userDetailsDto, @MappingTarget UserDetails userDetails);
}