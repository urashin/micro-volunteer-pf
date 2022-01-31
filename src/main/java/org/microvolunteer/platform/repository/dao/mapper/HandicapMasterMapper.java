package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.HandicapMaster;

import java.util.List;

@Mapper
public interface HandicapMasterMapper {
    void addHandicapType(HandicapMaster handicapMaster);
    List<HandicapMaster> getHandicapMasterList();
}
