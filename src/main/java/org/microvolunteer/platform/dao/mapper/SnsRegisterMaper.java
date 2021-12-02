package org.microvolunteer.platform.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.microvolunteer.platform.dto.SnsRegisterDto;
import org.mybatis.spring.annotation.MapperScan;

@Mapper
public interface SnsRegisterMaper {
    void SnsIdRegister(String snsId, String user_id);
}
