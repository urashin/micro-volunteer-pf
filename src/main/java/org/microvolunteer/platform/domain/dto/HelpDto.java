package org.microvolunteer.platform.domain.dto;

import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.RegisterHelp;

public class HelpDto {
    // service -> model
    public static RegisterHelp registerHelp(
            String user_id
            , HandicapInfo handicapInfo
            , String location
            , Integer area_id
            , Integer status) {
        return RegisterHelp.builder()
                .handicapped_id(user_id)
                .volunteer_id(null)
                .reliability_th(handicapInfo.getReliability_th())
                .severity(handicapInfo.getSeverity())
                .location(location)
                .handicap_type(handicapInfo.getHandicap_type())
                .handicap_level(handicapInfo.getHandicap_level())
                .area_id(area_id)
                .comment(handicapInfo.getComment())
                .status(status)
                .build();
    }
}
