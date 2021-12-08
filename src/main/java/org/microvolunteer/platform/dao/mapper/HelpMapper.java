package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.GeometryDto;
import org.microvolunteer.platform.dto.HelpDto;
import org.microvolunteer.platform.dto.NeighborDistanceDto;

import java.util.List;

@Mapper
public interface HelpMapper {
    void registerHelp(HelpDto helpDto);
    HelpDto getHelpInfo(Integer help_id);
    Integer countTargetVolunteers(Integer help_id); // 対象となるボランティア数をカウントする
    void accept(Integer help_id,String volunteer_id);
    void closeHelp(Integer help_id);
    List<NeighborDistanceDto> getNeighborhood(String my_id, String location);
    Integer checkMatchingVolunteer(Integer help_id, String volunteer_id);
    Integer checkMatchingHandicapped(Integer help_id, String handicapped_id);
}
