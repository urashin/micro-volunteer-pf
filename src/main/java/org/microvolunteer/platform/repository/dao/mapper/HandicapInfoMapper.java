package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.MyHandicap;
import org.microvolunteer.platform.domain.resource.RegisterHandicapInfo;

import java.util.List;

@Mapper
public interface HandicapInfoMapper {
    void registerHandicapInfo(RegisterHandicapInfo handicapInfo);
    HandicapInfo getHandicapInfo(Integer handicapinfo_id);
    List<MyHandicap> getHandicapList(String handicapped_id);
    MyHandicap getHandicap(Integer handicap_id);
}
