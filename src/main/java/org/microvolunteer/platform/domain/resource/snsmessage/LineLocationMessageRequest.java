package org.microvolunteer.platform.domain.resource.snsmessage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.IntStream;

@Data
@Builder
@JsonSerialize
public class LineLocationMessageRequest {
    private String sns_id;
    private LineLocationMessage location_message;
    private LineActionMessage action_message;
    @Data
    @Builder
    @JsonSerialize
    public static class LineLocationMessage {
        private String title;
        private String address;
        private Double latitude;
        private Double longitude;
    }
    @Data
    @Builder
    @JsonSerialize
    public static class LineActionMessage {
        private String title;
        private List<LineMessageDetail> details;
        private LineMessageActions actions;
    }
    @Data
    @Builder
    @JsonSerialize
    public static class LineMessageDetail {
        private String label;
        private String text;
    }
    @Data
    @Builder
    @JsonSerialize
    public static class LineMessageActions {
        private LineMessageAction primary;
        private LineMessageAction secondary;
    }
    @Data
    @Builder
    @JsonSerialize
    public static class LineMessageAction {
        private String text;
        private String link;
    }
}
