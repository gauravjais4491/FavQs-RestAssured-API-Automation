package com.automation.Utils;

import io.restassured.path.json.JsonPath;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JsonUtil {

    // Method to load and return the JsonPath of a JSON file
    public static JsonPath getJsonPathFromFile(String filePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JsonPath(jsonContent);
    }

    // Example of extracting a specific key if needed
    public static String getValueFromJsonFile(String filePath, String key) throws IOException {
        JsonPath jsonPath = getJsonPathFromFile(filePath);
        System.out.println(jsonPath);
        return jsonPath.getString(key);
    }
}