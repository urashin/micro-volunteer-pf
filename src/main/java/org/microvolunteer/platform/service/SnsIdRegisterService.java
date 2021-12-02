package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.SnsRegisterMaper;
import org.microvolunteer.platform.dao.mapper.TokenMapper;
import org.microvolunteer.platform.dto.SnsRegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsIdRegisterService {
    private SnsRegisterMaper snsRegisterMaper;
    private TokenMapper tokenMapper;

    @Autowired
    public SnsIdRegisterService(
            SnsRegisterMaper snsRegisterMaper,
            TokenMapper tokenMapper
            ) {
        this.snsRegisterMaper = snsRegisterMaper;
        this.tokenMapper = tokenMapper;
    }

    public void registerSnsId(String snsId, String user_id) {
        snsRegisterMaper.SnsIdRegister(snsId, user_id);
        return;
    }
}
