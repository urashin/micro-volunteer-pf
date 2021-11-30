package org.microvolunteer.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.microvolunteer.platform.resource.request.*;
import org.microvolunteer.platform.resource.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@Slf4j
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);

    /**
     * ☆新規ユーザー登録（よゆうがあれば作る、なければDBに直書）
     */

    /**
     * ★LINEとサービスのアカウント情報紐付けのためのonetimeurl発行API
     * ☆token発行（時間あればつくる、なければ固定のtokenでおこなう）
     * @param loginRequest
     * @return
     */
    @GetMapping("/user/login")
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        logger.info("ログインAPI: {}", loginRequest.getUserId());

        return LoginResponse.builder().token("tokenXXXXXXXXXXXXXXXXXXXX").build();
    }

    /**
     * ★LINEとサービスのアカウント情報紐付けのためのonetimeurl発行API
     */
    @GetMapping("/user/onetimeurl")
    @ResponseBody
    public OnetimeurlResponse onetimeurl(@RequestBody OnetimeurlRequest onetimeurlRequest){
        logger.info("onetimeurl API: {}", onetimeurlRequest.getUserId());

        return OnetimeurlResponse.builder().onetimeurl("http://tokenXXXXXXXXXXXXXXXXXXXX").build();
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @GetMapping("/matching/checkin")
    @ResponseBody
    public CheckInResponse checkin(@RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API: {}", checkInRequest.getUserId());

        return CheckInResponse.builder().result("OK").build();
    }

    /**
     * ★help（障害者側から）
     */
    @PostMapping("/matching/help")
    @ResponseBody
    public HelpResponse help(@RequestBody HelpRequest helpRequest){
        logger.info("CheckIn API: {}", helpRequest.getUserId());

        return HelpResponse.builder().result("OK").build();
    }

    /**
     * ★help詳細を取得（ボランティア側がヘルプ内容を参照）
     */
    @GetMapping("/matching/helpdetail")
    @ResponseBody
    public HelpDetailResponse helpDetail(@RequestBody HelpDetailRequest helpDetailRequest){
        logger.info("HelpDetail API: {}", helpDetailRequest.getHelpId());

        return HelpDetailResponse.builder()
                .helpId(1)
                .xGeometry("xxxx.xxxx")
                .yGeometry("yyyy.yyy")
                .helpType(5)
                .helpComment("help!")
                .build();
    }


    /**
     * ★accept（ボランティア側から）
     */
    @PostMapping("/matching/accept")
    @ResponseBody
    public AcceptResponse accept(@RequestBody AcceptRequest acceptRequest){
        logger.info("accept API: {}", acceptRequest.getHelpId());

        return AcceptResponse.builder()
                .helpId(1)
                .xGeometry("xxxx.xxxx")
                .yGeometry("yyyy.yyy")
                .build();
    }

    /**
     * ★thanks（障害者から）
     */
    @PostMapping("/evaluate/thanks")
    @ResponseBody
    public ThanksResponse thanks(@RequestBody ThanksRequest thanksRequest){
        logger.info("thanks API: {}", thanksRequest.getHelpId());

        return ThanksResponse.builder()
                .result("OK")
                .build();
    }


    /**
     * ★line callbackで通知の仕組みを作る
     * ★自分の評価一覧
     * ☆自分の評価を公開するようのurl発行
     * ☆指定スポット付近でよく発生する困りごと（自分の障害でフィルター可能）
     */

}
