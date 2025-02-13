package xyz.le30r.aiproxy.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(MeterRegistry meterRegistry) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(30000);
    requestFactory.setReadTimeout(90000);

    return new RestTemplateBuilder()
        .requestFactory(() -> requestFactory)
        .interceptors(new MetricsInterceptor(meterRegistry))
        .build();
  }
}
