package org.microvolunteer.platform.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.*;
import org.microvolunteer.platform.domain.resource.response.MyProfileResponse;
import org.microvolunteer.platform.domain.resource.response.SnsRegisterResponse;
import org.microvolunteer.platform.domain.resource.snsmessage.LineLocationMessageRequest;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.service.MatchingService;
import org.microvolunteer.platform.service.SnsIdRegisterService;
import org.microvolunteer.platform.service.TokenService;
import org.microvolunteer.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    @Value("${backend-api.uri}")
    private String api_uri;

    @GetMapping("/user/register/{sns_id}")
    public String register(@PathVariable String sns_id, Model model) {
        String api_url = api_uri + "/v1/api/user/register/" + sns_id;
        logger.info("sns register API");
        String token = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnsRegisterResponse> response = restTemplate
                    .exchange(api_url, HttpMethod.GET, null, SnsRegisterResponse.class);
            SnsRegisterResponse body = response.getBody();
            token = body.getToken();
            if (token.isEmpty()) throw new RestClientException("get token error.");
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "error"; // error page遷移
        }

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setToken(token);

        // modelに変数を設定
        model.addAttribute(registerUserRequest);
        return "user_registration";
    }

    @GetMapping("/user/login")
    public String login(@CookieValue(value="_token", required=false) String token, Model model) {
        String api_url = api_uri + "/v1/api/user/myprofile";
        logger.info("login");
        if (token != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + token);
                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<MyProfileResponse> response = restTemplate
                        .exchange(api_url, HttpMethod.GET, requestEntity, MyProfileResponse.class);
                MyProfileResponse body = response.getBody();
                MyProfile myProfile = MyProfile.builder()
                        .handicap_list(body.getHandicap_list())
                        .volunteer_summary(body.getVolunteer_summary())
                        .build();
                model.addAttribute(myProfile);
                HelpRequest helpRequest = new HelpRequest();
                model.addAttribute(helpRequest);
                return "my_profile";
            } catch (RestClientException e) {
                logger.info("RestClient error : {}", e.toString());
                return "error"; // error page遷移
            } catch (JWTDecodeException | TokenExpiredException e) {
                logger.error("JWT decode failed or expired.");
                return "error"; // error page遷移
            } catch (Exception e) {
                logger.info("error : {}", e.toString());
                return "error"; // error page遷移
            }
        }
        model.addAttribute(new LoginRequest());
        return "login_form";
    }

    /**
     * @param loginRequest
     * @return
     */
    @PostMapping("/user/mypage")
    public String default_login(HttpServletResponse response, LoginRequest loginRequest, Model model){
        logger.info("ログインAPI");
        String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        // api に置き換え

        if (user_id == null) {
            model.addAttribute("loginRequest", new LoginRequest());
            return "login_form";
        }
        String token = tokenService.getTokenByUserId(user_id);
        // api に置き換え
        Cookie cookie = new Cookie("_token",token);
        cookie.setPath("/");
        response.addCookie(cookie);

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        return "my_profile";
    }

    /**
     * @param token
     * @return
     */
    @GetMapping("/user/mypage")
    public String mypage(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("mypage API");
        String user_id = "";
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        return "my_profile";
    }

    @GetMapping("/user/handicap/register")
    public String add_handicap(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("add handicap API");
        try {
            // tokenからuser_idを取得
            tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

        HandicapRegisterRequest handicapRegisterRequest = HandicapRegisterRequest.builder()
                .token(token)
                .build();
        model.addAttribute(handicapRegisterRequest);
        return "handicap_form";
    }

    @PostMapping("/user/handicap/register")
    public String default_handicap_register(@CookieValue(value="_token", required=true) String token, HandicapRegisterRequest registerRequest, Model model){
        logger.info("handicap register API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        userService.registerHandicappedInfo(user_id,registerRequest);
        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        return "my_profile";
    }

    @GetMapping("/matching/listen")
    public String default_listen(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("listen API");
        try {
            // tokenからuser_idを取得
            String user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        // listen_listをしゅとくする
        ListenRequest listenRequest = ListenRequest.builder()
                .token(token)
                .build();
        model.addAttribute(listenRequest);
        return "listen";
    }

    @PostMapping("/user/register")
    public String default_register(@CookieValue(value="_token", required=true) String token, RegisterUserRequest registerUserRequest,Model model){
        logger.info("default register API");
        // tokenからuser_idを取得
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
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

    @GetMapping("/user/history")
    public String volunteer_history(@CookieValue(value="_token", required=true) String token, Model model) {
        logger.info("history");
        String volunteer_id;
        try {
            // tokenからuser_idを取得
            volunteer_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        List<MyActivity> history = userService.getMyActivities(volunteer_id, 10);
        model.addAttribute("history", history);
        return "my_history";
    }

    @GetMapping("/user/help/select/{handicap_id}")
    public String default_help_select(@CookieValue(value="_token", required=true) String token, @PathVariable Integer handicap_id, Model model) {
        logger.info("help_select api");
        try {
            // tokenからuser_idを取得
            tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        MyHandicap myHandicap = userService.getMyHandicap(handicap_id);

        model.addAttribute(myHandicap);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        return "help_call";
    }

    @PostMapping("/user/help/call")
    public String default_help_call(@CookieValue(value="_token", required=true) String token, HelpRequest helpRequest, Model model) {
        logger.info("help call API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
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
        return "help_wait";
    }

    @PostMapping("/user/help/cancel")
    public String default_help_cancel(@CookieValue(value="_token", required=true) String token, CancelRequest cancelRequest, Model model) {
        logger.info("help cancel API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        try {
            matchingService.help_cancel(user_id);
        } catch (Exception e) {
            logger.error("help cancel error.");
        }

        MyProfile myProfile = userService.getMyProfile(user_id);
        model.addAttribute(myProfile);
        HelpRequest helpRequest = new HelpRequest();
        model.addAttribute(helpRequest);
        return "my_profile";
    }

    @PostMapping("/matching/listen-signals")
    public String default_listen_signals(@CookieValue(value="_token", required=true) String token, ListenRequest listenRequest, Model model) {
        logger.info("listen-signals API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        SignalList signalList = matchingService.getHelpSignals(user_id, listenRequest.getX_geometry(), listenRequest.getY_geometry());

        AcceptRequest acceptRequest = new AcceptRequest();
        model.addAttribute(acceptRequest);
        model.addAttribute(signalList);
        return "listen_signals";
    }

    @GetMapping("/matching/accept/{help_id}")
    public String default_accept(@CookieValue(value="_token", required=true) String token, @PathVariable Integer help_id, Model model) {
        logger.info("listen-signals API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

        Location geo = matchingService.getMyGeometry(user_id);
        HelpSignal helpSignal = matchingService.getHelpSignal(help_id,geo.getX_geometry(), geo.getY_geometry());

        AcceptRequest acceptRequest = new AcceptRequest();
        model.addAttribute(acceptRequest);
        model.addAttribute(helpSignal);
        return "accept";
    }

    @PostMapping("/matching/accept")
    public String default_accept_rush(@CookieValue(value="_token", required=true) String token, AcceptRequest acceptRequest, Model model) {
        logger.info("accept API");
        String user_id;
        try {
            // tokenからuser_idを取得
            user_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        Location geo = matchingService.getMyGeometry(user_id);
        matchingService.accept(acceptRequest.getHelp_id(),user_id);
        HelpSignal helpSignal = matchingService.getHelpSignal(acceptRequest.getHelp_id(),geo.getX_geometry(), geo.getY_geometry());

        CancelRequest cancelRequest = new CancelRequest();
        model.addAttribute(cancelRequest);
        model.addAttribute(helpSignal);
        return "rush";
    }

    @GetMapping("/user/thanks_list")
    public String default_thanks_list(@CookieValue(value="_token", required=true) String token, Model model) {
        logger.info("thanks list");
        // tokenからuser_idを取得
        String handicapped_id;
        try {
            // tokenからuser_idを取得
            handicapped_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        model.addAttribute(thanksList);
        return "my_thankslist";
    }

    @GetMapping("/user/support_evaluation/{help_id}")
    public String getSupportEvaluation(@CookieValue(value="_token", required=true) String token, @PathVariable Integer help_id, Model model) {
        logger.info("support evaluation getAPI");
        // tokenからuser_idを取得
        String handicapped_id;
        try {
            // tokenからuser_idを取得
            handicapped_id = tokenService.getUserId(token);
            // handicapped_idのhelp_idであるかどうかを確認
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        SupportEvaluationRequest request = SupportEvaluationRequest.builder()
                .help_id(help_id)
                .build();
        model.addAttribute(request);
        return "support_evaluation";
    }

    @PostMapping("/user/support_evaluation")
    public String postSupportEvaluation(@CookieValue(value="_token", required=true) String token, SupportEvaluationRequest request, Model model) {
        logger.info("support evaluation postAPI");
        // tokenからuser_idを取得
        String handicapped_id;
        try {
            // tokenからuser_idを取得
            handicapped_id = tokenService.getUserId(token);
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
        userService.thanks(request.getHelp_id(), handicapped_id, request.getSatisfaction());
        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        model.addAttribute(thanksList);
        return "my_thankslist";
    }
}
