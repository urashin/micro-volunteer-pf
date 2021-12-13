package org.microvolunteer.platform.service;

import org.microvolunteer.platform.domain.resource.request.HandicapRegisterRequest;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.repository.dao.mapper.HandicapInfoRegisterMapper;
import org.microvolunteer.platform.repository.dao.mapper.ThanksMapper;
import org.microvolunteer.platform.repository.dao.mapper.UserMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.microvolunteer.platform.domain.resource.request.ThanksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class UsersService {
    private UserMapper userMapper;
    private ThanksMapper thanksMapper;
    private HandicapInfoRegisterMapper handicapInfoRegisterMapper;
    private MatchingService matchingService;

    @Autowired
    public UsersService(
            UserMapper userMapper
            ,ThanksMapper thanksMapper
            ,HandicapInfoRegisterMapper handicapInfoRegisterMapper
            ,MatchingService matchingService
    ) {
        this.userMapper = userMapper;
        this.thanksMapper = thanksMapper;
        this.handicapInfoRegisterMapper = handicapInfoRegisterMapper;
        this.matchingService= matchingService;
    }

    public String createUser() {
        UUID uuid = UUID.randomUUID();
        userMapper.registerUserProperty(
                RegisterUserProperty.builder()
                        .user_id(uuid.toString())
                        .email("mail")
                        .password("pass")
                        .name("name")
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
    public void thanks(ThanksRequest thanks, String volunteer_id) {
        thanksMapper.thanks(thanks.getHelp_id(),volunteer_id,thanks.getEvaluate(),1);
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
