package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.UserPropertyDto;
import org.mybatis.spring.annotation.MapperScan;

@Mapper
public interface UserMapper {
    UserPropertyDto getUserProperty(String user_id);
    void insertUserProperty(UserPropertyDto userProperty);
}
