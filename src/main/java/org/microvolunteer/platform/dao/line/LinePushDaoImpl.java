package org.microvolunteer.platform.dao.line;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "lineconnection")
public class LinePushDaoImpl implements LinePushDao {
    private Logger logger = LoggerFactory.getLogger(LinePushDao.class);
    private String access_token;

    public void notify(String message) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://notify-api.line.me/api/notify");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Authorization", "Bearer " + access_token);
            try (OutputStream os = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(os)) {
                writer.append("message=").append(URLEncoder.encode(message, "UTF-8")).flush();
                try (InputStream is = connection.getInputStream();
                     BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                    String res = r.lines().collect(Collectors.joining());
                    if (!res.contains("\"message\":\"ok\"")) {
                        System.out.println(res);
                        System.out.println("なんか失敗したっぽい");
                    }
                }
            }
        } catch (Exception ignore) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
