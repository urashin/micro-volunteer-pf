package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.HandicapInfoDto;

@Mapper
public interface HelpMapper {
    void registerHelp(HandicapInfoDto handicapInfo);
    //HandicapInfoDto getHelpInfo(HelpSearchCondition condition);
}
