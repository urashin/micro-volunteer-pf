package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.DoneThanks;
import org.microvolunteer.platform.domain.resource.SendThanks;
import org.microvolunteer.platform.domain.resource.VolunteerHistory;

import java.util.List;

@Mapper
public interface ThanksMapper {
    public void thanks(Integer help_id, String handicapped_id, Integer satisfaction, Integer status);
    public List<VolunteerHistory> getMyVolunteerHistory(String volunteer_id, Integer get_limit);
    public List<SendThanks> getSendList(String handicapped_id, Integer get_limit);
    public List<DoneThanks> getDoneList(String handicapped_id, Integer get_limit);
}
