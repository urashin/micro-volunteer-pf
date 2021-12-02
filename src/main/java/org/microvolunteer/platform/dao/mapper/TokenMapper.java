package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void registerToken(String session_id,String user_id);
    String getUserId(String token);
}
