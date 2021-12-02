package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.UserMapper;
import org.microvolunteer.platform.dto.UserPropertyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class UsersService {
    private UserMapper userMapper;

    @Autowired
    public UsersService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public String createUser() {
        UUID uuid = UUID.randomUUID();
        userMapper.insertUserProperty(
                UserPropertyDto.builder()
                        .user_id(uuid.toString())
                        .email("mail")
                        .password("pass")
                        .name("name")
                        .status(0)
                        .build()
        );
        return uuid.toString();
    }
    public UserPropertyDto getUserProperty(String user_id) {
        return userMapper.getUserProperty(user_id);
    }

}
