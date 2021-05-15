FROM adoptopenjdk:11-jre

LABEL \
  org.label-schema.name="bamboohr-slack-birthdays" \
  org.label-schema.description="Slack birthdays publisher" \
  org.label-schema.url="https://github.com/clarityai-eng/bamboohr-slack-birthdays" \
  org.label-schema.docker.Dockerfile="Dockerfile"

ARG SERVICE_HOME=/opt/clarity
ARG GUID=1001
ARG UID=1001

RUN mkdir -p ${SERVICE_HOME}
WORKDIR ${SERVICE_HOME}

COPY ./build/libs/bamboohr-slack-birthdays-*.jar ./app.jar

RUN addgroup --system --gid ${GUID} clarity && \
    adduser --system --uid ${UID} --ingroup clarity clarity

USER clarity

ENTRYPOINT ["java", "-jar", "app.jar"]
