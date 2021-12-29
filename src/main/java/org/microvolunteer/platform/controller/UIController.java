package org.microvolunteer.platform.controller;

import org.microvolunteer.platform.api.client.LineMessageRestClient;
import org.microvolunteer.platform.domain.resource.VolunteerHistory;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.service.MatchingService;
import org.microvolunteer.platform.service.TokenService;
import org.microvolunteer.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/history/{sns_id}")
    public String volunteer_history(@PathVariable String sns_id, Model model) {
        logger.info("line_thanks");
        String volunteer_id = tokenService.getUserIdBySnsId(sns_id);
        List<VolunteerHistory> history = userService.getMyVolunteerHistory(volunteer_id, 10);
        model.addAttribute("history", history);
        return "my_history";
    }

}
