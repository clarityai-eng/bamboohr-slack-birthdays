package ai.clarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackPublisher {

  private static final Logger log = LoggerFactory.getLogger(SlackPublisher.class);
  private final URL webHookUrl;

  public SlackPublisher(URL webHookUrl) {
    this.webHookUrl = webHookUrl;
  }

  public void publishMessage(String message) {
    try {
      log.info("Publishing '{}' slack message", message);
      var data = String.format("{ \"text\": \"%s\"}", message);
      String response = doPost(webHookUrl, data);
      log.info("Slack API response: {}", response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String doPost(URL url, String content) throws IOException {
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json; utf-8");
    con.setRequestProperty("Accept", "application/json");
    con.setDoOutput(true);
    try (var os = con.getOutputStream()) {
      byte[] input = content.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    var response = new StringBuilder();
    try (var br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }

    return response.toString();
  }
}
