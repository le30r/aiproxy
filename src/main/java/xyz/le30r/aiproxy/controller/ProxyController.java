package xyz.le30r.aiproxy.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class ProxyController {

  @Autowired
  private RestTemplate restTemplate;

  @RequestMapping
  public ResponseEntity<byte[]> proxyRequest(
      HttpServletRequest request, @RequestBody(required = false) byte[] body) {

    HttpMethod method = HttpMethod.valueOf(request.getMethod());


    String path = request.getRequestURI().replaceFirst(getPrefix(), "");
    String queryString = request.getQueryString();
    String url = getTargetUrl() + path + (queryString != null ? "?" + queryString : "");


    HttpHeaders headers = new HttpHeaders();
    Enumeration<String> headerNames = request.getHeaderNames();
    while(headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
        continue;
      }
      Enumeration<String> headerValues = request.getHeaders(headerName);
      while(headerValues.hasMoreElements()) {
        headers.add(headerName, headerValues.nextElement());
      }
    }


    HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

    try {

      ResponseEntity<byte[]> response = restTemplate.exchange(
          new URI(url),
          method,
          entity,
          byte[].class
      );

      HttpHeaders responseHeaders = new HttpHeaders();
      for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
        if (entry.getKey().equalsIgnoreCase(HttpHeaders.TRANSFER_ENCODING)) {
          continue;
        }
        responseHeaders.put(entry.getKey(), entry.getValue());
      }

      return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

    }
    catch (HttpClientErrorException e) {

      log.error("Proxy error:", e);

      return ResponseEntity
          .status(e.getStatusCode())
          .headers(e.getResponseHeaders())
          .body(e.getResponseBodyAsByteArray());
    }
    catch (Exception e) {
      log.error("Proxy error:", e);
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
  }

  abstract String getTargetUrl();
  abstract String getPrefix();
}
