package com.automation.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonUtil {

    public static JsonNode getJsonPathFromFile(String filePath) throws IOException {
        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Read the JSON file into a JsonNode tree
        return objectMapper.readTree(new File(filePath));
    }
}
