package org.microvolunteer.platform.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.*;
import org.microvolunteer.platform.domain.resource.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@Controller
@RequestMapping("/v1")
public class UIController {
    private Logger logger = LoggerFactory.getLogger(org.microvolunteer.platform.controller.Controller.class);

    @Value("${backend-api.uri}")
    private String api_uri;

    @Value("${line-login.client_id}")
    private String client_id;

    @Value("${line-login.client_secret}")
    private String client_secret;

    @Value("${line-login.login_redirect_uri}")
    private String login_redirect_uri;

    @GetMapping("/user/line-login")
    @ResponseBody
    public void linelogin(HttpServletResponse httpServletResponse) {
        logger.info("line login API");
        String redirect_url = "https://access.line.me/oauth2/v2.1/authorize?response_type=code&client_id=" + client_id + "&redirect_uri=" + login_redirect_uri + "&state=1&scope=openid%20profile";
        httpServletResponse.setHeader("Location", redirect_url);
        httpServletResponse.setStatus(302);
    }

    /**
     * LINE Auth
     */
    @GetMapping("/auth")
    @ResponseBody
    public String line_auth(HttpServletResponse response, @RequestParam("code") String code, Model model){
        logger.info("LINE Auth API");
        String api_url = api_uri + "/v1/api/auth?code=" + code;
        logger.info("sns register API");
        String lineId = ""; // LINE user ID
        /*
         * LINE APIを使用してLINE user IDを取得する。
         */
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<LoginResponse> registerResponse = restTemplate
                    .exchange(api_url, HttpMethod.GET, null, LoginResponse.class);
            // get LINE user ID
            lineId = registerResponse.getBody().getToken();
            if (lineId.isEmpty()) throw new RestClientException("get lineId error.");
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "error"; // error page遷移
        }


        /*
         * このシステムへの登録を行う
         */
        api_url = api_uri + "/v1/api/user/register/" + lineId;
        logger.info("sns register API");
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnsTokenResponse> registerResponse = restTemplate
                    .exchange(api_url, HttpMethod.GET, null, SnsTokenResponse.class);
            String token = registerResponse.getBody().getToken();
            if (token.isEmpty()) throw new RestClientException("get token error.");

            // cookieを設定
            Cookie cookie = new Cookie("_token",token);
            cookie.setPath("/");
            response.addCookie(cookie);

            // modelに変数を設定
            RegisterUserRequest registerUserRequest = new RegisterUserRequest();
            model.addAttribute(registerUserRequest);
            //response.setHeader("Location", api_uri + "/v1/user/");
            return "user_registration";
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "error"; // error page遷移
        }
    }

    /*
     * セキュリティ的な懸念があるため、要改善
     */
    @GetMapping("/user/register/{sns_id}")
    public String register(HttpServletResponse response, @PathVariable String sns_id, Model model) {
        String api_url = api_uri + "/v1/api/user/register/" + sns_id;
        logger.info("sns register API");
        String token = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnsTokenResponse> registerResponse = restTemplate
                    .exchange(api_url, HttpMethod.GET, null, SnsTokenResponse.class);
            SnsTokenResponse body = registerResponse.getBody();
            token = body.getToken();
            if (token.isEmpty()) throw new RestClientException("get token error.");

            // cookieを設定
            Cookie cookie = new Cookie("_token",token);
            cookie.setPath("/");
            response.addCookie(cookie);

            // modelに変数を設定
            RegisterUserRequest registerUserRequest = new RegisterUserRequest();
            model.addAttribute(registerUserRequest);
            return "user_registration";
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "error"; // error page遷移
        }
    }

    /*
     * ログイン画面の表示
     */
    @GetMapping("/user/login")
    public String login(@CookieValue(value="_token", required=false) String token, Model model) {
        logger.info("login");
        if (token != null && tokenCheck(token)) {
            // 有効なtokenがある場合は、profile画面を表示
            try {
                MyProfile myProfile = getMyProfile(token);
                model.addAttribute(myProfile);
                HelpRequest helpRequest = new HelpRequest();
                model.addAttribute(helpRequest);
                return "my_profile";
            } catch (Exception e) {
                logger.info("error : {}", e.toString());
                return "error"; // error page遷移
            }
        } else {
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    /**
     * ログイン後、mypageを表示
     * @param loginRequest
     * @return
     */
    @PostMapping("/user/mypage")
    public String default_login(HttpServletResponse response, LoginRequest loginRequest, Model model){
        logger.info("ログインAPI");
        // api に置き換え
        try {
            // login & token取得
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> loginRequestEntity = new HttpEntity<>(loginRequest,headers);
            RestTemplate loginRestTemplate = new RestTemplate();
            ResponseEntity<LoginResponse> loginResponse = loginRestTemplate
                    .exchange(api_uri + "/v1/api/user/login", HttpMethod.POST, loginRequestEntity, LoginResponse.class);
            String token = loginResponse.getBody().getToken();
            if (token == null) {
                model.addAttribute("loginRequest", new LoginRequest());
                return "login_form";
            }

            // cookieを設定
            Cookie cookie = new Cookie("_token",token);
            cookie.setPath("/");
            response.addCookie(cookie);

            MyProfile myProfile = getMyProfile(token);
            // model 設定
            model.addAttribute(myProfile);
            model.addAttribute(new HelpRequest());
            return "my_profile";
        } catch (Exception e) {
            logger.info("error : {}", e.toString());
            return "error"; // error page遷移
        }
    }

    /**
     * myprofile画面の表示
     * @param token
     * @return
     */
    @GetMapping("/user/mypage")
    public String mypage(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("mypage API");
        try {
            MyProfile myProfile = getMyProfile(token);
            model.addAttribute(myProfile);
            model.addAttribute(new HelpRequest());
            return "my_profile";
        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
            return "error"; // error page遷移
        } catch (JWTDecodeException | TokenExpiredException e) {
            logger.error("JWT decode failed or expired.");
            model.addAttribute(new LoginRequest());
            return "login_form";
        } catch (Exception e) {
            logger.info("error : {}", e.toString());
            return "error"; // error page遷移
        }
    }

    @GetMapping("/user/handicap/register")
    public String add_handicap(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("add handicap API");
        try {
            HandicapRegisterRequest handicapRegisterRequest = HandicapRegisterRequest.builder()
                    .build();
            model.addAttribute(handicapRegisterRequest);
            return "handicap_form";
        } catch (RestClientException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "Error";
        } catch (Exception e) {
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

    }

    /*
     * tokenの有効性を確認する
     */
    private Boolean tokenCheck(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/tokencheck", HttpMethod.GET, requestEntity, NormalResponse.class);
            if ("NG" == response.getBody().getResult()) {
                return false;
            } else {
                return true;
            }
        } catch (RestClientException e) {
            // error message log
            return false;
        } catch (Exception e) {
            // error message log
            //unexpected error
            return false;
        }
    }

    private MyProfile getMyProfile(String token) throws RestClientException, JWTDecodeException, TokenExpiredException  {
        try {
            // MyProfileの取得
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MyProfileResponse> myProfileResponse = restTemplate
                    .exchange(api_uri + "/v1/api/user/myprofile", HttpMethod.POST, requestEntity, MyProfileResponse.class);
            MyProfileResponse body = myProfileResponse.getBody();

            // model 設定
            MyProfile myProfile = MyProfile.builder()
                    .handicap_list(body.getHandicap_list())
                    .volunteer_summary(body.getVolunteer_summary())
                    .build();

            // cookieを設定
            Cookie cookie = new Cookie("_token",token);
            cookie.setPath("/");

            return myProfile;
        } catch (RestClientException e) {
            logger.info("getMyProfileAPI : RestClient error : {}", e.toString());
            throw e;
        }
    }

    @PostMapping("/user/handicap/register")
    public String default_handicap_register(@CookieValue(value="_token", required=true) String token, HandicapRegisterRequest registerRequest, Model model){
        logger.info("handicap register API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<HandicapRegisterRequest> requestEntity = new HttpEntity<>(registerRequest,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<HandicapRegisterResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/handicap/register", HttpMethod.POST, requestEntity, HandicapRegisterResponse.class);
            //String result = response.getBody().getResult();

            MyProfile myProfile = getMyProfile(token);
            model.addAttribute(myProfile);
            model.addAttribute(new HelpRequest());
            return "my_profile";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    /*
     * listen画面表示
     */
    @GetMapping("/matching/listen")
    public String default_listen(@CookieValue(value="_token", required=true) String token, Model model){
        logger.info("listen API");
        try {
            // tokenからuser_idを取得
            if (tokenCheck(token)) {
                throw new Exception("token error.");
            }

            ListenRequest listenRequest = ListenRequest.builder()
                    .build();
            model.addAttribute(listenRequest);
            return "listen";
        } catch (Exception e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    /*
     * ユーザー登録
     */
    @PostMapping("/user/register")
    public String default_register(@CookieValue(value="_token", required=true) String token, RegisterUserRequest registerUserRequest,Model model){
        logger.info("default register API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<RegisterUserRequest> requestEntity = new HttpEntity<>(registerUserRequest,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RegisterUserResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/register", HttpMethod.POST, requestEntity, RegisterUserResponse.class);
            //String result = response.getBody().getResult();
            // ログイン画面から登録したemail & passwordでログインしてもらう
            model.addAttribute("loginRequest", new LoginRequest());
            return "login_form";
        } catch (Exception e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }


    /*
     * 支援済みのhelp_idに対するLINEからのthanks
     * セキュリティ的な懸念があるため、要改善
     */
    @GetMapping("/line_accept/{sns_id}/{help_id}")
    public String accept(@PathVariable String sns_id, @PathVariable Integer help_id) {
        logger.info("line_accept");
        String token = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnsTokenResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/token/" + sns_id, HttpMethod.GET, requestEntity, SnsTokenResponse.class);
            if (response.getBody().getResult() == "OK") {
                token = response.getBody().getToken();
            } else {
                throw new Exception("get token error");
            }
        } catch (Exception e) {
            return "Error";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            AcceptRequest acceptRequest = new AcceptRequest();
            acceptRequest.setHelp_id(help_id);
            HttpEntity<AcceptRequest> requestEntity = new HttpEntity<>(acceptRequest, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/accept", HttpMethod.POST, requestEntity, NormalResponse.class);

            if (response.getBody().getResult() != "OK") {
                throw new Exception("accept error.");
            }
            return "Accepted";
        } catch (Exception e) {
            return "Error";
        }
    }

    /*
     * helpに対するLINEからのaccept
     * セキュリティ的な懸念があるため、要改善
     */
    @GetMapping("/line_thanks/{sns_id}/{help_id}/{satisfaction}")
    public String accept(@PathVariable String sns_id, @PathVariable Integer help_id, @PathVariable Integer satisfaction) {
        logger.info("line_thanks");
        String token = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnsTokenResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/token/" + sns_id, HttpMethod.GET, requestEntity, SnsTokenResponse.class);
            if (response.getBody().getResult() == "OK") {
                token = response.getBody().getToken();
            } else {
                throw new Exception("get token error");
            }
        } catch (Exception e) {
            return "Error";
        }

        try {
            // request作成
            ThanksRequest request = new ThanksRequest();
            request.setHelp_id(help_id);
            request.setEvaluate(satisfaction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<ThanksRequest> requestEntity = new HttpEntity<>(request,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/thanks", HttpMethod.POST, requestEntity, NormalResponse.class);
            if (response.getBody().getResult() != "OK") {
                throw new Exception("thanks error.");
            }
            return "Accepted";
        } catch (Exception e) {
            return "Error";
        }
    }

    @GetMapping("/user/history")
    public String volunteer_history(@CookieValue(value="_token", required=true) String token, Model model) {
        logger.info("history");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MyActivitiiesResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/myactivities", HttpMethod.GET, requestEntity, MyActivitiiesResponse.class);
            model.addAttribute("history", response.getBody().getMyActivityList());
            return "my_history";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    private MyHandicap getMyHandicap(String token, Integer handicap_id) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MyHandicapResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/user/handicap/" + handicap_id.toString(), HttpMethod.GET, requestEntity, MyHandicapResponse.class);

            return response.getBody().getMyHandicap();
        } catch (Exception e) {
            throw e;
        }
    }

    /*
     * 困った時、Help発信対象のHandicapIDを指定し、Help発信画面を表示する。
     */
    @GetMapping("/user/help/select/{handicap_id}")
    public String default_help_select(@CookieValue(value="_token", required=true) String token, @PathVariable Integer handicap_id, Model model) {
        logger.info("help_select api");
        try {
            model.addAttribute("myHandicap",getMyHandicap(token,handicap_id));
            model.addAttribute("helpRequest",new HelpRequest());
            return "help_call";
        } catch (Exception e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    @PostMapping("/user/help/call")
    public String default_help_call(@CookieValue(value="_token", required=true) String token, HelpRequest helpRequest, Model model) {
        logger.info("help call API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<HelpRequest> requestEntity = new HttpEntity<>(helpRequest, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<HelpResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/help", HttpMethod.POST, requestEntity, HelpResponse.class);

            model.addAttribute("myHandicap",getMyHandicap(token,helpRequest.getHandicapinfo_id()));
            return "help_wait";
        } catch (Exception e) {
            logger.error("help error.");
            return "Error";
        }

    }

    @PostMapping("/user/help/cancel")
    public String default_help_cancel(@CookieValue(value="_token", required=true) String token, Model model) {
        logger.info("help cancel API");
        String user_id;
        try {
            // check token

            // cancel API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/cancel", HttpMethod.POST, requestEntity, NormalResponse.class);
            model.addAttribute("myProfile",getMyProfile(token));
            model.addAttribute("helpRequest",new HelpRequest());
            return "my_profile";
        } catch (Exception e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }

    }

    @PostMapping("/matching/listen-signals")
    public String default_listen_signals(@CookieValue(value="_token", required=true) String token, ListenRequest listenRequest, Model model) {
        logger.info("listen-signals API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ListenSignalsResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/listen-signals", HttpMethod.GET, requestEntity, ListenSignalsResponse.class);

            SignalList signalList = SignalList.builder()
                    .helpSignals(response.getBody().getHelpSignals())
                    .build();

            model.addAttribute("acceptRequest",new AcceptRequest());
            model.addAttribute(signalList);
            return "listen_signals";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    /*
     * 特定のHelpSignalに対するaccept画面を表示する
     */
    @GetMapping("/matching/accept/{help_id}")
    public String default_accept(@CookieValue(value="_token", required=true) String token, @PathVariable Integer help_id, Model model) {
        logger.info("accept API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            SignalRequest signalRequest = new SignalRequest();
            signalRequest.setHelp_id(help_id);
            HttpEntity<SignalRequest> requestEntity = new HttpEntity<>(signalRequest,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GetSignalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/signal", HttpMethod.GET, requestEntity, GetSignalResponse.class);

            model.addAttribute("acceptRequest",new AcceptRequest());
            model.addAttribute("helpSignal",response.getBody().getHelpSignal());
            return "accept";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    @PostMapping("/matching/accept")
    public String default_accept_rush(@CookieValue(value="_token", required=true) String token, AcceptRequest acceptRequest, Model model) {
        logger.info("accept API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<AcceptRequest> requestEntity = new HttpEntity<>(acceptRequest,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/accept", HttpMethod.POST, requestEntity, NormalResponse.class);

            if (response.getBody().getResult() != "OK") {
                throw new Exception("accept error.");
            }

            SignalRequest signalRequest = new SignalRequest();
            signalRequest.setHelp_id(acceptRequest.getHelp_id());
            HttpEntity<SignalRequest> signalRequestEntity = new HttpEntity<>(signalRequest,headers);
            RestTemplate signalRestTemplate = new RestTemplate();
            ResponseEntity<GetSignalResponse> signalResponse = signalRestTemplate
                    .exchange(api_uri + "/v1/api/matching/signal", HttpMethod.GET, requestEntity, GetSignalResponse.class);

            // 自分がacceptしているsignalを取得し、表示する。
            model.addAttribute(signalResponse.getBody().getHelpSignal());
            return "rush";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        } catch (Exception e) {
            return "Error";
        }
    }

    private ThanksList getMyThanksList(String token) throws Exception {
        logger.info("thanks list");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ThanksListResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/thanks", HttpMethod.GET, requestEntity, ThanksListResponse.class);
            if (response.getBody().getResult() != "OK") {
                throw new Exception("thanks list error.");
            }
            return response.getBody().getThanksList();

        } catch (Exception e) {
            // invalid token, require login
            throw e;
        }
    }

    @GetMapping("/user/thanks_list")
    public String default_thanks_list(@CookieValue(value="_token", required=true) String token, Model model) {
        logger.info("thanks list");
        try {
            model.addAttribute("thanksList",getMyThanksList(token));
            return "my_thankslist";
        } catch (JWTDecodeException | TokenExpiredException e) {
            // invalid token, require login
            model.addAttribute(new LoginRequest());
            return "login_form";
        } catch (Exception e) {
            return "Error";
        }
    }

    @GetMapping("/user/support_evaluation/{help_id}")
    public String getSupportEvaluation(@CookieValue(value="_token", required=true) String token, @PathVariable Integer help_id, Model model) {
        logger.info("support evaluation getAPI");
        try {
            if (tokenCheck(token)) {
                throw new Exception("token error.");
            }
            ThanksRequest request = new ThanksRequest();
            request.setHelp_id(help_id);
            model.addAttribute("thanksRequest",request);
            return "support_evaluation";
        } catch (Exception e) {
            model.addAttribute(new LoginRequest());
            return "login_form";
        }
    }

    @PostMapping("/user/support_evaluation")
    public String postSupportEvaluation(@CookieValue(value="_token", required=true) String token, ThanksRequest request, Model model) {
        logger.info("support evaluation postAPI");
        try {
            // 評価：thanksを送る
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<ThanksRequest> requestEntity = new HttpEntity<>(request,headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NormalResponse> response = restTemplate
                    .exchange(api_uri + "/v1/api/matching/thanks", HttpMethod.POST, requestEntity, NormalResponse.class);

            // 現状のthanks listを取得する
            model.addAttribute("thanksList",getMyThanksList(token));
            return "my_thankslist";
        } catch (Exception e) {
            return "Error";
        }
    }
}
