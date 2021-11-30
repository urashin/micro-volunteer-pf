package org.microvolunteer.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.microvolunteer.platform.resource.request.LoginRequest;
import org.microvolunteer.platform.resource.response.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@Slf4j
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);

    /*
     * ☆新規ユーザー登録（よゆうがあれば作る、なければDBに直書）
     * ☆token発行（時間あれば、なけれ固定のtokenでおこなう）
     * ★LINEとサービスのアカウント情報紐付けのためのonetimeurl発行API
     * ★checkin（障害者＆ボランティア両方）
     * ★help（障害者側から）
     * ★help詳細を取得（ボランティア側がヘルプ内容を参照）
     * ★accept（ボランティア側から）
     * ★thanks（障害者から）
     * ★line callbackで通知の仕組みを作る
     * ★自分の評価一覧
     * ☆自分の評価を公開するようのurl発行
     * ☆指定スポット付近でよく発生する困りごと（自分の障害でフィルター可能）
     */

    /**
     * ★LINEとサービスのアカウント情報紐付けのためのonetimeurl発行API
     * ☆token発行（時間あれば、なけれ固定のtokenでおこなう）
     * @param loginRequest
     * @return
     */
    @GetMapping("/user/login")
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        logger.info("ログインAPI: {}", loginRequest.getUserId());

        return LoginResponse.builder().token("token").build();
    }
}
