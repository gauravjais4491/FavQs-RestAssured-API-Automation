package com.automation.models.pojo.Session.CreateUserSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentialsRequestBody {
    private String login;
    private String password;
}
