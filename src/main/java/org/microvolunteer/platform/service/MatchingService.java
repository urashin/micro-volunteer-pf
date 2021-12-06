package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.HelpMapper;
import org.microvolunteer.platform.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.dao.mapper.ThanksMapper;
import org.microvolunteer.platform.dto.*;
import org.microvolunteer.platform.resource.request.ThanksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {
    private MyGeometryMapper myGeometryMapper;
    private HelpMapper helpMapper;

    @Autowired
    public MatchingService (
           MyGeometryMapper myGeometryMapper
           ,HelpMapper helpMapper
    ) {
        this.myGeometryMapper = myGeometryMapper;
        this.helpMapper = helpMapper;
    }

    public GeometryDto getMyGeometry(String user_id) {
        LocationDto location = myGeometryMapper.getMyGeometry(user_id);
        return GeometryDto.builder()
                .x_geometry(location.getX_geometry())
                .y_geometry(location.getY_geometry())
                .build();
    }

    /*
     * 新規ユーザー登録時のMyGeometry tableへのレコード追加
     */
    public void insertMyGeometry(String user_id, GeometryDto location, Integer status) {
        myGeometryMapper.insertMyGeometry(user_id, location.getPoint(), status);
    }

    /*
     * checkin, matching時などの時の位置座標更新
     */
    public void updateMyGeometry(String user_id, GeometryDto location, Integer status) {
        myGeometryMapper.updateMyGeometry(user_id, location.getPoint(), status);
    }

    public void help(HelpDto helpDto) {
        helpMapper.registerHelp(helpDto);
    }

    public List<NeighborDistanceDto> getNeigborhood(String my_id, String location) {
        List<NeighborDistanceDto> neighborList = helpMapper.getNeighborhood(my_id, location);
        return neighborList;
    }

    public void accept(Integer help_id, String volunteer_id) {
        helpMapper.accept(help_id,volunteer_id);
    }

    public HelpDto getHelpInfo(Integer help_id) {
        return helpMapper.getHelpInfo(help_id);
    }
}
