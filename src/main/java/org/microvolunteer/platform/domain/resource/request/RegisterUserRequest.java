package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;

@Data
public class RegisterUserRequest {
    @NotNull
    @Size(min=16, max=64)
    private String token;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String name;

    @NotNull
    private String password;

    public HashMap<String,String> createHashMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("email", email);
        map.put("name", name);
        map.put("password", password);
        return map;
    }
}
