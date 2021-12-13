package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.Location;

@Mapper
public interface MyGeometryMapper {
    Location getMyGeometry(String user_id);
    void insertMyGeometry(String user_id, String location, Integer status);
    void updateMyGeometry(String user_id, String location, Integer status);
}
