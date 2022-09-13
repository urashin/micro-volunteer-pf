package org.microvolunteer.platform.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
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
     * LINE ログイン
     */
    @GetMapping("/user/line-login")
    @ResponseBody
    public NormalResponse line_login() {
        userService.lineLogin();
        return NormalResponse.builder().result("OK").build();
    }

    /**
     * LINE Auth
     */
    @GetMapping("/auth")
    @ResponseBody
    public LoginResponse line_auth(@RequestParam("code") String code){
        logger.info("LINE Auth API");
        String line_token = userService.lineAuth(code);
        String token = line_token;
        //String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        //String token = tokenService.createToken(user_id);
        return LoginResponse.builder().token(token).build();
    }

    /**
     * ログインAPI.
     */
    @PostMapping("/user/login")
    @ResponseBody
    @ApiOperation(value="login", notes="emailとpasswordでログインし、tokenを取得する")
    public LoginResponse app_login(@RequestBody LoginRequest loginRequest){
        logger.info("App ログインAPI");
        String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = tokenService.createToken(user_id);
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
    public SnsTokenResponse snsRegister(@PathVariable String sns_id){
        logger.info("sns register API");
        try {
            // 1) user_id を新規発行（個々の情報はパスワード設定など、個別に設定）
            String user_id = userService.createUser();

            // 2) session 管理のトークンを発行
            String token = tokenService.createToken(user_id);

            // 3) SnsId tableにuser_id&sns_idのペアで登録し、紐付け完了
            snsIdRegisterService.registerSnsId(sns_id, user_id, 1);
            return SnsTokenResponse.builder().token(token).result("OK").build();
            // Usersテーブルにuser_id & statusのみinsertする（他の要素はonetimeurl発行→登録
        } catch (Exception e) {
            return SnsTokenResponse.builder().result("NG").build();
        }
    }

    /*
     * sns_idからtokenを取得する
     */
    @GetMapping("/user/token/{sns_id}")
    @ApiOperation(value="既存ユーザーのSNS ID: for LINE user", notes="登録済みLINEのuser idからtokenを生成する")
    public SnsTokenResponse snsToken(@PathVariable String sns_id){
        logger.info("get token API");
        try {
            String user_id = tokenService.getUserIdBySnsId(sns_id);
            String token = tokenService.getTokenByUserId(user_id);
            return SnsTokenResponse.builder().token(token).result("OK").build();
        } catch (Exception e) {
            logger.error("bad sns_id");
            return SnsTokenResponse.builder().result("NG").build();
        }
    }

    @GetMapping("/user/tokencheck")
    @ResponseBody
    @ApiOperation(value="新規ユーザー登録(2) ユーザー情報設定(共通)", notes="新規ユーザー登録(1)で取得したtokenを用いて名前、email、passwordを設定する")
    public NormalResponse token_check(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("token check API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            return NormalResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            logger.info("bad token");
            return NormalResponse.builder().result("NG").build();
        }
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
    public RegisterUserResponse app_register(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody RegisterUserRequest registerUserRequest){
        logger.info("app register API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            userService.registerUserInfo(
                    user_id,
                    registerUserRequest.getName(),
                    registerUserRequest.getEmail(),
                    registerUserRequest.getPassword());
            return RegisterUserResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            logger.info("bad token");
            return RegisterUserResponse.builder().result("NG").build();
        }
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
    public HandicapRegisterResponse handicap_register(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody HandicapRegisterRequest registerRequest){
        logger.info("handicap register API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            userService.registerHandicappedInfo(user_id, registerRequest);
            return HandicapRegisterResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return HandicapRegisterResponse.builder().result("NG").build();
        }
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @PostMapping("/matching/checkin")
    @ResponseBody
    @ApiOperation(value="チェックイン(共通)", notes="現在地（緯度経度、特定のエリアに入っているかどうか）を登録する")
    public CheckInResponse checkin(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            matchingService.updateMyGeometry(user_id, checkInRequest);
            return CheckInResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return CheckInResponse.builder().result("NG").build();
        }
    }

    /*
     * checkout
     */
    @PostMapping("/matching/checkout")
    @ResponseBody
    @ApiOperation(value="チェックアウト(共通)", notes="チェックイン位置からの離脱を登録する[未実装]")
    public CheckInResponse checkout(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("CheckIn API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
//        matchingService.checkoutMyGeometry(user_id);
            throw new Exception("not impremented.");
            //return CheckInResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return CheckInResponse.builder().result("NG").build();
        } catch (Exception e) {
            return CheckInResponse.builder().result("NG").build();
        }
    }

    @PostMapping("/user/myprofile")
    @ResponseBody
    @ApiOperation(value="My profile取得(共通)", notes="自分のprofile情報（ボランティア情報の要約と、登録してあるHelp情報一覧の取得")
    public MyProfileResponse getMyProfile(@RequestHeader(value="Authorization",required=true) String auth){
        logger.info("getMyProfile API");
        try {
            String token = tokenService.getTokenFromAuth(auth);
            String user_id = tokenService.getUserId(token);
            MyProfile myProfile = userService.getMyProfile(user_id);
            return MyProfileResponse.builder()
                    .volunteer_summary(myProfile.getVolunteer_summary())
                    .handicap_list(myProfile.getHandicap_list())
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return MyProfileResponse.builder()
                    // ng message
                    .build();
        }
    }

    /**
     * my_handicap_list
     * 障害者のHelp画面の個々のヘルプボタンに割り当てるヘルプ情報を取得する
     */
    @GetMapping("/user/handicaplist")
    @ResponseBody
    @ApiOperation(value="自分のHelp設定状況を取得(障がい者側)", notes="自分が設定しているHelp情報をリストで取得する")
    public MyHandicapListResponse getMyHandicapList(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("handicaplist API");
        try {
            // 障害者の位置情報を更新
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            // 障害者の障害情報リストを取得
            List<MyHandicap> handicapInfoList = userService.getMyHandicapList(user_id);
            return MyHandicapListResponse.builder().handicapInfoList(handicapInfoList).build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return MyHandicapListResponse.builder()
                    // ng message
                    .build();
        }
    }

    /**
     * ★自分のhandicap情報をhandicap_idにより取得する（障害者側から）
     */
    @GetMapping("/user/handicap/{handicap_id}")
    @ResponseBody
    @ApiOperation(value="Help発信(障がい者側)", notes="登録済みのHelp情報に関するHelp発信を行う")
    public MyHandicapResponse getMyHandicap(
            @RequestHeader(value="Authorization",required=true) String auth,
            @PathVariable Integer handicap_id){
        logger.info("myHandicap API");
        try {
            // 障害者の位置情報を更新
            String my_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            MyHandicap myHandicap = userService.getMyHandicap(my_id, handicap_id);
            return MyHandicapResponse.builder()
                    .myHandicap(myHandicap)
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            logger.error("token error.");
            return MyHandicapResponse.builder()
                    // error message
                    .build();
        }
    }

    /**
     * ★help（障害者側から）
     */
    @PostMapping("/matching/help")
    @ResponseBody
    @ApiOperation(value="Help発信(障がい者側)", notes="登録済みのHelp情報に関するHelp発信を行う")
    public HelpResponse help(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody HelpRequest helpRequest){
        logger.info("help API");
        try {
            // 障害者の位置情報を更新
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            HandicapInfo handicapInfo = userService.getHandicappedInfo(helpRequest.getHandicapinfo_id());
            matchingService.help(user_id, helpRequest, handicapInfo);
            return HelpResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return HelpResponse.builder().result("NG").build();
        }
    }

    @PostMapping("/matching/accept")
    @ResponseBody
    @ApiOperation(value="Helpに応じる(ボランティア側)", notes="近くで発信されている特定のHelpSignalに対応する意思を示す")
    public NormalResponse acceptRush_api(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody AcceptRequest acceptRequest) {
        try {
            logger.info("accept API");
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            matchingService.accept(acceptRequest.getHelp_id(), user_id);
            return NormalResponse.builder()
                    .result("OK")
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return NormalResponse.builder()
                    .result("NG")
                    .build();
        }
    }

    @GetMapping("/matching/signal")
    @ResponseBody
    @ApiOperation(value="Helpの詳細を取得する(ボランティア側)", notes="近くで発信されている特定のHelpSignalの詳細を取得する")
    public GetSignalResponse getSignal(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody AcceptRequest acceptRequest) {
        try {
            logger.info("accept API");
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            // Help発信場所と自分の位置情報から距離を計算するためMyGeometryを取得
            Location geo = matchingService.getMyGeometry(user_id);
            matchingService.accept(acceptRequest.getHelp_id(), user_id);
            HelpSignal helpSignal = matchingService.getHelpSignal(acceptRequest.getHelp_id(), geo.getX_geometry(), geo.getY_geometry());
            return GetSignalResponse.builder()
                    .helpSignal(helpSignal)
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return GetSignalResponse.builder()
                    .build();
        }
    }

    /**
     * 送ったHelpのリスト、thanksを送っていないものと、送り済みにわけたリスト.
     */
    @GetMapping("/user/thanks")
    @ResponseBody
    @ApiOperation(value="自分が受けた支援行為一覧取得(障がい者側)", notes="Thanksを送る対象の支援行為一覧の取得")
    public ThanksListResponse getThanksList(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("get thanks list API: {}");
        try {
            String handicapped_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));

            ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
            return ThanksListResponse.builder()
                    .thanksList(thanksList)
                    .result("OK")
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return ThanksListResponse.builder()
                    .result("NG")
                    .build();
        }
    }

    /**
     * ★thanks（障害者から）
     */
    @PostMapping("/user/thanks")
    @ResponseBody
    @ApiOperation(value="支援行為に対する評価(障がい者側)", notes="help idで指定した支援行為に対する評価を行う")
    public NormalResponse thanks(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody ThanksRequest thanksRequest){
        logger.info("thanks API: {}", thanksRequest.getHelp_id());
        try {
            String handicapped_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));

            userService.thanks(thanksRequest.getHelp_id(), handicapped_id, thanksRequest.getEvaluate());
            return NormalResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return NormalResponse.builder().result("NG").build();
        }
    }

    /**
     * ★thanks（障害者から）
     */
    @PostMapping("/matching/cancel")
    @ResponseBody
    @ApiOperation(value="Helpのキャンセル(障がい者側)", notes="発信中のHelpをキャンセルする")
    public NormalResponse helpCancel(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("cancel API: {}");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            matchingService.help_cancel(user_id);
            return NormalResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return NormalResponse.builder().result("NG").build();
        }
    }

    /**
     * ★ボランティア履歴の取得
     */
    @GetMapping("/user/history")
    @ResponseBody
    @ApiOperation(value="自分が行った支援履歴の取得(ボランティア側)", notes="自分が行った支援履歴を取得する")
    public VolunteerHistoryResponse history(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("history API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));

            Integer get_limit = 10;
            List<VolunteerHistory> volunteerHistory = userService.getMyVolunteerHistory(user_id, get_limit);
            return VolunteerHistoryResponse.builder()
                    .volunteerHistory(volunteerHistory)
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return VolunteerHistoryResponse.builder()
                    // ng message
                    .build();
        }
    }

    /**
     * ★ボランティア履歴の取得
     */
    @GetMapping("/user/myactivities")
    @ResponseBody
    @ApiOperation(value="自分が行った支援履歴の取得(ボランティア側)", notes="自分が行った支援履歴を取得する")
    public MyActivitiiesResponse myactivities(
            @RequestHeader(value="Authorization",required=true) String auth) {
        logger.info("history API");
        try {
            Integer get_limit = 10;
            String volunteer_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            List<MyActivity> myHistory = userService.getMyActivities(volunteer_id, get_limit);
            return MyActivitiiesResponse.builder()
                    .myActivityList(myHistory)
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return MyActivitiiesResponse.builder()
                    // ng message
                    .build();
        }
    }

    @GetMapping("/matching/listen-signals")
    @ResponseBody
    @ApiOperation(value="周囲のHelpを探知(ボランティア側)", notes="自分の周囲で発信されているHelpを探し、リスト形式で取得")
    public ListenSignalsResponse listenSignals(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody ListenRequest listenRequest) {
        logger.info("listen-signals API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));
            SignalList signalList = matchingService.getHelpSignals(user_id, listenRequest.getX_geometry(), listenRequest.getY_geometry());

            return ListenSignalsResponse.builder()
                    .helpSignals(signalList.getHelpSignals())
                    .build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return ListenSignalsResponse.builder()
                    // ng message
                    .build();
        }
    }

    /**
     *
     */
    @PostMapping("/admin/area_register")
    @ResponseBody
    @ApiOperation(value="エリア設定(管理者機能)", notes="checkinにより地名を表示するエリアを設定する")
    public NormalResponse area_register(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody CheckinAreaRegisterRequest request){
        logger.info("area_register API");
        try {
            String user_id = tokenService.getUserId(tokenService.getTokenFromAuth(auth));

            matchingService.registerArea(request, user_id);
            return NormalResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return NormalResponse.builder().result("NG").build();
        }
    }

    @PostMapping("/admin/add_handicap_type")
    @ResponseBody
    @ApiOperation(value="障がいのタイプを追加(管理者機能)", notes="DBに新しい障がい情報を登録する（アイコン登録は別）")
    public NormalResponse add_handicap_type(
            @RequestHeader(value="Authorization",required=true) String auth,
            @RequestBody AddHandicapTypeRequest request){
        logger.info("add handicap type API");
        try {
            String auth_code = request.getAuth_code();
            HandicapMaster master = HandicapMaster.builder()
                    .handicap_name(request.getHandicap_name())
                    .comment(request.getComment())
                    .icon_path(request.getIcon_path())
                    .build();

            adminService.addHandicapMaster(master, auth_code);
            return NormalResponse.builder().result("OK").build();
        } catch (JWTDecodeException | TokenExpiredException e) {
            return NormalResponse.builder().result("NG").build();
        }
    }

    @GetMapping("/hello")
    @ResponseBody
    public CheckInResponse checkin(){
        logger.info("疎通確認 URL");
        return CheckInResponse.builder().result("OK").build();
    }

}
