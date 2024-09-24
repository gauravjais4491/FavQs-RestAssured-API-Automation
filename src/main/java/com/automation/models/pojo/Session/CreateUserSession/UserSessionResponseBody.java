package com.automation.models.pojo.Session.CreateUserSession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionResponseBody {
    @JsonProperty("User-Token")
    private String userToken;
    private String login;
    private String email;
}
