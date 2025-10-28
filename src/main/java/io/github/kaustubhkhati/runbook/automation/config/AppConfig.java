package io.github.kaustubhkhati.runbook.automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

   @Bean
   public ObjectMapper objectMapper() {
      ObjectMapper mapper = new ObjectMapper();
      // Register JavaTimeModule to handle OffsetDateTime, LocalDate, etc.
      mapper.registerModule(new JavaTimeModule());
      // Optionally: configure serialization of dates (to avoid timestamps)
      // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
   }

   @Bean
   public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
      return new NamedParameterJdbcTemplate(jdbcTemplate);
   }


   @Bean
   public RestTemplate getRestTemplate() throws NoSuchAlgorithmException, KeyManagementException {

      // Create SSL context to trust all certificates
      SSLContext sslContext = SSLContext.getInstance("TLS");

      // Define trust managers to accept all certificates
      TrustManager[] trustManagers = new TrustManager[] {new X509TrustManager() {
         // Method to check client's trust - accepting all certificates
         public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
         }

         // Method to check server's trust - accepting all certificates
         public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
         }

         // Method to get accepted issuers - returning an empty array
         public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
         }
      }};

      // Initialize SSL context with the defined trust managers
      sslContext.init(null, trustManagers, null);

      // Disable SSL verification for RestTemplate

      // Set the default SSL socket factory to use the custom SSL context
      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

      // Set the default hostname verifier to allow all hostnames
      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

      // Create a RestTemplate with a custom request factory

      // Build RestTemplate with SimpleClientHttpRequestFactory

      return new RestTemplateBuilder().requestFactory(SimpleClientHttpRequestFactory.class)
          .build(); // Return the configured RestTemplate
   }
}
