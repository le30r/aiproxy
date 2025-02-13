package xyz.le30r.aiproxy.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gemini-client/**")
public class GeminiProxyController extends ProxyController {

  private static final String TARGET_URL = "https://generativelanguage.googleapis.com";
  private static final String PREFIX = "/gemini";


  @Override
  String getTargetUrl() {
    return TARGET_URL;
  }

  @Override
  String getPrefix() {
    return PREFIX;
  }
}