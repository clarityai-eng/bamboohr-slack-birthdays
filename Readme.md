bamboohr-slack-birthdays
========================

Very simple cli to download Birthdays from Bamboo iCal feed and publish a message congratulating people for this birthday.

### Usage

#### As a java Jar

call `java -jar bamboohr-slack-birthdays.jar {iCalFeedUrl} {slackWehHookUrl}`

#### As a Docker container

call `docker run bamboohr-slack-birthdays {iCalFeedUrl} {slackWehHookUrl}`

### Development

The application has been developed with the idea of using the least dependencies possible.  
By now only a library to parse iCal and SLF4J are used.