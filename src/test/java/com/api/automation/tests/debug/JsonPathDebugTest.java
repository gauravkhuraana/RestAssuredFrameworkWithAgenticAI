package com.api.automation.tests.debug;

import com.api.automation.utils.JsonPathUtils;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonPathDebugTest {
    
    @Test
    public void testJsonPathDirectly() {
        String jsonResponse = "[{\"id\":1,\"title\":\"Test\"}]";
        
        // Test with REST Assured JsonPath directly
        JsonPath jsonPath = JsonPath.from(jsonResponse);
        Object value = jsonPath.get("$[0].id");
        System.out.println("Direct JsonPath result: " + value);
        System.out.println("Value is null: " + (value == null));
        
        // Test with our utility
        boolean exists = JsonPathUtils.pathExists(jsonResponse, "$[0].id");
        System.out.println("JsonPathUtils.pathExists result: " + exists);
        
        // Try alternative path syntaxes
        Object value2 = jsonPath.get("[0].id");
        System.out.println("Alternative path [0].id result: " + value2);
        
        boolean exists2 = JsonPathUtils.pathExists(jsonResponse, "[0].id");
        System.out.println("JsonPathUtils.pathExists with [0].id: " + exists2);
        
        // Extract value
        Integer id = JsonPathUtils.extractInt(jsonResponse, "[0].id");
        System.out.println("Extracted ID: " + id);
        
        assertTrue(exists2, "Path [0].id should exist");
    }
}
