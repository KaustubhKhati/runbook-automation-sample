package io.github.kaustubhkhati.runbook.automation.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Utility class for JSON serialization/deserialization. Uses the Spring-managed ObjectMapper bean,
 * ensuring JavaTimeModule is registered.
 */
@Component
public class Jsons {

   private final ObjectMapper mapper;

   // Constructor injection gets the ObjectMapper from AppConfig
   public Jsons(ObjectMapper mapper) {
      this.mapper = mapper;
   }

   /**
    * Serialize an object to JSON string.
    */
   public String write(Object object) {
      try {
         return mapper.writeValueAsString(object);
      } catch (Exception e) {
         throw new RuntimeException("Failed to serialize object to JSON", e);
      }
   }

   /**
    * Deserialize JSON string into a specific type.
    */
   public <T> T read(String json, Class<T> type) {
      try {
         return mapper.readValue(json, type);
      } catch (Exception e) {
         throw new RuntimeException("Failed to deserialize JSON to " + type.getSimpleName(), e);
      }
   }

   /**
    * Deserialize JSON string into a List of specific element type.
    */
   public <T> List<T> readList(String json, Class<T> elementType) {
      try {
         return mapper.readValue(json, new TypeReference<>() {
         });
      } catch (Exception e) {
         throw new RuntimeException(
             "Failed to deserialize JSON to List<" + elementType.getSimpleName() + ">", e);
      }
   }
}
