package org.microvolunteer.platform.service;

import org.microvolunteer.platform.dao.mapper.MyGeometryMapper;
import org.microvolunteer.platform.dao.mapper.UserMapper;
import org.microvolunteer.platform.dto.GeometryDto;
import org.microvolunteer.platform.dto.UserPropertyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UsersService {
    private UserMapper userMapper;
    private MatchingService matchingService;

    @Autowired
    public UsersService(
            UserMapper userMapper
            ,MatchingService matchingService
    ) {
        this.userMapper = userMapper;
        this.matchingService= matchingService;
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
        // 初期座標を登録する
        String x = "00.0000";
        String y = "00.0000";
        GeometryDto location = GeometryDto.builder().xGeometry(x).yGeometry(y).build();
        Integer status = 0;
        matchingService.insertMyGeometry(uuid.toString(), location, status);
        return uuid.toString();
    }
    public UserPropertyDto getUserProperty(String user_id) {
        return userMapper.getUserProperty(user_id);
    }

}
