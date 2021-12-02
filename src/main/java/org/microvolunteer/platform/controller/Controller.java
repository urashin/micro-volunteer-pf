package org.microvolunteer.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.microvolunteer.platform.resource.request.*;
import org.microvolunteer.platform.resource.response.*;
import org.microvolunteer.platform.service.SnsIdRegisterService;
import org.microvolunteer.platform.service.TokenService;
import org.microvolunteer.platform.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@Slf4j
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);
    private UsersService usersService;
    private TokenService tokenService;
    private SnsIdRegisterService snsIdRegisterService;

    @Autowired
    public Controller(
             UsersService usersService
            ,TokenService tokenService
            ,SnsIdRegisterService snsIdRegisterService
    ) {
        this.usersService = usersService;
        this.tokenService = tokenService;
        this.snsIdRegisterService = snsIdRegisterService;
    }

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

    /*
     * LINE Bot起点のユーザー登録処理
     * [1] LINE Bot"microvlunteer（マイクロボランティア）"を友達登録
     * [2] 友達登録ボタン or 登録状態判定で表示する登録url
     * [3] /user/sns_register APIでsns_idを登録＆トークン発行
     * [4] /user/register APIでemail, password, nameを登録
     */

    /**
     * [3] /user/sns_register APIでsns_idを登録＆トークン発行
     * ☆新規ユーザー登録(LINE Bot経由：現在の登録手段はこれのみ）
     * 1) SnsIdテーブルにsns_idを追加
     * 2) Usersテーブルにuser_id(create), email(default), password(default), status(init)をinsertする
     *
     * @param snsRegisterRequest
     * @return
     */
    @GetMapping("/user/sns_register")
    @ResponseBody
    public SnsRegisterResponse snsRegister(@RequestBody SnsRegisterRequest snsRegisterRequest){
        logger.info("sns register API: {}", snsRegisterRequest.getSns_id());
        // 1) user_id を新規発行（個々の情報はパスワード設定など、個別に設定）
        String user_id = usersService.createUser();

        // 2) session 管理のトークンを発行
        String token = tokenService.createToken(user_id);

        // 3) SnsId tableにuser_id&sns_idのペアで登録し、紐付け完了
        snsIdRegisterService.registerSnsId(snsRegisterRequest.getSns_id(),user_id, snsRegisterRequest.getSns_type());
        return SnsRegisterResponse.builder().token(token).build();
        // Usersテーブルにuser_id & statusのみinsertする（他の要素はonetimeurl発行→登録
    }

    /**
     * [4] /user/register APIでemail, password, nameを登録
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("/user/register")
    @ResponseBody
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest){
        logger.info("sns register API: {}", registerRequest.getEmail());
        // user_idを生成
        // x SnsIdテーブルを更新：UPDATE SnsId SET user_id = xxx, sns_id = sss WHERE SnsId.onetimepath = onetimepath;
        // o SnsIdてーぶるにインサートする
        // Usersテーブルにuser_id & statusのみinsertする（他の要素はonetimeurl発行→登録
        return RegisterResponse.builder().result("OK").build();
    }

    /**
     * LINE Bot以外の通常のユーザー登録処理
     * 引数のemailにonetimeurlを送付する（メールサーバたてるなど面倒なので今回は省略）
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
     * よくわからなくなってきたので、JohnさんのPython実装におまかせ
     */
    public LineMessageResponse lineMessage() {
        // LINE(Botの初回発言が -> このAPI: sns_idからonetime_pathを生成し、SnsIdテーブルに記録
        // LINE -> ID連携するためのページ -> パラメータ：sns_id, ユーザー入力：email&password -> このAPI
        // tokenからuser_idを取得
        // sns_id, user_id, sns_typeをSnsIdテーブルへ登録
        return LineMessageResponse.builder().message("コマンドに応じたメッセージを入れる").build();
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @PostMapping("/matching/checkin")
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
     * マッチングが成立→help_idが発行→通知
     * help_idをもとに、障害者がボランティアの、ボランティアが障害者の詳細情報を取得する
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

    @GetMapping("/kakunin")
    @ResponseBody
    public CheckInResponse checkin(){
        logger.info("疎通確認 URL");
        return CheckInResponse.builder().result("OK").build();
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
