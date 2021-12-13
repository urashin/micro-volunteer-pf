package org.microvolunteer.platform.domain.dto;

import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.RegisterHelp;

public class HelpDto {
    // service -> model
    public static RegisterHelp registerHelp(
            String user_id
            , HandicapInfo handicapInfo
            , String location
            , Integer status) {
        return RegisterHelp.builder()
                .handicapped_id(user_id)
                .volunteer_id(null)
                .reliability_th(handicapInfo.getReliability_th())
                .severity(handicapInfo.getSeverity())
                .location(location)
                .comment(handicapInfo.getComment())
                .status(status)
                .build();
    }
}
