package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.SnsRegisterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsIdRegisterService {
    private SnsRegisterMapper snsRegisterMapper;

    @Autowired
    public SnsIdRegisterService(
            SnsRegisterMapper snsRegisterMapper
            ) {
        this.snsRegisterMapper = snsRegisterMapper;
    }

    public void registerSnsId(String sns_id, String user_id, Integer sns_type) {
        snsRegisterMapper.registerSnsId(sns_id, user_id, sns_type);
        return;
    }
}
