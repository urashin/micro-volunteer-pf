package org.microvolunteer.platform.api.client;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.microvolunteer.platform.api.interceptor.HttpRequestInterceptor;
import org.microvolunteer.platform.domain.resource.HandicapInfo;
import org.microvolunteer.platform.domain.resource.NeighborDistance;
import org.microvolunteer.platform.domain.resource.snsmessage.LineLocationMessageRequest;
import org.microvolunteer.platform.domain.resource.snsmessage.LineLocationMessageResponse;
import org.microvolunteer.platform.service.MatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class LineMessageRestClient {
    private Logger logger = LoggerFactory.getLogger(MatchingService.class);

    @Value("${line-message.api_uri}")
    private String api_uri;

    @Value("${line-message.accept_button_uri}")
    private String accept_button_uri;

    @Value("${line-message.ignore_button_uri}")
    private String ignore_button_uri;

    @Value("${line-message.accept_button_label}")
    private String accept_button_label;

    @Value("${line-message.ignore_button_label}")
    private String ignore_button_label;

    public void request(String sns_id, NeighborDistance neighborDistance, HandicapInfo handicapInfo) {
        RestTemplate restTemplate = new RestTemplate();

        /*
         * LocationMessageDetailsリストの作成
         */
        LineLocationMessageRequest.LineMessageDetail messageDetail_a
                = LineLocationMessageRequest.LineMessageDetail.builder()
                .label("handicap type")
                .text(handicapInfo.getHandicap_type().toString())
                .build();
        LineLocationMessageRequest.LineMessageDetail messageDetail_b
                = LineLocationMessageRequest.LineMessageDetail.builder()
                .label("handicap level")
                .text(handicapInfo.getHandicap_level().toString())
                .build();
        List<LineLocationMessageRequest.LineMessageDetail> messageDetails = new ArrayList<>();
        messageDetails.add(messageDetail_a);
        messageDetails.add(messageDetail_b);

        /*
         * ボタンの作成
         */
        LineLocationMessageRequest.LineMessageAction messageAction_accept =
                LineLocationMessageRequest.LineMessageAction.builder()
                        .text(accept_button_label)
                        .link(accept_button_uri)
                        .build();
        LineLocationMessageRequest.LineMessageAction messageAction_ignore =
                LineLocationMessageRequest.LineMessageAction.builder()
                        .text(ignore_button_label)
                        .link(ignore_button_uri)
                        .build();
        LineLocationMessageRequest.LineMessageActions messageActions
                = LineLocationMessageRequest.LineMessageActions.builder()
                .primary(messageAction_accept)
                .secondary(messageAction_ignore)
                .build();

        /*
         * RequestMessage本体の作成
         */
        LineLocationMessageRequest lineLocationMessage = LineLocationMessageRequest.builder()
                .sns_id(sns_id)
                .location_message(
                        LineLocationMessageRequest.LineLocationMessage.builder()
                                .title("Help!")
                                .address("近く")
                                .longitude(Double.valueOf(neighborDistance.getY_geometry()))
                                .latitude(Double.valueOf(neighborDistance.getX_geometry()))
                                .build()
                )
                .action_message(
                        LineLocationMessageRequest.LineActionMessage.builder()
                                .title(handicapInfo.getComment())
                                .details(messageDetails)
                                .actions(messageActions)
                                .build()
                )
                .build();

        /*
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson2.toJson(lineLocationMessage);
        logger.info("LINE Message request {}", prettyJson);
         */

        try {

            RequestEntity<LineLocationMessageRequest> request = RequestEntity
                    .post(api_uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(lineLocationMessage);
            ResponseEntity<LineLocationMessageResponse> response = restTemplate.exchange(request, LineLocationMessageResponse.class);

        } catch (RestClientException e) {
            logger.info("RestClient error : {}", e.toString());
        }

    }
}
