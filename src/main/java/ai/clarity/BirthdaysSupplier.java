package ai.clarity;


import ai.clarity.BirthdaysSupplier.Birthday;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirthdaysSupplier implements Supplier<List<Birthday>> {

  private static final Logger log = LoggerFactory.getLogger(BirthdaysSupplier.class);
  private final URL iCalFeed;
  private final Date date;

  public BirthdaysSupplier(URL iCalFeed, Date date) {
    this.iCalFeed = iCalFeed;
    this.date = date;
  }

  @Override
  public List<Birthday> get() {
    List<Birthday> birthdays;
    try {
      var iCalContent = readStringFromURL(iCalFeed);
      ICalendar iCal = Biweekly.parse(iCalContent).first();
      log.info("Got {} events from iCal feed", iCal.getEvents().size());

      birthdays = iCal.getEvents().stream()
        .filter(event -> isEventForDate(event, date))
        .filter(event -> event.getCategories().stream().flatMap(cat -> cat.getValues().stream()).anyMatch(cat -> cat.equals("Birthdays")))
        .map(this::toBirthDay)
        .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return birthdays;
  }

  public boolean isEventForDate(VEvent event, Date date) {
    var calendar = toCalendar(date);
    var eventCalendar = toCalendar(event.getDateStart().getValue());

    return (eventCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))
      && (eventCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE));
  }


  public Calendar toCalendar(Date date) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }


  private String readStringFromURL(URL requestURL) throws IOException {
    try (Scanner scanner = new Scanner(requestURL.openStream(), StandardCharsets.UTF_8)) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }


  private Birthday toBirthDay(VEvent event) {
    String summary = event.getSummary().getValue();
    String name = summary.split(" \\- ")[0];
    LocalDate eventDate = event.getDateStart().getValue().toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();

    return new Birthday(name, eventDate);
  }

  static class Birthday {

    final String name;
    final LocalDate date;

    Birthday(String name, LocalDate date) {
      this.name = name;
      this.date = date;
    }

    public String getName() {
      return name;
    }

    public LocalDate getDate() {
      return date;
    }

    @Override
    public String toString() {
      return "Birthday{" +
        "name='" + name + '\'' +
        ", date=" + date +
        '}';
    }
  }
}
