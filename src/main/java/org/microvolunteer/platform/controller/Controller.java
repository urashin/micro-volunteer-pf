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
     * Pythonで実装
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
     * ここら辺はPythonでJohnさんにおまかせ？
     * ☆新規ユーザー登録（よゆうがあれば作る、なければDBに直書）
     * ontimeurlから遷移したユーザー登録画面からよばれる
     * @param loginRequest
     * @return
     */
    @PostMapping("/user/{onetimepath}/register")
    @ResponseBody
    public RegisterResponse register(@PathVariable String onetimepath, @RequestBody LoginRequest loginRequest){
        logger.info("ログインAPI: {}", onetimepath);
        // user_idを生成
        // Usersテーブルにユーザーを追加：passwordと一緒にUsersテーブルへ記録
        // SnsIdテーブルを更新：UPDATE SnsId SET user_id = xxx, sns_id = sss WHERE SnsId.onetimepath = onetimepath;
        return RegisterResponse.builder().result("OK").build();
    }

    /**
     * ここら辺はPythonでJohnさんにおまかせ？
     * ★LINEとサービスのアカウント情報紐付けのためのonetimeurl発行API
     */
    @GetMapping("/user/onetimeurl")
    @ResponseBody
    public OnetimeurlResponse onetimeurl(@RequestBody OnetimeurlRequest onetimeurlRequest){
        logger.info("onetimeurl API: {}", onetimeurlRequest.getSnsId());
        // LINE(Botの初回発言が -> このAPI: sns_idからonetime_pathを生成し、SnsIdテーブルに記録
        //
        // LINE -> ID連携するためのページ -> パラメータ：sns_id, ユーザー入力：email&password -> このAPI
        // tokenからuser_idを取得
        // sns_id, user_id, sns_typeをSnsIdテーブルへ登録
        return OnetimeurlResponse.builder().onetimeurl("http://tokenXXXXXXXXXXXXXXXXXXXX").build();
    }

    /**
     * ★LINE Botコールバック
     * よくわからなくなってきたので、Johnさんに相談するw
     */
    @GetMapping("/user/line_message")
    @ResponseBody
    public LineMessageResponse lineMessage(@RequestBody LineMessageRequest lineMessageRequest){
        logger.info("onetimeurl API: {}", lineMessageRequest.getSnsId());
        // LINE(Botの初回発言が -> このAPI: sns_idからonetime_pathを生成し、SnsIdテーブルに記録
        //
        // LINE -> ID連携するためのページ -> パラメータ：sns_id, ユーザー入力：email&password -> このAPI
        // tokenからuser_idを取得
        // sns_id, user_id, sns_typeをSnsIdテーブルへ登録
        return LineMessageResponse.builder().message("http://tokenXXXXXXXXXXXXXXXXXXXX").build();
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @GetMapping("/matching/checkin")
    @ResponseBody
    public CheckInResponse checkin(@RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API: {}", checkInRequest.getUserId());
        // tokenからuser_idを取得
        // user_idと位置座標をMyGEOMETRYテーブルに登録
        return CheckInResponse.builder().result("OK").build();
    }

    /**
     * ★help（障害者側から）
     */
    @PostMapping("/matching/help")
    @ResponseBody
    public HelpResponse help(@RequestBody HelpRequest helpRequest){
        logger.info("CheckIn API: {}", helpRequest.getUserId());
        // 対象ボランティアの抽出（マッチング）
        // 対象ボランティアへのpush通知(python APIを使う)
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
     * ☆QRコード生成（引数のURLに入れておくもの：
     * ☆QRコード解釈（url取得→URLに入れる情報
     * ☆自分の評価を公開するようのurl発行
     * ☆指定スポット付近でよく発生する困りごと（自分の障害でフィルター可能）
     */

}
