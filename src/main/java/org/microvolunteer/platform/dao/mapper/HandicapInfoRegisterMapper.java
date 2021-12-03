package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.HandicapInfoDto;

@Mapper
public interface HandicapInfoRegisterMapper {
    void registerHandicapInfo(HandicapInfoDto handicapInfo);
    HandicapInfoDto getHandicapInfo(String handicaped_id);
}
