package org.microvolunteer.platform.domain.resource.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microvolunteer.platform.domain.resource.MyHandicap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyHandicapResponse {
    MyHandicap myHandicap;
}
