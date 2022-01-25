package org.microvolunteer.platform.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.repository.dao.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private SnsRegisterMapper snsRegisterMapper;

    public String createToken(String user_id) {
        /*
        try {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 60); // 有効期限2ヶ月
            Date expire = calendar.getTime();

            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(expire)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            //無効なトークンの場合
        }
         */
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

    public String getTokenByUserId(String user_id) {
        return tokenMapper.getTokenByUserId(user_id);
    }
    public String getUserIdBySnsId(String sns_id) {
        return snsRegisterMapper.getUserId(sns_id);
    }
}
