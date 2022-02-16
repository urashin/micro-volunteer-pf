package org.microvolunteer.platform.service;

import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.dto.CheckinAreaDto;
import org.microvolunteer.platform.domain.resource.request.CheckInRequest;
import org.microvolunteer.platform.domain.resource.request.CheckinAreaRegisterRequest;
import org.microvolunteer.platform.domain.resource.request.HelpRequest;
import org.microvolunteer.platform.repository.dao.mapper.CheckinAreaMapper;
import org.microvolunteer.platform.repository.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.repository.dao.mapper.HelpMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.microvolunteer.platform.domain.dto.HelpDto;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingService {
    private Logger logger = LoggerFactory.getLogger(MatchingService.class);

    @Autowired
    private MyGeometryMapper myGeometryMapper;

    @Autowired
    private HelpMapper helpMapper;

    @Autowired
    private SnsRegisterMapper snsRegisterMapper;

    @Autowired
    private LineMessageRestClient lineMessageRestClient;

    @Autowired
    private CheckinAreaMapper checkinAreaMapper;

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
        Integer area_id = checkinAreaMapper.getAreaId(location);
        Integer status = 1;
        myGeometryMapper.updateMyGeometry(user_id, location, area_id, status);
    }

    public void help(String my_id, HelpRequest request, HandicapInfo handicapInfo) {
        String location = GeometryDto.getPoint(
                request.getX_geometry()
                ,request.getY_geometry());
        // area情報があれば取得
        Integer area_id = checkinAreaMapper.getAreaId(location);
        Integer status = 1;
        RegisterHelp help = HelpDto.registerHelp(
                my_id
                ,handicapInfo
                ,location
                ,area_id
                ,status);

        helpMapper.registerHelp(help);
        //Integer help_id = helpMapper.getHelpId(my_id);
        // 対象ボランティアの抽出（マッチング）
        // 近くにいる人達を検索する。
        // 他の障害者、ボランティア混在しているが、助けられる人が助ければよいので分ける必要は無いと思う。
        List<NeighborDistance> neighborsList = helpMapper.getNeighborhood(my_id, location);
        try {
            for (NeighborDistance neighborDistance : neighborsList) {
                String sns_id = snsRegisterMapper.getSnsId(neighborDistance.getUser_id());
                lineMessageRestClient.requestHelp(sns_id, neighborDistance, handicapInfo);
            }
        } catch(Exception e) {
            logger.error("line message error.");
        }
    }

    public void help_cancel(String my_id) {
        helpMapper.cancel(my_id);
    }

    public SignalList getHelpSignals(String my_id, String x_geometry, String y_geometry) {
        String location = GeometryDto.getPoint(x_geometry, y_geometry);
        List<HelpSignal> helpSignals;
        try {
            helpSignals = helpMapper.getHelpSignals(my_id, location);
        } catch (Exception e) {
            helpSignals = new ArrayList<>();
            logger.info("no help signal found.");
        }
        return SignalList.builder().help_signals(helpSignals).build();
    }

    public HelpSignal getHelpSignal(Integer help_id, String x_geometry, String y_geometry) {
        String location = GeometryDto.getPoint(x_geometry, y_geometry);
        HelpSignal helpSignal;
        try {
            helpSignal = helpMapper.getHelpSignal(help_id, location);
        } catch (Exception e) {
            helpSignal = null;
            logger.info("no help signal found.");
        }
        return helpSignal;
    }

    public List<NeighborDistance> getNeigborhood(String my_id, String location) {
        List<NeighborDistance> neighborList = helpMapper.getNeighborhood(my_id, location);
        return neighborList;
    }

    public void accept(Integer help_id, String volunteer_id) {
        helpMapper.accept(help_id,volunteer_id);

        /*
         * 障害者側にThanksボタンを表示
         */
        String handicappedId = helpMapper.getHandicappedId(help_id);
        String handicapped_sns_id = snsRegisterMapper.getSnsId(handicappedId);
        lineMessageRestClient.requestThanks(handicapped_sns_id,help_id);
    }

    public Help getHelpInfo(Integer help_id) {
        return helpMapper.getHelpInfo(help_id);
    }

    public void registerArea(CheckinAreaRegisterRequest request,String editor_id) {
        checkinAreaMapper.insertArea(CheckinAreaDto.checkinArea(request,editor_id));
    }
}
