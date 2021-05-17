package ai.clarity;

import ai.clarity.BirthdaysSupplier.Birthday;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    if (args.length == 2) {
      var iCalFeedUrl = new URL(args[0]);
      var slackWehHookUrl = new URL(args[1]);
      var today = new Date();
      new Main().run(iCalFeedUrl, slackWehHookUrl, today);
    } else {
      System.err.println("Expected 2 params");
      System.err.println("Usage: bamboo-slack-birthdays.jar {iCalFeedUrl} {slackWehHookUrl}");
    }
  }

  private void run(URL iCalFeedUrl, URL slackWehHookUrl, Date date) {
    log.info("Looking for BirthDays events for {}", date);
    List<BirthdaysSupplier.Birthday> birthdays = new BirthdaysSupplier(iCalFeedUrl, date).get();

    if (!birthdays.isEmpty()) {
      log.info("Today's birthdays: {}", birthdays);
      String message = createCongratsMessage(birthdays);

      new SlackPublisher(slackWehHookUrl).publishMessage(message);
    } else {
      log.info("There's no birthdays on iCal feed for today.");
    }
  }

  private String createCongratsMessage(List<Birthday> birthdays) {
    String people = mergeNames(birthdays.stream().map(Birthday::getName).collect(Collectors.toList()));
    return String.format("Let's congratulate %s for his birthday!! :birthday: :tada:", people);
  }

  private String mergeNames(List<String> names) {
    String res;
    if (names.isEmpty()) {
      res = "";
    } else if (names.size() == 1) {
      res = names.get(0);
    } else {
      var element1 = String.join(", ", names.subList(0, names.size() - 1));
      var element2 = names.get(names.size() - 1);
      res = element1 + " and " + element2;
    }

    return res;
  }
}
