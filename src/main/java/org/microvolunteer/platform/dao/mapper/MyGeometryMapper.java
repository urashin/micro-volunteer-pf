package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.GeometryDto;
import org.springframework.data.geo.Point;

@Mapper
public interface MyGeometryMapper {
    GeometryDto getMyGeometry(String user_id);
    void insertMyGeometry(String user_id, String location, Integer status);
    void updateMyGeometry(String user_id, String location, Integer status);
}
