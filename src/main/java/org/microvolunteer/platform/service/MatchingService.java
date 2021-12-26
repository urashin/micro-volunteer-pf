package org.microvolunteer.platform.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.controller.Controller;
import org.microvolunteer.platform.domain.resource.request.CheckInRequest;
import org.microvolunteer.platform.domain.resource.request.HelpRequest;
import org.microvolunteer.platform.domain.resource.snsmessage.LineLocationMessageRequest;
import org.microvolunteer.platform.domain.resource.snsmessage.LineLocationMessageResponse;
import org.microvolunteer.platform.domain.resource.snsmessage.LineMessageRequest;
import org.microvolunteer.platform.domain.resource.snsmessage.LineMessageResponse;
import org.microvolunteer.platform.repository.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.repository.dao.mapper.HelpMapper;
import org.microvolunteer.platform.domain.dto.GeometryDto;
import org.microvolunteer.platform.domain.dto.HelpDto;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.repository.dao.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
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
                request.getY_geometry()
                ,request.getX_geometry());
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
        for (NeighborDistance neighborDistance : neighborsList) {
            String sns_id = snsRegisterMapper.getSnsId(neighborDistance.getUser_id());
            lineMessageRestClient.request(sns_id,neighborDistance,handicapInfo);
            //sendLineLocationMessage(sns_id,neighborDistance,handicapInfo);
            //sendLineMessage(sns_id,handicapInfo);
        }
        // 対象ボランティアへのpush通知(python APIを使う)
    }

    /*
     * LINE通知のためのPython側との連携を、一旦WebAPIで実装しているが、
     * MQで置き換えたい。
     */
    private void sendLineLocationMessage(String sns_id,NeighborDistance neighborDistance,HandicapInfo handicapInfo) {
        //String URL = "http://localhost:8000/v1/sns/send-request-volunteer";
        String URL = "http://52.199.10.90:8000/v1/sns/send-request-volunteer";

        /*
         * LocationMessageDetailsリストの作成
         */
        LineLocationMessageRequest.LineMessageDetail messageDetail_a
                = LineLocationMessageRequest.LineMessageDetail.builder()
                .label("ハンディキャップタイプ")
                .text(handicapInfo.getHandicap_type().toString())
                .build();
        LineLocationMessageRequest.LineMessageDetail messageDetail_b
                = LineLocationMessageRequest.LineMessageDetail.builder()
                .label("ハンディキャップレベル")
                .text(handicapInfo.getHandicap_level().toString())
                .build();
        List<LineLocationMessageRequest.LineMessageDetail> messageDetails = new ArrayList<>();
        messageDetails.add(messageDetail_a);
        messageDetails.add(messageDetail_b);

        /*
         * ボタンの作成
         */
        LineLocationMessageRequest.LineMessageAction messageAction_accept =
                LineLocationMessageRequest.LineMessageAction.builder()
                        .text("助ける")
                        .link("https://example.com/action?yahoo")
                        .build();
        LineLocationMessageRequest.LineMessageAction messageAction_ignore =
                LineLocationMessageRequest.LineMessageAction.builder()
                        .text("今は無理")
                        .link("https://example.com/action?yahoo")
                        .build();
        LineLocationMessageRequest.LineMessageActions messageActions
            = LineLocationMessageRequest.LineMessageActions.builder()
                .primary(messageAction_accept)
                .secondary(messageAction_ignore)
                .build();

        /*
         * RequestMessage本体の作成
         */
        LineLocationMessageRequest lineLocationMessage = LineLocationMessageRequest.builder()
                .sns_id(sns_id)
                .location_message(
                        LineLocationMessageRequest.LineLocationMessage.builder()
                                .title("Help!")
                                .address("近隣の住所")
                                .longitude(Double.valueOf(neighborDistance.getY_geometry()))
                                .latitude(Double.valueOf(neighborDistance.getX_geometry()))
                        .build()
                )
                .action_message(
                        LineLocationMessageRequest.LineActionMessage.builder()
                                .title(handicapInfo.getComment())
                                .details(messageDetails)
                                .actions(messageActions)
                        .build()
                )
                .build();

        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(lineLocationMessage);
        logger.info("LINE Message request {}", prettyJson);

        RestTemplate restTemplate = new RestTemplate();
        LineLocationMessageResponse response = restTemplate.postForObject(URL,lineLocationMessage, LineLocationMessageResponse.class);
    }

    /*
     * LINE通知のためのPython側との連携を、一旦WebAPIで実装しているが、
     * MQで置き換えたい。
     */
    private void sendLineMessage(String sns_id,HandicapInfo handicapInfo) {
        String URL = "http://localhost:8000/v1/sns/send-text";
        LineMessageRequest lineMessage = LineMessageRequest.builder()
                .sns_id(sns_id)
                .message(handicapInfo.getComment())
                .build();
        RestTemplate restTemplate = new RestTemplate();
        LineMessageResponse response = restTemplate.postForObject(URL,lineMessage,LineMessageResponse.class);
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
