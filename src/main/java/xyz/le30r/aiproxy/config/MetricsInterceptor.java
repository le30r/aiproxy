package xyz.le30r.aiproxy.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;

public class MetricsInterceptor implements ClientHttpRequestInterceptor {

  private final MeterRegistry meterRegistry;

  public MetricsInterceptor(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    Timer.Sample sample = Timer.start(meterRegistry);
    try {
      ClientHttpResponse response = execution.execute(request, body);
      sample.stop(Timer.builder("rest.client.request")
          .tag("method", request.getMethod().name())
          .tag("uri", request.getURI().toString())
          .tag("status", String.valueOf(response.getStatusCode().value()))
          .register(meterRegistry));
      return response;
    } catch (IOException e) {
      sample.stop(Timer.builder("rest.client.request")
          .tag("method", request.getMethod().name())
          .tag("uri", request.getURI().toString())
          .tag("status", "ERROR")
          .register(meterRegistry));
      throw e;
    }
  }
}
