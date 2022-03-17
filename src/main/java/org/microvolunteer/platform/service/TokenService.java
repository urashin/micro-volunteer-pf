package org.microvolunteer.platform.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.microvolunteer.platform.repository.dao.mapper.SnsRegisterMapper;
import org.microvolunteer.platform.repository.dao.mapper.TokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private Logger logger = LoggerFactory.getLogger(org.microvolunteer.platform.service.TokenService.class);

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private SnsRegisterMapper snsRegisterMapper;

    @Value("${encrypt.jwt.secret}")
    private String jwt_secret;

    public String createToken(String user_id) {
        String token = null;
        try {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 60); // 有効期限2ヶ月
            Date expire = calendar.getTime();

            Algorithm algorithm = Algorithm.HMAC256(jwt_secret);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(expire)
                    .withClaim("user_id",user_id)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            logger.error("error : " + exception.getMessage());
            //無効なトークンの場合
        }
        /*
        UUID token = UUID.randomUUID();
        tokenMapper.registerToken(
                token.toString()
                , user_id
        );

         */
        return token;
    }

    public String getUserId(String token) {
        String user_id = null;
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim claim = jwt.getClaim("user_id");
            user_id = claim.asString();
        } catch (JWTDecodeException exception){
            //Invalid token
        }
        return user_id;
//        return tokenMapper.getUserId(token);
    }

    public String getTokenByUserId(String user_id) {
        return this.createToken(user_id);
        //return tokenMapper.getTokenByUserId(user_id);
    }
    public String getUserIdBySnsId(String sns_id) {
        return snsRegisterMapper.getUserId(sns_id);
    }
}
