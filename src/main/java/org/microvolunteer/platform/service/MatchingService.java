package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.dto.GeometryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {
    private MyGeometryMapper myGeometryMapper;

    @Autowired
    public MatchingService (
           MyGeometryMapper myGeometryMapper
    ) {
        this.myGeometryMapper = myGeometryMapper;
    }

    public GeometryDto getMyGeometry(String user_id) {
        return myGeometryMapper.getMyGeometry(user_id);
    }

    public void insertMyGeometry(String user_id, GeometryDto location, Integer status) {
        myGeometryMapper.insertMyGeometry(user_id, location.getPoint(), status);
    }

    public void updateMyGeometry(String user_id, GeometryDto location, Integer status) {
        myGeometryMapper.updateMyGeometry(user_id, location.getPoint(), status);
    }

}
