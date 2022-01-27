package org.microvolunteer.platform.controller;

import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.resource.*;
import org.microvolunteer.platform.domain.resource.request.HandicapRegisterRequest;
import org.microvolunteer.platform.domain.resource.request.LoginRequest;
import org.microvolunteer.platform.domain.resource.request.RegisterUserRequest;
import org.microvolunteer.platform.domain.resource.response.LoginResponse;
import org.microvolunteer.platform.domain.resource.response.SnsRegisterResponse;
import org.microvolunteer.platform.domain.resource.response.UserRegisterResponse;
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
import java.util.HashMap;
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

    @GetMapping("/user/login")
    public String login(Model model) {
        logger.info("login");
        model.addAttribute(new LoginRequest());
        return "login_form";
    }

    /**
     * @param loginRequest
     * @return
     */
    @PostMapping("/user/default/login")
    public String default_login(LoginRequest loginRequest, Model model){
        logger.info("ログインAPI");
        String user_id = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = tokenService.getTokenByUserId(user_id);

        List<MyHandicap> handicap_list = new ArrayList<>();
        handicap_list.add(MyHandicap.builder()
                .comment("陳列棚の高いところに手がとどきません。近くの方、とっていただけませんか？")
                .handicap_level(3)
                .handicap_type("2")
                .handicap_name("車椅子")
                .reliability_th(3)
                .severity(2)
                .build());
        MyProfile myProfile = MyProfile.builder()
                .token(token)
                .volunteer_summary(MyVolunteerSummary.builder()
                        .average_satisfaction("5")
                        .my_name("浦川")
                        .support_count("2")
                        .build())
                .handicap_list(handicap_list)
                .build();
        model.addAttribute(myProfile);
        return "my_profile";
    }

    @GetMapping("/user/add_handicap/{token}")
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
        return "handicap_form";
    }

    @PostMapping("/user/default/register/{token}")
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

    @GetMapping("/history/{token}")
    public String volunteer_history(@PathVariable String token, Model model) {
        logger.info("history");
        // tokenからuser_idを取得
        String volunteer_id = tokenService.getUserId(token);
        List<MyActivity> history = userService.getMyActivities(volunteer_id, 10);
        model.addAttribute("history", history);
        return "my_history";
    }

}
