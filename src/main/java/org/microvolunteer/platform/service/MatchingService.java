package org.microvolunteer.platform.service;

import org.microvolunteer.platform.domain.resource.request.CheckInRequest;
import org.microvolunteer.platform.domain.resource.request.HelpRequest;
import org.microvolunteer.platform.repository.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.repository.dao.mapper.HelpMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.microvolunteer.platform.domain.dto.HelpDto;
import org.microvolunteer.platform.domain.resource.*;
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

    public Location getMyGeometry(String user_id) {
        Location location = myGeometryMapper.getMyGeometry(user_id);
        return location;
    }

    /*
     * 新規ユーザー登録時のMyGeometry tableへのレコード追加
     */
    public void insertMyGeometry(String user_id, String location, Integer status) {
        myGeometryMapper.insertMyGeometry(user_id, location, status);
    }

    /*
     * checkin, matching時などの時の位置座標更新
     */
    public void updateMyGeometry(String user_id, CheckInRequest request) {
        String location = GeometryDto.getPoint(request.getX_geometry(),request.getY_geometry());
        Integer status = 1;
        myGeometryMapper.updateMyGeometry(user_id, location, status);
    }

    public void help(String my_id, HelpRequest request, HandicapInfo handicapInfo) {
        String location = GeometryDto.getPoint(
                request.getX_geometry()
                ,request.getY_geometry());
        // 障害者の障害情報を取得
        Integer status = 1;
        RegisterHelp help = HelpDto.registerHelp(
                my_id
                ,handicapInfo
                ,location
                ,status);

        helpMapper.registerHelp(help);
        // 対象ボランティアの抽出（マッチング）
        // 近くにいる人達を検索する。
        // 他の障害者、ボランティア混在しているが、助けられる人が助ければよいので分ける必要は無いと思う。
        List<NeighborDistance> neighborsList = helpMapper.getNeighborhood(my_id, location);
        // 対象ボランティアへのpush通知(python APIを使う)
    }

    public List<NeighborDistance> getNeigborhood(String my_id, String location) {
        List<NeighborDistance> neighborList = helpMapper.getNeighborhood(my_id, location);
        return neighborList;
    }

    public void accept(Integer help_id, String volunteer_id) {
        helpMapper.accept(help_id,volunteer_id);
    }

    public Help getHelpInfo(Integer help_id) {
        return helpMapper.getHelpInfo(help_id);
    }
}
