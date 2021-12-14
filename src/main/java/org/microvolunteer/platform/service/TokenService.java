package org.microvolunteer.platform.service;

import org.microvolunteer.platform.repository.dao.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenMapper tokenMapper;

    public String createToken(String user_id) {
        UUID token = UUID.randomUUID();
        tokenMapper.registerToken(
                token.toString()
                , user_id
        );
        return token.toString();
    }

    public String getUserId(String token) {
        return tokenMapper.getUserId(token);
    }
}
