package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.RegisterHandicapInfo;

import java.util.List;

@Mapper
public interface HandicapInfoRegisterMapper {
    void registerHandicapInfo(RegisterHandicapInfo handicapInfo);
    HandicapInfo getHandicapInfo(Integer handicapinfo_id);
    List<HandicapInfo> getHandicapInfoList(String handicapped_id);
}
