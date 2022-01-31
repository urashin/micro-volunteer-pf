package org.microvolunteer.platform.service;

import org.microvolunteer.platform.domain.resource.HandicapMaster;
import org.microvolunteer.platform.repository.dao.mapper.HandicapMasterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    HandicapMasterMapper handicapMasterMapper;

    @Value("${encrypt.volunteerdb.key}")
    private String auth_key;

    public void addHandicapMaster(HandicapMaster handicapMaster, String auth_code) {
        if (!auth_code.equals(auth_key)) {
            return;
        }
        handicapMasterMapper.addHandicapType(handicapMaster);
    }

}
