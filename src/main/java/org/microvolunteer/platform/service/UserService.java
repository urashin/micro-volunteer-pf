package org.microvolunteer.platform.service;

import org.microvolunteer.platform.domain.resource.request.HandicapRegisterRequest;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.RegisterRequest;
import org.microvolunteer.platform.repository.dao.mapper.HandicapInfoRegisterMapper;
import org.microvolunteer.platform.repository.dao.mapper.ThanksMapper;
import org.microvolunteer.platform.repository.dao.mapper.UserMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.microvolunteer.platform.domain.resource.request.ThanksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;


@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThanksMapper thanksMapper;

    @Autowired
    private HandicapInfoRegisterMapper handicapInfoRegisterMapper;

    @Autowired
    private MatchingService matchingService;

    /*
     * ユーザーの新規作成（ユーザー情報は空）
     * 1 user_idの作成
     * 2 x,yの初期座標(mygeometry)作成
     * 3 user_id & MyGeometryの登録
     */
    public String createUser() {
        UUID uuid = UUID.randomUUID();
        userMapper.registerUserProperty(
                RegisterUserProperty.builder()
                        .user_id(uuid.toString())
                        .email("")
                        .password("")
                        .name("")
                        .status(0)
                        .build()
        );
        // 初期座標を登録する
        String x = "00.0000";
        String y = "00.0000";
        String location = GeometryDto.getPoint(x,y);
        Integer status = 0;
        matchingService.insertMyGeometry(uuid.toString(), location, status);
        return uuid.toString();
    }

    /*
     *
     */
    public void registerUserInfo(String user_id,RegisterRequest registerRequest) {
        userMapper.registerUserProperty(RegisterUserProperty.builder()
                .user_id(user_id)
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .build());
    }

    /**
     * ユーザー情報取得 API
     * @param user_id
     * @return
     */
    public UserProperty getUserProperty(String user_id) {
        return userMapper.getUserProperty(user_id);
    }

    /**
     * 障害者が障害情報を登録するためのAPI
     * @param user_id
     * @param registerRequest
     */
    public void registerHandicappedInfo(String user_id, HandicapRegisterRequest registerRequest) {
        RegisterHandicapInfo registerHandicapInfo = RegisterHandicapInfo.builder()
                .handicapped_id(user_id)
                .reliability_th(registerRequest.getReliability_th())
                .severity(registerRequest.getSeverity())
                .handicap_type(registerRequest.getHandicap_type())
                .handicap_level(registerRequest.getHandicap_level())
                .comment(registerRequest.getComment())
                .build();
        handicapInfoRegisterMapper.registerHandicapInfo(registerHandicapInfo);
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapinfo_id
     */
    public HandicapInfo getHandicappedInfo(Integer handicapinfo_id) {
        HandicapInfo handicapInfo = handicapInfoRegisterMapper.getHandicapInfo(handicapinfo_id);
        return handicapInfo;
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapped_id
     */
    public List<HandicapInfo> getMyHandicapList(String handicapped_id) {
        List<HandicapInfo> handicaplist = handicapInfoRegisterMapper.getHandicapInfoList(handicapped_id);
        return handicaplist;
    }

    /**
     * 助けてもらったお礼、評価
     */
    public void thanks(ThanksRequest thanks, String handicapped_id) {
        thanksMapper.thanks(thanks.getHelp_id(),handicapped_id,thanks.getEvaluate(),1);
    }

    /**
     * ボランティア履歴の取得
     * @param volunteer_id
     * @param get_limit
     */
    public List<VolunteerHistory> getMyVolunteerHistory(String volunteer_id, Integer get_limit) {
        List<VolunteerHistory> history = thanksMapper.getMyVolunteerHistory(volunteer_id,get_limit);
        return history;
    }
}
