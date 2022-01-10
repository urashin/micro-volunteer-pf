package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.domain.resource.Login;
import org.microvolunteer.platform.domain.resource.RegisterUserProperty;
import org.microvolunteer.platform.domain.resource.UserProperty;

@Mapper
public interface UserMapper {
    UserProperty getUserProperty(String user_id);
    void registerUserProperty(RegisterUserProperty userProperty);
    void updateUserProperty(RegisterUserProperty userProperty);
    String login(Login login);
}
