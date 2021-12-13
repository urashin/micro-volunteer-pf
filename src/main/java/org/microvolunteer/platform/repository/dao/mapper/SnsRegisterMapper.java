package org.microvolunteer.platform.repository.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SnsRegisterMapper {
    void registerSnsId(String sns_id, String user_id, Integer sns_type);
}
