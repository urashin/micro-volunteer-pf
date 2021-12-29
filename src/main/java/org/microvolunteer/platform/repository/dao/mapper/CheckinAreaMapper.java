package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.CheckinArea;

@Mapper
public interface CheckinAreaMapper {
    public void insertArea(CheckinArea checkinArea);
    public Integer getAreaId(String location);

}
