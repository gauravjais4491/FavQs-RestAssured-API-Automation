package com.automation.Config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentLoader {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getEnvVariable(String key) {
        return dotenv.get(key);
    }
}
