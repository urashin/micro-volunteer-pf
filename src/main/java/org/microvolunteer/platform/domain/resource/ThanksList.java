package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ThanksList {
    List<SendThanks> send_list;
    List<DoneThanks> done_list;
    /*
    @Data
    @Builder
    public static class SendThanks {
        private Integer help_id;
        private String handicap_name;
        private String handicap_type;
        private String datetime;
        private String area_name;
        private String satisfaction;
    }

    @Data
    @Builder
    public static class DoneThanks {
        private String handicap_name;
        private String handicap_type;
        private String datetime;
        private String area_name;
        private String satisfaction;
    }
     */
}
