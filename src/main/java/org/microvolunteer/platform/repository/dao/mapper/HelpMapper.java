package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.Help;
import org.microvolunteer.platform.domain.resource.HelpSignal;
import org.microvolunteer.platform.domain.resource.NeighborDistance;
import org.microvolunteer.platform.domain.resource.RegisterHelp;

import java.util.List;

@Mapper
public interface HelpMapper {
    void registerHelp(RegisterHelp help);
    void cancel(String handicapped_id);
    Help getHelpInfo(Integer help_id);
    Integer getHelpId(String user_id);
    String getVolunteerId(Integer help_id);
    String getHandicappedId(Integer help_id);
    List<HelpSignal> getHelpSignals(String my_id, String location);
    HelpSignal getHelpSignal(Integer help_id, String location);
    //Integer countTargetVolunteers(Integer help_id); // 対象となるボランティア数をカウントする
    void accept(Integer help_id,String volunteer_id);
    //void closeHelp(Integer help_id);
    List<NeighborDistance> getNeighborhood(String my_id, String location);
    //Integer checkMatchingVolunteer(Integer help_id, String volunteer_id);
    //Integer checkMatchingHandicapped(Integer help_id, String handicapped_id);
}
