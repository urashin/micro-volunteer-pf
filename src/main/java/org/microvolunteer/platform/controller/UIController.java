package org.microvolunteer.platform.controller;

import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.HandicapRegisterRequest;
import org.microvolunteer.platform.domain.resource.request.LoginRequest;
import org.microvolunteer.platform.domain.resource.request.RegisterUserRequest;
import org.microvolunteer.platform.domain.resource.request.SupportEvaluationRequest;
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

import java.util.ArrayList;
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
        return "user_registration";
    }

    @GetMapping("/default/user/login")
    public String login(Model model) {
        logger.info("login");
        model.addAttribute(new LoginRequest());
        return "login_form";
    }

    /**
     * @param loginRequest
     * @return
     */
    @PostMapping("/default/user/mypage")
    public String default_login(LoginRequest loginRequest, Model model){
        logger.info("ログインAPI");
        String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = tokenService.getTokenByUserId(user_id);

        MyProfile myProfile = userService.getMyProfile(user_id, token);
        model.addAttribute(myProfile);
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

        MyProfile myProfile = userService.getMyProfile(user_id, token);
        model.addAttribute(myProfile);
        model.addAttribute("token", token);
        return "my_profile";
    }

    @GetMapping("/default/user/handicap/register/{token}")
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

    @PostMapping("/default/user/handicap/register/{token}")
    public String default_handicap_register(@PathVariable String token, HandicapRegisterRequest registerRequest, Model model){
        logger.info("handicap register API");
        String user_id = tokenService.getUserId(registerRequest.getToken());
        userService.registerHandicappedInfo(user_id,registerRequest);
        MyProfile myProfile = userService.getMyProfile(user_id, registerRequest.getToken());
        model.addAttribute(myProfile);
        model.addAttribute("token", token);
        return "my_profile";
    }

    @PostMapping("/default/user/register/{token}")
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

    @GetMapping("/default/user/history/{token}")
    public String volunteer_history(@PathVariable String token, Model model) {
        logger.info("history");
        // tokenからuser_idを取得
        String volunteer_id = tokenService.getUserId(token);
        List<MyActivity> history = userService.getMyActivities(volunteer_id, 10);
        model.addAttribute("history", history);
        model.addAttribute("token", token);
        return "my_history";
    }

    @GetMapping("/default/user/thanks_list/{token}")
    public String default_thanks_list(@PathVariable String token, Model model) {
        logger.info("thanks list");
        // tokenからuser_idを取得
        String handicapped_id = tokenService.getUserId(token);
        ThanksList thanksList = userService.getMyThanksList(handicapped_id, 10);
        model.addAttribute(thanksList);
        model.addAttribute("token", token);
        return "my_thankslist";
    }

    @GetMapping("/default/user/support_evaluation/{token}/{help_id}")
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

    @PostMapping("/default/user/support_evaluation/{token}")
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
