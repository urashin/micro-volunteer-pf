package org.microvolunteer.platform.service;

import org.microvolunteer.platform.domain.dto.ActivityDto;
import org.microvolunteer.platform.domain.resource.request.HandicapRegisterRequest;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.repository.dao.mapper.HandicapInfoMapper;
import org.microvolunteer.platform.repository.dao.mapper.ThanksMapper;
import org.microvolunteer.platform.repository.dao.mapper.UserMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThanksMapper thanksMapper;

    @Autowired
    private HandicapInfoMapper handicapInfoMapper;

    @Autowired
    private MatchingService matchingService;

    @Value("${encrypt.volunteerdb.key}")
    private String encrypt_key;

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
                        .encrypt_key(encrypt_key)
                        .user_id(uuid.toString())
                        .email("")
                        .password("password")
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
    public void registerUserInfo(
            String user_id,
            String user_name,
            String user_email,
            String user_password) {
        userMapper.updateUserProperty(
                RegisterUserProperty.builder()
                .encrypt_key(encrypt_key)
                .user_id(user_id)
                .name(user_name)
                .email(user_email)
                .password(user_password)
                .status(1)
                .build());
    }

    /*
     *
     */
    public String login(
            String email,
            String password) {
        String user_id = userMapper.login(
                Login.builder()
                        .encrypt_key(encrypt_key)
                        .email(email)
                        .password(password)
                        .build());
        return user_id;
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
                .reliability_th(0)
                .severity(registerRequest.getSeverity())
                .handicap_type(registerRequest.getHandicap_type())
                .handicap_level(registerRequest.getHandicap_level())
                .comment(registerRequest.getComment())
                .build();
        handicapInfoMapper.registerHandicapInfo(registerHandicapInfo);
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapinfo_id
     */
    public HandicapInfo getHandicappedInfo(Integer handicapinfo_id) {
        HandicapInfo handicapInfo = handicapInfoMapper.getHandicapInfo(handicapinfo_id);
        return handicapInfo;
    }

    /**
     * 障害者の障害情報を取得
     * @param handicapped_id
     */
    public List<MyHandicap> getMyHandicapList(String handicapped_id) {
        List<MyHandicap> handicaplist = handicapInfoMapper.getHandicapList(handicapped_id);
        return handicaplist;
    }

    /*
     * 助けてもらったお礼、評価
     */
    public void thanks(Integer help_id, String handicapped_id, Integer satisfaction) {
        thanksMapper.thanks(help_id,handicapped_id,satisfaction,1);
    }

    /**
     * ボランティア履歴の取得
     * @param volunteer_id
     * @param get_limit
     */
    public List<MyActivity> getMyActivities(String volunteer_id, Integer get_limit) {
        List<VolunteerHistory> historyList = thanksMapper.getMyVolunteerHistory(volunteer_id,get_limit);
        List<MyActivity>  myActivities = new ArrayList<>();
        for (VolunteerHistory history : historyList) {
            MyActivity activity = ActivityDto.getActivity(history);
            myActivities.add(activity);
        }
        return myActivities;
    }

    public List<VolunteerHistory> getMyVolunteerHistory(String volunteer_id, Integer get_limit) {
        List<VolunteerHistory> historyList = thanksMapper.getMyVolunteerHistory(volunteer_id,get_limit);
        return historyList;
    }

    public MyProfile getMyProfile(String user_id, String token) {
        List<MyHandicap> handicap_list = getMyHandicapList(user_id);
        MyVolunteerSummary mySummary = userMapper.getMyVolunteerSummary(user_id);

        MyProfile myProfile = MyProfile.builder()
                .token(token)
                .volunteer_summary(mySummary)
                .handicap_list(handicap_list)
                .build();
        return myProfile;
    }

    public ThanksList getMyThanksList(String handicapped_id, Integer th) {
        List<SendThanks> send_list = thanksMapper.getSendList(handicapped_id,th);
        List<DoneThanks> done_list = thanksMapper.getDoneList(handicapped_id,th);
        ThanksList thanksList = ThanksList.builder()
                .send_list(send_list)
                .done_list(done_list)
                .build();
        return thanksList;
    }
}
