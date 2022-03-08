package org.microvolunteer.platform.controller;

import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.*;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.service.MatchingService;
import org.microvolunteer.platform.service.SnsIdRegisterService;
import org.microvolunteer.platform.service.TokenService;
import org.microvolunteer.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/v1")
public class UIController {
    private Logger logger = LoggerFactory.getLogger(org.microvolunteer.platform.controller.Controller.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private SnsRegisterMapper snsRegisterMapper;

    @Autowired
    private LineMessageRestClient lineMessageRestClient;

    @Autowired
    private SnsIdRegisterService snsIdRegisterService;

    @GetMapping("/user/register/{sns_id}")
    public String register(@PathVariable String sns_id, Model model) {
        logger.info("sns register API");
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        // 1) user_id を新規発行（個々の情報はパスワード設定など、個別に設定）
        String user_id = userService.createUser();

        // 2) session 管理のトークンを発行
        String token = tokenService.createToken(user_id);
        registerUserRequest.setToken(token);

        // 3) SnsId tableにuser_id&sns_idのペアで登録し、紐付け完了
        snsIdRegisterService.registerSnsId(sns_id,user_id, 1);

        // 4) modelに変数を設定
        model.addAttribute(registerUserRequest);
        model.addAttribute("token", token);
        return "user_registration";
    }

    @GetMapping("/user/login")
    public String login(@CookieValue(value="_token", required=false) String token, Model model) {
        logger.info("login");
        if (token != null) {
            String user_id = "";
            try {
                user_id = tokenService.getUserId(token);
            } catch (Exception e) {
                return "abuser";
            }
            MyProfile myProfile = userService.getMyProfile(user_id);
            model.addAttribute(myProfile);
            HelpRequest helpRequest = new HelpRequest();
            model.addAttribute(helpRequest);
            model.addAttribute("token", token);
            return "my_profile";
        }
        model.addAttribute(new LoginRequest());
        return "login_form";
    }

    /**
     * @param loginRequest
     * @return
     */
    @PostMapping("/user/mypage")
    public String default_login(HttpServletResponse response, @CookieValue(value="_token", required=false) String token, LoginRequest loginRequest, Model model){
        logger.info("ログインAPI");
        String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (token == null) {
            token = tokenService.getTokenByUserId(user_id);
            response.addCookie(new Cookie("_token",token));
        }

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        model.addAttribute("token", token);
        return "my_profile";
    }

    /**
     * @param token
     * @return
     */
    @GetMapping("/user/mypage/{token}")
    public String mypage(@PathVariable String token, Model model){
        logger.info("mypage API");
        String user_id = "";
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (Exception e) {
            return "abuser";
        }

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        model.addAttribute("token", token);
        return "my_profile";
    }

    @GetMapping("/user/handicap/register/{token}")
    public String add_handicap(@PathVariable String token, Model model){
        logger.info("add handicap API");
        try {
            // tokenからuser_idを取得
            tokenService.getUserId(token);
        } catch (Exception e) {
            return "abuser";
        }

        HandicapRegisterRequest handicapRegisterRequest = HandicapRegisterRequest.builder()
                .token(token)
                .build();
        model.addAttribute(handicapRegisterRequest);
        model.addAttribute("token", token);
        return "handicap_form";
    }

    @PostMapping("/user/handicap/register/{token}")
    public String default_handicap_register(@PathVariable String token, HandicapRegisterRequest registerRequest, Model model){
        logger.info("handicap register API");
        String user_id = tokenService.getUserId(registerRequest.getToken());
        userService.registerHandicappedInfo(user_id,registerRequest);
        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        model.addAttribute("token", token);
        return "my_profile";
    }

    /*
       <a th:href="@{/v1/matching/listen/{token}(token=${token})}" class="secondary-content"><i class="material-icons">hearing</i></a>
       <a th:href="@{/v1/matching/checkin/{token}(token=${token})}" class="secondary-content"><i class="material-icons">location_on</i></a>
     */
    @GetMapping("/matching/listen/{token}")
    public String default_listen(@PathVariable String token, Model model){
        logger.info("listen API");
        String user_id = tokenService.getUserId(token);
        // listen_listをしゅとくする
        ListenRequest listenRequest = ListenRequest.builder()
                .token(token)
                .build();
        model.addAttribute(listenRequest);
        model.addAttribute("token", token);
        return "listen";
    }

    @PostMapping("/user/register/{token}")
    public String default_register(@PathVariable String token, RegisterUserRequest registerUserRequest,Model model){
        logger.info("default register API");
        // tokenからuser_idを取得
        String user_id = tokenService.getUserId(token);
        userService.registerUserInfo(
                user_id,
                registerUserRequest.getName(),
                registerUserRequest.getEmail(),
                registerUserRequest.getPassword());
        model.addAttribute("loginRequest", new LoginRequest());
        return "login_form";
    }


    @GetMapping("/line_accept/{sns_id}/{help_id}")
    public String accept(@PathVariable String sns_id, @PathVariable Integer help_id) {
        logger.info("line_accept");
        String user_id = tokenService.getUserIdBySnsId(sns_id);
        matchingService.accept(help_id,user_id);
        return "Accepted";
    }

    @GetMapping("/line_thanks/{sns_id}/{help_id}/{satisfaction}")
    public String accept(@PathVariable String sns_id, @PathVariable Integer help_id, @PathVariable Integer satisfaction) {
        logger.info("line_thanks");
        String handicapped_id = tokenService.getUserIdBySnsId(sns_id);
        userService.thanks(help_id,handicapped_id,satisfaction);
        return "Accepted";
    }

    @GetMapping("/user/history/{token}")
    public String volunteer_history(@PathVariable String token, Model model) {
        logger.info("history");
        // tokenからuser_idを取得
        String volunteer_id = tokenService.getUserId(token);
        List<MyActivity> history = userService.getMyActivities(volunteer_id, 10);
        model.addAttribute("history", history);
        model.addAttribute("token", token);
        return "my_history";
    }

    @GetMapping("/user/help/select/{token}/{handicap_id}")
    public String default_help_select(@PathVariable String token, @PathVariable Integer handicap_id, Model model) {
        logger.info("help_select api");
        try {
            tokenService.getUserId(token);
        } catch (Exception e) {
            logger.info("abuser");
            return "abuser";
        }
        MyHandicap myHandicap = userService.getMyHandicap(handicap_id);

        model.addAttribute(myHandicap);
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setToken(token);
        model.addAttribute(helpRequest);
        model.addAttribute("token", token);
        return "help_call";
    }

    @PostMapping("/user/help/call")
    public String default_help_call(HelpRequest helpRequest, Model model) {
        logger.info("help call API");
        String token = helpRequest.getToken();
        String user_id = tokenService.getUserId(token);
        HandicapInfo handicapInfo = userService.getHandicappedInfo(helpRequest.getHandicapinfo_id());
        try {
            matchingService.help(user_id, helpRequest, handicapInfo);
        } catch (Exception e) {
            logger.error("help error.");
        }

        MyHandicap myHandicap = userService.getMyHandicap(helpRequest.getHandicapinfo_id());

        model.addAttribute(myHandicap);
        CancelRequest cancelRequest = new CancelRequest();
        model.addAttribute(cancelRequest);
        model.addAttribute("token", token);
        return "help_wait";
    }

    @PostMapping("/user/help/cancel")
    public String default_help_cancel(CancelRequest cancelRequest, Model model) {
        logger.info("help cancel API");
        String token = cancelRequest.getToken();
        String user_id = tokenService.getUserId(token);
        try {
            matchingService.help_cancel(user_id);
        } catch (Exception e) {
            logger.error("help cancel error.");
        }

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        model.addAttribute("token", token);
        return "my_profile";
    }

    @PostMapping("/matching/listen-signals")
    public String default_listen_signals(ListenRequest listenRequest, Model model) {
        logger.info("listen-signals API");
        String token = listenRequest.getToken();
        String user_id = tokenService.getUserId(token);
        SignalList signalList = matchingService.getHelpSignals(user_id, listenRequest.getX_geometry(), listenRequest.getY_geometry());

        AcceptRequest acceptRequest = new AcceptRequest();
        model.addAttribute(acceptRequest);
        model.addAttribute(signalList);
        model.addAttribute("token", token);
        return "listen_signals";
    }

    @GetMapping("/matching/accept/{token}/{help_id}")
    public String default_accept(@PathVariable String token, @PathVariable Integer help_id, Model model) {
        logger.info("listen-signals API");
        String user_id = tokenService.getUserId(token);
        Location geo = matchingService.getMyGeometry(user_id);
        HelpSignal helpSignal = matchingService.getHelpSignal(help_id,geo.getX_geometry(), geo.getY_geometry());

        AcceptRequest acceptRequest = new AcceptRequest();
        model.addAttribute(acceptRequest);
        model.addAttribute(helpSignal);
        model.addAttribute("token", token);
        return "accept";
    }

    @PostMapping("/matching/accept")
    public String default_accept_rush(AcceptRequest acceptRequest, Model model) {
        logger.info("accept API");
        String user_id = tokenService.getUserId(acceptRequest.getToken());
        Location geo = matchingService.getMyGeometry(user_id);
        matchingService.accept(acceptRequest.getHelp_id(),user_id);
        HelpSignal helpSignal = matchingService.getHelpSignal(acceptRequest.getHelp_id(),geo.getX_geometry(), geo.getY_geometry());

        CancelRequest cancelRequest = new CancelRequest();
        model.addAttribute(cancelRequest);
        model.addAttribute(helpSignal);
        model.addAttribute("token", acceptRequest.getToken());
        return "rush";
    }

    @GetMapping("/user/thanks_list/{token}")
    public String default_thanks_list(@PathVariable String token, Model model) {
        logger.info("thanks list");
        // tokenからuser_idを取得
        String handicapped_id = tokenService.getUserId(token);
        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        model.addAttribute(thanksList);
        model.addAttribute("token", token);
        return "my_thankslist";
    }

    @GetMapping("/user/support_evaluation/{token}/{help_id}")
    public String getSupportEvaluation(@PathVariable String token, @PathVariable Integer help_id, Model model) {
        logger.info("support evaluation getAPI");
        // tokenからuser_idを取得
        String handicapped_id = tokenService.getUserId(token);
        SupportEvaluationRequest request = SupportEvaluationRequest.builder()
                .help_id(help_id)
                .build();
        model.addAttribute(request);
        model.addAttribute("token", token);
        return "support_evaluation";
    }

    @PostMapping("/user/support_evaluation/{token}")
    public String postSupportEvaluation(@PathVariable String token, SupportEvaluationRequest request, Model model) {
        logger.info("support evaluation postAPI");
        // tokenからuser_idを取得
        String handicapped_id = tokenService.getUserId(token);
        userService.thanks(request.getHelp_id(), handicapped_id, request.getSatisfaction());
        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        model.addAttribute(thanksList);
        model.addAttribute("token", token);
        return "my_thankslist";
    }
}
