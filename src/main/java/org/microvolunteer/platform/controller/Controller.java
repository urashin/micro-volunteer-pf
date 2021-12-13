package org.microvolunteer.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.microvolunteer.platform.domain.resource.request.*;
import org.microvolunteer.platform.domain.resource.response.*;
import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.Location;
import org.microvolunteer.platform.domain.resource.VolunteerHistory;
import org.microvolunteer.platform.service.MatchingService;
import org.microvolunteer.platform.service.SnsIdRegisterService;
import org.microvolunteer.platform.service.TokenService;
import org.microvolunteer.platform.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1")
@Slf4j
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);
    private UsersService usersService;
    private MatchingService matchingService;
    private TokenService tokenService;
    private SnsIdRegisterService snsIdRegisterService;

    @Autowired
    public Controller(
              UsersService usersService
            , MatchingService matchingService
            , TokenService tokenService
            , SnsIdRegisterService snsIdRegisterService
    ) {
        this.usersService = usersService;
        this.matchingService = matchingService;
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
        logger.info("ログインAPI: {}", loginRequest.getUser_id());

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
     * sns_idもしくはonetimeurlにより、tokenは取得できている状態
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("/user/register")
    @ResponseBody
    public HandicapRegisterResponse register(@RequestBody RegisterRequest registerRequest){
        logger.info("register API: {}", registerRequest.getEmail());
        // tokenからuser_idを取得
        String user_id = tokenService.getUserId(registerRequest.getToken());
        // email, passwordを登録する
        return HandicapRegisterResponse.builder().result("OK").build();
    }

    /**
     * 障害者が適切な手助けを受けられるよう、障害情報を登録するためのAPI
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("/user/handicap/register")
    @ResponseBody
    public HandicapRegisterResponse handicap_register(@RequestBody HandicapRegisterRequest registerRequest){
        logger.info("handicap register API: {}", registerRequest.getHandicap_level());
        String user_id = tokenService.getUserId(registerRequest.getToken());
        usersService.registerHandicappedInfo(user_id,registerRequest);
        return HandicapRegisterResponse.builder().result("OK").build();
    }

    /**
     * LINE Bot以外の通常のユーザー登録処理（今回は対象外）
     * 引数のemailにonetimeurlを送付する（メールサーバたてるなど面倒なので今回は省略）
     */
    @GetMapping("/user/onetimeurl")
    @ResponseBody
    public OnetimeurlResponse onetimeurl(@RequestBody OnetimeurlRequest onetimeurlRequest){
        //
        return OnetimeurlResponse.builder().onetimeurl("http://tokenXXXXXXXXXXXXXXXXXXXX").build();
    }

    /**
     * ★checkin（障害者＆ボランティア両方）
     */
    @PostMapping("/matching/checkin")
    @ResponseBody
    public CheckInResponse checkin(@RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API: {}", checkInRequest.getToken());
        String user_id = tokenService.getUserId(checkInRequest.getToken());
        matchingService.updateMyGeometry(user_id,checkInRequest);
        logger.info("CheckIn API user_id : {}", user_id);
        return CheckInResponse.builder().result("OK").build();
    }

    @PostMapping("/matching/checkout")
    @ResponseBody
    public CheckInResponse checkout(@RequestBody CheckInRequest checkInRequest){
        logger.info("CheckIn API: {}", checkInRequest.getToken());
        String user_id = tokenService.getUserId(checkInRequest.getToken());
        matchingService.updateMyGeometry(user_id,checkInRequest);
        return CheckInResponse.builder().result("OK").build();
    }

    /**
     * my_handicap_list
     * 障害者のHelp画面の個々のヘルプボタンに割り当てるヘルプ情報を取得する
     */
    @GetMapping("/user/handicaplist")
    @ResponseBody
    public MyHandicapInfoResponse getMyHandicapList(@RequestBody MyHandicapInfoRequest request){
        logger.info("handicaplist API");
        // 障害者の位置情報を更新
        String user_id = tokenService.getUserId(request.getToken());
        // 障害者の障害情報リストを取得
        List<HandicapInfo> handicapInfoList = usersService.getMyHandicapList(user_id);
        return MyHandicapInfoResponse.builder().handicapInfoList(handicapInfoList).build();
    }

    /**
     * ★help（障害者側から）
     */
    @PostMapping("/matching/help")
    @ResponseBody
    public HelpResponse help(@RequestBody HelpRequest helpRequest){
        logger.info("help API: {}", helpRequest.toString());
        // 障害者の位置情報を更新
        String user_id = tokenService.getUserId(helpRequest.getToken());
        HandicapInfo handicapInfo = usersService.getHandicappedInfo(helpRequest.getHandicapinfo_id());
        matchingService.help(user_id, helpRequest, handicapInfo);

        // 対象ボランティアの抽出（マッチング）
        // 近くにいる人達を検索する。
        // 他の障害者、ボランティア混在しているが、助けられる人が助ければよいので分ける必要は無いと思う。
        //List<NeighborDistanceDto> neighborsList = matchingService.getNeigborhood(user_id, location.getPoint());
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
        logger.info("HelpDetail API: {}", helpDetailRequest.getHelp_id());
        // 未実装
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
     * acceptでも位置座標を受け取り直したほうが良さそう、チェックインから移動しているため。
     */
    @PostMapping("/matching/accept")
    @ResponseBody
    public AcceptResponse accept(@RequestBody AcceptRequest acceptRequest){
        logger.info("accept API: {}", acceptRequest.getHelp_id());
        String user_id = tokenService.getUserId(acceptRequest.getToken());
        Location geo = matchingService.getMyGeometry(user_id);
        matchingService.accept(acceptRequest.getHelp_id(),user_id);

        return AcceptResponse.builder()
                .helpId(acceptRequest.getHelp_id())
                .xGeometry(geo.getX_geometry())
                .yGeometry(geo.getY_geometry())
                .build();
    }

    /**
     * ★thanks（障害者から）
     */
    @PostMapping("/user/thanks")
    @ResponseBody
    public ThanksResponse thanks(@RequestBody ThanksRequest thanksRequest){
        logger.info("thanks API: {}", thanksRequest.getHelp_id());
        String user_id = tokenService.getUserId(thanksRequest.getToken());

        usersService.thanks(thanksRequest, user_id);
        return ThanksResponse.builder()
                .result("OK")
                .build();
    }

    /**
     * ★ボランティア履歴の取得
     */
    @GetMapping("/user/history")
    @ResponseBody
    public VolunteerHistoryResponse history(@RequestBody VolunteerHistoryRequest historyRequest){
        logger.info("history API");
        String user_id = tokenService.getUserId(historyRequest.getToken());

        Integer get_limit = 10;
        List<VolunteerHistory> volunteerHistory = usersService.getMyVolunteerHistory(user_id,get_limit);
        return VolunteerHistoryResponse.builder()
                .volunteerHistory(volunteerHistory)
                .build();
    }

    @GetMapping("/kakunin")
    @ResponseBody
    public CheckInResponse checkin(){
        logger.info("疎通確認 URL");
        return CheckInResponse.builder().result("OK").build();
    }

}
