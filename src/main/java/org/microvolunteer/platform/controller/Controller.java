package org.microvolunteer.platform.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.*;
import org.microvolunteer.platform.domain.resource.response.*;
import org.microvolunteer.platform.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/api")
@Slf4j
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SnsIdRegisterService snsIdRegisterService;

    @Autowired
    private AdminService adminService;

    /**
     * ログインAPI.
     */
    @PostMapping("/user/login")
    @ResponseBody
    @ApiOperation(value="login", notes="emailとpasswordでログインし、tokenを取得する")
    public LoginResponse app_login(@RequestBody LoginRequest loginRequest){
        logger.info("App ログインAPI");
        String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return LoginResponse.builder().token(token).build();
    }

    /**
     * [3] /user/sns_register APIでsns_idを登録＆トークン発行
     * ☆新規ユーザー登録(LINE Bot経由：現在の登録手段はこれのみ）
     * 残課題　issue : https://github.com/urashin/micro-volunteer-docs/issues/37
     * 1) SnsIdテーブルにsns_idを追加
     * 2) Usersテーブルにuser_id(create), email(default), password(default), status(init)をinsertする
     *
     */
    @GetMapping("/user/register/{sns_id}")
    @ApiOperation(value="新規ユーザー登録(1) for LINE user", notes="LINEのuser idを用いた新規ユーザーユーザー登録")
    public SnsRegisterResponse snsRegister(@PathVariable String sns_id){
        logger.info("sns register API");
        // 1) user_id を新規発行（個々の情報はパスワード設定など、個別に設定）
        String user_id = userService.createUser();

        // 2) session 管理のトークンを発行
        String token = tokenService.createToken(user_id);

        // 3) SnsId tableにuser_id&sns_idのペアで登録し、紐付け完了
        snsIdRegisterService.registerSnsId(sns_id,user_id, 1);
        return SnsRegisterResponse.builder().token(token).build();
        // Usersテーブルにuser_id & statusのみinsertする（他の要素はonetimeurl発行→登録
    }

    /**
     * email, password, nameを登録.
     * sns_idもしくはonetimeurlにより、tokenは取得できている状態
     *
     * @param registerUserRequest
     * @return
     */
    @PostMapping("/user/register")
    @ResponseBody
    @ApiOperation(value="新規ユーザー登録(2) ユーザー情報設定(共通)", notes="新規ユーザー登録(1)で取得したtokenを用いて名前、email、passwordを設定する")
    public UserRegisterResponse app_register(@RequestBody RegisterUserRequest registerUserRequest){
        logger.info("app register API");
        // tokenからuser_idを取得
        String user_id = tokenService.getUserId(registerUserRequest.getToken());
        userService.registerUserInfo(
                user_id,
                registerUserRequest.getName(),
                registerUserRequest.getEmail(),
                registerUserRequest.getPassword());
        return UserRegisterResponse.builder().result("OK").build();
    }


    /**
     * 障害者が適切な手助けを受けられるよう、障害情報を登録するためのAPI
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("/user/handicap/register")
    @ResponseBody
    @ApiOperation(value="Helpボタン追加(障がい者側)", notes="Helpボタンを１つ追加し、Help発信の内容を設定する")
    public HandicapRegisterResponse handicap_register(@RequestBody HandicapRegisterRequest registerRequest){
        logger.info("handicap register API");
        String user_id = tokenService.getUserId(registerRequest.getToken());
        userService.registerHandicappedInfo(user_id,registerRequest);
        return HandicapRegisterResponse.builder().result("OK").build();
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @PostMapping("/matching/checkin")
    @ResponseBody
    @ApiOperation(value="チェックイン(共通)", notes="現在地（緯度経度、特定のエリアに入っているかどうか）を登録する")
    public CheckInResponse checkin(@RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API");
        String user_id = tokenService.getUserId(checkInRequest.getToken());
        matchingService.updateMyGeometry(user_id,checkInRequest);
        return CheckInResponse.builder().result("OK").build();
    }

    @PostMapping("/matching/checkout")
    @ResponseBody
    @ApiOperation(value="チェックアウト(共通)", notes="チェックイン位置からの離脱を登録する")
    public CheckInResponse checkout(@RequestBody SimpleRequest simpleRequest){
        logger.info("CheckIn API");
        String user_id = tokenService.getUserId(simpleRequest.getToken());
//        matchingService.checkoutMyGeometry(user_id);
        return CheckInResponse.builder().result("OK").build();
    }

    @GetMapping("/user/myprofile")
    @ResponseBody
    @ApiOperation(value="My profile取得(共通)", notes="自分のprofile情報（ボランティア情報の要約と、登録してあるHelp情報一覧の取得")
    public MyProfileResponse getMyProfile(@RequestBody SimpleRequest request){
        logger.info("getMyProfile API");
        String user_id = tokenService.getUserId(request.getToken());
        MyProfile myProfile = userService.getMyProfile(user_id);
        return MyProfileResponse.builder()
                .volunteer_summary(myProfile.getVolunteer_summary())
                .handicap_list(myProfile.getHandicap_list())
                .build();
    }

    /**
     * my_handicap_list
     * 障害者のHelp画面の個々のヘルプボタンに割り当てるヘルプ情報を取得する
     */
    @GetMapping("/user/handicaplist")
    @ResponseBody
    @ApiOperation(value="自分のHelp設定状況を取得(障がい者側)", notes="自分が設定しているHelp情報をリストで取得する")
    public MyHandicapInfoResponse getMyHandicapList(@RequestBody SimpleRequest request){
        logger.info("handicaplist API");
        // 障害者の位置情報を更新
        String user_id = tokenService.getUserId(request.getToken());
        // 障害者の障害情報リストを取得
        List<MyHandicap> handicapInfoList = userService.getMyHandicapList(user_id);
        return MyHandicapInfoResponse.builder().handicapInfoList(handicapInfoList).build();
    }

    /**
     * ★help（障害者側から）
     */
    @PostMapping("/matching/help")
    @ResponseBody
    @ApiOperation(value="Help発信(障がい者側)", notes="登録済みのHelp情報に関するHelp発信を行う")
    public HelpResponse help(@RequestBody HelpRequest helpRequest){
        logger.info("help API");
        // 障害者の位置情報を更新
        String user_id = tokenService.getUserId(helpRequest.getToken());
        HandicapInfo handicapInfo = userService.getHandicappedInfo(helpRequest.getHandicapinfo_id());
        matchingService.help(user_id, helpRequest, handicapInfo);
        return HelpResponse.builder().result("OK").build();
    }

    @PostMapping("/matching/accept")
    @ResponseBody
    @ApiOperation(value="発信されたHelpに応じる(ボランティア側)", notes="近くで発信されているHelpに応じる")
    public NormalResponse acceptRush_api(@RequestBody AcceptRequest acceptRequest) {
        logger.info("accept API");
        String user_id = tokenService.getUserId(acceptRequest.getToken());
        Location geo = matchingService.getMyGeometry(user_id);
        matchingService.accept(acceptRequest.getHelp_id(),user_id);
        //HelpSignal helpSignal = matchingService.getHelpSignal(acceptRequest.getHelp_id(),geo.getX_geometry(), geo.getY_geometry());
        return NormalResponse.builder()
                .result("OK")
                .build();
    }

    /**
     * 送ったHelpのリスト、thanksを送っていないものと、送り済みにわけたリスト.
     */
    @GetMapping("/user/thanks")
    @ResponseBody
    @ApiOperation(value="自分が受けた支援行為一覧取得(障がい者側)", notes="Thanksを送る対象の支援行為一覧の取得")
    public ThanksListResponse getThanksList(@RequestBody SimpleRequest request){
        logger.info("get thanks list API: {}");
        String handicapped_id = tokenService.getUserId(request.getToken());

        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        return ThanksListResponse.builder()
                .doneList(thanksList.getDone_list())
                .sendList(thanksList.getSend_list())
                .build();
    }

    /**
     * ★thanks（障害者から）
     */
    @PostMapping("/user/thanks")
    @ResponseBody
    @ApiOperation(value="支援行為に対する評価(障がい者側)", notes="help idで指定した支援行為に対する評価を行う")
    public NormalResponse thanks(@RequestBody ThanksRequest thanksRequest){
        logger.info("thanks API: {}", thanksRequest.getHelp_id());
        String handicapped_id = tokenService.getUserId(thanksRequest.getToken());

        userService.thanks(thanksRequest.getHelp_id(),handicapped_id,thanksRequest.getEvaluate());
        return NormalResponse.builder()
                .result("OK")
                .build();
    }

    /**
     * ★ボランティア履歴の取得
     */
    @GetMapping("/user/history")
    @ResponseBody
    @ApiOperation(value="自分が行った支援履歴の取得(ボランティア側)", notes="自分が行った支援履歴を取得する")
    public VolunteerHistoryResponse history(@RequestBody SimpleRequest request){
        logger.info("history API");
        String user_id = tokenService.getUserId(request.getToken());

        Integer get_limit = 10;
        List<VolunteerHistory> volunteerHistory = userService.getMyVolunteerHistory(user_id,get_limit);
        return VolunteerHistoryResponse.builder()
                .volunteerHistory(volunteerHistory)
                .build();
    }

    @PostMapping("/matching/listen-signals")
    @ResponseBody
    @ApiOperation(value="周囲のHelpを探知(ボランティア側)", notes="自分の周囲で発信されているHelpを探し、リスト形式で取得")
    public ListenSignalsResponse listenSignals(@RequestBody ListenRequest listenRequest) {
        logger.info("listen-signals API");
        String token = listenRequest.getToken();
        String user_id = tokenService.getUserId(token);
        SignalList signalList = matchingService.getHelpSignals(user_id, listenRequest.getX_geometry(), listenRequest.getY_geometry());

        return ListenSignalsResponse.builder()
                .helpSignals(signalList.getHelpSignals())
                .build();
    }

    /**
     *
     */
    @PostMapping("/admin/area_register")
    @ResponseBody
    @ApiOperation(value="エリア設定(管理者機能)", notes="checkinにより地名を表示するエリアを設定する")
    public String area_register(@RequestBody CheckinAreaRegisterRequest request){
        logger.info("area_register API");
        String user_id = tokenService.getUserId(request.getToken());

        matchingService.registerArea(request,user_id);
        return "OK";
    }

    @PostMapping("/admin/add_handicap_type")
    @ResponseBody
    @ApiOperation(value="障がいのタイプを追加(管理者機能)", notes="DBに新しい障がい情報を登録する（アイコン登録は別）")
    public String add_handicap_type(@RequestBody AddHandicapTypeRequest request){
        logger.info("add handicap type API");
        String auth_code = request.getAuth_code();
        HandicapMaster master = HandicapMaster.builder()
                .handicap_name(request.getHandicap_name())
                .comment(request.getComment())
                .icon_path(request.getIcon_path())
                .build();

        adminService.addHandicapMaster(master,auth_code);
        return "OK";
    }

    @GetMapping("/hello")
    @ResponseBody
    public CheckInResponse checkin(){
        logger.info("疎通確認 URL");
        return CheckInResponse.builder().result("OK").build();
    }

}
