package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.HandicapInfoDto;

import java.util.List;

@Mapper
public interface HandicapInfoRegisterMapper {
    void registerHandicapInfo(HandicapInfoDto handicapInfo);
    HandicapInfoDto getHandicapInfo(Integer handicapinfo_id);
    List<HandicapInfoDto> getHandicapList(String handicapped_id);
}
