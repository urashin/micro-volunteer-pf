package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.VolunteerHistoryDto;

import java.util.List;

@Mapper
public interface ThanksMapper {
    public void thanks(Integer help_id, String volunteer_id, Integer satisfaction, Integer status);
    public List<VolunteerHistoryDto> getMyVolunteerHistory(String volunteer_id, Integer get_limit);
}
