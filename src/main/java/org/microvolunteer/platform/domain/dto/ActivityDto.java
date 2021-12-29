package org.microvolunteer.platform.domain.dto;


import org.microvolunteer.platform.domain.resource.MyActivity;
import org.microvolunteer.platform.domain.resource.VolunteerHistory;

public class ActivityDto {
    public static MyActivity getActivity(VolunteerHistory history) {
        String area_name = history.getArea_name();
        if (area_name.isEmpty()) {
            area_name = "経度：" + history.getX_geometry() + " / 緯度：" + history.getY_geometry();
        }
        return MyActivity.builder()
                .datetime(history.getDatetime())
                .area_name(area_name)
                .icon_path("/img/" + history.getHandicap_type() + ".png")
                .satisfaction("評価：" + history.getSatisfaction().toString())
                .build();
    }
}
