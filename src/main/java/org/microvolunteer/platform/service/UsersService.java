package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.HandicapInfoRegisterMapper;
import org.microvolunteer.platform.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.dao.mapper.ThanksMapper;
import org.microvolunteer.platform.dao.mapper.UserMapper;
import org.microvolunteer.platform.dto.GeometryDto;
import org.microvolunteer.platform.dto.HandicapInfoDto;
import org.microvolunteer.platform.dto.UserPropertyDto;
import org.microvolunteer.platform.dto.VolunteerHistoryDto;
import org.microvolunteer.platform.resource.request.ThanksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
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
        userMapper.insertUserProperty(
                UserPropertyDto.builder()
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
        GeometryDto location = GeometryDto.builder().x_geometry(x).y_geometry(y).build();
        Integer status = 0;
        matchingService.insertMyGeometry(uuid.toString(), location, status);
        return uuid.toString();
    }

    /**
     * ユーザー情報取得 API
     * @param user_id
     * @return
     */
    public UserPropertyDto getUserProperty(String user_id) {
        return userMapper.getUserProperty(user_id);
    }

    /**
     * 障害者が障害情報を登録するためのAPI
     * @param handicapInfo
     */
    public void registerHandicappedInfo(HandicapInfoDto handicapInfo) {
        handicapInfoRegisterMapper.registerHandicapInfo(handicapInfo);
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapinfo_id
     */
    public HandicapInfoDto getHandicappedInfo(Integer handicapinfo_id) {
        HandicapInfoDto handicapInfo = handicapInfoRegisterMapper.getHandicapInfo(handicapinfo_id);
        return handicapInfo;
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapped_id
     */
    public List<HandicapInfoDto> getMyHandicapList(String handicapped_id) {
        List<HandicapInfoDto> handicaplist = handicapInfoRegisterMapper.getHandicapList(handicapped_id);
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
    public List<VolunteerHistoryDto> getMyVolunteerHistory(String volunteer_id,Integer get_limit) {
        List<VolunteerHistoryDto> history = thanksMapper.getMyVolunteerHistory(volunteer_id,get_limit);
        return history;
    }
}
